import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Consumer;

public abstract class QueueBasedSearch<S, A> implements SearchForActions<S, A>, SearchForStates<S, A> {
    protected final QueueSearch<S, A> impl;
    private final Queue<Node<S, A>> frontier;

    protected QueueBasedSearch(QueueSearch<S, A> impl, Queue<Node<S, A>> queue) {
        this.impl = impl;
        this.frontier = queue;
    }

    @Override
    public Optional<List<A>> findActions(Problem<S, A> p) {
        impl.getNodeFactory().useParentLinks(true);
        frontier.clear();
        Optional<Node<S, A>> node = impl.findNode(p, frontier);
        return SearchUtils.toActions(node);
    }

    @Override
    public Optional<S> findState(Problem<S, A> p) {
        impl.getNodeFactory().useParentLinks(false);
        frontier.clear();
        Optional<Node<S, A>> node = impl.findNode(p, frontier);
        return SearchUtils.toState(node);
    }

    @Override
    public Metrics getMetrics() {
        return impl.getMetrics();
    }

    @Override
    public void addNodeListener(Consumer<Node<S, A>> listener)  {
        impl.getNodeFactory().addNodeListener(listener);
    }

    @Override
    public boolean removeNodeListener(Consumer<Node<S, A>> listener) {
        return impl.getNodeFactory().removeNodeListener(listener);
    }
}
