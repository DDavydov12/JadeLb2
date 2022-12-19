import java.util.Comparator;
import java.util.function.ToDoubleFunction;

public class BestFirstSearch<S, A> extends QueueBasedSearch<S, A> implements Informed<S, A> {

    private final EvaluationFunction<S, A> evalFn;

    /**
     * Constructs a best first search from a specified search execution strategy and an
     * evaluation function.
     *
     * @param impl
     *            A search execution strategy.
     * @param evalFn
     *            An evaluation function, which returns a number purporting to
     *            describe the desirability (or lack thereof) of expanding a
     *            node.
     */
    public BestFirstSearch(QueueSearch<S, A> impl, final EvaluationFunction<S, A> evalFn) {
        super(impl, QueueFactory.createPriorityQueue(Comparator.comparing(evalFn::applyAsDouble)));
        this.evalFn = evalFn;
    }

    /** Modifies the evaluation function. */
    @Override
    public void setHeuristicFunction(ToDoubleFunction<Node<S, A>> h) {
        evalFn.setHeuristicFunction(h);
    }
}
