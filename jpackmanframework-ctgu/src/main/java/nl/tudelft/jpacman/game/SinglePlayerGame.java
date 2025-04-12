package nl.tudelft.jpacman.game;

import com.google.common.collect.ImmutableList;
import nl.tudelft.jpacman.level.Level;
import nl.tudelft.jpacman.level.Player;

import java.util.List;

/**
 * 单人游戏的具体实现类，继承自抽象类Game。
 * 管理单个玩家与单个关卡的交互逻辑，提供必要的游戏状态访问方法。
 *
 * @author Jeroen Roosen
 */
public class SinglePlayerGame extends Game {

    /**
     * 当前游戏的玩家实例（如Pac-Man角色）。
     * 通过构造函数注入，生命周期与游戏实例绑定。
     */
    private final Player player;

    /**
     * 当前游戏的关卡实例，包含地图、豆子、幽灵等实体。
     * 通过构造函数注入，玩家会被注册到该关卡中。
     */
    private final Level level;

    /**
     * 构造一个单人游戏实例。
     * 注：玩家和关卡必须非空，玩家将自动注册到关卡中。
     *
     * @param player 玩家实例（非null）
     * @param level  关卡实例（非null）
     * @throws AssertionError 如果player或level为null
     */
    protected SinglePlayerGame(Player player, Level level) {
        assert player != null : "玩家实例不能为null";
        assert level != null : "关卡实例不能为null";

        this.player = player;
        this.level = level;
        this.level.registerPlayer(player); // 将玩家注册到关卡中，使关卡能跟踪玩家状态
    }

    /**
     * 获取当前游戏的所有玩家列表（单人游戏仅包含一个玩家）。
     * 返回的列表是不可变的，防止外部修改。
     *
     * @return 包含单个玩家的不可变列表
     */
    @Override
    public List<Player> getPlayers() {
        return ImmutableList.of(player);
    }

    /**
     * 获取当前游戏的关卡实例。
     * 用于委托关卡相关操作（如移动逻辑、胜负判断）。
     *
     * @return 当前关联的关卡对象
     */
    @Override
    public Level getLevel() {
        return level;
    }
}
