import java.util.function.ToDoubleFunction;

public abstract class EvaluationFunction<S, A> implements ToDoubleFunction<Node<S, A>> {
    protected ToDoubleFunction<Node<S, A>> h;

    public EvaluationFunction() {
        this(node -> 0.0);
    }

    public EvaluationFunction(ToDoubleFunction<Node<S, A>> h) {
        this.h = h;
    }

    public ToDoubleFunction<Node<S, A>> getHeuristicFunction() {
        return h;
    }

    // Problem solving agents need to be able to change the heuristic function after formulating a new goal.
    public void setHeuristicFunction(ToDoubleFunction<Node<S, A>> h) {
        this.h = h;
    }
}

