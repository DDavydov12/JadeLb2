public interface StepCostFunction<S, A> {
    double applyAsDouble(S s, A a, S sDelta);
}