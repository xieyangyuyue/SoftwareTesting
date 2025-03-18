package cn.edu.ctgu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 处理通话计费逻辑，包含时长计算和费用计算功能.
 * 该类提供基于调整后通话时长（考虑夏令时和取整规则）的费用计算功能，
 * 支持基础费率和分段费率两种计费模式。
 */
public class BillingCalculator {
    private static final Logger LOGGER = LoggerFactory.getLogger(BillingCalculator.class);

    private static final int CEILING_ADJUSTMENT = 59;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int BASE_MINUTES_LIMIT = 20;
    private static final double BASE_RATE = 0.05;
    private static final double EXTRA_RATE = 0.10;
    private static final double BASE_FEE = 1.00;
    private static final int MINIMUM_BILLABLE_MINUTES = 1;

    /**
     * 计算调整后的通话时长（分钟），考虑夏令时和向上取整规则.
     * 实际计算时会进行以下处理：
     * 1. 计算精确到秒的时间差
     * 2. 向上取整到分钟（例如30秒→1分钟，61秒→2分钟）
     * 3. 保证至少计费1分钟
     *
     * @param start 通话开始时间（带时区）
     * @param end   通话结束时间（带时区）
     * @return 调整后的计费分钟数（至少1分钟）
     */
    public int calculateAdjustedDuration(ZonedDateTime start, ZonedDateTime end) {
        long seconds = ChronoUnit.SECONDS.between(start, end);
        LOGGER.debug("时间差计算: [{}] 到 [{}] -> {} 秒", start, end, seconds);

        long totalMinutes = (seconds + CEILING_ADJUSTMENT) / SECONDS_PER_MINUTE;
        LOGGER.debug("向上取整后分钟数: {} 分钟", totalMinutes);

        int adjustedMinutes = (int) Math.max(totalMinutes, MINIMUM_BILLABLE_MINUTES);
        LOGGER.info("最终计费时长: {} 分钟", adjustedMinutes);
        return adjustedMinutes;
    }

    /**
     * 根据调整后的通话时长计算费用.
     * 使用分段计费策略：
     * 1. 20分钟及以下：0.05美元/分钟
     * 2. 超过20分钟：1美元基础费 + 超时部分0.10美元/分钟
     *
     * @param adjustedMinutes 调整后的通话分钟数
     * @return 计算出的费用（美元）
     */
    public double calculateCharge(int adjustedMinutes) {
        LOGGER.debug("计算费用，时长: {} 分钟", adjustedMinutes);

        double charge;
        if (adjustedMinutes <= BASE_MINUTES_LIMIT) {
            charge = BASE_RATE * adjustedMinutes;
            LOGGER.trace("基础费率: {}$", charge);
        } else {
            double extraCharge = EXTRA_RATE * (adjustedMinutes - BASE_MINUTES_LIMIT);
            charge = BASE_FEE + extraCharge;
            LOGGER.trace("分段费率: 基础1$ + 超时部分{}$ = {}$", extraCharge, charge);
        }

        LOGGER.info("最终费用: {}$", charge);
        return charge;
    }
}