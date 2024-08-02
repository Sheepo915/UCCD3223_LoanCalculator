package com.utar.loancalculator.internal.dataclass

import com.utar.loancalculator.internal.enums.LoanType


data class CalculationInput(
    override val type: LoanType,
    override val amount: Double,
    override val period: Int,
    override val interest: Double,
    override val startDate: Long
): CalculationData
