import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface SearchForActions<S, A> {
    /**
     * Returns a list of actions leading to a goal state if a goal was found,
     * otherwise empty. Note that the list can be empty which means that the
     * initial state is a goal state.
     *
     * @param p
     *            the search problem
     *
     * @return a (possibly empty) list of actions or empty
     */
    Optional<List<A>> findActions(Problem<S, A> p);

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

