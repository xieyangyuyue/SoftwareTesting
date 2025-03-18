package cn.edu.ctgu;

public class YesterdayDate {

    public static String getYesterday(int year, int month, int day) {
        // 输入合法性检测
        validateInput(year, month, day);
        if (day > 1) {
            day--;
        } else {
            // 处理跨月或跨年
            if (month == 1) {
                // 上一年的12月
                year--;
                month = 12;
                day = 31;
            } else {
                month--;
                // 获取上个月的天数
                day = getDaysInMonth(year, month);
            }
        }
        return String.format("%d-%02d-%02d", year, month, day);
    }

    private static void validateInput(int year, int month, int day) {
        // 检测年份合法性
        if (year < 0) {
            throw new IllegalArgumentException("年份不能为负数: " + year);
        }

        // 检测月份合法性
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("月份必须为1-12之间的整数: " + month);
        }

        // 检测日期合法性
        int maxDay = getDaysInMonth(year, month);
        if (day < 1 || day > maxDay) {
            throw new IllegalArgumentException(
                    String.format("日期无效：%d年%d月不存在第%d天", year, month, day)
            );
        }
    }

    private static int getDaysInMonth(int year, int month) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                return isLeapYear(year) ? 29 : 28;
            default:
                throw new IllegalArgumentException("Invalid month: " + month);
        }

//        return switch (month) {
//            case 1, 3, 5, 7, 8, 10, 12 -> 31;
//            case 4, 6, 9, 11 -> 30;
//            case 2 -> isLeapYear(year) ? 29 : 28;
//            default -> throw new IllegalArgumentException("Invalid month: " + month);
//        };
    }

    private static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
}