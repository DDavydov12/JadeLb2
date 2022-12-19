import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.*;

public class NavigatorAgent extends Agent {

    private WumpusKnowledgeBase kb = new WumpusKnowledgeBase(
            EnvironmentAgent.cave.getCaveXDimension(),
            EnvironmentAgent.cave.getCaveYDimension(),
            EnvironmentAgent.cave.getStart(),
            new DPLL()
    );

    protected int t = 0;

    /// plan, an action sequence, initially empty
    protected Queue<WumpusAction> plan = new LinkedList<>(); // FIFOQueue

    protected PlayerPosition currentPosition;

    protected PlayerPosition start = EnvironmentAgent.cave.getStart();

    @Override
    protected void setup() {
        DFAgentDescription description = new DFAgentDescription();
        description.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName("navigator");
        sd.setType("navigator");
        description.addServices(sd);
        try {
            DFService.register(this, description);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        addBehaviour(new HandleIncomingMessagesBehaviour());
    }

    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        System.out.println("Navigator-agent " + getAID().getName() + " terminating.");
    }

    private class HandleIncomingMessagesBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate cfpTemplate = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage actionMessage = myAgent.receive(cfpTemplate);

            if (actionMessage != null) {
                addBehaviour(new RespondToRequestBehaviour(actionMessage));
            } else {
                block();
            }
        }
    }

    private class RespondToRequestBehaviour extends OneShotBehaviour {

        private final ACLMessage message;

        public RespondToRequestBehaviour(ACLMessage requestMessage) {
            message = requestMessage;
        }

        @Override
        public void action() {
            WumpusPercept wumpusPercept = new WumpusPercept();

            String content = message.getContent();

            List<String> states = Arrays.stream(content.split("\\.")).toList();

            for (String state : states) {
                if (PlayerAgent.BREEZE.equals(state)) {
                    wumpusPercept.setBreeze();
                }
                if (PlayerAgent.BUMP.equals(state)) {
                    wumpusPercept.setBump();
                }
                if (PlayerAgent.SCREAM.equals(state)) {
                    wumpusPercept.setScream();
                }
                if (PlayerAgent.GLITTER.equals(state)) {
                    wumpusPercept.setGlitter();
                }
                if (PlayerAgent.STENCH.equals(state)) {
                    wumpusPercept.setStench();
                }
            }
            /// TELL(KB, MAKE-PERCEPT-SENTENCE(percept, t))
            kb.makePerceptSentence(wumpusPercept, t);
            /// TELL the KB the temporal "physics" sentences for time t
            kb.tellTemporalPhysicsSentences(t);

            Set<Room> safe = null;
            Set<Room> unvisited = null;

            // Optimization: Do not ask anything during plan execution (different from pseudo-code)
            if (plan.isEmpty()) {
//                notifyViews("Reasoning (t=" + t + ", Percept=" + percept + ") ...");
                currentPosition = kb.askCurrentPosition(t);
//                notifyViews("Ask position -> " + currentPosition);
                /// safe <- {[x, y] : ASK(KB, OK<sup>t</sup><sub>x,y</sub>) = true}
                safe = kb.askSafeRooms(t);
//                notifyViews("Ask safe -> " + safe);
            }

            /// if ASK(KB, Glitter<sup>t</sup>) = true then (can only be true when plan is empty!)
            if (plan.isEmpty() && kb.askGlitter(t)) {
                /// plan <- [Grab] + PLAN-ROUTE(current, {[1,1]}, safe) + [Climb]
                Set<Room> goals = new LinkedHashSet<>();
                goals.add(start.getRoom());
                plan.add(WumpusAction.GRAB);
                plan.addAll(planRouteToRooms(goals, safe));
                plan.add(WumpusAction.CLIMB);
            }

            /// if plan is empty then
            if (plan.isEmpty()) {
                /// unvisited <- {[x, y] : ASK(KB, L<sup>t'</sup><sub>x,y</sub>) = false for all t' &le; t}
                unvisited = kb.askUnvisitedRooms(t);
//                notifyViews("Ask unvisited -> " + unvisited);
                /// plan <- PLAN-ROUTE(current, unvisited &cap; safe, safe)
                plan.addAll(planRouteToRooms(SetOps.intersection(unvisited, safe), safe));
            }

            /// if plan is empty and ASK(KB, HaveArrow<sup>t</sup>) = true then
            if (plan.isEmpty() && kb.askHaveArrow(t)) {
                /// possible_wumpus <- {[x, y] : ASK(KB, ~W<sub>x,y</sub>) = false}
                Set<Room> possibleWumpus = kb.askPossibleWumpusRooms(t);
//                notifyViews("Ask possible Wumpus positions -> " + possibleWumpus);
                /// plan <- PLAN-SHOT(current, possible_wumpus, safe)
                plan.addAll(planShot(possibleWumpus, safe));
            }

            /// if plan is empty then (no choice but to take a risk)
            if (plan.isEmpty()) {
                /// not_unsafe <- {[x, y] : ASK(KB, ~OK<sup>t</sup><sub>x,y</sub>) = false}
                Set<Room> notUnsafe = kb.askNotUnsafeRooms(t);
//                notifyViews("Ask not unsafe -> " + notUnsafe);
                /// plan <- PLAN-ROUTE(current, unvisited &cap; not_unsafe, safe)
                // Correction: Last argument must be not_unsafe!
                plan.addAll(planRouteToRooms(unvisited, notUnsafe));
            }

            /// if plan is empty then
            if (plan.isEmpty()) {
//                notifyViews("Going home.");
                /// plan PLAN-ROUTE(current, {[1,1]}, safe) + [Climb]
                Set<Room> goal = new LinkedHashSet<>();
                goal.add(start.getRoom());
                plan.addAll(planRouteToRooms(goal, safe));
                plan.add(WumpusAction.CLIMB);
            }
            /// action <- POP(plan)
            WumpusAction action = plan.remove();
            /// TELL(KB, MAKE-ACTION-SENTENCE(action, t))
            kb.makeActionSentence(action, t);
            /// t <- t+1
            t = t + 1;

            replyWithAction(action);
        }

        private void replyWithAction(WumpusAction action) {
            ACLMessage reply = message.createReply();
            reply.setPerformative(ACLMessage.CFP);
            reply.setContent(action.toString());
            myAgent.send(reply);
        }

        public List<WumpusAction> planShot(Set<Room> possibleWumpus, Set<Room> allowed) {

            Set<PlayerPosition> shootingPositions = new LinkedHashSet<>();

            for (Room room : possibleWumpus) {
                int x = room.getX();
                int y = room.getY();

                for (int i = 1; i <= kb.getCaveXDimension(); i++) {
                    if (i < x)
                        shootingPositions.add(new PlayerPosition(i, y, PlayerPosition.Orientation.FACING_EAST));
                    if (i > x)
                        shootingPositions.add(new PlayerPosition(i, y, PlayerPosition.Orientation.FACING_WEST));
                }
                for (int i = 1; i <= kb.getCaveYDimension(); i++) {
                    if (i < y)
                        shootingPositions.add(new PlayerPosition(x, i, PlayerPosition.Orientation.FACING_NORTH));
                    if (i > y)
                        shootingPositions.add(new PlayerPosition(x, i, PlayerPosition.Orientation.FACING_SOUTH));
                }
            }

            // Can't have a shooting position from any of the rooms the wumpus could reside
            for (Room room : possibleWumpus)
                for (PlayerPosition.Orientation orientation : PlayerPosition.Orientation.values())
                    shootingPositions.remove(new PlayerPosition(room.getX(), room.getY(), orientation));

            List<WumpusAction> actions = new ArrayList<>(planRoute(shootingPositions, allowed));
            actions.add(WumpusAction.SHOOT);
            return actions;
        }

        public List<WumpusAction> planRouteToRooms(Set<Room> goals, Set<Room> allowed) {
            final Set<PlayerPosition> goalPositions = new LinkedHashSet<>();
            for (Room goalRoom : goals) {
                int x = goalRoom.getX();
                int y = goalRoom.getY();
                for (PlayerPosition.Orientation orientation : PlayerPosition.Orientation.values())
                    goalPositions.add(new PlayerPosition(x, y, orientation));
            }
            return planRoute(goalPositions, allowed);
        }

        public List<WumpusAction> planRoute(Set<PlayerPosition> goals, Set<Room> allowed) {

            WumpusCave cave = new WumpusCave(kb.getCaveXDimension(), kb.getCaveYDimension()).setAllowed(allowed);
            Problem<PlayerPosition, WumpusAction> problem = new GeneralProblem<>(currentPosition,
                    WumpusFunctions.createActionsFunction(cave),
                    WumpusFunctions.createResultFunction(cave), goals::contains);
            SearchForActions<PlayerPosition, WumpusAction> search =
                    new AStarSearch<>(new GraphSearch<>(), WumpusFunctions.createManhattanDistanceFunction(goals));
            Optional<List<WumpusAction>> actions = search.findActions(problem);

            return actions.orElse(Collections.emptyList());
        }
    }
}
