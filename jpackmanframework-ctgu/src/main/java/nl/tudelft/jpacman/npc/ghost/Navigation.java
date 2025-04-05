package nl.tudelft.jpacman.npc.ghost;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.Unit;

/**
 * Navigation 提供在游戏棋盘上导航的工具类，用于计算最短路径、查找最近单位等。
 * 本类采用广度优先搜索（BFS）算法实现路径查找。
 * 注意：该类为工具类，不可实例化。
 *
 * @author Jeroen Roosen
 */
public final class Navigation {

    // 私有构造函数防止实例化
    private Navigation() {
    }

    /**
     * 计算从起点到终点的最短路径。使用广度优先搜索（BFS）算法实现。
     * 若指定旅行者（traveller），则路径需满足可通行条件；若traveller为null，则忽略地形限制。
     *
     * @param from        起始方块
     * @param to          目标方块
     * @param traveller   旅行者单位（可为null，表示忽略地形）
     * @return            最短路径的方向列表。若起点等于终点，返回空列表；若不可达，返回null。
     */
    public static List<Direction> shortestPath(Square from, Square to, Unit traveller) {
        // 起点与终点相同，直接返回空路径
        if (from.equals(to)) {
            return new ArrayList<>();
        }

        List<Node> targets = new ArrayList<>();   // 待处理的节点队列
        Set<Square> visited = new HashSet<>();   // 已访问的方块集合
        targets.add(new Node(null, from, null));  // 初始节点（无父节点和方向）

        while (!targets.isEmpty()) {
            Node node = targets.remove(0);        // 取出队列首节点
            Square square = node.getSquare();

            // 到达目标，返回路径
            if (square.equals(to)) {
                return node.getPath();
            }

            visited.add(square);
            addNewTargets(traveller, targets, visited, node, square); // 扩展相邻节点
        }
        return null; // 无可用路径
    }

    /**
     * 扩展当前节点的相邻方块，将合法的新节点加入目标队列。
     *
     * @param traveller  旅行者单位（决定是否检查地形）
     * @param targets    目标节点队列
     * @param visited    已访问方块集合
     * @param node       当前处理的节点
     * @param square     当前方块
     */
    private static void addNewTargets(Unit traveller, List<Node> targets,
                                      Set<Square> visited, Node node, Square square) {
        // 遍历所有可能方向（北、南、东、西）
        for (Direction direction : Direction.values()) {
            Square target = square.getSquareAt(direction);

            // 若未访问且可通行（或忽略地形），则加入队列
            if (!visited.contains(target) &&
                (traveller == null || target.isAccessibleTo(traveller))) {
                targets.add(new Node(direction, target, node));
            }
        }
    }

    /**
     * 从当前方块出发，查找最近的指定类型单位（使用BFS）。
     *
     * @param type            目标单位类型（如幽灵、玩家）
     * @param currentLocation 起始方块
     * @return                找到的最近单位，若不存在则返回null
     */
    public static Unit findNearest(Class<? extends Unit> type, Square currentLocation) {
        List<Square> toDo = new ArrayList<>();    // 待处理的方块队列
        Set<Square> visited = new HashSet<>();    // 已访问的方块集合
        toDo.add(currentLocation);

        while (!toDo.isEmpty()) {
            Square square = toDo.remove(0);
            Unit unit = findUnit(type, square);

            // 找到目标单位，立即返回
            if (unit != null) {
                assert unit.hasSquare();
                return unit;
            }

            visited.add(square);
            // 将未访问的相邻方块加入队列
            for (Direction direction : Direction.values()) {
                Square newTarget = square.getSquareAt(direction);
                if (!visited.contains(newTarget) && !toDo.contains(newTarget)) {
                    toDo.add(newTarget);
                }
            }
        }
        return null; // 未找到
    }

    /**
     * 在整个棋盘上查找指定类型的单位。
     *
     * @param clazz  目标单位类型（如Ghost.class）
     * @param board  目标棋盘
     * @param <T>    泛型类型，继承自Unit
     * @return       第一个找到的目标单位，若不存在则返回null
     */
    public static <T extends Unit> T findUnitInBoard(Class<T> clazz, Board board) {
        // 遍历棋盘所有方块
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                T unit = findUnit(clazz, board.squareAt(x, y));
                if (unit != null) {
                    return unit;
                }
            }
        }
        return null;
    }

    /**
     * 在指定方块中查找特定类型的单位。
     *
     * @param type   目标单位类型
     * @param square 待搜索的方块
     * @param <T>    泛型类型，继承自Unit
     * @return       匹配的单位实例，若不存在则返回null
     */
    @SuppressWarnings("unchecked")
    public static <T extends Unit> T findUnit(Class<T> type, Square square) {
        // 遍历方块上的所有占用者
        for (Unit unit : square.getOccupants()) {
            if (type.isInstance(unit)) {
                assert unit.hasSquare();
                return (T) unit; // 类型匹配，返回实例
            }
        }
        return null;
    }

    /**
     * 内部类：表示搜索路径中的节点，用于跟踪路径方向。
     */
    private static final class Node {
        private final Direction direction; // 到达此节点的方向（根节点为null）
        private final Node parent;         // 父节点（根节点为null）
        private final Square square;        // 当前节点对应的方块

        /**
         * 构造节点。
         *
         * @param direction 移动至此节点的方向
         * @param square    当前方块
         * @param parent    父节点
         */
        Node(Direction direction, Square square, Node parent) {
            this.direction = direction;
            this.square = square;
            this.parent = parent;
        }

        /**
         * @return 此节点的移动方向（根节点返回null）
         */
        private Direction getDirection() {
            return direction;
        }

        /**
         * @return 此节点对应的方块
         */
        private Square getSquare() {
            return square;
        }

        /**
         * @return 父节点（根节点返回null）
         */
        private Node getParent() {
            return parent;
        }

        /**
         * 从根节点到当前节点的路径方向列表。
         *
         * @return 路径方向列表（按顺序从起点到当前节点）
         */
        private List<Direction> getPath() {
            if (parent == null) {
                return new ArrayList<>();
            }
            List<Direction> path = parent.getPath(); // 递归获取父节点路径
            path.add(getDirection());                // 添加当前方向
            return path;
        }
    }
}
