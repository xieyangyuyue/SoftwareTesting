package nl.tudelft.jpacman.sprite;

import java.awt.Graphics;

/**
 * 游戏中物体的视觉表示接口。
 * <p>
 * 该接口定义了绘制精灵、分割精灵以及获取精灵尺寸的基本方法。
 * 所有具体的精灵实现类必须实现该接口。
 *
 * @author Jeroen Roosen
 * @since 1.0
 */
public interface Sprite {

    /**
     * 在指定的图形上下文中绘制精灵。
     *
     * @param graphics
     *            用于绘制的图形上下文。
     * @param x
     *            绘制的起始x坐标。
     * @param y
     *            绘制的起始y坐标。
     * @param width
     *            绘制区域的宽度。
     * @param height
     *            绘制区域的高度。
     */
    void draw(Graphics graphics, int x, int y, int width, int height);

    /**
     * 将当前精灵分割成一个较小的精灵。
     *
     * @param x
     *            分割区域的起始x坐标。
     * @param y
     *            分割区域的起始y坐标。
     * @param width
     *            分割后的精灵宽度。
     * @param height
     *            分割后的精灵高度。
     * @return 分割后的精灵，若区域超出当前精灵范围，则返回一个 {@link EmptySprite}。
     */
    Sprite split(int x, int y, int width, int height);

    /**
     * 获取精灵的宽度（以像素为单位）。
     *
     * @return 精灵的宽度。
     */
    int getWidth();

    /**
     * 获取精灵的高度（以像素为单位）。
     *
     * @return 精灵的高度。
     */
    int getHeight();
}
