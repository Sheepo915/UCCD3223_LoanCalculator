package com.utar.loancalculator.internal.dataclass

data class AmortizationScheduleEntry(
    val paymentNumber: Int,
    val principalPayment: Double,
    val interestPayment: Double,
    val monthlyPayment: Double,
    val remainingBalance: Double,
    val accumulatedRepayment: Double,
    val paymentDate: Long
)