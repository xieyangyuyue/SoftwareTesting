package nl.tudelft.jpacman.npc.ghost;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.npc.Ghost;
import nl.tudelft.jpacman.sprite.Sprite;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 实现经典Pac-Man游戏中Pinky幽灵的AI逻辑。
 * <p>
 * Pinky（别名Speedy）的独特行为是预测玩家（Pac-Man）的移动方向，并尝试拦截其前方4格的位置。
 * 当玩家面朝上时，由于历史Bug，Pinky的目标位置会向左偏移。
 * </p>
 *
 * @author Jeroen Roosen
 * @since 1.0
 */
public class Pinky extends Ghost {

    /**
     * 预测玩家前方4格的位置
     */
    private static final int SQUARES_AHEAD = 4;

    /**
     * 移动间隔的随机波动范围（毫秒），使幽灵移动更自然
     */
    private static final int INTERVAL_VARIATION = 50;

    /**
     * 幽灵的基础移动间隔（毫秒）
     */
    private static final int MOVE_INTERVAL = 200;

    /**
     * 创建Pinky幽灵实例。
     *
     * @param spriteMap 包含不同方向的精灵图像映射（如向左、向右的动画帧）。
     */
    public Pinky(Map<Direction, Sprite> spriteMap) {
        super(spriteMap, MOVE_INTERVAL, INTERVAL_VARIATION);
    }

    /**
     * 计算Pinky的下一步移动方向。
     * <p>
     * 1. 寻找最近的玩家。
     * 2. 计算玩家前方4格的目标位置。
     * 3. 规划从当前位置到目标的最短路径。
     * 4. 返回路径的第一步方向，若无路径则静止。
     * </p>
     *
     * @return 移动方向的Optional值，若无可达路径则为空。
     */
    @Override
    public Optional<Direction> nextAiMove() {
        assert hasSquare(); // 确保幽灵当前位于地图上

        // 1. 寻找最近的玩家
        Unit player = Navigation.findNearest(Player.class, getSquare());
        if (player == null) {
            return Optional.empty(); // 玩家不存在，静止
        }

        assert player.hasSquare(); // 确保玩家位置有效

        // 2. 计算目标位置（玩家前方4格，考虑经典Bug）
        Square destination = player.squaresAheadOf(SQUARES_AHEAD);

        // 3. 计算最短路径
        List<Direction> path = Navigation.shortestPath(getSquare(), destination, this);

        // 4. 返回路径的第一步方向
        return path != null && !path.isEmpty()
            ? Optional.of(path.get(0))
            : Optional.empty();
    }
}
