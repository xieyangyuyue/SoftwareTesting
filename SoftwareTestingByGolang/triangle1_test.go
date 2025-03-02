package main

import (
	"log"
	"testing"
)

// TestParseAndValidate 测试 parseAndValidate 函数
func TestParseAndValidate(t *testing.T) {
	tests := []struct { // 定义测试用例的结构
		name    string   // 测试用例的名称
		args    []string // 输入参数，表示要解析的字符串数组
		wantErr bool     // 是否期望函数返回错误
	}{
		{"Insufficient parameter", []string{"1", "2"}, true},          // 参数数量不足
		{"Non-Integer Parameters", []string{"a", "2", "3"}, true},     // 参数包含非整数
		{"Parameters Out of Range", []string{"0", "101", "50"}, true}, // 参数超出范围 [1, 100]
		{"Valid Parameters", []string{"3", "4", "5"}, false},          // 参数完全合法

	}

	for _, tt := range tests { // 遍历每个测试用例
		t.Run(tt.name, func(t *testing.T) { // 使用 t.Run 为每个测试用例创建子测试
			_, _, _, err := parseAndValidate(tt.args) // 调用被测试的函数
			if (err != nil) != tt.wantErr {           // 检查实际错误是否符合期望
				log.Printf("Test failed: %s, args: %v, error: %v, wantErr: %v\n", tt.name, tt.args, err, tt.wantErr)
				t.Errorf("parseAndValidate(%v) error = %v, wantErr %v", tt.args, err, tt.wantErr)
			} else {
				log.Printf("Test passed: %s, args: %v\n", tt.name, tt.args)
			}
		})
	}
}

// TestIsTriangle 测试 isTriangle 函数
func TestIsTriangle(t *testing.T) {
	tests := []struct { // 定义测试用例的结构
		name    string // 测试用例的名称
		a, b, c int    // 输入的三条边
		want    bool   // 期望的返回值（是否能构成三角形）
	}{
		{"Non-Triangle", 1, 2, 3, false},  // 不能构成三角形
		{"Valid Triangle", 3, 4, 5, true}, // 可以构成三角形
	}

	for _, tt := range tests { // 遍历每个测试用例
		t.Run(tt.name, func(t *testing.T) { // 使用 t.Run 为每个测试用例创建子测试
			if got := isTriangle(tt.a, tt.b, tt.c); got != tt.want { // 调用被测试的函数
				log.Printf("Test failed: %s, inputs: %d, %d, %d, want: %v\n", tt.name, tt.a, tt.b, tt.c, tt.want)
				t.Errorf("isTriangle(%d, %d, %d) = %v, want %v", tt.a, tt.b, tt.c, got, tt.want)
			} else {
				log.Printf("Test passed: %s, inputs: %d, %d, %d\n", tt.name, tt.a, tt.b, tt.c)
			}
		})
	}
}

// TestDetermineTriangleType 测试 determineTriangleType 函数
func TestDetermineTriangleType(t *testing.T) {
	tests := []struct { // 定义测试用例的结构
		name    string // 测试用例的名称
		a, b, c int    // 输入的三条边
		want    string // 期望的返回值（三角形的类型）
	}{
		{"Equilateral Triangle", 3, 3, 3, "等边三角形"}, // 等边三角形
		{"Isosceles Triangle", 3, 3, 4, "等腰三角形"},   // 等腰三角形
		{"Scalene Triangle", 3, 4, 5, "一般三角形"},     // 一般三角形
	}

	for _, tt := range tests { // 遍历每个测试用例
		t.Run(tt.name, func(t *testing.T) { // 使用 t.Run 为每个测试用例创建子测试
			if got := determineTriangleType(tt.a, tt.b, tt.c); got != tt.want { // 调用被测试的函数
				log.Printf("Test failed: %s, inputs: %d, %d, %d, want: %s\n", tt.name, tt.a, tt.b, tt.c, tt.want)
				t.Errorf("determineTriangleType(%d, %d, %d) = %v, want %v", tt.a, tt.b, tt.c, got, tt.want)
			} else {
				log.Printf("Test passed: %s, inputs: %d, %d, %d\n", tt.name, tt.a, tt.b, tt.c)
			}
		})
	}
}
