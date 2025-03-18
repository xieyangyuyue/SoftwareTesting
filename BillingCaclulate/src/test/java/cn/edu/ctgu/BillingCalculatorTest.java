package cn.edu.ctgu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 测试BillingCalculator类的计费逻辑，包括基础费率、分段费率、夏令时转换和时间进位规则.
 */
class BillingCalculatorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(BillingCalculatorTest.class);
    private final BillingCalculator calculator = new BillingCalculator();
    private static final ZoneId ZONE = ZoneId.of("America/New_York");

    private static final int MONTH_JUNE = 6;
    private static final int HOUR_29 = 29;
    private static final int MINUTE_20 = 20;
    private static final int SECONDS_59 = 59;
    private static final double DELTA = 0.001;


    /**
     * 测试基础费率的计费逻辑（通话时长≤20分钟）.
     */
    @Nested
    @DisplayName("基础费率（≤20分钟）")
    class BaseRateTests {
        private static final int YEAR = 2025;
        private static final int MONTH_MAY = 5;
        private static final int DAY = 12;
        private static final int HOUR = 12;
        private static final int MINUTE = 59;
        private static final double BASE_RATE = 0.05;


        /**
         * 验证1分钟通话按基础费率计费.
         */
        @Test
        @DisplayName("1分钟通话")
        void test1MinuteCall() {
            LOGGER.info("1分钟通话");
            ZonedDateTime start = ZonedDateTime.of(YEAR, MONTH_JUNE, DAY, HOUR, MINUTE, 0, 0, ZONE);
            ZonedDateTime end = start.plusSeconds(SECONDS_59);
            LOGGER.debug("Start: {}, End: {}", start, end);

            int duration = calculator.calculateAdjustedDuration(start, end);
            LOGGER.debug("Adjusted duration: {} minutes", duration);
            assertEquals(1, duration, "应向上取整到1分钟");
            assertEquals(BASE_RATE, calculator.calculateCharge(duration), "费用应为0.05美元");
        }

        /**
         * 验证20分钟通话按基础费率计费.
         */
        @Test
        @DisplayName("精确20分钟通话")
        void testExactly20Minutes() {
            LOGGER.info("测试精确20分钟通话");
            ZonedDateTime start = ZonedDateTime.of(YEAR, MONTH_MAY, DAY, HOUR, 0, 0, 0, ZONE);
            ZonedDateTime end = start.plusMinutes(MINUTE_20);
            LOGGER.debug("Start: {}, End: {}", start, end);

            int duration = calculator.calculateAdjustedDuration(start, end);
            LOGGER.debug("Adjusted duration: {} minutes", duration);
            assertEquals(MINUTE_20, duration, "应为精确20分钟");
            assertEquals(1, calculator.calculateCharge(duration), DELTA, "费用应为1.00美元");
        }
    }

    /**
     * 测试分段费率的计费逻辑（通话时长>20分钟）.
     */
    @Nested
    @DisplayName("分段费率（>20分钟）")
    class TieredRateTests {
        private static final double TIERED_RATE = 0.10;
        private static final double TIERED_RATE_1 = 1.10;

        private static final int YEAR = 2025;
        private static final int MONTH_6 = 6;
        private static final int MONTH_7 = 7;
        private static final int MINUTE_21 = 21;
        private static final int MINUTE_59 = 59;
        private static final int HOUR = 12;
        private static final int HOUR_20 = 20;
        private static final int MINUTES_1800 = 1800;
        private static final int DURATION_21_MINUTES = 21;

        /**
         * 验证21分钟通话按分段费率计费.
         */
        @Test
        @DisplayName("21分钟通话（费率切换边界）")
        void test21MinutesCall() {
            LOGGER.info("21分钟通话（费率切换边界）");
            ZonedDateTime start = ZonedDateTime.of(YEAR, MONTH_6, 1, HOUR, 0, 0, 0, ZONE);
            ZonedDateTime end = start.plusMinutes(MINUTE_21);
            LOGGER.debug("Start (EST): {}, End (EDT): {}", start, end);

            int duration = calculator.calculateAdjustedDuration(start, end);
            assertEquals(DURATION_21_MINUTES, duration, "21分钟通话（费率切换边界）");
            assertEquals(TIERED_RATE_1, calculator.calculateCharge(duration), DELTA, "费用应为1.10美元");
        }

        /**
         * 验证30小时通话按分段费率计费.
         */
        @Test
        @DisplayName("30小时通话（最大允许时长）")
        void testMaxDurationCall() {
            LOGGER.info("测试超长通话（接近30小时）");
            ZonedDateTime start = ZonedDateTime.of(YEAR, MONTH_7, 1, HOUR, 0, 0, 0, ZONE);
            ZonedDateTime end = start.plusHours(HOUR_29).plusMinutes(MINUTE_59).plusSeconds(SECONDS_59);
            LOGGER.debug("Start: {}, End: {}", start, end);

            int duration = calculator.calculateAdjustedDuration(start, end);
            LOGGER.debug("Adjusted duration: {} minutes", duration);
            assertEquals(MINUTES_1800, duration, "29小时59分59秒应进位到30小时");
            double expectedCharge = 1.00 + TIERED_RATE * (duration - HOUR_20);
            assertEquals(expectedCharge, calculator.calculateCharge(duration), DELTA);
        }
    }

    /**
     * 测试夏令时转换期间的计费处理.
     */

    @Nested
    @DisplayName("夏令时转换场景")
    class DSTTests {

        /**
         * 验证春季夏令时转换期间的计费.
         */
        private static final int MINUTES_30 = 30;
        private static final int MINUTES_60 = 60;
        private static final int MINUTES_150 = 150;
        private static final double TIERED_RATE_5 = 5.00;
        private static final double TIERED_RATE_14 = 14.00;

        /**
         * 验证春季夏令时转换期间的计费.
         */
        @Test
        @DisplayName("春季转换")
        void testSpringDSTWithCorrectDate() {
            LOGGER.info("测试春季夏令时转换（丢失1小时）");
            // 时间线：01:30 EST → 03:30 EDT（实际物理时间1小时）
            ZonedDateTime start = ZonedDateTime.parse("2025-03-12T01:30:00-05:00");
            ZonedDateTime end = ZonedDateTime.parse("2025-03-12T03:30:00-04:00");
            LOGGER.debug("Start (EST): {}, End (EDT): {}", start, end);

            int duration = calculator.calculateAdjustedDuration(start, end);
            LOGGER.debug("Adjusted duration: {} minutes", duration);
            assertEquals(MINUTES_60, duration, "应计算实际1小时");
            assertEquals(TIERED_RATE_5, calculator.calculateCharge(duration), DELTA, "费用应为5.00美元");
        }

        /**
         * 验证秋季转换内重复时段通话时转换期间的计费.
         */
        @Test
        @DisplayName("秋季转换内重复时段通话")
        void testCallWithinFallDSTOverlap() {
            LOGGER.info("测试秋季夏令时转换（重复1小时）");
            // 时间线：01:30 EDT → 本地时间02:30（实际物理时间2.5小时）
            ZonedDateTime start = ZonedDateTime.parse("2025-11-05T01:30:00-04:00");
            // 保持本地时间连续性
            ZonedDateTime end = start.plusHours(1).plusMinutes(MINUTES_30).withZoneSameLocal(ZONE);
            LOGGER.debug("Start (EDT): {}, End (EST): {}", start, end);

            int duration = calculator.calculateAdjustedDuration(start, end);
            LOGGER.debug("Adjusted duration: {} minutes", duration);
            assertEquals(MINUTES_150, duration, "应计算实际2.5小时");
            assertEquals(TIERED_RATE_14, calculator.calculateCharge(duration), DELTA, "费用应为14.00美元");
        }
    }


    /**
     * 时间进位规则测试组.
     */
    @Nested
    @DisplayName("时间进位测试")
    class RoundingTests {
        private static final int YEAR = 2025;
        private static final int HOUR = 12;
        private static final int MINUTES = 10;
        private static final int SECONDS = 30;
        private static final double TIERED_RATE = 0.05;
        private static final double TIERED_RATE_1 = 0.5;


        /**
         * 测试短时通话秒数进位规则.
         */

        @Test
        @DisplayName("59秒进位到1分钟")
        void test59SecondsRounding() {
            LOGGER.info("测试短时通话秒数进位规则");
            ZonedDateTime start = ZonedDateTime.of(YEAR, MONTH_JUNE, 1, HOUR, 0, 0, 0, ZONE);
            ZonedDateTime end = start.plusSeconds(SECONDS); // 30秒通话
            LOGGER.debug("Start: {}, End: {}", start, end);

            int duration = calculator.calculateAdjustedDuration(start, end);
            LOGGER.debug("Adjusted duration: {} minutes", duration);
            assertEquals(1, duration, "30秒应进位到1分钟");
            assertEquals(TIERED_RATE, calculator.calculateCharge(duration), DELTA, "费用应为0.05美元");
        }

        /**
         * "0秒不进位.
         */
        @Test
        @DisplayName("0秒不进位")
        void testExactMinute() {
            LOGGER.info("0秒不进位");
            ZonedDateTime start = ZonedDateTime.of(YEAR, MONTH_JUNE, 1, HOUR, 0, 0, 0, ZONE);
            ZonedDateTime end = start.plusMinutes(MINUTES);
            LOGGER.debug("Start: {}, End: {}", start, end);

            int duration = calculator.calculateAdjustedDuration(start, end);
            LOGGER.debug("Adjusted duration: {} minutes", duration);
            assertEquals(MINUTES, duration);
            assertEquals(TIERED_RATE_1, calculator.calculateCharge(duration), DELTA, "费用应为0.5美元");
        }
    }
}