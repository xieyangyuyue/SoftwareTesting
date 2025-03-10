package Billing

import (
	"log"
	"time"
)

const (
	CeilingAdjustment      = 59
	SecondsPerMinute       = 60
	BaseMinutesLimit       = 20
	BaseRate               = 0.05
	ExtraRate              = 0.10
	BaseFee                = 1.00
	MinimumBillableMinutes = 1
)

// BillingCalculator 处理通话计费逻辑，包含时长计算和费用计算功能
type BillingCalculator struct {
	logger *log.Logger
}

// NewBillingCalculator 创建一个新的BillingCalculator实例
func NewBillingCalculator() *BillingCalculator {
	return &BillingCalculator{
		logger: log.New(log.Writer(), "BillingCalculator: ", log.Flags()),
	}
}

// CalculateAdjustedDuration 计算调整后的通话时长（分钟），考虑夏令时和向上取整规则
func (b *BillingCalculator) CalculateAdjustedDuration(start, end time.Time) int {
	duration := end.Sub(start)
	seconds := int(duration.Seconds())
	b.logger.Printf("时间差计算: [%.0d] 秒", seconds)

	totalMinutes := (seconds + CeilingAdjustment) / SecondsPerMinute
	b.logger.Printf("向上取整后分钟数: %d 分钟", totalMinutes)

	adjustedMinutes := totalMinutes
	if adjustedMinutes <= MinimumBillableMinutes {
		adjustedMinutes = MinimumBillableMinutes
	}
	b.logger.Printf("最终计费时长: %d 分钟", adjustedMinutes)

	return adjustedMinutes
}

// CalculateCharge 根据调整后的通话时长计算费用
func (b *BillingCalculator) CalculateCharge(adjustedMinutes int) float64 {
	b.logger.Printf("计算费用，时长: %d 分钟", adjustedMinutes)

	var charge float64
	if adjustedMinutes <= BaseMinutesLimit {
		charge = BaseRate * float64(adjustedMinutes)
		b.logger.Printf("基础费率: %.2f 美元", charge)
	} else {
		extraMinutes := adjustedMinutes - BaseMinutesLimit
		extraCharge := ExtraRate * float64(extraMinutes)
		charge = BaseFee + extraCharge
		b.logger.Printf("分段费率: 基础 %.2f 美元 + 超时部分 %.2f 美元 = %.2f 美元", BaseFee, extraCharge, charge)
	}

	b.logger.Printf("最终费用: %.2f 美元", charge)
	return charge
}
