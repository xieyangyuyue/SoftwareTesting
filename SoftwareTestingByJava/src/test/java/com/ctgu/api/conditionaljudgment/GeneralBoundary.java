package com.ctgu.api.tools;

import java.util.ArrayList;
import java.util.List;


import static com.ctgu.api.tools.ParameterClassification.isValid;
import static com.ctgu.api.tools.WriteToFile.writeToFile;

public class GeneralBoundary {
    private static final int NORMAL = 50; // 正常值
    private static final int[] GENERAL_BOUNDARIES = {1, 2, 50, 99, 100}; // 一般边界值

    /**
     * 生成一般边界值测试数据（13条用例）
     */
    public static void generateGeneralBoundaryData() {
        List<String> testCases = new ArrayList<>();

        // 分别测试a、b、c的边界值，其他参数固定为NORMAL
        for (int boundary : GENERAL_BOUNDARIES) {
            testCases.add(String.format("%d,%d,%d,%b", boundary, NORMAL, NORMAL, isValid(boundary, NORMAL, NORMAL)));
            testCases.add(String.format("%d,%d,%d,%b", NORMAL, boundary, NORMAL, isValid(NORMAL, boundary, NORMAL)));
            testCases.add(String.format("%d,%d,%d,%b", NORMAL, NORMAL, boundary, isValid(NORMAL, NORMAL, boundary)));
        }

        // 添加一个全正常值的用例
        testCases.add(String.format("%d,%d,%d,%b", NORMAL, NORMAL, NORMAL, true));

        writeToFile("GeneralBoundary.csv", testCases);
    }

}
