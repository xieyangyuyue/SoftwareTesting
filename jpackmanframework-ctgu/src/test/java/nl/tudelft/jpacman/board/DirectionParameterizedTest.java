package nl.tudelft.jpacman.board;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 针对 Direction 枚举的参数化测试类。
 * 验证每个方向的 deltaX 和 deltaY 是否正确，并检查枚举值的完整性。
 */
public class DirectionParameterizedTest {

    /**
     * 参数化测试方法：验证每个 Direction 的 deltaX 和 deltaY 是否符合预期。
     *
     * @param direction      被测试的 Direction 枚举实例
     * @param expectedDeltaX 期望的 x 坐标变化值
     * @param expectedDeltaY 期望的 y 坐标变化值
     */
    @ParameterizedTest
    @MethodSource("directionProvider")
    // 指定参数来源方法
    void testDirection(Direction direction, int expectedDeltaX, int expectedDeltaY) {
        // 断言 deltaX 是否符合预期
        assertThat(direction.getDeltaX()).isEqualTo(expectedDeltaX);
        // 断言 deltaY 是否符合预期
        assertThat(direction.getDeltaY()).isEqualTo(expectedDeltaY);
    }

    /**
     * 提供测试数据的静态方法。
     * 返回一个包含所有 Direction 实例及其预期坐标变化的流。
     *
     * @return Stream<Arguments> 参数流，每个参数对应一个测试用例
     */
    private static Stream<Arguments> directionProvider() {
        return Stream.of(
            // 每个 Arguments.of 对应一个测试用例：
            // 参数1: Direction 实例
            // 参数2: 预期 deltaX
            // 参数3: 预期 deltaY
            Arguments.of(Direction.NORTH, 0, -1),  // 北方向：x不变，y减1
            Arguments.of(Direction.SOUTH, 0, 1),   // 南方向：x不变，y增1
            Arguments.of(Direction.WEST, -1, 0),   // 西方向：x减1，y不变
            Arguments.of(Direction.EAST, 1, 0)     // 东方向：x增1，y不变
        );
    }

    /**
     * 测试 Direction 枚举的 values() 方法。
     * 验证枚举值的数量和顺序是否正确。
     */
    @Test
    void testEnumValues() {
        // 获取所有 Direction 枚举值
        Direction[] values = Direction.values();
        // 断言枚举值数量为4，且顺序为 NORTH, SOUTH, WEST, EAST
        assertThat(values)
            .containsExactly(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST);
    }
}
