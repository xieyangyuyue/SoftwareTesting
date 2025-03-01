package com.ctgu.api.tools;

import static com.ctgu.api.tools.GeneralBoundary.generateGeneralBoundaryData;
import static com.ctgu.api.tools.RobustBoundary.generateRobustBoundaryData;
import static com.ctgu.api.tools.WorstCaseGeneral.generateWorstCaseGeneralData;
import static com.ctgu.api.tools.WorstCaseRobust.generateWorstCaseRobustData;
import static com.ctgu.api.tools.WorstCaseRobustType.generateWorstCaseRobustTypeData;

public class TestDataGenerator {

    public static void main(String[] args) {
        generateGeneralBoundaryData();
        generateRobustBoundaryData();
        generateWorstCaseGeneralData();
        generateWorstCaseRobustData();
        generateWorstCaseRobustTypeData();

    }
}