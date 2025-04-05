// 定义GhostMapParser类，继承自MapParser，用于解析包含幽灵位置信息的自定义地图格式
package nl.tudelft.jpacman.npc.ghost;

// 导入必要的依赖

import nl.tudelft.jpacman.board.BoardFactory;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.level.LevelFactory;
import nl.tudelft.jpacman.level.MapParser;
import nl.tudelft.jpacman.npc.Ghost;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 测试辅助工具类，用于编写幽灵单元测试。
 * 建议使用Navigation类中的findUnitInBoard方法从地图中检索幽灵。
 */
public final class GhostMapParser extends MapParser {

    // 幽灵工厂，用于创建特定类型的幽灵实例
    private final GhostFactory ghostFactory;

    /**
     * 构造增强版地图解析器。
     * 允许开发者在地图指定位置放置特定类型的幽灵。
     *
     * @param levelFactory 提供NPC对象和关卡的工厂
     * @param boardFactory 创建地图元素的工厂
     * @param ghostFactory 创建幽灵的工厂
     */
    public GhostMapParser(LevelFactory levelFactory, BoardFactory boardFactory,
                          GhostFactory ghostFactory) {
        super(levelFactory, boardFactory);  // 调用父类构造器初始化基础功能
        this.ghostFactory = ghostFactory;   // 注入幽灵工厂依赖
    }

    /**
     * 覆盖父类方法，增强地图解析能力。
     * 当前仅支持Clyde幽灵，需添加其他幽灵类型处理逻辑。
     *
     * @param grid           网格二维数组，表示游戏地图
     * @param ghosts         幽灵列表，用于收集生成的幽灵实例
     * @param startPositions 起始位置列表（如吃豆人出生点）
     * @param x              当前处理的x坐标
     * @param y              当前处理的y坐标
     * @param c              地图字符标识（如'C'代表Clyde）
     */
    @Override
    protected void addSquare(Square[][] grid, List<Ghost> ghosts,
                             List<Square> startPositions, int x, int y, char c) {
        // 显式声明泛型类型
        Map<Character, Supplier<Ghost>> ghostFactoryMap = new HashMap<>();
        ghostFactoryMap.put('C', ghostFactory::createClyde);
        ghostFactoryMap.put('B', ghostFactory::createBlinky);
        ghostFactoryMap.put('I', ghostFactory::createInky);

        if (ghostFactoryMap.containsKey(c)) {
            Ghost ghost = ghostFactoryMap.get(c).get();
            grid[x][y] = makeGhostSquare(ghosts, ghost);
        } else {
            super.addSquare(grid, ghosts, startPositions, x, y, c);
        }
    }

/*    protected void addSquare(Square[][] grid, List<Ghost> ghosts,
                             List<Square> startPositions, int x, int y, char c) {

        // 当字符为'C'时，在当前位置生成Clyde幽灵
        if (c == 'C') {
            // 创建幽灵实例
            Ghost clyde = ghostFactory.createClyde();
            // 创建包含幽灵的方格，并添加到网格和幽灵列表
            grid[x][y] = makeGhostSquare(ghosts, clyde);
        }
        else if (c == 'B') {
            // 创建幽灵实例
            Ghost Blink = ghostFactory.createClyde();
            // 创建包含幽灵的方格，并添加到网格和幽灵列表
            grid[x][y] = makeGhostSquare(ghosts, Blink );
        }
        else if (c == 'I') {
            // 创建幽灵实例
            Ghost Inky = ghostFactory.createClyde();
            // 创建包含幽灵的方格，并添加到网格和幽灵列表
            grid[x][y] = makeGhostSquare(ghosts, Inky );
        }
        else {
            // 其他字符交给父类处理（如墙壁、豆子等）
            super.addSquare(grid, ghosts, startPositions, x, y, c);
        }
    }*/
}
