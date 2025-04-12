package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.board.Unit;

/**
 * 一个包含不同类型单位之间所有(相关)碰撞的表格。
 * <p>
 * 该接口定义了游戏中不同单位之间碰撞的处理机制。
 * 实现类需要具体定义不同类型单位碰撞时的行为。
 *
 * @author Jeroen Roosen
 */
public interface CollisionMap {

    /**
     * 碰撞两个单位并处理碰撞结果，结果可能是什么都不做。
     *
     * @param <C1>     碰撞者类型（必须是Unit的子类）
     * @param <C2>     被碰撞者类型（必须是Unit的子类）
     * @param collider 引起碰撞的单位，它移动到了一个已有其他单位的方格
     * @param collidee 已经被占据的方格上的单位
     */
    <C1 extends Unit, C2 extends Unit> void collide(C1 collider, C2 collidee);
}
