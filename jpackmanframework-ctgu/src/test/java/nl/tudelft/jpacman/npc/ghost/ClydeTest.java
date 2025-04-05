package nl.tudelft.jpacman.npc.ghost;

import nl.tudelft.jpacman.board.BoardFactory;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.level.*;
import nl.tudelft.jpacman.sprite.PacManSprites;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 测试Clyde在不同情况下的行为
 */
public class ClydeTest {
    private MapParser mapParser; // 用于解析地图的工具
    private PlayerFactory playerFactory; // 用于创建玩家的工厂

    @BeforeEach
    public void setUp() {
        // 初始化 sprites、boardFactory、ghostFactory 和 levelFactory
        PacManSprites sprites = new PacManSprites();
        BoardFactory boardFactory = new BoardFactory(sprites);
        GhostFactory ghostFactory = new GhostFactory(sprites);
        LevelFactory levelFactory = new LevelFactory(sprites, ghostFactory);
        // 初始化 mapParser 和 playerFactory
        mapParser = new GhostMapParser(levelFactory, boardFactory, ghostFactory);
        playerFactory = new PlayerFactory(sprites);
    }

    /**
     * 测试Clyde离玩家远距离的行为
     */
    @Test
    @DisplayName("Clyde离Player距离大于8个方块")
    public void testClydeFarFromPlayer() {
        // 创建一个地图，玩家和Clyde位于两端
        List<String> map = Arrays.asList(
            "############",
            "#P........C#",
            "############"
        );
        Level level = mapParser.parseMap(map);
        Player player = playerFactory.createPacMan();
        player.setDirection(Direction.EAST);
        level.registerPlayer(player);

        // 找到Clyde并测试其AI移动方向
        Clyde clyde = Navigation.findUnitInBoard(Clyde.class, level.getBoard());
        if (clyde != null) {
            // 预期Clyde会向西移动，靠近玩家
            assertThat(clyde.nextAiMove()).contains(Direction.WEST);
        }
    }

    /**
     * 测试Clyde离玩家近距离的行为
     */
    @Test
    @DisplayName("Clyde离Player距离小于8个方块")
    public void testClydeCloseToPlayer() {
        // 创建一个地图，玩家和Clyde靠近
        List<String> map = Arrays.asList(
            "############",
            "#P...C     #",
            "############"
        );
        Level level = mapParser.parseMap(map);
        Player player = playerFactory.createPacMan();
        player.setDirection(Direction.EAST);
        level.registerPlayer(player);

        // 找到Clyde并测试其AI移动方向
        Clyde clyde = Navigation.findUnitInBoard(Clyde.class, level.getBoard());
        if (clyde != null) {
            // 预期Clyde会移动，但方向可能随机
            assertThat(clyde.nextAiMove()).isNotEmpty();
            assertThat(clyde.nextAiMove()).contains(Direction.EAST);
        }
    }

    /**
     * 测试Clyde和玩家处于适当距离的行为
     */
    @Test
    @DisplayName("Clyde与Player处于适当距离的行为")
    public void testClydeMediumDistanceToPlayer() {
        // 创建一个地图，玩家和Clyde相距一段距离
        List<String> map = Arrays.asList(
            "############",
            "#P......C  #",
            "############"
        );
        Level level = mapParser.parseMap(map);
        Player player = playerFactory.createPacMan();
        player.setDirection(Direction.EAST);
        level.registerPlayer(player);

        // 找到Clyde并测试其AI移动方向
        Clyde clyde = Navigation.findUnitInBoard(Clyde.class, level.getBoard());
        if (clyde != null) {
            // 预期Clyde会向东移动
            assertThat(clyde.nextAiMove()).contains(Direction.EAST);
        }
    }

    /**
     * 测试Clyde触碰边界的行为
     */
    @Test
    @DisplayName("Clyde与Player直接没有路径")
    public void testClydeNoValidMoves() {
        // 创建一个地图，Clyde被墙包围
        List<String> map = Arrays.asList(
            "############",
            "# P######C##",
            "############"
        );
        Level level = mapParser.parseMap(map);
        Player player = playerFactory.createPacMan();
        player.setDirection(Direction.EAST);
        level.registerPlayer(player);

        // 找到Clyde并测试其AI移动方向
        Clyde clyde = Navigation.findUnitInBoard(Clyde.class, level.getBoard());
        if (clyde != null) {
            // 预期Clyde无法移动
            assertThat(clyde.nextAiMove()).isEmpty();
        }
    }




    /**
     * 测试当关卡中没有存活玩家时Clyde（幽灵）的行为
     */
    @Test
    @DisplayName("Clyde没有Player")  // 测试用例显示名称，说明测试场景
    void departWithoutPlayer() {
        // 构建测试地图（ASCII结构）
        List<String> map = Arrays.asList(
            "##############",        // 顶部围墙
            "#.C...........",        // 中间行：C表示Clyde，.为路径
            "##############"         // 底部围墙
        );

        // 解析地图生成关卡对象
        Level level = mapParser.parseMap(map);

        // 在关卡中查找Clyde实例
        Clyde clyde = Navigation.findUnitInBoard(Clyde.class, level.getBoard());

        // 验证Clyde实例存在
        assertThat(clyde).isNotNull();

        // 验证Clyde初始方向为EAST（东）
        assertThat(clyde.getDirection()).isEqualTo(Direction.valueOf("EAST"));

        // 确认关卡中没有存活玩家（地图无P标识符）
        assertThat(level.isAnyPlayerAlive()).isFalse();

        // 获取Clyde的下一步AI移动决策
        Optional<Direction> opt = clyde.nextAiMove();

        // 验证无移动决策（因玩家不存在，幽灵应停止移动）
        assertThat(opt.isPresent()).isFalse();
    }

}
