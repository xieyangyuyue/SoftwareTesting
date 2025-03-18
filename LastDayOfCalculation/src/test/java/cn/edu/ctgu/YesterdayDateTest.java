package cn.edu.ctgu;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class YesterdayDateTest {

    @Test
    void testRegularDay() {
        assertEquals("2025-05-19", YesterdayDate.getYesterday(2025, 5, 20));
    }

    @Test
    void testCrossTo30DayMonth() {
        assertEquals("2025-04-30", YesterdayDate.getYesterday(2025, 5, 1));
    }


    @Test
    void testCrossTo31DayMonth() {
        assertEquals("2025-07-31", YesterdayDate.getYesterday(2025, 8, 1));
    }

    @Test
    void testLeapYearFebruary() {
        assertEquals("2024-02-29", YesterdayDate.getYesterday(2024, 3, 1));
    }

    @Test
    void testNonLeapYearFebruary() {
        assertEquals("2025-02-28", YesterdayDate.getYesterday(2025, 3, 1));
    }

    @Test
    void testCrossYear() {
        assertEquals("2024-12-31", YesterdayDate.getYesterday(2025, 1, 1));
    }

    @Test
    void testEndOfDecember() {
        assertEquals("2025-12-30", YesterdayDate.getYesterday(2025, 12, 31));
    }

    @Test
    void testLeapYearEndOfFebruary() {
        assertEquals("2024-02-28", YesterdayDate.getYesterday(2024, 2, 29));
    }

    @Test
    void testNonLeapYearEndOfFebruary() {
        assertEquals("2025-02-27", YesterdayDate.getYesterday(2025, 2, 28));
    }

    // 新增测试：输入非法月份
    @Test
    void testInvalidMonth() {
        assertThrows(IllegalArgumentException.class, () ->
                        YesterdayDate.getYesterday(2025, 0, 1),
                "月份为0时应抛出异常"
        );
        assertThrows(IllegalArgumentException.class, () ->
                        YesterdayDate.getYesterday(2025, 13, 1),
                "月份为13时应抛出异常"
        );
    }

    // 新增测试：输入非法日期
    @Test
    void testInvalidDay() {
        // 非闰年2月最多28天
        assertThrows(IllegalArgumentException.class, () ->
                        YesterdayDate.getYesterday(2025, 2, 29),
                "2023年2月29日不存在"
        );
        // 4月只有30天
        assertThrows(IllegalArgumentException.class, () ->
                        YesterdayDate.getYesterday(2025, 4, 31),
                "4月31日不存在"
        );
    }

    // 新增测试：输入非法年份
    @Test
    void testInvalidYear() {
        assertThrows(IllegalArgumentException.class, () ->
                        YesterdayDate.getYesterday(-1, 1, 1),
                "年份不能为负数"
        );
    }

    // 新增测试：边界值检测（最小合法日期）
    @Test
    void testMinValidDate() {
        assertEquals("0-01-01", YesterdayDate.getYesterday(0, 1, 2));
        assertThrows(IllegalArgumentException.class, () ->
                        YesterdayDate.getYesterday(0, 1, 0),
                "日期不能为0"
        );
    }
}
