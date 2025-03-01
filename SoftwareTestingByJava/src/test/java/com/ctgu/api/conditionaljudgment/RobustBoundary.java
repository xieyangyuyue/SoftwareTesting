package com.ctgu.api.tools;

import java.util.ArrayList;
import java.util.List;

import static com.ctgu.api.tools.ParameterClassification.isValid;
import static com.ctgu.api.tools.WriteToFile.writeToFile;


public class RobustBoundary {
    private static final int[] ROBUST_BOUNDARIES = {0, 1, 2, 50, 99, 100, 101}; // 健壮性边界值

    /**
     * 生成健壮性边界值测试数据（19条用例）
     */
    public static void generateRobustBoundaryData() {
        List<String> testCases = new ArrayList<>();

        // 分别测试a、b、c的越界值
        for (int boundary : ROBUST_BOUNDARIES) {
            testCases.add(String.format("%d,%d,%d,%b", boundary, 50, 50, isValid(boundary, 50, 50)));
            testCases.add(String.format("%d,%d,%d,%b", 50, boundary, 50, isValid(50, boundary, 50)));
            testCases.add(String.format("%d,%d,%d,%b", 50, 50, boundary, isValid(50, 50, boundary)));
        }
        writeToFile("RobustBoundary.csv", testCases);
    }
}
