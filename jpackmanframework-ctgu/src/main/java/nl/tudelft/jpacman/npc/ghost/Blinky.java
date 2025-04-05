package nl.tudelft.jpacman.npc.ghost;

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
 * 经典Pac-Man幽灵Blinky（别名Shadow）的实现。
 * <p>
 * <b>特性:</b>
 * - 迷宫右上角巡逻
 * - 追踪Pac-Man时优先缩短最大距离方向
 * - 在 pellets 剩余较少时加速（待实现）
 *
 * @see <a href="http://strategywiki.org/wiki/Pac-Man/Getting_Started">策略参考</a>
 * @author Jeroen Roosen
 */
public class Blinky extends Ghost {

    /**
     * 移动间隔随机性参数（毫秒）
     */
    private static final int INTERVAL_VARIATION = 50;

    /**
     * 基础移动间隔（毫秒）
     */
    private static final int MOVE_INTERVAL = 250;

    /**
     * 构造函数初始化Blinky
     * @param spriteMap 各方向精灵动画映射
     */
    public Blinky(Map<Direction, Sprite> spriteMap) {
        super(spriteMap, MOVE_INTERVAL, INTERVAL_VARIATION);
    }

    /**
     * AI核心逻辑：计算Blinky的下一步移动方向
     * @return Optional包装的移动方向，空表示无有效移动
     */
    @Override
    public Optional<Direction> nextAiMove() {
        assert hasSquare(); // 确保当前在有效方格

        // 定位最近的玩家
        Unit nearest = Navigation.findNearest(Player.class, getSquare());
        if (nearest == null) {
            return Optional.empty(); // 无玩家可追踪
        }

        // 获取玩家所在方格
        assert nearest.hasSquare();
        Square target = nearest.getSquare();

        // 计算最短路径
        List<Direction> path = Navigation.shortestPath(getSquare(), target, this);

        if (path != null && !path.isEmpty()) {
            return Optional.ofNullable(path.get(0)); // 取路径第一步
        }
        return Optional.empty();
    }
}
