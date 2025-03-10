package triangle

import (
	"SoftwareTestingByGolang/testutils" // 替换为实际模块路径
	"fmt"
	"path/filepath"
	"testing"
)

func TestTriangle(t *testing.T) {
	//csvPath := filepath.Join("testdata", "GeneralBoundary.csv")
	//csvPath := filepath.Join("testdata", "RobustBoundary.csv")
	//csvPath := filepath.Join("testdata", "WorstCaseGeneral.csv")
	csvPath := filepath.Join("../testdata", "WorstCaseRobust.csv")

	testCases, err := testutils.LoadCSVTestCases(csvPath, true)
	if err != nil {
		t.Fatalf("加载测试数据失败: %v", err)
	}

	for _, tc := range testCases {
		t.Run(tc.Name, func(t *testing.T) {
			// 参数验证测试
			_, _, _, err := parseAndValidate([]string{
				fmt.Sprint(tc.A),
				fmt.Sprint(tc.B),
				fmt.Sprint(tc.C),
			})

			if tc.IsValid && err != nil {
				t.Errorf("预期有效但返回错误: %v", err)
			}
			if !tc.IsValid && err == nil {
				t.Error("预期无效但未返回错误")
			}

			// 三角形判断测试
			if tc.IsValid {
				got := isTriangle(tc.A, tc.B, tc.C)
				if got != tc.IsTriangle {
					t.Errorf("输入 (%d, %d, %d) 预期 %v 实际 %v",
						tc.A, tc.B, tc.C, tc.IsTriangle, got)
				}
			}
		})
	}
}
