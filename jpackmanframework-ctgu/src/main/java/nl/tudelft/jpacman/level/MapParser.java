package nl.tudelft.jpacman.level;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import nl.tudelft.jpacman.PacmanConfigurationException;
import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.BoardFactory;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.npc.Ghost;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * 地图解析器：将文本格式的地图转换为可玩的游戏关卡。
 * <p>
 * 支持字符说明：
 * <ul>
 * <li>' ' - 空地</li>
 * <li>'#' - 墙壁</li>
 * <li>'.' - 豆子</li>
 * <li>'P' - 玩家起始位置</li>
 * <li>'G' - 幽灵</li>
 * </ul>
 * 
 * @author Jeroen Roosen
 */
public class MapParser {

    /**
     * 关卡工厂：用于创建关卡、豆子和幽灵对象
     */
    private final LevelFactory levelCreator;

    /**
     * 地图工厂：用于创建地图方块（地面、墙壁）和游戏面板
     */
    private final BoardFactory boardCreator;

    /**
     * 构造地图解析器
     * 
     * @param levelFactory 关卡工厂，提供NPC和关卡创建功能
     * @param boardFactory 地图工厂，提供方块和面板创建功能
     */
    public MapParser(LevelFactory levelFactory, BoardFactory boardFactory) {
        this.levelCreator = levelFactory;
        this.boardCreator = boardFactory;
    }

    /**
     * 解析二维字符数组生成游戏关卡
     * 
     * @param map 二维字符数组，map[x][y]表示坐标(x,y)的方块类型
     * @return 生成的游戏关卡对象
     * @throws PacmanConfigurationException 如果遇到无效字符或格式错误
     */
    public Level parseMap(char[][] map) {
        // 验证地图尺寸
        int width = map.length;
        int height = map[0].length;

        // 初始化游戏元素存储
        Square[][] grid = new Square[width][height];
        List<Ghost> ghosts = new ArrayList<>();
        List<Square> startPositions = new ArrayList<>();

        // 填充地图网格
        makeGrid(map, width, height, grid, ghosts, startPositions);

        // 创建游戏面板和关卡
        Board board = boardCreator.createBoard(grid);
        return levelCreator.createLevel(board, ghosts, startPositions);
    }

    /**
     * 遍历地图字符填充网格
     * 
     * @param map           原始字符地图
     * @param width         地图宽度
     * @param height        地图高度
     * @param grid          目标方块网格
     * @param ghosts        幽灵集合（输出参数）
     * @param startPositions 玩家起始位置集合（输出参数）
     */
    private void makeGrid(char[][] map, int width, int height,
                         Square[][] grid, List<Ghost> ghosts, List<Square> startPositions) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                char c = map[x][y];
                addSquare(grid, ghosts, startPositions, x, y, c);
            }
        }
    }

    /**
     * 根据字符创建对应类型的游戏方块
     * 
     * @param grid           方块网格
     * @param ghosts         幽灵集合（输出参数）
     * @param startPositions 玩家起始位置集合（输出参数）
     * @param x              X坐标
     * @param y              Y坐标
     * @param c              地图字符
     * @throws PacmanConfigurationException 如果遇到无效字符
     */
    protected void addSquare(Square[][] grid, List<Ghost> ghosts,
                            List<Square> startPositions, int x, int y, char c) {
        switch (c) {
            case ' ' ->  // 空地
                grid[x][y] = boardCreator.createGround();
            
            case '#' ->  // 墙壁
                grid[x][y] = boardCreator.createWall();
            
            case '.' -> {  // 豆子
                Square pelletSquare = boardCreator.createGround();
                grid[x][y] = pelletSquare;
                // 在方块上放置豆子
                levelCreator.createPellet().occupy(pelletSquare);
            }
            
            case 'G' -> {  // 幽灵
                Ghost ghost = levelCreator.createGhost();
                Square ghostSquare = makeGhostSquare(ghosts, ghost);
                grid[x][y] = ghostSquare;
            }
            
            case 'P' -> {  // 玩家起始位置
                Square playerSquare = boardCreator.createGround();
                grid[x][y] = playerSquare;
                startPositions.add(playerSquare);
            }
            
            default ->  // 无效字符处理
                throw new PacmanConfigurationException("非法字符位于 " 
                    + x + "," + y + ": " + c);
        }
    }

    /**
     * 创建幽灵方块并注册幽灵对象
     * 
     * @param ghosts 幽灵集合（输出参数）
     * @param ghost  新创建的幽灵对象
     * @return 包含幽灵的方块
     */
    protected Square makeGhostSquare(List<Ghost> ghosts, Ghost ghost) {
        Square ghostSquare = boardCreator.createGround();
        ghosts.add(ghost);
        ghost.occupy(ghostSquare);  // 让幽灵占据方块
        return ghostSquare;
    }

    /**
     * 解析字符串列表生成游戏关卡
     * 
     * @param text 字符串列表，每个字符串代表一行地图
     * @return 生成的游戏关卡对象
     * @throws PacmanConfigurationException 如果地图格式错误
     */
    public Level parseMap(List<String> text) {
        checkMapFormat(text);
        
        int height = text.size();
        int width = text.get(0).length();
        
        // 将字符串列表转换为二维字符数组
        char[][] map = new char[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[x][y] = text.get(y).charAt(x);  // 注意坐标转换
            }
        }
        return parseMap(map);
    }

    /**
     * 验证地图文本格式
     * 
     * @param text 地图文本行
     * @throws PacmanConfigurationException 如果格式不符合要求
     */
    private void checkMapFormat(List<String> text) {
        if (text == null) {
            throw new PacmanConfigurationException("地图文本不能为null");
        }
        
        if (text.isEmpty()) {
            throw new PacmanConfigurationException("地图必须至少包含一行");
        }
        
        int width = text.get(0).length();
        if (width == 0) {
            throw new PacmanConfigurationException("地图行不能为空");
        }
        
        for (String line : text) {
            if (line.length() != width) {
                throw new PacmanConfigurationException("地图行宽度不一致");
            }
        }
    }

    /**
     * 从输入流解析地图
     * 
     * @param source 输入流
     * @return 生成的游戏关卡对象
     * @throws IOException 读取失败时抛出
     */
    public Level parseMap(InputStream source) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                source, StandardCharsets.UTF_8))) {
            List<String> lines = new ArrayList<>();
            while (reader.ready()) {
                lines.add(reader.readLine());
            }
            return parseMap(lines);
        }
    }

    /**
     * 从资源文件解析地图
     * 
     * @param mapName 资源文件路径
     * @return 生成的游戏关卡对象
     * @throws IOException 读取失败时抛出
     */
    @SuppressFBWarnings(value = "OBL_UNSATISFIED_OBLIGATION",
            justification = "使用try-with-resources确保资源释放")
    public Level parseMap(String mapName) throws IOException {
        try (InputStream boardStream = MapParser.class.getResourceAsStream(mapName)) {
            if (boardStream == null) {
                throw new PacmanConfigurationException("找不到资源文件: " + mapName);
            }
            return parseMap(boardStream);
        }
    }

    /**
     * 获取地图工厂（用于测试或扩展）
     * 
     * @return 地图工厂实例
     */
    protected BoardFactory getBoardCreator() {
        return boardCreator;
    }
}