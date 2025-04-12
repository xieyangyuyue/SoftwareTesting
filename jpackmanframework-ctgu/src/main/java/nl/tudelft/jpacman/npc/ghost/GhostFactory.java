package nl.tudelft.jpacman.npc.ghost;

import nl.tudelft.jpacman.npc.Ghost;
import nl.tudelft.jpacman.sprite.PacManSprites;

/**
 * 幽灵角色工厂类，用于创建不同颜色和行为的幽灵实例。
 * <p>
 * 该类封装了幽灵的创建逻辑，通过传入的精灵资源（{@link PacManSprites}）生成具体类型的幽灵。
 * 每个幽灵类型对应游戏中的经典角色（如Blinky、Pinky等），并绑定特定的精灵图像。
 *
 * @author Jeroen Roosen
 * @since 1.0
 */
public class GhostFactory {

    /**
     * 幽灵精灵资源存储器，用于获取不同幽灵的精灵图像。
     */
    private final PacManSprites sprites;

    /**
     * 创建幽灵工厂实例。
     *
     * @param spriteStore 精灵资源提供对象，必须包含幽灵的精灵图像。
     * @throws NullPointerException 如果 {@code spriteStore} 为 {@code null}。
     */
    public GhostFactory(PacManSprites spriteStore) {
        this.sprites = spriteStore;
    }

    //---------- 幽灵创建方法 ----------//

    /**
     * 创建红色幽灵 Blinky（别名 Shadow）。
     * <p>
     * Blinky 是经典的追击型幽灵，会直接追踪玩家的位置。
     *
     * @return 新创建的 Blinky 实例。
     * @see Blinky 具体实现类
     */
    public Ghost createBlinky() {
        return new Blinky(sprites.getGhostSprite(GhostColor.RED));
    }

    /**
     * 创建粉色幽灵 Pinky（别名 Speedy）。
     * <p>
     * Pinky 的行为模式是尝试绕到玩家前方进行伏击。
     *
     * @return 新创建的 Pinky 实例。
     * @see Pinky 具体实现类
     */
    public Ghost createPinky() {
        return new Pinky(sprites.getGhostSprite(GhostColor.PINK));
    }

    /**
     * 创建青色幽灵 Inky（别名 Bashful）。
     * <p>
     * Inky 的行为依赖于其他幽灵的位置，通常与Blinky配合进行包抄。
     *
     * @return 新创建的 Inky 实例。
     * @see Inky 具体实现类
     */
    public Ghost createInky() {
        return new Inky(sprites.getGhostSprite(GhostColor.CYAN));
    }

    /**
     * 创建橙色幽灵 Clyde（别名 Pokey）。
     * <p>
     * Clyde 的行为较为随机，当靠近玩家时会切换为逃跑模式。
     *
     * @return 新创建的 Clyde 实例。
     * @see Clyde 具体实现类
     */
    public Ghost createClyde() {
        return new Clyde(sprites.getGhostSprite(GhostColor.ORANGE));
    }
}
