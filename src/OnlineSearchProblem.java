import java.util.List;

public interface OnlineSearchProblem<S, A> {

    /**
     * Returns the initial state of the agent.
     */
    S getInitialState();

    /**
     * Returns the description of the possible actions available to the agent.
     */
    List<A> getActions(S state);

    /**
     * Determines whether a given state is a goal state.
     */
    boolean testGoal(S state);

    /**
     * Returns the <b>step cost</b> of taking action <code>action</code> in state <code>state</code> to reach state
     * <code>stateDelta</code> denoted by c(s, a, s').
     */
    double getStepCosts(S state, A action, S stateDelta);
}