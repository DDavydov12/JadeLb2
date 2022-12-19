import java.util.function.ToDoubleFunction;

public interface Informed<S, A> {
    void setHeuristicFunction(ToDoubleFunction<Node<S, A>> h);
}