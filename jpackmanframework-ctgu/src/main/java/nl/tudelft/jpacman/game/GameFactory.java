package nl.tudelft.jpacman.game;

import nl.tudelft.jpacman.level.Level;
import nl.tudelft.jpacman.level.PlayerFactory;

/**
 * 游戏工厂类，负责创建特定类型的游戏实例（如单人游戏）。
 * 封装游戏对象的创建逻辑，遵循工厂设计模式，提高代码扩展性和可维护性。
 *
 * @author Jeroen Roosen
 */
public class GameFactory {

    /**
     * 玩家工厂对象，用于创建玩家实例（如Pac-Man角色）。
     * 通过依赖注入的方式与GameFactory绑定，解耦玩家创建逻辑。
     */
    private final PlayerFactory playerFactory;

    /**
     * 构造游戏工厂实例，绑定指定的玩家工厂。
     *
     * @param playerFactory 用于创建玩家的工厂对象（非null）
     */
    public GameFactory(PlayerFactory playerFactory) {
        this.playerFactory = playerFactory;
    }

    /**
     * 创建一个单人游戏实例。
     * 通过玩家工厂生成Pac-Man角色，并与指定关卡绑定。
     *
     * @param level 游戏关卡对象，包含地图、豆子、幽灵等配置
     * @return 初始化完成的单人游戏实例（具体为SinglePlayerGame）
     */
    public Game createSinglePlayerGame(Level level) {
        // 创建Pac-Man玩家，并与关卡绑定
        return new SinglePlayerGame(playerFactory.createPacMan(), level);
    }

    /**
     * 获取当前绑定的玩家工厂。
     * 通常用于子类扩展或需要访问玩家创建逻辑的场景。
     *
     * @return 关联的PlayerFactory实例
     */
    protected PlayerFactory getPlayerFactory() {
        return playerFactory;
    }
}
