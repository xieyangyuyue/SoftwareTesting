package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.npc.Ghost;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 表示 Pac-Man 游戏的一个关卡，管理游戏板、玩家、NPC（幽灵）的移动逻辑，以及游戏状态。
 * 负责处理玩家/NPC移动、碰撞检测、胜负判断，并通过观察者模式通知状态变化。
 *
 * @author Jeroen Roosen
 */
@SuppressWarnings("PMD.TooManyMethods")
public class Level {

    /**
     * 当前关卡的游戏地图。
     */
    private final Board board;

    /**
     * 移动操作同步锁，确保玩家和NPC的移动操作原子性。
     */
    private final Object moveLock = new Object();

    /**
     * 启动/停止操作同步锁，防止状态冲突。
     */
    private final Object startStopLock = new Object();

    /**
     * NPC（幽灵）与其调度服务的映射表。
     * Key: 幽灵实例
     * Value: 控制幽灵周期性移动的调度服务（null表示未启动）
     */
    private final Map<Ghost, ScheduledExecutorService> npcs;

    /**
     * 当前关卡是否正在进行中（true表示玩家/NPC可移动）。
     */
    private boolean inProgress;

    /**
     * 玩家的起始位置列表，支持多个起始点轮换分配。
     */
    private final List<Square> startSquares;

    /**
     * 当前使用的起始位置索引（循环递增）。
     */
    private int startSquareIndex;

    /**
     * 当前关卡注册的玩家列表。
     */
    private final List<Player> players;

    /**
     * 碰撞处理器，处理单位间的碰撞事件（如玩家与幽灵、豆子）。
     */
    private final CollisionMap collisions;

    /**
     * 关卡观察者集合，用于通知关卡胜利/失败事件。
     */
    private final Set<LevelObserver> observers;

    /**
     * 构造一个游戏关卡实例。
     *
     * @param board          关卡地图（非null）
     * @param ghosts         NPC（幽灵）列表（非null）
     * @param startPositions 玩家起始位置列表（非null且非空）
     * @param collisionMap   碰撞处理映射表（非null）
     */
    public Level(Board board, List<Ghost> ghosts, List<Square> startPositions,
                 CollisionMap collisionMap) {
        assert board != null;
        assert ghosts != null;
        assert startPositions != null;

        this.board = board;
        this.inProgress = false;
        this.npcs = new HashMap<>();
        for (Ghost ghost : ghosts) {
            npcs.put(ghost, null); // 初始时NPC未激活
        }
        this.startSquares = startPositions;
        this.startSquareIndex = 0;
        this.players = new ArrayList<>();
        this.collisions = collisionMap;
        this.observers = new HashSet<>();
    }

    /**
     * 添加关卡观察者。
     *
     * @param observer 观察者实例（如Game对象）
     */
    public void addObserver(LevelObserver observer) {
        observers.add(observer);
    }

    /**
     * 移除关卡观察者。
     *
     * @param observer 要移除的观察者实例
     */
    public void removeObserver(LevelObserver observer) {
        observers.remove(observer);
    }

    /**
     * 注册玩家到当前关卡，分配起始位置。
     * 若玩家已注册则忽略。起始位置按列表顺序循环分配。
     *
     * @param player 玩家实例（非null）
     */
    public void registerPlayer(Player player) {
        assert player != null;
        assert !startSquares.isEmpty();

        if (players.contains(player)) {
            return; // 避免重复注册
        }
        players.add(player);
        Square square = startSquares.get(startSquareIndex);
        player.occupy(square);    // 将玩家放置到起始位置
        startSquareIndex = (startSquareIndex + 1) % startSquares.size(); // 循环索引
    }

    /**
     * 获取当前关卡的地图。
     *
     * @return 关卡地图实例
     */
    public Board getBoard() {
        return board;
    }

