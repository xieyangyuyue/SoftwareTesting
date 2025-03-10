package Billing

import (
	"log"
	"testing"
	"time"
)

// TestBillingCalculator 测试BillingCalculator的计费逻辑
func TestBillingCalculator(t *testing.T) {
	calculator := NewBillingCalculator()

	// 测试基础费率
	t.Run("基础费率（≤20分钟）", func(t *testing.T) {
		test1MinuteCall(t, calculator)
		testExactly20Minutes(t, calculator)
	})

	// 测试分段费率
	t.Run("分段费率（>20分钟）", func(t *testing.T) {
		test21MinutesCall(t, calculator)
		testMaxDurationCall(t, calculator)
	})

	// 测试夏令时转换
	t.Run("夏令时转换场景", func(t *testing.T) {
		testSpringDSTWithCorrectDate(t, calculator)
		testCallWithinFallDSTOverlap(t, calculator)
	})

	// 测试时间进位规则
	t.Run("时间进位测试", func(t *testing.T) {
		test59SecondsRounding(t, calculator)
		testExactMinute(t, calculator)
	})
}

// 测试1分钟通话
func test1MinuteCall(t *testing.T, calculator *BillingCalculator) {
	log.Println("1分钟通话")
	start := time.Date(2025, 6, 12, 12, 59, 0, 0, time.UTC)
	end := start.Add(59 * time.Second)
	log.Printf("Start: %v, End: %v", start, end)

	duration := calculator.CalculateAdjustedDuration(start, end)
	log.Printf("Adjusted duration: %d minutes", duration)
	if duration != 1 {
		t.Errorf("应向上取整到1分钟，实际得到: %d", duration)
	}
	charge := calculator.CalculateCharge(duration)
	if charge != 0.05 {
		t.Errorf("费用应为0.05美元，实际得到: %.2f", charge)
	}
}

// 测试精确20分钟通话
func testExactly20Minutes(t *testing.T, calculator *BillingCalculator) {
	log.Println("测试精确20分钟通话")
	start := time.Date(2025, 5, 12, 12, 0, 0, 0, time.UTC)
	end := start.Add(20 * time.Minute)
	log.Printf("Start: %v, End: %v", start, end)

	duration := calculator.CalculateAdjustedDuration(start, end)
	log.Printf("Adjusted duration: %d minutes", duration)
	if duration != 20 {
		t.Errorf("应为精确20分钟，实际得到: %d", duration)
	}
	charge := calculator.CalculateCharge(duration)
	if charge != 1.00 {
		t.Errorf("费用应为1.00美元，实际得到: %.2f", charge)
	}
}

// 测试21分钟通话（费率切换边界）
func test21MinutesCall(t *testing.T, calculator *BillingCalculator) {
	log.Println("21分钟通话（费率切换边界）")
	start := time.Date(2025, 6, 1, 12, 0, 0, 0, time.UTC)
	end := start.Add(21 * time.Minute)
	log.Printf("Start: %v, End: %v", start, end)

	duration := calculator.CalculateAdjustedDuration(start, end)
	if duration != 21 {
		t.Errorf("应为21分钟，实际得到: %d", duration)
	}
	charge := calculator.CalculateCharge(duration)
	if charge != 1.10 {
		t.Errorf("费用应为1.10美元，实际得到: %.2f", charge)
	}
}

// 测试超长通话（接近30小时）
func testMaxDurationCall(t *testing.T, calculator *BillingCalculator) {
	log.Println("测试超长通话（接近30小时）")
	start := time.Date(2025, 7, 1, 12, 0, 0, 0, time.UTC)
	end := start.Add(29*time.Hour + 59*time.Minute + 59*time.Second)
	log.Printf("Start: %v, End: %v", start, end)

	duration := calculator.CalculateAdjustedDuration(start, end)
	log.Printf("Adjusted duration: %d minutes", duration)
	if duration != 1800 {
		t.Errorf("29小时59分59秒应进位到30小时，实际得到: %d", duration)
	}
	expectedCharge := 1.00 + 0.10*float64(duration-20)
	charge := calculator.CalculateCharge(duration)
	if charge != expectedCharge {
		t.Errorf("费用应为%.2f美元，实际得到: %.2f", expectedCharge, charge)
	}
}

// 测试春季夏令时转换（丢失1小时）
func testSpringDSTWithCorrectDate(t *testing.T, calculator *BillingCalculator) {
	log.Println("测试春季夏令时转换（丢失1小时）")
	start, _ := time.Parse("2006-01-02T15:04:05-07:00", "2025-03-12T01:30:00-05:00")
	end, _ := time.Parse("2006-01-02T15:04:05-07:00", "2025-03-12T03:30:00-04:00")
	log.Printf("Start (EST): %v, End (EDT): %v", start, end)

	duration := calculator.CalculateAdjustedDuration(start, end)
	log.Printf("Adjusted duration: %d minutes", duration)
	if duration != 60 {
		t.Errorf("应计算实际1小时，实际得到: %d", duration)
	}
	charge := calculator.CalculateCharge(duration)
	if charge != 5.00 {
		t.Errorf("费用应为5.00美元，实际得到: %.2f", charge)
	}
}

// 测试秋季夏令时转换（重复1小时）
func testCallWithinFallDSTOverlap(t *testing.T, calculator *BillingCalculator) {
	log.Println("测试秋季夏令时转换（重复1小时）")
	start, _ := time.Parse("2006-01-02T15:04:05-07:00", "2025-11-05T01:30:00-04:00")
	end := start.Add(1*time.Hour + 30*time.Minute).In(time.UTC)
	log.Printf("Start (EDT): %v, End (EST): %v", start, end)

	duration := calculator.CalculateAdjustedDuration(start, end)
	log.Printf("Adjusted duration: %d minutes", duration)
	if duration != 90 {
		t.Errorf("应计算实际1.5小时，实际得到: %d", duration)
	}
	charge := calculator.CalculateCharge(duration)
	if charge != 8.00 {
		t.Errorf("费用应为8.00美元，实际得到: %.2f", charge)
	}
}

// 测试59秒进位到1分钟
func test59SecondsRounding(t *testing.T, calculator *BillingCalculator) {
	log.Println("测试短时通话秒数进位规则")
	start := time.Date(2025, 6, 1, 12, 0, 0, 0, time.UTC)
	end := start.Add(30 * time.Second)
	log.Printf("Start: %v, End: %v", start, end)

	duration := calculator.CalculateAdjustedDuration(start, end)
	log.Printf("Adjusted duration: %d minutes", duration)
	if duration != 1 {
		t.Errorf("30秒应进位到1分钟，实际得到: %d", duration)
	}
	charge := calculator.CalculateCharge(duration)
	if charge != 0.05 {
		t.Errorf("费用应为0.05美元，实际得到: %.2f", charge)
	}
}

// 测试0秒不进位
func testExactMinute(t *testing.T, calculator *BillingCalculator) {
	log.Println("0秒不进位")
	start := time.Date(2025, 6, 1, 12, 0, 0, 0, time.UTC)
	end := start.Add(10 * time.Minute)
	log.Printf("Start: %v, End: %v", start, end)

	duration := calculator.CalculateAdjustedDuration(start, end)
	log.Printf("Adjusted duration: %d minutes", duration)
	if duration != 10 {
		t.Errorf("应为10分钟，实际得到: %d", duration)
	}
	charge := calculator.CalculateCharge(duration)
	if charge != 0.50 {
		t.Errorf("费用应为0.50美元，实际得到: %.2f", charge)
	}
}
