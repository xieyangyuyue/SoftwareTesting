package com.ctgu.api.growthparameter;

import static com.ctgu.api.conditionaljudgment.GeneralBoundary.generateGeneralBoundaryData;
import static com.ctgu.api.conditionaljudgment.RobustBoundary.generateRobustBoundaryData;
import static com.ctgu.api.conditionaljudgment.WorstCaseGeneral.generateWorstCaseGeneralData;
import static com.ctgu.api.conditionaljudgment.WorstCaseRobust.generateWorstCaseRobustData;
import static com.ctgu.api.conditionaljudgment.WorstCaseRobustType.generateWorstCaseRobustTypeData;

public class TestDataGenerator {
    public static void main(String[] args) {
        generateGeneralBoundaryData();
        generateRobustBoundaryData();
        generateWorstCaseGeneralData();
        generateWorstCaseRobustData();
        generateWorstCaseRobustTypeData();
    }
}