    /**
     * 移动指定单位到给定方向，处理碰撞事件。
     * 仅在游戏进行中时执行，移动操作原子化。
     *
     * @param unit      要移动的单位（玩家或NPC，非null）
     * @param direction 移动方向（非null）
     */
    public void move(Unit unit, Direction direction) {
        assert unit != null;
        assert direction != null;
        assert unit.hasSquare();

        if (!isInProgress()) {
            return; // 游戏未进行时不处理
        }

        synchronized (moveLock) { // 确保移动原子性
            unit.setDirection(direction);
            Square current = unit.getSquare();
            Square target = current.getSquareAt(direction);

            if (target.isAccessibleTo(unit)) { // 检查目标方块是否可达
                List<Unit> occupants = target.getOccupants();
                unit.occupy(target); // 移动单位到目标方块
                // 处理与目标方块内所有单位的碰撞
                for (Unit occupant : occupants) {
                    collisions.collide(unit, occupant);
                }
            }
            updateObservers(); // 更新游戏状态并通知观察者
        }
    }

    /**
     * 启动关卡，允许移动并激活NPC自动移动。
     */
    public void start() {
        synchronized (startStopLock) {
            if (isInProgress()) return;
            startNPCs();    // 启动所有NPC调度
            inProgress = true;
            updateObservers();
        }
    }

    /**
     * 停止关卡，禁止移动并停止NPC。
     */
    public void stop() {
        synchronized (startStopLock) {
            if (!isInProgress()) return;
            stopNPCs();     // 停止所有NPC调度
            inProgress = false;
        }
    }

    /**
     * 启动所有NPC的周期性移动任务。
     */
    private void startNPCs() {
        for (final Ghost ghost : npcs.keySet()) {
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            // 初始延迟为间隔的一半，避免NPC同时移动
            scheduler.schedule(new NpcMoveTask(scheduler, ghost),
                ghost.getInterval() / 2, TimeUnit.MILLISECONDS);
            npcs.put(ghost, scheduler);
        }
    }

    /**
     * 停止所有NPC移动任务并释放资源。
     */
    private void stopNPCs() {
        for (Entry<Ghost, ScheduledExecutorService> entry : npcs.entrySet()) {
            entry.getValue().shutdownNow(); // 立即终止任务
        }
    }

    /**
     * 检查关卡是否进行中。
     *
     * @return true表示进行中，false表示已停止
     */
    public boolean isInProgress() {
        return inProgress;
    }

    /**
     * 更新观察者状态：检查胜负条件并触发通知。
     */
    private void updateObservers() {
        if (!isAnyPlayerAlive()) {
            observers.forEach(LevelObserver::levelLost); // 玩家全灭触发失败
        } else if (remainingPellets() == 0) {
            observers.forEach(LevelObserver::levelWon);  // 豆子吃光触发胜利
        }
    }

    /**
     * 检查是否有玩家存活。
     *
     * @return true表示至少一个玩家存活
     */
    public boolean isAnyPlayerAlive() {
        return players.stream().anyMatch(Player::isAlive);
    }

    /**
     * 计算地图中剩余的豆子数量。
     *
     * @return 剩余豆子数（>=0）
     */
    public int remainingPellets() {
        int count = 0;
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                for (Unit unit : board.squareAt(x, y).getOccupants()) {
                    if (unit instanceof Pellet) count++;
                }
            }
        }
        assert count >= 0;
        return count;
    }

    //------------------------ 内部类 ------------------------

    /**
     * NPC移动任务，周期性执行幽灵移动并重新调度自身。
     */
    private final class NpcMoveTask implements Runnable {
        private final ScheduledExecutorService scheduler;
        private final Ghost ghost;

        /**
         * @param scheduler 任务调度器
         * @param ghost     要移动的幽灵实例
         */
        NpcMoveTask(ScheduledExecutorService scheduler, Ghost ghost) {
            this.scheduler = scheduler;
            this.ghost = ghost;
        }

        @Override
        public void run() {
            Direction direction = ghost.nextMove();
            if (direction != null) {
                move(ghost, direction); // 执行移动
            }
            // 重新调度任务，间隔由幽灵的移动速度决定
            scheduler.schedule(this, ghost.getInterval(), TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 关卡观察者接口，定义关卡状态变化的回调。
     */
    public interface LevelObserver {
        /**
         * 关卡胜利回调（所有豆子被吃掉）。
         */
        void levelWon();

        /**
         * 关卡失败回调（所有玩家死亡）。
         */
        void levelLost();
    }
}
