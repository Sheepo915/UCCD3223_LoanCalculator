package com.utar.loancalculator.internal.dataclass

data class AmortizationScheduleEntry(
    val month: Int,
    val principalPayment: Double,
    val interestPayment: Double,
    val totalPayment: Double,
    val remainingBalance: Double,
    val paymentDate: Long
)