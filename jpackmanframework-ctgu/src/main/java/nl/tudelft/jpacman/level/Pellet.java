package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.sprite.Sprite;

/**
 * 表示游戏中的能量豆（Pellet）实体，是Pac-Man必须收集的小圆点。
 * <p>
 * 每个能量豆具有固定的分数值和对应的图像精灵。继承自{@link Unit}类，
 * 在游戏地图上占据一个单元位置。当玩家移动到能量豆所在的位置时，
 * 能量豆会被“吃掉”，玩家获得分数，能量豆从地图上消失。
 *
 * @author J Roosen
 * @see Unit 基础单元类
 * @since 1.0
 */
public class Pellet extends Unit {

    /**
     * 该能量豆的显示精灵，用于在游戏界面中渲染。
     */
    private final Sprite image;

    /**
     * 该能量豆的分数价值，表示玩家收集后获得的分数。
     */
    private final int value;

    /**
     * 创建一个新的能量豆实例。
     *
     * @param points 能量豆的分数值，必须为正整数。
     * @param sprite 能量豆的显示精灵，不能为null。
     * @throws IllegalArgumentException 如果以下条件不满足：
     *                                  - {@code points <= 0}（分数非正）
     *                                  - {@code sprite == null}（精灵未提供）
     */
    public Pellet(int points, Sprite sprite) {
        if (points <= 0) {
            throw new IllegalArgumentException("Points must be positive");
        }
        if (sprite == null) {
            throw new IllegalArgumentException("Sprite cannot be null");
        }
        this.image = sprite;
        this.value = points;
    }

    /**
     * 获取能量豆的分数值。
     *
     * @return 该能量豆的分数值，始终大于0。
     */
    public int getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     *
     * @return 该能量豆对应的图像精灵，用于界面渲染。
     */
    @Override
    public Sprite getSprite() {
        return image;
    }
}
