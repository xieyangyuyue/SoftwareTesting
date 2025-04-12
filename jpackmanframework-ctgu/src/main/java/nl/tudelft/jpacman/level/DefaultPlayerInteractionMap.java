package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.npc.Ghost;


/**
 * 一个可扩展的默认交互映射，处理由玩家引起的碰撞。
 * <p>
 * 该实现利用了交互映射，因此可以轻松地在添加新类型的单位（如幽灵、玩家等）时进行声明式的扩展。
 *
 * @author Arie van Deursen
 * @author Jeroen Roosen
 */
public class DefaultPlayerInteractionMap implements CollisionMap {
    private final CollisionMap collisions = defaultCollisions();

    @Override
    public void collide(Unit mover, Unit movedInto) {
        collisions.collide(mover, movedInto);
    }

    /**
     * 创建默认的碰撞交互映射，处理玩家与幽灵和玩家与豆子的碰撞。
     *
     * @return 包含玩家与幽灵和玩家与豆子碰撞处理的交互映射。
     */
    private static CollisionInteractionMap defaultCollisions() {
        CollisionInteractionMap collisionMap = new CollisionInteractionMap();
        // 当玩家与幽灵发生碰撞时，玩家死亡
        collisionMap.onCollision(Player.class, Ghost.class,
            (player, ghost) -> player.setAlive(false));
        // 当玩家与豆子发生碰撞时，豆子离开方格，玩家获得豆子的分数
        collisionMap.onCollision(Player.class, Pellet.class,
            (player, pellet) -> {
                pellet.leaveSquare();
                player.addPoints(pellet.getValue());
            });
        return collisionMap;
    }
}
