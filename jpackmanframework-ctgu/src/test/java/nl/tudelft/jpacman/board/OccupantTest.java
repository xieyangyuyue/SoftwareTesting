package nl.tudelft.jpacman.board;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 测试 {@link Unit} 类在占据/离开 {@link Square} 时的正确性。
 * 验证单位与方块的交互逻辑是否符合预期。
 *
 * @author Jeroen Roosen
 */
class OccupantTest {


    /**
     * 被测试的 Unit 实例（可能为 Pacman 或幽灵的测试实现）。
     */
    private Unit unit;

    /**
     * 在每个测试方法执行前重置被测试的 Unit。
     * 使用 @BeforeEach 确保测试独立性。
     */
    @BeforeEach
    void setUp() {
        unit = new BasicUnit(); // BasicUnit 是 Unit 的简单测试实现
    }

    /**
     * 测试初始状态：新创建的 Unit 不应关联任何 Square。
     * 验证 hasSquare() 返回 false。
     */
    @Test
    void noStartSquare() {
        assertThat(unit).isNotNull();       // 非空检查
        assertThat(unit.hasSquare()).isFalse(); // 初始未占据任何方块
    }

    /**
     * 测试正常占据逻辑：
     * 1. 调用 occupy() 后，Unit 应关联目标 Square
     * 2. 目标 Square 的占据者列表应包含此 Unit
     */
    @Test
    void testOccupy() {
        Square target = new BasicSquare(); // 基础 Square 实现
        unit.occupy(target);


        // 验证 Unit 的当前方块是同一个实例
        assertThat(unit.getSquare()).isSameAs(target);


        // 验证方块的占用者列表包含该 Unit 实例
        assertThat(target.getOccupants()).contains(unit);
    }

    /**
     * 测试重复占据同一方块的场景：
     * 1. 第一次占据后状态正确
     * 2. 再次占据同一方块时，应保持一致性
     * 3. 方块的占据者列表不应重复添加 Unit
     */
    @Test
    void testReoccupy() {
        Square square = new BasicSquare();

        // 第一次占据
        unit.occupy(square);
        assertThat(unit.getSquare()).isEqualTo(square);
        assertThat(square.getOccupants()).contains(unit);

        // 重复占据同一方块
        unit.occupy(square);

        // 验证方块引用未改变
        assertThat(unit.getSquare()).isEqualTo(square);
        // 验证占据者列表仅包含一次（避免重复添加）
        assertThat(square.getOccupants()).containsExactly(unit);
    }
}
