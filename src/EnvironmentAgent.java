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

public class EnvironmentAgent extends Agent {

    static final String caveConfig = ""
            + ". . . P "
            + "W G P . "
            + ". . . . "
            + "S . P . ";
    static WumpusCave cave = new WumpusCave(4, 4, caveConfig);
    private boolean isWumpusAlive = true;
    private boolean isGoldGrabbed;

    private List<PlayerPosition> playerPositions = new ArrayList<>();
    //    private Map<Agent, PlayerPosition> PlayerPositions = new HashMap<>();
    private Set<Agent> bumpedAgents = new HashSet<>();

    private boolean isAgentBumped = false;
    private Set<Agent> agentsHavingArrow = new HashSet<>();
    private Agent agentJustKillingWumpus;
    private boolean isAgentJustKillingWumpus = false;

    public WumpusCave getCave() {
        return cave;
    }

    public boolean isWumpusAlive() {
        return isWumpusAlive;
    }

    public boolean isGoldGrabbed() {
        return isGoldGrabbed;
    }

    @Override
    protected void setup() {
        super.setup();

        playerPositions.add(cave.getStart());

        DFAgentDescription description = new DFAgentDescription();
        description.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName("environment");
        sd.setType("environment-walking");
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

        System.out.println("Environment-agent " + getAID().getName() + " terminating.");
    }

    private boolean isAgentFacingWumpus(PlayerPosition pos) {
        Room wumpus = cave.getWumpus();
        switch (pos.getOrientation()) {
            case FACING_NORTH:
                return pos.getX() == wumpus.getX() && pos.getY() < wumpus.getY();
            case FACING_SOUTH:
                return pos.getX() == wumpus.getX() && pos.getY() > wumpus.getY();
            case FACING_EAST:
                return pos.getY() == wumpus.getY() && pos.getX() < wumpus.getX();
            case FACING_WEST:
                return pos.getY() == wumpus.getY() && pos.getX() > wumpus.getX();
        }
        return false;
    }

    private PlayerPosition getCurrentPlayerPosition() {
        int lastIndex = playerPositions.size() - 1;
        return playerPositions.get(lastIndex);
    }

    private WumpusPercept getCurrentPerceptOfPlayer() {
        WumpusPercept result = new WumpusPercept();
        PlayerPosition pos = getCurrentPlayerPosition();
        List<Room> adjacentRooms = Arrays.asList(
                new Room(pos.getX() - 1, pos.getY()), new Room(pos.getX() + 1, pos.getY()),
                new Room(pos.getX(), pos.getY() - 1), new Room(pos.getX(), pos.getY() + 1)
        );
        for (Room r : adjacentRooms) {
            if (r.equals(cave.getWumpus()))
                result.setStench();
            if (cave.isPit(r))
                result.setBreeze();
        }
        if (pos.getRoom().equals(cave.getGold()))
            result.setGlitter();
        if (isAgentBumped) {
            result.setBump();
        }
        if (isAgentJustKillingWumpus) {
            result.setScream();
        }
        return result;
    }

    private ActionResult executeAction(WumpusAction action) {
        isAgentBumped = false;
        if (isAgentJustKillingWumpus) {
            isAgentJustKillingWumpus = false;
        }
        PlayerPosition pos = getCurrentPlayerPosition();
        ActionResult result = ActionResult.NOTHING;
        switch (action) {
            case FORWARD:
                PlayerPosition newPos = cave.moveForward(pos);
                playerPositions.add(newPos);
                if (newPos.equals(pos)) {
                    isAgentBumped = true;
                } else if (cave.isPit(newPos.getRoom()) || newPos.getRoom().equals(cave.getWumpus()) && isWumpusAlive) {
                    result = ActionResult.DEFEAT;
                }
                break;
            case TURN_LEFT:
                playerPositions.set(playerPositions.size() - 1, cave.turnLeft(pos));
                break;
            case TURN_RIGHT:
                playerPositions.set(playerPositions.size() - 1, cave.turnRight(pos));
                break;
            case GRAB:
                if (!isGoldGrabbed && pos.getRoom().equals(cave.getGold()))
                    isGoldGrabbed = true;
                break;
            case SHOOT:
                if (isAgentFacingWumpus(pos)) {
                    isWumpusAlive = false;
                    isAgentJustKillingWumpus = true;
                }
                break;
            case CLIMB: {
                if (isGoldGrabbed()) {
                    result = ActionResult.VICTORY;
                } else {
                    result = ActionResult.DEFEAT;
                }
                break;
            }
        }
        System.out.println(getCurrentPlayerPosition());
        return result;
    }

    private class HandleIncomingMessagesBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate cfpTemplate = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            MessageTemplate requestTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage actionMessage = myAgent.receive(cfpTemplate);
            ACLMessage requestMessage = myAgent.receive(requestTemplate);

            if (actionMessage != null) {
                System.out.println("Environment received action " + actionMessage.getContent());
                WumpusAction actionToDo = WumpusAction.valueOf(actionMessage.getContent());
                ActionResult result = executeAction(actionToDo);
                ACLMessage acceptMessage = actionMessage.createReply();
                acceptMessage.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                acceptMessage.setContent(result.toString());
                myAgent.send(acceptMessage);
            } else if (requestMessage != null) {
                myAgent.addBehaviour(new RespondToRequestBehaviour(requestMessage));
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
            WumpusPercept currentPlayerPercept = getCurrentPerceptOfPlayer();
            ACLMessage perceptReply = message.createReply();
            perceptReply.setPerformative(ACLMessage.INFORM);
            try {
                perceptReply.setContentObject(currentPlayerPercept);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            myAgent.send(perceptReply);
        }
    }
}

enum ActionResult {
    VICTORY, DEFEAT, NOTHING
}
