package com.utar.loancalculator.internal.dataclass.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.utar.loancalculator.internal.dataclass.CalculationData
import com.utar.loancalculator.internal.enums.LoanType

@Entity
data class SavedCalculation(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    override val type: LoanType,
    override val amount: Double,
    override val period: Int,
    override val interest: Double,
    override val startDate: Long
) : CalculationData