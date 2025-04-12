package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.sprite.PacManSprites;

/**
 * 用于创建Player对象的工厂类。
 * <p>
 * 该工厂类封装了Player对象的创建过程，通过提供一个统一的接口来创建Player实例。
 * 它依赖于PacManSprites类来获取创建Player所需的精灵图像。
 *
 * @author Jeroen Roosen
 * @since 1.0
 */
public class PlayerFactory {

    /**
     * 用于存储Pac-Man精灵图像的PacManSprites实例。
     */
    private final PacManSprites sprites;

    /**
     * 创建一个新的PlayerFactory实例。
     *
     * @param spriteStore 包含Pac-Man精灵图像的PacManSprites实例，不能为空。
     */
    public PlayerFactory(PacManSprites spriteStore) {
        this.sprites = spriteStore;
    }

    /**
     * 使用工厂创建一个新的Player对象。
     *
     * @return 新创建的Player对象。
     */
    public Player createPacMan() {
        return new Player(getSprites().getPacmanSprites(), getSprites().getPacManDeathAnimation());
    }

    /**
     * 获取工厂使用的PacManSprites实例。
     *
     * @return 包含Pac-Man精灵图像的PacManSprites实例。
     */
    protected PacManSprites getSprites() {
        return sprites;
    }
}
