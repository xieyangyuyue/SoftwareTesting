package nl.tudelft.jpacman.board;

/**
 * 表示游戏棋盘，由二维方块（Square）矩阵构成，提供棋盘尺寸查询和方块访问功能。
 * 本类保证棋盘初始化后所有方块非空，并提供安全的坐标边界检查。
 *
 * @author Jeroen Roosen
 */
public class Board {

    /**
     * 存储棋盘的二维方块数组，board[x][y]表示第x列、第y行的方块。
     * 该数组在构造时直接引用传入的grid参数，调用者需确保其不可变性。
     */
    private final Square[][] board;

    /**
     * 构造棋盘实例。
     * 注：直接存储传入的grid引用（未做防御性拷贝），需确保外部不会修改原数组。
     *
     * @param grid 二维方块数组，需满足grid[x][y]不为null且为矩形结构
     * @throws AssertionError 如果grid包含null元素或非矩形结构
     */
    @SuppressWarnings("PMD.ArrayIsStoredDirectly")
    Board(Square[][] grid) {
        assert grid != null;
        this.board = grid;
        // 强制检查棋盘不变式：所有方块必须非空
        assert invariant() : "初始棋盘包含null方块，违反不变式！";
    }

    /**
     * 棋盘不变式校验：确保所有方块非空。
     * @return 如果任意方块为null返回false，否则返回true
     */
    protected final boolean invariant() {
        for (Square[] row : board) {
            for (Square square : row) {
                if (square == null) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 获取棋盘列数（宽度）。
     * @return 棋盘列数，即二维数组第一维长度
     */
    public int getWidth() {
        return board.length;
    }

    /**
     * 获取棋盘行数（高度）。
     * 注意：假设棋盘为矩形结构，取第一列的长度作为行数。
     * @return 棋盘行数，即二维数组第二维长度
     */
    public int getHeight() {
        return board[0].length;
    }

    /**
     * 获取指定坐标的方块。
     * 前置条件：坐标(x, y)必须在棋盘范围内（可通过withinBorders()验证）
     *
     * @param x 列索引（从0开始）
     * @param y 行索引（从0开始）
     * @return 非空的Square对象
     * @throws AssertionError 如果坐标越界或返回null（违反不变式）
     */
    public Square squareAt(int x, int y) {
        assert withinBorders(x, y) : "访问越界坐标 (" + x + ", " + y + ")";
        Square result = board[x][y];
        assert result != null : "不变式被破坏：棋盘包含null方块！";
        return result;
    }

    /**
     * 验证坐标是否在棋盘合法范围内。
     *
     * @param x 列索引
     * @param y 行索引
     * @return true表示坐标有效，false表示越界
     */
    public boolean withinBorders(int x, int y) {
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }
}
