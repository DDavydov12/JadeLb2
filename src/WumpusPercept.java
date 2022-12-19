import java.io.Serializable;

public class WumpusPercept implements Serializable {
    private boolean stench;
    private boolean breeze;
    private boolean glitter;
    private boolean bump;
    private boolean scream;

    public WumpusPercept setStench() {
        stench = true;
        return this;
    }

    public WumpusPercept setBreeze() {
        breeze = true;
        return this;
    }

    public WumpusPercept setGlitter() {
        glitter = true;
        return this;
    }

    public WumpusPercept setBump() {
        bump = true;
        return this;
    }

    public WumpusPercept setScream() {
        scream = true;
        return this;
    }

    public boolean isStench() {
        return stench;
    }

    public boolean isBreeze() {
        return breeze;
    }

    public boolean isGlitter() {
        return glitter;
    }

    public boolean isBump() {
        return bump;
    }

    public boolean isScream() {
        return scream;
    }

    @Override
    public String toString() {
        return "Stench " + stench + ", Breeze " + breeze + ", Glitter " + glitter + ", Bump " + bump + ", Scream " + scream;
    }
}