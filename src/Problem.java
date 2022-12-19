import java.util.List;

public interface Problem<S, A> extends OnlineSearchProblem<S, A> {

    /**
     * Returns the initial state of the agent.
     */
    S getInitialState();

    /**
     * Returns the set of actions that can be executed in the given state.
     * We say that each of these actions is <b>applicable</b> in the state.
     */
    List<A> getActions(S state);

    /**
     * Returns the description of what each action does.
     */
    S getResult(S state, A action);

    /**
     * Determines whether a given state is a goal state.
     */
    boolean testGoal(S state);

    /**
     * Returns the <b>step cost</b> of taking action <code>action</code> in state <code>state</code> to reach state
     * <code>stateDelta</code> denoted by c(s, a, s').
     */
    double getStepCosts(S state, A action, S stateDelta);

    /**
     * Tests whether a node represents an acceptable solution. The default implementation
     * delegates the check to the goal test. Other implementations could make use of the additional
     * information given by the node (e.g. the sequence of actions leading to the node). To compute
     * all or the five best solutions (not just the best), tester implementations could return false
     * and internally collect the paths of all nodes whose state passes the goal test until enough
     * solutions have been collected.
     * Search implementations should always access the goal test via this method to support
     * solution acceptance testing.
     */
    default boolean testSolution(Node<S, A> node) {
        return testGoal(node.getState());
    }
}

