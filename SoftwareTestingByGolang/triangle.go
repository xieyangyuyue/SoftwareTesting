package main

import (
	"errors"  // 导入errors包，用于创建自定义错误
	"strconv" // 导入strconv包，用于字符串与数字之间的转换
)

// parseAndValidate 解析并验证输入参数
// 输入参数是一个字符串切片args，返回三个整数a、b、c和一个错误error。
func parseAndValidate(args []string) (int, int, int, error) {
	// 检查输入参数的长度是否为3，如果不是，返回错误"参数不足"
	if len(args) != 3 {
		return 0, 0, 0, errors.New("参数不足")
	}

	// 将输入的字符串参数转换为整数
	// strconv.Atoi会尝试将字符串转换为整数，如果失败则返回错误
	a, err1 := strconv.Atoi(args[0]) // 转换第一个参数为整数a
	b, err2 := strconv.Atoi(args[1]) // 转换第二个参数为整数b
	c, err3 := strconv.Atoi(args[2]) // 转换第三个参数为整数c

	// 检查转换过程中是否有错误，如果有错误，说明输入的参数不是整数
	// 返回错误"参数超出范围时抛出异常"（这里错误信息的描述可能不够准确，建议修改为"输入参数不是整数"）
	if err1 != nil || err2 != nil || err3 != nil {
		return 0, 0, 0, errors.New("参数超出范围时抛出异常")
	}

	// 检查转换后的整数是否在范围[1, 100]内
	// 如果不在范围，返回错误"非整数参数时抛出异常"（同样，错误信息的描述可能需要调整为"参数超出范围"）
	if a < 1 || a > 100 || b < 1 || b > 100 || c < 1 || c > 100 {
		return 0, 0, 0, errors.New("非整数参数时抛出异常")
	}

	// 如果所有检查通过，返回解析后的整数a、b、c和nil作为错误
	return a, b, c, nil
}

// isTriangle 判断是否为三角形
// 输入三个整数a、b、c，返回一个布尔值，表示这三个边是否能构成三角形
func isTriangle(a, b, c int) bool {
	// 根据三角形的性质，任意两边之和必须大于第三边
	// 如果满足条件，返回true，否则返回false
	return a+b > c && a+c > b && b+c > a
}

// determineTriangleType 判断三角形类型
// 输入三个整数a、b、c，返回一个字符串，表示三角形的类型
func determineTriangleType(a, b, c int) string {
	// 判断三条边是否相等，如果相等，则为等边三角形
	if a == b && b == c {
		return "等边三角形"
	} else if a == b || a == c || b == c {
		return "等腰三角形"
	} else {
		return "一般三角形"
	}
}
