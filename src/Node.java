public class Node<S, A> {

    // n.STATE: the state in the state space to which the node corresponds;
    private final S state;

    // n.PARENT: the node in the search tree that generated this node;
    private final Node<S, A> parent;

    // n.ACTION: the action that was applied to the parent to generate the node;
    private final A action;

    // n.PATH-COST: the cost, traditionally denoted by g(n), of the path from
    // the initial state to the node, as indicated by the parent pointers.
    private final double pathCost;

    private final int depth;

    /**
     * Constructs a root node for the specified state.
     *
     * @param state
     *            the state in the state space to which the node corresponds.
     */
    public Node(S state) {
        this(state, null, null, 0.0);
    }

    /**
     * Constructs a node with the specified state, parent, action, and path
     * cost.
     *
     * @param state
     *            the state in the state space to which the node corresponds.
     * @param parent
     *            the node in the search tree that generated the node.
     * @param action
     *            the action that was applied to the parent to generate the
     *            node.
     * @param pathCost
     *            full pathCost from the root node to here, typically
     *            the root's path costs plus the step costs for executing
     *            the the specified action.
     */
    public Node(S state, Node<S, A> parent, A action, double pathCost) {
        this.state = state;
        this.parent = parent;
        this.action = action;
        this.pathCost = pathCost;
        depth = parent != null ? parent.getDepth()+1 : 0;
    }

    /**
     * Returns the state in the state space to which the node corresponds.
     *
     * @return the state in the state space to which the node corresponds.
     */
    public S getState() {
        return state;
    }

    /**
     * Returns this node's parent node, from which this node was generated.
     *
     * @return the node's parenet node, from which this node was generated.
     */
    public Node<S, A> getParent() {
        return parent;
    }

    /**
     * Returns the action that was applied to the parent to generate the node.
     *
     * @return the action that was applied to the parent to generate the node.
     */
    public A getAction() {
        return action;
    }

    /**
     * Returns the cost of the path from the initial state to this node as
     * indicated by the parent pointers.
     *
     * @return the cost of the path from the initial state to this node as
     *         indicated by the parent pointers.
     */
    public double getPathCost() {
        return pathCost;
    }

    /**
     * Returns <code>true</code> if the node has no parent.
     *
     * @return <code>true</code> if the node has no parent.
     */
    public boolean isRootNode() {
        return parent == null;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public String toString() {
        return "[parent=" + parent + ", action=" + action + ", state=" + getState() + ", pathCost=" + pathCost + "]";
    }
}
