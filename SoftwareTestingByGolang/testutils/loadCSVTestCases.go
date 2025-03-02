package testutils

import (
	"encoding/csv"
	"fmt"
	"os"
	"strconv"
)

// AddTestCase 定义测试用例结构体
type AddTestCase struct {
	Name       string
	A          int
	B          int
	C          int
	IsValid    bool
	IsTriangle bool
}

// LoadCSVTestCases 加载CSV测试用例
func LoadCSVTestCases(path string, skipHeader bool) ([]AddTestCase, error) {
	file, err := os.Open(path)
	if err != nil {
		return nil, fmt.Errorf("打开文件失败: %w", err)
	}
	defer func(file *os.File) {
		_ = file.Close()
	}(file)

	reader := csv.NewReader(file)
	lines, err := reader.ReadAll()
	if err != nil {
		return nil, fmt.Errorf("读取CSV失败: %w", err)
	}

	var cases []AddTestCase
	startIndex := 0
	if skipHeader && len(lines) > 0 {
		startIndex = 1
	}

	for i := startIndex; i < len(lines); i++ {
		line := lines[i]
		if len(line) < 5 {
			return nil, fmt.Errorf("第%d行数据不完整", i+1)
		}

		a, err := strconv.Atoi(line[0])
		if err != nil {
			return nil, fmt.Errorf("第%d行A值转换错误: %v", i+1, err)
		}

		b, err := strconv.Atoi(line[1])
		if err != nil {
			return nil, fmt.Errorf("第%d行B值转换错误: %v", i+1, err)
		}

		c, err := strconv.Atoi(line[2])
		if err != nil {
			return nil, fmt.Errorf("第%d行C值转换错误: %v", i+1, err)
		}

		isValid, err := strconv.ParseBool(line[3])
		if err != nil {
			return nil, fmt.Errorf("第%d行isValid转换错误: %v", i+1, err)
		}

		isTriangle, err := strconv.ParseBool(line[4])
		if err != nil {
			return nil, fmt.Errorf("第%d行isTriangle转换错误: %v", i+1, err)
		}

		name := fmt.Sprintf("Test_%d", i+1)
		cases = append(cases, AddTestCase{
			Name:       name,
			A:          a,
			B:          b,
			C:          c,
			IsValid:    isValid,
			IsTriangle: isTriangle,
		})
	}

	return cases, nil
}
