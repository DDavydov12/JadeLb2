import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ReceiverBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.wrapper.ControllerException;

import java.io.IOException;

public class PlayerAgent extends Agent {

    private AID environmentAgent = null;
    private AID navigatorAgent = null;

    static final String BREEZE = "Breeze";
    static final String STENCH = "Stench";
    static final String GLITTER = "Glitter";
    static final String BUMP = "Bump";
    static final String SCREAM = "Scream";

    @Override
    protected void setup() {
        super.setup();

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("environment-walking");
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            environmentAgent = result[0].getName();
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        DFAgentDescription templateNavigator = new DFAgentDescription();
        ServiceDescription sdNavigator = new ServiceDescription();
        sdNavigator.setType("navigator");
        templateNavigator.addServices(sdNavigator);
        try {
            DFAgentDescription[] result = DFService.search(this, templateNavigator);
            navigatorAgent = result[0].getName();
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        addBehaviour(new RequestPerformer());
    }

    @Override
    protected void takeDown() {
        super.takeDown();
        System.out.println("Player-agent " + getAID().getName() + " terminating.");
    }

    private class RequestPerformer extends Behaviour {

        private Integer step = 0;
        private MessageTemplate mt = null;

        @Override
        public void action() {
            switch (step) {
                case 0 -> {
                    ACLMessage requestMessage = new ACLMessage(ACLMessage.REQUEST);
                    requestMessage.setConversationId("request-id");
                    requestMessage.addReceiver(environmentAgent);
                    requestMessage.setReplyWith("request" + System.currentTimeMillis());
                    myAgent.send(requestMessage);
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("request-id"),
                            MessageTemplate.MatchInReplyTo(requestMessage.getReplyWith()));
                    step++;
                }
                case 1 -> {
                    ACLMessage reply = myAgent.receive(mt);
                    if (reply != null) {
                        if (reply.getPerformative() == ACLMessage.INFORM) {
                            try {
                                WumpusPercept percept = (WumpusPercept) reply.getContentObject();
                                System.out.println("Current environment percept is " + percept);
                                String messageContent = createStringToNavigator(percept);
                                ACLMessage requestMessage = new ACLMessage(ACLMessage.CFP);
                                requestMessage.setConversationId("navigator-message-id");
                                requestMessage.addReceiver(navigatorAgent);
                                requestMessage.setContent(messageContent);
                                requestMessage.setReplyWith("response" + System.currentTimeMillis());
                                myAgent.send(requestMessage);
                                mt = MessageTemplate.and(MessageTemplate.MatchConversationId("navigator-message-id"),
                                        MessageTemplate.MatchInReplyTo(requestMessage.getReplyWith()));
                                step++;
                            } catch (UnreadableException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else {
                        block();
                    }
                }
                case 2 -> {
                    ACLMessage reply = myAgent.receive(mt);
                    if (reply != null) {
                        if (reply.getPerformative() == ACLMessage.CFP) {
                            ACLMessage requestMessage = new ACLMessage(ACLMessage.CFP);
                            requestMessage.setConversationId("action-id");
                            requestMessage.addReceiver(environmentAgent);
                            requestMessage.setContent(reply.getContent());
                            requestMessage.setReplyWith("response" + System.currentTimeMillis());
                            mt = MessageTemplate.and(MessageTemplate.MatchConversationId("action-id"),
                                    MessageTemplate.MatchInReplyTo(requestMessage.getReplyWith()));
                            myAgent.send(requestMessage);
                            step++;
                        }
                    } else {
                        block();
                    }
                }
                case 3 -> {
                    ACLMessage reply = myAgent.receive(mt);
                    if (reply != null) {
                        if (reply.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                            ActionResult result = ActionResult.valueOf(reply.getContent());
                            switch (result) {
                                case VICTORY, DEFEAT -> {
                                    System.out.println(result);
                                    try {
                                        getContainerController().getPlatformController().kill();
                                    } catch (ControllerException e) {
                                        throw new RuntimeException(e);
                                    }
//                                    doDelete();
                                }
                                case NOTHING -> step = 0;
                            }
                        }
                    } else {
                        block();
                    }
                }
            }
        }

        @Override
        public boolean done() {
            return step == 4;
        }
    }

    private String createStringToNavigator(WumpusPercept percept) {
        StringBuilder sb = new StringBuilder();
        if (percept.isScream()) {
            sb.append(SCREAM);
            sb.append(".");
        }
        if (percept.isBreeze()) {
            sb.append(BREEZE);
            sb.append(".");
        }
        if (percept.isBump()) {
            sb.append(BUMP);
            sb.append(".");
        }
        if (percept.isGlitter()) {
            sb.append(GLITTER);
            sb.append(".");
        }
        if (percept.isStench()) {
            sb.append(STENCH);
            sb.append(".");
        }
        return sb.toString();
    }
}
