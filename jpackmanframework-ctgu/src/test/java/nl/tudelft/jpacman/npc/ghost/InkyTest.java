package nl.tudelft.jpacman.npc.ghost;

import nl.tudelft.jpacman.board.BoardFactory;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.level.Level;
import nl.tudelft.jpacman.level.LevelFactory;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.level.PlayerFactory;
import nl.tudelft.jpacman.sprite.PacManSprites;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class InkyTest {

    private static final PacManSprites SPRITES = new PacManSprites();
    private PlayerFactory playerFactory;
    private GhostMapParser ghostMapParser;

    @BeforeEach
    void setup() {
        playerFactory = new PlayerFactory(SPRITES);
        BoardFactory boardFactory = new BoardFactory(SPRITES);
        GhostFactory ghostFactory = new GhostFactory(SPRITES);
        LevelFactory levelFactory = new LevelFactory(SPRITES, ghostFactory);
        ghostMapParser = new GhostMapParser(levelFactory, boardFactory, ghostFactory);
    }

    /**
     * 注册Player到关卡并设置方向。
     */
    private void registerPlayer(Level level, Direction direction) {
        Player player = playerFactory.createPacMan();
        player.setDirection(direction);
        level.registerPlayer(player);
    }

    /**
     * 解析自定义地图布局。
     */
    private Level parseMap(List<String> grid) {
        return ghostMapParser.parseMap(grid);
    }

    @Test
    @DisplayName("当Inky、Blink和Player在同一直线时，Inky应朝Player方向移动")
    void shouldMoveTowardsPlayerWhenAlignedWithBlinky() {
        Level level = parseMap(Arrays.asList(
            "#####################",
            "#      P    B    I  #",
            "#####################"
        ));
        registerPlayer(level, Direction.EAST);
        Inky inky = Navigation.findUnitInBoard(Inky.class, level.getBoard());

        if (inky != null) {
            assertThat(inky.nextAiMove()).contains(Direction.WEST);
        }
    }

    @Test
    @DisplayName("当Player前方路径被阻挡时，Inky向Blinky移动")
    void shouldMoveWhenPathIsBlocked() {
        Level level = parseMap(Arrays.asList(
            "#####################",
            "#P#       B       I #",
            "#####################"
        ));
        registerPlayer(level, Direction.EAST);
        Inky inky = Navigation.findUnitInBoard(Inky.class, level.getBoard());


        if (inky != null) {
            assertThat(inky.nextAiMove()).contains(Direction.WEST);
        }
    }

    @Test
    @DisplayName("当Blinky不存在时，Inky无法计算路径")
    void shouldNotMoveWithoutBlinky() {
        Level level = parseMap(Arrays.asList(
            "#############",
            "#    P    I #",
            "#############"
        ));
        registerPlayer(level, Direction.WEST);
        Inky inky = Navigation.findUnitInBoard(Inky.class, level.getBoard());

        if (inky != null) {
            assertThat(inky.nextAiMove()).isEmpty();
        }
    }

    @Test
    @DisplayName("当Player不存在时，Inky无目标点")
    void shouldNotMoveWithoutPlayer() {
        Level level = parseMap(Arrays.asList(
            "#############",
            "#    B    I #",
            "#############"
        ));
        // 不注册Player
        Inky inky = Navigation.findUnitInBoard(Inky.class, level.getBoard());


        if (inky != null) {
            assertThat(inky.nextAiMove()).isEmpty();
        }
    }

    @Test
    @DisplayName("当Player面朝北方时，Inky的目标点计算包含经典偏移")
    void shouldCalculateTargetWithOffsetWhenPlayerFacesNorth() {
        Level level = parseMap(Arrays.asList(
            "######################",
            "#         P          #",
            "#         B          #",
            "#                  I #",
            "######################"
        ));
        registerPlayer(level, Direction.NORTH);
        Inky inky = Navigation.findUnitInBoard(Inky.class, level.getBoard());


        if (inky != null) {
            assertThat(inky.nextAiMove()).contains(Direction.WEST);
        }
    }

    @Test
    @DisplayName("当Blinky远离Player时，Inky朝延长路径方向移动")
    void shouldMoveToExtendedPathWhenBlinkyIsDistant() {
        Level level = parseMap(Arrays.asList(
            "######################",
            "# P               I  #",
            "#                    #",
            "#          B         #",
            "######################"
        ));
        registerPlayer(level, Direction.EAST);
        Inky inky = Navigation.findUnitInBoard(Inky.class, level.getBoard());

        if (inky != null) {
            assertThat(inky.nextAiMove()).contains(Direction.SOUTH);
        }
    }
}
