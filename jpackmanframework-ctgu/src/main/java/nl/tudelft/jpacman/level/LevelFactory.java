package nl.tudelft.jpacman.level;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.npc.Ghost;
import nl.tudelft.jpacman.npc.ghost.GhostColor;
import nl.tudelft.jpacman.npc.ghost.GhostFactory;
import nl.tudelft.jpacman.sprite.PacManSprites;
import nl.tudelft.jpacman.sprite.Sprite;

/**
 * 关卡工厂类，负责创建游戏关卡（Level）、幽灵（Ghost）和豆子（Pellet）。
 * 实现工厂模式，集中管理游戏元素的创建逻辑。
 *
 * @author Jeroen Roosen
 */
public class LevelFactory {

    // 幽灵类型常量定义
    private static final int GHOSTS = 4;      // 幽灵种类总数
    private static final int BLINKY = 0;      // 红色幽灵Blinky
    private static final int INKY = 1;        // 青色幽灵Inky
    private static final int PINKY = 2;       // 粉色幽灵Pinky
    private static final int CLYDE = 3;       // 橙色幽灵Clyde

    /**
     * 单个豆子的默认分数值。
     */
    private static final int PELLET_VALUE = 10;

    /**
     * 精灵资源库，提供单位（幽灵、豆子）的贴图。
     */
    private final PacManSprites sprites;

    /**
     * 幽灵类型循环索引，用于按顺序创建不同种类的幽灵。
     */
    private int ghostIndex;

    /**
     * 幽灵工厂，用于创建特定类型的幽灵。
     */
    private final GhostFactory ghostFact;

    /**
     * 构造关卡工厂实例。
     *
     * @param spriteStore  提供单位贴图的精灵库
     * @param ghostFactory 幽灵工厂，用于创建具体幽灵类型
     */
    public LevelFactory(PacManSprites spriteStore, GhostFactory ghostFactory) {
        this.sprites = spriteStore;
        this.ghostIndex = -1; // 初始化为-1，首次调用createGhost()后变为0
        this.ghostFact = ghostFactory;
    }

    /**
     * 创建游戏关卡实例。
     *
     * @param board          关卡地图，包含地形和单位位置
     * @param ghosts         关卡中的幽灵列表
     * @param startPositions 玩家起始位置列表
     * @return 配置完成的关卡对象，使用默认玩家碰撞处理器
     */
    public Level createLevel(Board board, List<Ghost> ghosts,
                             List<Square> startPositions) {
        // 使用默认的玩家碰撞处理器（处理玩家与幽灵、豆子的交互）
        CollisionMap collisionMap = new PlayerCollisions();
        return new Level(board, ghosts, startPositions, collisionMap);
    }

    /**
     * 创建新幽灵实例，按顺序循环生成四种经典幽灵。
     * 顺序：Blinky -> Inky -> Pinky -> Clyde -> Blinky...
     *
     * @return 新幽灵实例
     */
    Ghost createGhost() {
        ghostIndex = (ghostIndex + 1) % GHOSTS; // 循环索引0-3
        switch (ghostIndex) {
            case BLINKY:
                return ghostFact.createBlinky();  // 红色幽灵
            case INKY:
                return ghostFact.createInky();    // 青色幽灵
            case PINKY:
                return ghostFact.createPinky();   // 粉色幽灵
            case CLYDE:
                return ghostFact.createClyde();   // 橙色幽灵
            default: // 理论上不会执行，防御性代码
                return new RandomGhost(sprites.getGhostSprite(GhostColor.RED));
        }
    }

    /**
     * 创建新豆子实例。
     *
     * @return 豆子对象，包含分数值和贴图
     */
    public Pellet createPellet() {
        return new Pellet(PELLET_VALUE, sprites.getPelletSprite());
    }

    //------------------------ 内部类 ------------------------

    /**
     * 随机移动的幽灵实现（备用方案，当GhostFactory未配置时使用）。
     * 移动间隔固定为175ms，无特定AI逻辑。
     */
    private static final class RandomGhost extends Ghost {
        private static final long DELAY = 175L; // 移动间隔（毫秒）

        /**
         * 构造随机移动幽灵。
         *
         * @param ghostSprite 幽灵各方向的贴图
         */
        RandomGhost(Map<Direction, Sprite> ghostSprite) {
            super(ghostSprite, (int) DELAY, 0); // 延迟转换为int
        }

        /**
         * AI移动逻辑：返回空表示随机移动。
         */
        @Override
        public Optional<Direction> nextAiMove() {
            return Optional.empty(); // 由父类处理随机移动
        }
    }
}
