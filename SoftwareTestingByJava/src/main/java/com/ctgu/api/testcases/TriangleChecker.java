package com.ctgu.api;

public class TriangleChecker {
    // 解析并验证输入参数
    public static int[] parseAndValidate(String[] args) throws IllegalArgumentException {
        if (args.length != 3) {
            throw new IllegalArgumentException("参数不足");
        }

        try {
            int a = Integer.parseInt(args[0]);
            int b = Integer.parseInt(args[1]);
            int c = Integer.parseInt(args[2]);

            if (a < 1 || a > 100 || b < 1 || b > 100 || c < 1 || c > 100) {
                throw new IllegalArgumentException("参数超出范围时抛出异常");
            }

            return new int[]{a, b, c};
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("非整数参数时抛出异常");
        }
    }

    // 判断是否为三角形
    public static boolean isTriangle(int a, int b, int c) {
        return a + b > c && a + c > b && b + c > a;
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