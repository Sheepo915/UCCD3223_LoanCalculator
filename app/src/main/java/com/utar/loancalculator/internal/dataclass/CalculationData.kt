package com.utar.loancalculator.internal.dataclass

import com.utar.loancalculator.internal.enums.LoanType

interface CalculationData {
    val type: LoanType
    val amount: Double
    val period: Int
    val interest: Double
    val startDate: Long
}