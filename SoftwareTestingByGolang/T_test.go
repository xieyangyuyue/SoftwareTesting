package main

import (
	"path/filepath"
	"testing"
)

func TestAdd_CSV(t *testing.T) {
	// 获取 CSV 绝对路径
	csvPath := filepath.Join("testdata", "add_cases.csv")

	// 加载测试数据
	testCases, err := LoadCSVTestCases(csvPath, true)
	if err != nil {
		t.Fatalf("加载测试数据失败: %v", err)
	}

	//
}
