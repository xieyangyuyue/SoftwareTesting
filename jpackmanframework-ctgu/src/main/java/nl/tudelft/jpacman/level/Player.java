package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.sprite.AnimatedSprite;
import nl.tudelft.jpacman.sprite.Sprite;

import java.util.Map;

/**
 * 表示游戏中由玩家控制的角色。
 * <p>
 * 玩家具有得分、方向显示、生死状态等功能。根据移动方向显示不同的图像，
 * 并在死亡时显示动画精灵。
 *
 * @author Jeroen Roosen
 * @since 1.0
 */
public class Player extends Unit {

    /**
     * 玩家的得分，初始值为0。
     */
    private int score;

    /**
     * 方向到精灵的映射，用于根据移动方向显示不同的图像。
     */
    private final Map<Direction, Sprite> sprites;

    /**
     * 玩家死亡时显示的动画精灵。
     */
    private final AnimatedSprite deathSprite;

    /**
     * 玩家是否存活。
     */
    private boolean alive;

    /**
     * 创建一个新的玩家实例。
     * <p>
     * 初始化得分为0，存活状态为{@code true}，并将死亡动画精灵的动画设置为不播放。
     *
     * @param spriteMap      方向到精灵的映射，不能为空。
     * @param deathAnimation 玩家死亡时显示的动画精灵，不能为空。
     */
    protected Player(Map<Direction, Sprite> spriteMap, AnimatedSprite deathAnimation) {
        this.score = 0;
        this.alive = true;
        this.sprites = spriteMap;
        this.deathSprite = deathAnimation;
        deathSprite.setAnimating(false);
    }

    /**
     * 返回玩家是否存活。
     *
     * @return {@code true} 表示存活，{@code false} 表示死亡。
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * 设置玩家的存活状态。
     * <p>
     * 如果存活状态为{@code true}，停止死亡动画；
     * 如果存活状态为{@code false}，重新启动死亡动画。
     *
     * @param isAlive {@code true} 表示存活，{@code false} 表示死亡。
     */
    public void setAlive(boolean isAlive) {
        if (isAlive) {
            deathSprite.setAnimating(false);
        }
        if (!isAlive) {
            deathSprite.restart();
        }
        this.alive = isAlive;
    }

    /**
     * 返回玩家的当前得分。
     *
     * @return 玩家的得分，初始值为0。
     */
    public int getScore() {
        return score;
    }

    @Override
    public Sprite getSprite() {
        if (isAlive()) {
            return sprites.get(getDirection());
        }
        return deathSprite;
    }

    /**
     * 将指定的分数加到玩家的得分中。
     *
     * @param points 要增加的分数，必须为正整数。
     */
    public void addPoints(int points) {
        score += points;
    }
}
