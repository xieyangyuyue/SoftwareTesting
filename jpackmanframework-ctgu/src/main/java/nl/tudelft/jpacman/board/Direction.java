package nl.tudelft.jpacman.board;

/**
 * 表示二维方格棋盘上的移动方向枚举类。
 * 定义了北（NORTH）、南（SOUTH）、西（WEST）、东（EAST）四个基础方向，
 * 每个方向包含坐标偏移量，用于计算相邻方块的坐标。
 * <p>
 * 注：坐标系以左上角为原点 (0, 0)，x 轴向右延伸，y 轴向下延伸。
 *
 * @author Jeroen Roosen
 */
public enum Direction {

    /**
     * 北方向（向上移动），y 坐标减少，坐标偏移量为 (0, -1)
     */
    NORTH(0, -1),

    /**
     * 南方向（向下移动），y 坐标增加，坐标偏移量为 (0, 1)
     */
    SOUTH(0, 1),

    /**
     * 西方向（向左移动），x 坐标减少，坐标偏移量为 (-1, 0)
     */
    WEST(-1, 0),

    /**
     * 东方向（向右移动），x 坐标增加，坐标偏移量为 (1, 0)
     */
    EAST(1, 0);

    /**
     * x 轴方向偏移量（水平变化值）。
     * 例如：EAST 方向 deltaX=1，表示 x 坐标增加 1。
     */
    private final int deltaX;

    /**
     * y 轴方向偏移量（垂直变化值）。
     * 例如：SOUTH 方向 deltaY=1，表示 y 坐标增加 1。
     */
    private final int deltaY;

    /**
     * 构造方向枚举实例，指定坐标偏移量。
     *
     * @param deltaX x 轴偏移量（正数向右，负数向左）
     * @param deltaY y 轴偏移量（正数向下，负数向上）
     */
    Direction(int deltaX, int deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    /**
     * 获取当前方向的 x 轴偏移量。
     * 例如：WEST 方向返回 -1。
     *
     * @return x 轴偏移量
     */
    public int getDeltaX() {
        return deltaX;
    }

    /**
     * 获取当前方向的 y 轴偏移量。
     * 例如：NORTH 方向返回 -1。
     *
     * @return y 轴偏移量
     */
    public int getDeltaY() {
        return deltaY;
    }
}
