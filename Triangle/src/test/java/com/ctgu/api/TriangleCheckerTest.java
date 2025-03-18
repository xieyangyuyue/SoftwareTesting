package com.ctgu.api;

import com.ctgu.api.testcases.TriangleChecker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class TriangleCheckerTest {
    private static final Logger logger = LoggerFactory.getLogger(TriangleCheckerTest.class);

    // 参数不足时抛出异常
    @ParameterizedTest
    @DisplayName("参数不足时抛出异常")
//    @ValueSource(strings = {"1", "1,2", "1,2,3,4"})
    @CsvFileSource(resources = "/InsufficientParameters.csv")
    public void testParseAndValidate_InsufficientParameters(String input) {
        String[] args = input.split(",");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> TriangleChecker.parseAndValidate(args)
        );

        logger.info("InsufficientParameters: {} Catch exception: {}", args, exception.getMessage());
        assertEquals("参数不足", exception.getMessage());
    }


    // 非整数参数时抛出异常
    @ParameterizedTest
    @DisplayName("非整数参数时抛出异常")
    @CsvSource({
            "a, 2, 3",
            "1, b, 3",
            "1, 2, c"
    })
    public void testParseAndValidate_NonIntegerParameters(String a, String b, String c) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> TriangleChecker.parseAndValidate(new String[]{a, b, c})
        );

        logger.info("NonIntegerParameters :{} Catch exception: {}", new String[]{a, b, c}, exception.getMessage());
        assertEquals("非整数参数时抛出异常", exception.getMessage());
    }

    // 参数超出范围时抛出异常
    @ParameterizedTest
    @DisplayName("参数超出范围时抛出异常")
    @CsvSource({
            "0, 10, 20",
            "10, 0, 30",
            "10, 20, 0",
            "101, 20, 30",
            "10, 200, 101",
            "10, 20, 101"
    })
    public void testParseAndValidate_OutOfRangeParameters(String a, String b, String c) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> TriangleChecker.parseAndValidate(new String[]{a, b, c})
        );
        logger.info(" OutOfRangeParameters :{} Catch exception: {}", new String[]{a, b, c}, exception.getMessage());
        assertEquals("参数超出范围时抛出异常", exception.getMessage());
    }


    @Test
    @DisplayName("合法参数解析正确")
    public void testParseAndValidate_ValidParameters() {
        int[] expected = {3, 4, 5};
        logger.info("testParseAndValidate_ValidParameters : {}", TriangleChecker.parseAndValidate(new String[]{"3", "4", "5"}));
        assertArrayEquals(expected, TriangleChecker.parseAndValidate(new String[]{"3", "4", "5"}));
    }


    @ParameterizedTest
    @DisplayName("非三角形判断")
    @CsvSource({
            "1, 2, 3",
            "1, 1, 2",
            "10, 1, 1"
    })
    public void testIsTriangle_NonTriangle(int a, int b, int c) {
        logger.info("NonTriangle :{} testIsTriangle_NonTriangle: {}", new int[]{a, b, c}, TriangleChecker.isTriangle(a, b, c));
        assertFalse(TriangleChecker.isTriangle(a, b, c));
    }


    @ParameterizedTest
    @DisplayName("合法三角形判断")
    @CsvSource({
            "3, 4, 5",
            "5, 12, 13",
            "7, 24, 25"
    })
    public void testIsTriangle_ValidTriangle(int a, int b, int c) {
        logger.info("ValidTriangle: {}testIsTriangle_ValidTriangle: {}", new int[]{a, b, c}, TriangleChecker.isTriangle(a, b, c));
        assertTrue(TriangleChecker.isTriangle(a, b, c));
    }

    // 等边三角形判断
    @ParameterizedTest
    @DisplayName("等边三角形判断")
    @ValueSource(ints = {3, 5, 7})
    public void testDetermineTriangleType_Equilateral(int side) {
        logger.info("Equilateral: {}testDetermineTriangleType_Equilateral: {}", new int[]{side, side, side}, TriangleChecker.determineTriangleType(side, side, side));
        assertEquals("等边三角形", TriangleChecker.determineTriangleType(side, side, side));
    }

    @ParameterizedTest
    @DisplayName("等腰三角形判断")
    @CsvSource({
            "3, 3, 4",
            "5, 5, 8",
            "7, 7, 10"
    })
    public void testDetermineTriangleType_Isosceles(int a, int b, int c) {
        logger.info("Isosceles: {} testDetermineTriangleType_Isosceles: {}", new int[]{a, b, c}, TriangleChecker.determineTriangleType(a, b, c));
        assertEquals("等腰三角形", TriangleChecker.determineTriangleType(a, b, c));
    }


    @ParameterizedTest
    @DisplayName("一般三角形判断")
    @CsvSource({
            "3, 4, 5",
            "5, 12, 13",
            "7, 24, 25"
    })
    public void testDetermineTriangleType_Scalene(int a, int b, int c) {
        logger.info("Scalene: {} testDetermineTriangleType_Scalene: {}", new int[]{a, b, c}, TriangleChecker.determineTriangleType(a, b, c));
        assertEquals("一般三角形", TriangleChecker.determineTriangleType(a, b, c));
    }


    @ParameterizedTest
    @DisplayName("参数合法性最坏情况健壮性边界值分析")
//    @CsvFileSource(resources = "/GeneralBoundary.csv",numLinesToSkip = 1)
//    @CsvFileSource(resources = "/RobustBoundary.csv",numLinesToSkip = 1)
//    @CsvFileSource(resources = "/WorstCaseGeneral.csv",numLinesToSkip = 1)
    @CsvFileSource(resources = "/WorstCaseRobust.csv", numLinesToSkip = 1)
    public void testGeneralBoundary(int a, int b, int c, boolean isValid) {
        if (isValid) {
            assertDoesNotThrow(() -> TriangleChecker.parseAndValidate(new String[]{String.valueOf(a), String.valueOf(b), String.valueOf(c)}));
        } else {
            assertThrows(IllegalArgumentException.class, () -> TriangleChecker.parseAndValidate(new String[]{String.valueOf(a), String.valueOf(b), String.valueOf(c)}));
        }
    }


    @ParameterizedTest
    @DisplayName("合法三角形最坏情况健壮性边界值分析")
//    @CsvFileSource(resources = "/GeneralBoundary.csv", numLinesToSkip = 1)
//    @CsvFileSource(resources = "/RobustBoundary.csv",numLinesToSkip = 1)
//    @CsvFileSource(resources = "/WorstCaseGeneral.csv",numLinesToSkip = 1)
    @CsvFileSource(resources = "/WorstCaseRobust.csv",numLinesToSkip = 1)
    public void testIsTriangle(int a, int b, int c, boolean isValid, boolean isTriangle) {
        if (isTriangle) {
            assertTrue(TriangleChecker.isTriangle(a, b, c));
        } else {
            assertFalse(TriangleChecker.isTriangle(a, b, c));
        }
    }

    @ParameterizedTest
    @DisplayName("三角形类型最坏情况健壮性边界值分析")
    @CsvFileSource(resources = "/WorstCaseRobustType.csv", numLinesToSkip = 1)
    public void testDetermineTriangleType(int a, int b, int c, String expected) {
        assertEquals(expected, TriangleChecker.determineTriangleType(a, b, c));
    }
}