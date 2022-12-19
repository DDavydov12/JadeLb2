import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

public class WumpusFunctions {

    public static Function<PlayerPosition, List<WumpusAction>> createActionsFunction(WumpusCave cave) {
        return state -> {
            List<WumpusAction> actions = new ArrayList<>();
            PlayerPosition pos = cave.moveForward(state);
            if (!pos.equals(state))
                actions.add(WumpusAction.FORWARD);
            actions.add(WumpusAction.TURN_LEFT);
            actions.add(WumpusAction.TURN_RIGHT);
            return actions;
        };
    }

    public static BiFunction<PlayerPosition, WumpusAction, PlayerPosition> createResultFunction(WumpusCave cave) {
        return (state, action) -> {
            PlayerPosition result = state;
            switch (action) {
                case FORWARD:
                    result = cave.moveForward(state);
                    break;
                case TURN_LEFT:
                    result = cave.turnLeft(state);
                    break;
                case TURN_RIGHT:
                    result = cave.turnRight(state);
                    break;
            }
            return result;
        };
    }

    public static ToDoubleFunction<Node<PlayerPosition, WumpusAction>> createManhattanDistanceFunction
            (Set<PlayerPosition> goals) {
        return node -> {
            PlayerPosition curr = node.getState();
            return goals.stream().
                    mapToInt(goal -> Math.abs(goal.getX() - curr.getX()) + Math.abs(goal.getY() - curr.getY())).min().
                    orElse(Integer.MAX_VALUE);
        };
    }
}

