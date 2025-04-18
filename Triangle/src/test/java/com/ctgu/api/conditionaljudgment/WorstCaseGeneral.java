package com.ctgu.api.conditionaljudgment;

import java.util.ArrayList;
import java.util.List;

import static com.ctgu.api.conditionaljudgment.ParameterClassification.isTriangle;
import static com.ctgu.api.conditionaljudgment.ParameterClassification.isValid;
import static com.ctgu.api.growthparameter.WriteToFile.writeToFile;

public class WorstCaseGeneral {
    /**
     * 生成最坏情况一般边界值测试数据（125条用例）
     */
    public static void generateWorstCaseGeneralData() {
        List<String> testCases = new ArrayList<>();
        int[] boundaries = {1, 2, 50, 99, 100};

        for (int a : boundaries) {
            for (int b : boundaries) {
                for (int c : boundaries) {
                    boolean valid = isValid(a, b, c);
                    boolean triangle = isTriangle(a, b, c);
                    testCases.add(String.format("%d,%d,%d,%b,%b", a, b, c, valid, triangle));
                }
            }
        }

        writeToFile("WorstCaseGeneral.csv", testCases);
    }
}
