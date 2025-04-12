package nl.tudelft.jpacman.npc;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.sprite.Sprite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * 幽灵NPC的抽象基类，定义幽灵的通用行为（移动逻辑、贴图管理等）。
 * 子类需实现具体AI移动策略，未实现时将默认随机移动。
 *
 * @author Jeroen Roosen
 */
public abstract class Ghost extends Unit {

    /**
     * 各方向对应的精灵贴图映射表：
     * Key: 方向（NORTH/SOUTH/EAST/WEST）
     * Value: 对应方向的精灵贴图
     */
    private final Map<Direction, Sprite> sprites;

    /**
     * 基础移动间隔时间（毫秒），幽灵两次移动之间的基础等待时间
     */
    private final int moveInterval;

    /**
     * 移动间隔随机变化范围，实际间隔为 moveInterval + [0, intervalVariation) 的随机值
     */
    private final int intervalVariation;

    /**
     * 构造幽灵实例。
     *
     * @param spriteMap         各方向精灵贴图映射（必须包含所有四个方向）
     * @param moveInterval      基础移动间隔（毫秒）
     * @param intervalVariation 移动间隔随机变化范围（>=0）
     */
    protected Ghost(Map<Direction, Sprite> spriteMap, int moveInterval, int intervalVariation) {
        this.sprites = spriteMap;
        this.intervalVariation = intervalVariation;
        this.moveInterval = moveInterval;
    }

    /**
     * 获取下一步移动方向（AI决策优先，失败则随机移动）。
     * 合并策略：若AI未返回有效方向，则调用随机移动逻辑。
     *
     * @return 移动方向（可能为null，当无可用路径时）
     */
    public Direction nextMove() {
        return nextAiMove().orElseGet(this::randomMove);
    }

    /**
     * 抽象方法：由子类实现具体的AI移动策略。
     * 示例：追踪玩家、逃跑等高级行为。
     *
     * @return Optional包装的移动方向，empty表示AI未找到有效路径
     */
    public abstract Optional<Direction> nextAiMove();

    /**
     * 获取当前方向对应的精灵贴图。
     *
     * @return 当前面朝方向的精灵对象
     */
    @Override
    public Sprite getSprite() {
        return sprites.get(getDirection());
    }

    /**
     * 计算实际移动间隔时间，增加随机性避免幽灵同步移动。
     *
     * @return 当前移动间隔（毫秒）
     */
    public long getInterval() {
        return this.moveInterval + new Random().nextInt(this.intervalVariation);
    }

    /**
     * 随机选择可行移动方向。
     * 逻辑：
     * 1. 收集当前所在方块四个方向中可通行的方向
     * 2. 若无可用方向返回null
     * 3. 随机选择一个可用方向
     *
     * @return 随机方向或null（无可用路径时）
     */
    protected Direction randomMove() {
        Square currentSquare = getSquare();
        List<Direction> validDirections = new ArrayList<>();

        // 筛选四个方向中的可通行方向
        for (Direction dir : Direction.values()) {
            Square target = currentSquare.getSquareAt(dir);
            if (target.isAccessibleTo(this)) { // 检查是否可移动到目标方块
                validDirections.add(dir);
            }
        }

        return validDirections.isEmpty() ?
            null : // 无可用路径
            validDirections.get(new Random().nextInt(validDirections.size())); // 随机选择
    }
}
