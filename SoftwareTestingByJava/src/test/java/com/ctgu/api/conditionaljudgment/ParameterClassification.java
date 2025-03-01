package com.ctgu.api.tools;
//参数分类
public class ParameterClassification {

    // 辅助方法：验证参数是否合法
    public static boolean isValid(int a, int b, int c) {
        return a >= 1 && a <= 100 && b >= 1 && b <= 100 && c >= 1 && c <= 100 ;
    }
    // 判断是否为三角形
    public static boolean isTriangle(int a, int b, int c) {
        return  a + b > c && a + c > b && b + c > a;
    }

    // 判断三角形类型
    public static String determineTriangleType(int a, int b, int c) {
        if (a == b && b == c) {
            return "等边三角形";
        } else if (a == b || a == c || b == c) {
            return "等腰三角形";
        } else {
            return "一般三角形";
        }
    }
}
