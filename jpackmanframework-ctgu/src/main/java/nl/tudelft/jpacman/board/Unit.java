package nl.tudelft.jpacman.board;

import nl.tudelft.jpacman.sprite.Sprite;

import java.util.Objects;

/**
 * 游戏板上可放置的单位（如Pacman或幽灵）的抽象基类。
 * 管理单位的位置、方向及移动逻辑。
 *
 * @author Jeroen Roosen
 */
public abstract class Unit {

    /**
     * 当前单位所在的格子。
     */
    private Square square;

    /**
     * 单位当前面对的方向（默认向东）。
     */
    private Direction direction;

    /**
     * 构造方法，初始化单位方向为东。
     */
    protected Unit() {
        this.direction = Direction.EAST;
    }

    /**
     * 设置单位的新方向。
     *
     * @param newDirection 新方向（不能为null）
     */
    public void setDirection(Direction newDirection) {
        Objects.requireNonNull(newDirection, "Direction cannot be null");
        this.direction = newDirection;
    }

    /**
     * 获取当前方向。
     *
     * @return 当前方向实例
     */
    public Direction getDirection() {
        return this.direction;
    }

    /**
     * 获取当前所在格子（调用前需确保单位已放置在格子上）。
     *
     * @return 当前格子对象
     * @throws AssertionError 若未放置在格子上（断言启用时）
     */
    public Square getSquare() {
        if (!hasSquare()) {
            throw new IllegalStateException("Unit is not on any square");
        }
        assert invariant() : "Invariant violated: Unit's square mismatch";      // 验证状态一致性
        assert square != null;   // 确保格子存在
        return square;
    }

    /**
     * 检查单位是否已放置在格子上。
     *
     * @return true 如果格子存在
     */
    public boolean hasSquare() {
        return square != null;
    }

    /**
     * 占据目标格子（需确保目标允许进入）。
     * 先离开原格子（若存在），再占据新格子。
     *
     * @param target 要占据的目标格子（非null）
     */
    public void occupy(Square target) {
        Objects.requireNonNull(target, "Target square cannot be null");
        if (!target.isAccessibleTo(this)) {
            throw new IllegalArgumentException("Target square is not accessible");
        }
        if (square != null) {
            square.remove(this); // 从原格子移除
        }
        square = target;         // 更新当前格子
        target.put(this);        // 添加到目标格子
        assert invariant() : "Invariant violated after occupation";      // 验证状态一致性
    }

    /**
     * 离开当前格子，从板上移除。
     */
    public void leaveSquare() {
        if (square != null) {
            square.remove(this); // 从当前格子移除
            square = null;       // 清空引用
        }
        assert invariant() : "Invariant violated after leaving";
    }

    /**
     * 类不变式：验证格子与单位状态的一致性。
     *
     * @return true 如果单位未占据格子，或其所在格子包含该单位
     */
    protected boolean invariant() {
        return square == null || (square.getOccupants().contains(this) && square.isAccessibleTo(this));
    }

    /**
     * 抽象方法，获取单位的精灵（由子类实现）。
     *
     * @return 代表单位外观的精灵对象
     */
    public abstract Sprite getSprite();

    /**
     * 计算单位前方指定步数的格子（用于幽灵AI）。
     * 沿当前方向逐步查找，若路径被阻挡可能返回不可达格子。
     *
     * @param amountToLookAhead 向前查看的步数
     * @return 目标格子，可能为null（若路径不完整）
     */
    public Square squaresAheadOf(int amountToLookAhead) {
        Direction targetDirection = this.getDirection();
        Square destination = this.getSquare();
        for (int i = 0; i < amountToLookAhead; i++) {
            // 获取当前格子在目标方向的下一个格子
            destination = destination.getSquareAt(targetDirection);
        }
        return destination;
    }

//    public Square squaresAheadOf(int amountToLookAhead) {
//        if (amountToLookAhead < 0) {
//            throw new IllegalArgumentException("Lookahead cannot be negative");
//        }
//        Square current = getSquare(); // 会检查是否在方块上
//        for (int i = 0; i < amountToLookAhead; i++) {
//            Square next = current.getSquareAt(direction);
//            if (next == null || !next.isAccessibleTo(this)) {
//                throw new IllegalStateException("Path blocked at step " + (i + 1));
//            }
//            current = next;
//        }
//        return current;
//    }
}
