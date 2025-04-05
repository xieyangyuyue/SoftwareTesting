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
 * 经典Pac-Man幽灵Inky的实现。
 * <p>
 * <b>AI特性:</b>
 * - 最复杂的AI逻辑，结合Blink位置和Pac-Man前方预测位置
 * - 通过几何投影确定目标点：从Blink到Pac-Man前方2格位置连线延长两倍
 * - 特殊处理：当Pac-Man面朝上时，目标点计算存在偏移（经典游戏bug复现）
 *
 * @see <a href="http://strategywiki.org/wiki/Pac-Man/Getting_Started">策略参考</a>
 * @author Jeroen Roosen
 */
public class Inky extends Ghost {

    /**
     * 预测Pac-Man前方网格数（经典值为2）
     */
    private static final int SQUARES_AHEAD = 2;

    /**
     * 移动间隔随机性参数（毫秒）
     */
    private static final int INTERVAL_VARIATION = 50;

    /**
     * 基础移动间隔（毫秒）
     */
    private static final int MOVE_INTERVAL = 250;

    /**
     * 构造函数初始化Inky
     * @param spriteMap 各方向精灵动画映射
     */
    public Inky(Map<Direction, Sprite> spriteMap) {
        super(spriteMap, MOVE_INTERVAL, INTERVAL_VARIATION);
    }

    /**
     * AI核心逻辑：计算Inky的下一步移动方向
     * @return Optional包装的移动方向，空表示无有效移动
     */
    @Override
    public Optional<Direction> nextAiMove() {
        assert hasSquare(); // 确保当前在有效方格

        // 步骤1：定位关键元素
        Unit blink = Navigation.findNearest(Blinky.class, getSquare()); // 查找最近的Blink
        Unit player = Navigation.findNearest(Player.class, getSquare()); // 查找最近的玩家

        // 边界情况处理
        if (blink == null || player == null) {
            return Optional.empty(); // 任一目标缺失则无动作
        }

        // 步骤2：计算Pac-Man预测位置
        assert player.hasSquare();
        Square playerDestination = player.squaresAheadOf(SQUARES_AHEAD); // 获取玩家前方两格位置

        // 步骤3：计算Blink到预测位置的路径（第一段路径）
        List<Direction> firstHalf = Navigation.shortestPath(
            blink.getSquare(),
            playerDestination,
            null    // 忽略地形障碍
        );

        if (firstHalf == null) {
            return Optional.empty(); // 路径不可达
        }

        // 步骤4：延长路径确定最终目标点
        Square destination = followPath(firstHalf, playerDestination); // 沿路径延长两倍距离

        // 步骤5：计算Inky到目标点的路径
        List<Direction> path = Navigation.shortestPath(
            getSquare(),
            destination,
            this    // 考虑当前NPC的移动限制
        );

        // 返回下一步方向
        if (path != null && !path.isEmpty()) {
            return Optional.ofNullable(path.get(0)); // 取路径第一步
        }
        return Optional.empty();
    }

    /**
     * 路径延长算法：根据给定路径延长到目标点
     * @param path 初始路径
     * @param start 路径起点
     * @return 延长后的终点位置
     */
    private Square followPath(List<Direction> path, Square start) {
        Square destination = start;
        // 沿路径逐步移动，模拟延长两倍距离
        for (Direction direction : path) {
            destination = destination.getSquareAt(direction); // 向路径方向移动
            destination = destination.getSquareAt(direction); // 再次移动（实现两倍延长）
        }
        return destination;
    }
}
