package nl.tudelft.jpacman.game;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.level.Level;
import nl.tudelft.jpacman.level.Level.LevelObserver;
import nl.tudelft.jpacman.level.Player;

import java.util.List;

/**
 * Pac-Man游戏的核心抽象类，管理游戏流程（开始/暂停/移动）和状态（胜利/失败）。
 * 实现LevelObserver接口以监听关卡事件（如关卡胜利或失败）。
 *
 * @author Jeroen Roosen
 */
public abstract class Game implements LevelObserver {

    /**
     * 游戏是否正在进行中的状态标志。
     * true表示游戏正在运行，false表示已暂停或未开始。
     */
    private boolean inProgress;

    /**
     * 用于同步start()和stop()方法的锁对象，确保线程安全。
     * 防止多线程环境下游戏状态的不一致。
     */
    private final Object progressLock = new Object();

    /**
     * 构造Game实例，初始化游戏状态为未开始。
     * 注：由于是抽象类，构造函数设为protected，限制直接实例化。
     */
    protected Game() {
        inProgress = false;
    }

    /**
     * 启动或恢复游戏。
     * 需满足以下条件才会启动：
     * 1. 游戏未在运行中；
     * 2. 至少一个玩家存活；
     * 3. 关卡中仍有未被吃掉的豆子。
     * 方法内部使用同步锁保证线程安全。
     */
    public void start() {
        synchronized (progressLock) {
            if (isInProgress()) {
                return; // 已在运行中，直接返回
            }
            // 检查关卡是否可继续
            if (getLevel().isAnyPlayerAlive() && getLevel().remainingPellets() > 0) {
                inProgress = true;
                getLevel().addObserver(this); // 注册为关卡观察者
                getLevel().start();          // 启动关卡逻辑（如NPC移动）
            }
        }
    }

    /**
     * 暂停游戏。
     * 方法内部使用同步锁保证线程安全。
     */
    public void stop() {
        synchronized (progressLock) {
            if (!isInProgress()) {
                return; // 未在运行中，直接返回
            }
            inProgress = false;
            getLevel().stop(); // 停止关卡逻辑（如NPC移动）
        }
    }

    /**
     * 获取当前游戏是否正在进行中。
     *
     * @return true表示游戏正在运行，false表示已暂停或未开始
     */
    public boolean isInProgress() {
        return inProgress;
    }

    /**
     * 获取当前游戏的所有玩家列表（不可变）。
     * 具体实现由子类提供。
     *
     * @return 玩家的不可变列表
     */
    public abstract List<Player> getPlayers();

    /**
     * 获取当前正在进行的关卡。
     * 具体实现由子类提供。
     *
     * @return 当前关卡实例
     */
    public abstract Level getLevel();

    /**
     * 尝试将指定玩家向某方向移动一格。
     * 仅在游戏进行中时执行移动操作。
     *
     * @param player    要移动的玩家
     * @param direction 移动方向
     */
    public void move(Player player, Direction direction) {
        if (isInProgress()) {
            getLevel().move(player, direction); // 委托给关卡处理移动逻辑
        }
    }

    /**
     * 当关卡被胜利完成时触发，停止游戏。
     */
    @Override
    public void levelWon() {
        stop();
    }

    /**
     * 当关卡失败（所有玩家死亡）时触发，停止游戏。
     */
    @Override
    public void levelLost() {
        stop();
    }
}
