package nl.tudelft.jpacman.board;

import com.google.common.collect.ImmutableList;
import nl.tudelft.jpacman.sprite.Sprite;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 表示棋盘（Board）上的一个方块，抽象类。
 * 负责管理方块的相邻关系、当前占据该方块的单位（Unit），并提供基础访问控制逻辑。
 * 具体子类需实现通行权限检查和贴图获取（如地面或墙壁）。
 *
 * @author Jeroen Roosen
 */
public abstract class Square {

    /**
     * 当前占据此方块的所有单位列表，按进入顺序存储。
     */
    private final List<Unit> occupants;

    /**
     * 相邻方块的映射表，键为方向，值为对应方向的相邻方块。
     * 使用EnumMap优化方向键的访问效率。
     */
    private final Map<Direction, Square> neighbours;

    /**
     * 构造一个空的方块。
     * 初始化时无单位占据，且相邻关系为空。
     */
    protected Square() {
        this.occupants = new ArrayList<>();
        this.neighbours = new EnumMap<>(Direction.class);
        assert invariant(); // 构造时验证不变式
    }

    /**
     * 获取指定方向上的相邻方块。
     *
     * @param direction 目标方向
     * @return 相邻方块，若未连接则返回null
     */
    public Square getSquareAt(Direction direction) {
        return neighbours.get(direction);
    }

    /**
     * 单向连接当前方块到相邻方块。
     * 注：此操作仅设置当前方块对相邻方块的引用，不自动设置反向连接。
     *
     * @param neighbour 相邻方块
     * @param direction 相邻方向（从当前方块视角）
     */
    public void link(Square neighbour, Direction direction) {
        neighbours.put(direction, neighbour);
        assert invariant(); // 连接后验证不变式
    }

    /**
     * 获取当前方块上所有占据者的不可变副本列表。
     * 列表顺序按单位进入方块的先后排列（先进入者在前）。
     *
     * @return 不可修改的占据者列表
     */
    public List<Unit> getOccupants() {
        return ImmutableList.copyOf(occupants);
    }

    /**
     * 添加一个单位到当前方块。
     *
     * @param occupant 要添加的单位（非null且未存在于当前方块）
     * @throws AssertionError 如果参数非法或单位已存在
     */
    void put(Unit occupant) {
        assert occupant != null;
        assert !occupants.contains(occupant);

        occupants.add(occupant);
    }

    /**
     * 从当前方块移除指定单位（如果存在）。
     *
     * @param occupant 要移除的单位（非null）
     */
    void remove(Unit occupant) {
        assert occupant != null;
        occupants.remove(occupant);
    }

    /**
     * 验证不变式：所有占据者必须正确关联当前方块。
     * 即，每个占据者的getSquare()必须返回当前方块。
     *
     * @return true表示验证通过，false表示存在不一致
     */
    protected final boolean invariant() {
        for (Unit occupant : occupants) {
            if (occupant.hasSquare() && occupant.getSquare() != this) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断指定单位是否可进入此方块。
     * 具体逻辑由子类实现（如地面允许通行，墙壁禁止通行）。
     *
     * @param unit 要检查的单位
     * @return true表示允许进入，false表示禁止
     */
    public abstract boolean isAccessibleTo(Unit unit);

    /**
     * 获取此方块的贴图精灵。
     * 具体贴图由子类实现（如地面或墙壁的不同贴图）。
     *
     * @return 方块的精灵对象
     */
    public abstract Sprite getSprite();
}
