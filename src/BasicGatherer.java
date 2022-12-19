import java.util.Set;

public abstract class BasicGatherer<T> implements PLVisitor<Set<T>, Set<T>> {

    @Override
    public Set<T> visitPropositionSymbol(PropositionSymbol s, Set<T> arg) {
        return arg;
    }

    @Override
    public Set<T> visitUnarySentence(ComplexSentence s, Set<T> arg) {
        return SetOps.union(arg, s.getSimplerSentence(0).accept(this, arg));
    }

    @Override
    public Set<T> visitBinarySentence(ComplexSentence s, Set<T> arg) {
        Set<T> termunion = SetOps.union(
                s.getSimplerSentence(0).accept(this, arg),
                s.getSimplerSentence(1).accept(this, arg));
        return SetOps.union(arg, termunion);
    }
}
