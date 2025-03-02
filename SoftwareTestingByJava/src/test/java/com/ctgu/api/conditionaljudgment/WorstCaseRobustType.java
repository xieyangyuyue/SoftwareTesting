package com.ctgu.api.conditionaljudgment;

import java.util.ArrayList;
import java.util.List;

import static com.ctgu.api.conditionaljudgment.ParameterClassification.determineTriangleType;
import static com.ctgu.api.growthparameter.WriteToFile.writeToFile;

public class WorstCaseRobustType {
    public static void generateWorstCaseRobustTypeData() {
        List<String> testCases = new ArrayList<>();
        int[] boundaries = {0, 1, 2, 50, 99, 100, 101};

        for (int a : boundaries) {
            for (int b : boundaries) {
                for (int c : boundaries) {
                    String type = determineTriangleType(a, b, c);
                    testCases.add(String.format("%d,%d,%d,%s", a, b, c, type));
                }
            }
        }

        writeToFile("WorstCaseRobustType.csv", testCases);
    }
}
