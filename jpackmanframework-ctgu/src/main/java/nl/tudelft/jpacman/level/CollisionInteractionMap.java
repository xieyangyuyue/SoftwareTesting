package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.board.Unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 碰撞交互映射表，实现碰撞处理逻辑的核心类。
 * 通过注册不同类型的碰撞处理器（CollisionHandler），管理游戏中各种单位（Unit）间的碰撞响应。
 * 支持双向对称碰撞处理和继承关系匹配，自动选择最具体的碰撞处理器。
 *
 * @author Michael de Jong
 * @author Jeroen Roosen
 */
public class CollisionInteractionMap implements CollisionMap {

    /**
     * 碰撞处理器双层映射表结构：
     * 外层Map键：碰撞发起者（Collider）的类类型
     * 内层Map键：被碰撞者（Collidee）的类类型
     * 值：对应的碰撞处理器实例
     */
    private final Map<Class<? extends Unit>,
        Map<Class<? extends Unit>, CollisionHandler<?, ?>>> handlers;

    /**
     * 构造空碰撞映射表。
     */
    public CollisionInteractionMap() {
        this.handlers = new HashMap<>();
    }

    /**
     * 注册双向对称的碰撞处理器（自动处理A-B和B-A两种碰撞情况）。
     *
     * @param collider 碰撞发起者类型（如Player.class）
     * @param collidee 被碰撞者类型（如Ghost.class）
     * @param handler  碰撞处理器实例
     * @param <C1>     碰撞发起者泛型类型
     * @param <C2>     被碰撞者泛型类型
     */
    public <C1 extends Unit, C2 extends Unit> void onCollision(
        Class<C1> collider, Class<C2> collidee, CollisionHandler<C1, C2> handler) {
        onCollision(collider, collidee, true, handler);
    }

    /**
     * 注册碰撞处理器，可指定是否对称处理。
     *
     * @param collider  碰撞发起者类型
     * @param collidee  被碰撞者类型
     * @param symmetric 是否自动生成反向处理器
     * @param handler   原始碰撞处理器
     */
    public <C1 extends Unit, C2 extends Unit> void onCollision(
        Class<C1> collider, Class<C2> collidee, boolean symmetric,
        CollisionHandler<C1, C2> handler) {
        addHandler(collider, collidee, handler);
        if (symmetric) {
            // 创建反向处理器交换碰撞双方位置
            addHandler(collidee, collider, new InverseCollisionHandler<>(handler));
        }
    }

    /**
     * 将处理器添加到映射表中。
     *
     * @param collider 碰撞发起者类型
     * @param collidee 被碰撞者类型
     * @param handler  处理器实例
     */
    private void addHandler(Class<? extends Unit> collider,
                            Class<? extends Unit> collidee, CollisionHandler<?, ?> handler) {
        handlers.computeIfAbsent(collider, k -> new HashMap<>())
            .put(collidee, handler);
    }

    /**
     * 处理两个单位的碰撞事件，自动匹配最具体的处理器。
     *
     * @param collider 碰撞发起者实例（如玩家）
     * @param collidee 被碰撞者实例（如幽灵）
     */
    @SuppressWarnings("unchecked")
    @Override
    public <C1 extends Unit, C2 extends Unit> void collide(C1 collider, C2 collidee) {
        // 获取collider的最匹配类型
        Class<? extends Unit> colliderKey = getMostSpecificClass(handlers, collider.getClass());
        if (colliderKey == null) return;

        // 获取collidee的最匹配类型
        Map<Class<? extends Unit>, CollisionHandler<?, ?>> map = handlers.get(colliderKey);
        Class<? extends Unit> collideeKey = getMostSpecificClass(map, collidee.getClass());
        if (collideeKey == null) return;

        // 执行匹配到的处理器
        CollisionHandler<C1, C2> handler = (CollisionHandler<C1, C2>) map.get(collideeKey);
        if (handler != null) {
            handler.handleCollision(collider, collidee);
        }
    }

    /**
     * 在映射表中查找最具体的匹配类型（考虑继承层次）。
     *
     * @param map 要搜索的映射表
     * @param key 要匹配的具体类型
     * @return 映射表中存在的最近父类或接口类型
     */
    private Class<? extends Unit> getMostSpecificClass(
        Map<Class<? extends Unit>, ?> map, Class<? extends Unit> key) {
        for (Class<? extends Unit> type : getInheritance(key)) {
            if (map.containsKey(type)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 获取类型继承链（包含自身、所有父类及实现的接口）。
     *
     * @param clazz 要分析的类类型
     * @return 继承链列表，按从具体到抽象的顺序排列
     */
    @SuppressWarnings("unchecked")
    private List<Class<? extends Unit>> getInheritance(Class<? extends Unit> clazz) {
        List<Class<? extends Unit>> inheritance = new ArrayList<>();
        inheritance.add(clazz); // 首先添加自身

        int index = 0;
        while (index < inheritance.size()) {
            Class<?> current = inheritance.get(index++);

            // 添加直接父类
            Class<?> superClass = current.getSuperclass();
            if (superClass != null && Unit.class.isAssignableFrom(superClass)) {
                inheritance.add((Class<? extends Unit>) superClass);
            }

            // 添加实现接口
            for (Class<?> iface : current.getInterfaces()) {
                if (Unit.class.isAssignableFrom(iface)) {
                    inheritance.add((Class<? extends Unit>) iface);
                }
            }
        }
        return inheritance;
    }

    /**
     * 碰撞处理器功能接口。
     */
    public interface CollisionHandler<C1 extends Unit, C2 extends Unit> {
        /**
         * 处理碰撞事件的具体逻辑。
         */
        void handleCollision(C1 collider, C2 collidee);
    }

    /**
     * 反向碰撞处理器包装类，用于实现对称碰撞处理。
     */
    private static class InverseCollisionHandler<C1 extends Unit, C2 extends Unit>
        implements CollisionHandler<C1, C2> {

        private final CollisionHandler<C2, C1> handler;

        InverseCollisionHandler(CollisionHandler<C2, C1> handler) {
            this.handler = handler;
        }

        @Override
        public void handleCollision(C1 collider, C2 collidee) {
            // 交换碰撞双方位置调用原始处理器
            handler.handleCollision(collidee, collider);
        }
    }
}
