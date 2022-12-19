public enum WumpusAction {

    FORWARD("Forward"), TURN_LEFT("TurnLeft"), TURN_RIGHT("TurnRight"), GRAB("Grab"), SHOOT("Shoot"), CLIMB("Climb");

    public String getSymbol() {
        return symbol;
    }

    private String symbol;

    WumpusAction(String sym) {
        symbol = sym;
    }
}

