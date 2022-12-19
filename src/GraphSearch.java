import java.util.HashSet;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

public class GraphSearch<S, A> extends TreeSearch<S, A> {

    private Set<S> explored = new HashSet<>();

    public GraphSearch() {
        this(new NodeFactory<>());
    }

    public GraphSearch(NodeFactory<S, A> nodeFactory) {
        super(nodeFactory);
    }

    /**
     * Clears the set of explored states and calls the search implementation of
     * {@link TreeSearch}.
     */
    @Override
    public Optional<Node<S, A>> findNode(Problem<S, A> problem, Queue<Node<S, A>> frontier) {
        // initialize the explored set to be empty
        explored.clear();
        return super.findNode(problem, frontier);
    }

    /**
     * Inserts the node at the tail of the frontier if the corresponding state
     * was not yet explored.
     */
    @Override
    protected void addToFrontier(Node<S, A> node) {
        if (!explored.contains(node.getState())) {
            frontier.add(node);
            updateMetrics(frontier.size());
        }
    }

    /**
     * Removes the node at the head of the frontier, adds the corresponding
     * state to the explored set, and returns the node. Leading nodes of already
     * explored states are dropped. So the resulting node state will always be
     * unexplored yet.
     *
     * @return the node at the head of the frontier.
     */
    @Override
    protected Node<S, A> removeFromFrontier() {
        cleanUpFrontier(); // not really necessary because isFrontierEmpty should be called before...
        Node<S, A> result = frontier.remove();
        explored.add(result.getState());
        updateMetrics(frontier.size());
        return result;
    }

    /**
     * Pops nodes of already explored states from the head of the frontier
     * and checks whether there are still some nodes left.
     */
    @Override
    protected boolean isFrontierEmpty() {
        cleanUpFrontier();
        updateMetrics(frontier.size());
        return frontier.isEmpty();
    }

    /**
     * Helper method which removes nodes of already explored states from the head
     * of the frontier.
     */
    private void cleanUpFrontier() {
        while (!frontier.isEmpty() && explored.contains(frontier.element().getState()))
            frontier.remove();
    }
}