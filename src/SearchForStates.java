import java.util.Optional;
import java.util.function.Consumer;

public interface SearchForStates<S, A> {
    /**
     * Returns a state which might be, but not necessarily is a goal state of
     * the problem, or empty.
     *
     * @param p
     *            the search problem
     *
     * @return a state or empty.
     */
    Optional<S> findState(Problem<S, A> p);

    /**
     * Returns all the metrics of the search.
     */
    Metrics getMetrics();

    /**
     * Adds a listener to the list of node listeners. It is informed whenever a
     * node is expanded during search.
     */
    void addNodeListener(Consumer<Node<S, A>> listener);

    /**
     * Removes a listener from the list of node listeners.
     */
    boolean removeNodeListener(Consumer<Node<S, A>> listener);
}
