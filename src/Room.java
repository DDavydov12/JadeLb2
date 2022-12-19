public class Room {
    private int x = 1;
    private int y = 1;

    /**
     * Constructor.
     *
     * @param x
     *            the room's x location.
     * @param y
     *            the room's y location.
     */
    public Room(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     *
     * @return the room's x location.
     */
    public int getX() {
        return x;
    }

    /**
     *
     * @return the room's y location.
     */
    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "[" + x + "," + y + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Room) {
            Room r = (Room) o;
            return x == r.x && y == r.y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + getX();
        result = 43 * result + getY();
        return result;
    }
}