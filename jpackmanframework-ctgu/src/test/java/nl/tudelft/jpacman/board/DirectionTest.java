package nl.tudelft.jpacman.board;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A very simple (and not particularly useful)
 * test class to have a starting point where to put tests.
 *
 * @author Arie van Deursen
 */
public class DirectionTest {
    /**
     * Do we get the correct delta when moving north?
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectionTest.class);

    @Test
    void testNorth() {
        Direction north = Direction.NORTH;
        LOGGER.debug("north getDeltaX:{},north getDeltaY :{}", north.getDeltaX(), north.getDeltaY());
        assertThat(north.getDeltaX()).isEqualTo(0);
        assertThat(north.getDeltaY()).isEqualTo(-1);
    }

    @Test
    void testSouth() {
        Direction south = Direction.SOUTH;
        assertThat(south.getDeltaX()).isEqualTo(0);
        assertThat(south.getDeltaY()).isEqualTo(1);
    }

    @Test
    void testWest() {
        Direction west = Direction.WEST;
        assertThat(west.getDeltaX()).isEqualTo(-1);
        assertThat(west.getDeltaY()).isEqualTo(0);
    }

    @Test
    void testEast() {
        Direction east = Direction.EAST;
        assertThat(east.getDeltaX()).isEqualTo(1);
        assertThat(east.getDeltaY()).isEqualTo(0);
    }

    @Test
    void testEnumValuesCount() {
        Direction[] values = Direction.values();

        assertThat(values).hasSize(4); // 确保只有四个方向
    }
}
