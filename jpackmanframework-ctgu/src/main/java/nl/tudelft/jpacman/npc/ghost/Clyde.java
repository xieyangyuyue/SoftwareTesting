package nl.tudelft.jpacman.npc.ghost;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.npc.Ghost;
import nl.tudelft.jpacman.sprite.Sprite;

/**
 * 经典Pac-Man幽灵Clyde（别名Pokey）的实现。
 * <p>
 * Clyde的特性：
 * - 最后一个离开重生区域的幽灵，通常独自巡逻迷宫左下角
 * - 当距离Pac-Man超过8格时，像Blink一样精准追踪
 * - 当距离小于等于8格时，切换为逃跑模式
 * - 行为具有一定随机性，危险系数较高
 * <p>
 * AI逻辑说明：
 * 使用双模式AI，通过路径长度判断距离状态。基于策略Wiki的行为描述实现。
 *
 * @see <a href="http://strategywiki.org/wiki/Pac-Man/Getting_Started">行为策略参考</a>
 * @author Jeroen Roosen
 */
public class Clyde extends Ghost {

    /**
     * 警戒距离阈值（单位：网格），当与Pac-Man距离≤8时切换行为
     */
    private static final int SHYNESS = 8;

    /**
     * 移动间隔随机性参数（毫秒），增加幽灵行为动态性
     */
    private static final int INTERVAL_VARIATION = 50;

    /**
     * 基础移动间隔（毫秒）
     */
    private static final int MOVE_INTERVAL = 250;

    /**
     * 方向反向映射表，用于逃跑时取反方向
     */
    private static final Map<Direction, Direction> OPPOSITES = new EnumMap<>(Direction.class);

    // 静态初始化方向反向映射
    static {
        OPPOSITES.put(Direction.NORTH, Direction.SOUTH);
        OPPOSITES.put(Direction.SOUTH, Direction.NORTH);
        OPPOSITES.put(Direction.WEST, Direction.EAST);
        OPPOSITES.put(Direction.EAST, Direction.WEST);
    }

    /**
     * 构造函数，初始化Clyde的精灵和移动参数
     *
     * @param spriteMap 包含各方向精灵动画的映射
     */
    public Clyde(Map<Direction, Sprite> spriteMap) {
        super(spriteMap, MOVE_INTERVAL, INTERVAL_VARIATION);
    }

    /**
     * 计算下一步移动方向（AI核心逻辑）
     *
     * @return Optional包装的移动方向，空表示无有效移动
     */
    @Override
    public Optional<Direction> nextAiMove() {
        // 断言确保幽灵当前位于某个方格上
        assert hasSquare();

        // 寻找最近的玩家单位
        Unit nearest = Navigation.findNearest(Player.class, getSquare());
        if (nearest == null) {
            return Optional.empty(); // 无玩家可追踪
        }

        // 获取玩家所在方格并计算最短路径
        assert nearest.hasSquare();
        Square target = nearest.getSquare();
        List<Direction> path = Navigation.shortestPath(getSquare(), target, this);

        if (path != null && !path.isEmpty()) {
            Direction direction = path.get(0); // 取路径第一步方向

            // 根据距离阈值决定行为模式
            if (path.size() <= SHYNESS) {
                // 近距离模式：朝反方向移动（逃跑）
                return Optional.ofNullable(OPPOSITES.get(direction));
            } else {
                // 远距离模式：向玩家方向移动（追击）
                return Optional.of(direction);
            }
        }

        return Optional.empty(); // 无有效路径
    }
}
