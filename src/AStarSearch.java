import java.util.function.ToDoubleFunction;

public class AStarSearch<S, A> extends BestFirstSearch<S, A> {

    /**
     * Constructs an A* search from a specified search execution
     * strategy and a heuristic function.
     *
     * @param impl A search execution strategy (e.g. TreeSearch, GraphSearch).
     * @param h    A heuristic function <em>h(n)</em>, which estimates the cost
     *             of the cheapest path from the state of node <em>n</em> to a
     *             goal state.
     */
    public AStarSearch(QueueSearch<S, A> impl, ToDoubleFunction<Node<S, A>> h) {
        super(impl, createEvalFn(h));
    }

    // f(n) = g(n) + h(n)
    public static <S, A> EvaluationFunction<S, A> createEvalFn(ToDoubleFunction<Node<S, A>> h) {
        return new EvaluationFunction<S, A>(h) {
            @Override
            public double applyAsDouble(Node<S, A> node) {
                return node.getPathCost() + this.h.applyAsDouble(node);
            }
        };
    }
}