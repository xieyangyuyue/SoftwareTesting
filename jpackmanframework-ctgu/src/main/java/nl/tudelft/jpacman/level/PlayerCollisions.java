package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.npc.Ghost;

/**
 * 基于简单类型检查的玩家碰撞处理实现类。
 * 通过多重`instanceof`判断分派碰撞逻辑，适用于基础场景，但扩展性有限。
 *
 * <p>
 * 设计说明：
 * 1. 实现{@link CollisionMap}接口，处理玩家、幽灵、豆子之间的碰撞。
 * 2. 使用`instanceof`检查单位类型，直接调用对应的私有方法。
 * 3. 对玩家与幽灵、玩家与豆子的碰撞提供具体逻辑。
 * <p>
 * 注意：对于复杂碰撞逻辑（如多种单位类型），建议改用{@link CollisionInteractionMap}，
 * 该类通过注册处理器实现可扩展的碰撞管理。
 *
 * @author Arie van Deursen, 2014
 */
public class PlayerCollisions implements CollisionMap {

    /**
     * 处理两个单位之间的碰撞事件。
     * 根据碰撞发起者（mover）的类型，分派到不同的私有方法。
     *
     * @param mover      主动移动并引发碰撞的单位（如玩家、幽灵或豆子）
     * @param collidedOn 被碰撞的静止单位（可能为幽灵、豆子等）
     */
    @Override
    public void collide(Unit mover, Unit collidedOn) {
        if (mover instanceof Player) {
            // 玩家主动移动碰撞其他单位
            playerColliding((Player) mover, collidedOn);
        } else if (mover instanceof Ghost) {
            // 幽灵主动移动碰撞其他单位
            ghostColliding((Ghost) mover, collidedOn);
        } else if (mover instanceof Pellet) {
            // 豆子主动移动碰撞其他单位（通常豆子不会移动，此分支可能为防御性代码）
            pelletColliding((Pellet) mover, collidedOn);
        }
        // 其他单位类型可在此扩展
    }

    /**
     * 处理玩家（mover）碰撞其他单位的情况。
     *
     * @param player     引发碰撞的玩家
     * @param collidedOn 被碰撞的单位（可能是幽灵或豆子）
     */
    private void playerColliding(Player player, Unit collidedOn) {
        if (collidedOn instanceof Ghost) {
            // 玩家撞到幽灵：玩家死亡
            playerVersusGhost(player, (Ghost) collidedOn);
        }
        if (collidedOn instanceof Pellet) {
            // 玩家撞到豆子：收集豆子
            playerVersusPellet(player, (Pellet) collidedOn);
        }
    }

    /**
     * 处理幽灵（mover）碰撞其他单位的情况。
     *
     * @param ghost      引发碰撞的幽灵
     * @param collidedOn 被碰撞的单位（可能是玩家）
     */
    private void ghostColliding(Ghost ghost, Unit collidedOn) {
        if (collidedOn instanceof Player) {
            // 幽灵撞到玩家：玩家死亡（逻辑与玩家撞幽灵相同）
            playerVersusGhost((Player) collidedOn, ghost);
        }
    }

    /**
     * 处理豆子（mover）碰撞其他单位的情况（理论上豆子不会移动，此方法为防御性设计）。
     *
     * @param pellet     引发碰撞的豆子
     * @param collidedOn 被碰撞的单位（可能是玩家）
     */
    private void pelletColliding(Pellet pellet, Unit collidedOn) {
        if (collidedOn instanceof Player) {
            // 豆子撞到玩家：收集豆子（逻辑与玩家撞豆子相同）
            playerVersusPellet((Player) collidedOn, pellet);
        }
    }

    //---------- 具体碰撞逻辑 ----------//

    /**
     * 处理玩家与幽灵的碰撞，导致玩家死亡。
     * 注意：无论玩家主动碰撞幽灵还是幽灵主动碰撞玩家，均调用此方法。
     *
     * @param player 参与碰撞的玩家
     * @param ghost  参与碰撞的幽灵
     */
    public void playerVersusGhost(Player player, Ghost ghost) {
        player.setAlive(false); // 设置玩家状态为死亡
    }

    /**
     * 处理玩家与豆子的碰撞，玩家获得分数，豆子被移除。
     * 注意：无论玩家主动碰撞豆子还是豆子主动碰撞玩家，均调用此方法。
     *
     * @param player 参与碰撞的玩家
     * @param pellet 参与碰撞的豆子
     */
    public void playerVersusPellet(Player player, Pellet pellet) {
        pellet.leaveSquare();       // 将豆子从地图上移除
        player.addPoints(pellet.getValue()); // 玩家增加分数
    }
}
