package nl.tudelft.jpacman.board;

import nl.tudelft.jpacman.sprite.PacManSprites;
import nl.tudelft.jpacman.sprite.Sprite;

/**
 * 棋盘工厂类，用于创建棋盘（Board）并初始化方块的连接关系。
 * 提供地面（Ground）和墙壁（Wall）方块的创建方法，管理棋盘精灵资源。
 *
 * @author Jeroen Roosen
 */
public class BoardFactory {

    /**
     * 精灵资源库，用于获取棋盘方块的背景精灵（如地面和墙壁的贴图）。
     */
    private final PacManSprites sprites;

    /**
     * 构造棋盘工厂实例，绑定精灵资源库。
     *
     * @param spriteStore 提供棋盘方块背景精灵的资源库
     */
    public BoardFactory(PacManSprites spriteStore) {
        this.sprites = spriteStore;
    }

    /**
     * 根据二维方块数组创建棋盘，并连接相邻方块。
     * 注：采用循环连接策略，边缘方块会连接到对侧（如吃豆人的隧道效果）。
     *
     * @param grid 二维方块数组，grid[x][y]表示第x列、第y行的方块
     * @return 初始化完成的棋盘对象
     * @throws AssertionError 如果grid为null
     */
    public Board createBoard(Square[][] grid) {
        assert grid != null;

        Board board = new Board(grid);
        int width = board.getWidth();
        int height = board.getHeight();

        // 遍历所有方块，建立与四个方向的连接
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Square square = grid[x][y];
                for (Direction dir : Direction.values()) {
                    // 计算相邻坐标（使用模运算实现循环连接）
                    int dirX = (width + x + dir.getDeltaX()) % width;
                    int dirY = (height + y + dir.getDeltaY()) % height;
                    Square neighbour = grid[dirX][dirY];
                    square.link(neighbour, dir); // 建立双向连接
                }
            }
        }
        return board;
    }

    /**
     * 创建可通行的地面方块。
     *
     * @return 配置了地面精灵的可通行方块
     */
    public Square createGround() {
        return new Ground(sprites.getGroundSprite());
    }

    /**
     * 创建不可通行的墙壁方块。
     *
     * @return 配置了墙壁精灵的不可通行方块
     */
    public Square createWall() {
        return new Wall(sprites.getWallSprite());
    }

    //------------------------ 内部类 ------------------------

    /**
     * 墙壁方块，不可被任何单位通过。
     */
    private static final class Wall extends Square {
        private final Sprite background;

        /**
         * 构造墙壁方块。
         *
         * @param sprite 墙壁的贴图精灵
         */
        Wall(Sprite sprite) {
            this.background = sprite;
        }

        @Override
        public boolean isAccessibleTo(Unit unit) {
            return false; // 始终不可通行
        }

        @Override
        public Sprite getSprite() {
            return background;
        }
    }

    /**
     * 地面方块，可被所有单位通过。
     */
    private static final class Ground extends Square {
        private final Sprite background;

        /**
         * 构造地面方块。
         *
         * @param sprite 地面的贴图精灵
         */
        Ground(Sprite sprite) {
            this.background = sprite;
        }

        @Override
        public boolean isAccessibleTo(Unit unit) {
            return true; // 始终可通行
        }

        @Override
        public Sprite getSprite() {
            return background;
        }
    }
}
