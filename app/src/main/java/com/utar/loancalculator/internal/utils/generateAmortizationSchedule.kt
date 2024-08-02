package com.utar.loancalculator.internal.utils

import com.utar.loancalculator.internal.dataclass.AmortizationScheduleEntry
import java.util.Calendar
import kotlin.math.pow

fun generatePersonalAmortizationSchedule(
    principal: Double, annualInterestRate: Double, months: Int, startDate: Long
): MutableList<AmortizationScheduleEntry> {
    val amortizationSchedule = mutableListOf<AmortizationScheduleEntry>()

    val monthlyInterestRate = (annualInterestRate / 12) / 100
    val monthlyInstallment = (principal * (1 + monthlyInterestRate * months)) / months

    val interestPayment = principal * monthlyInterestRate
    val principalPayment = monthlyInstallment - interestPayment

    var initialBalance = monthlyInstallment

    for (month in 1..months) {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = startDate
            add(Calendar.MONTH, month - 1)
        }
        val paymentDate = calendar.timeInMillis

        amortizationSchedule.add(
            AmortizationScheduleEntry(
                month = month,
                principalPayment = principalPayment,
                interestPayment = interestPayment,
                totalPayment = monthlyInstallment,
                remainingBalance = initialBalance,
                paymentDate = paymentDate
            )
        )

        initialBalance += monthlyInstallment
    }

    return amortizationSchedule
}

fun generateHousingAmortizationSchedule(
    principal: Double, annualInterestRate: Double, months: Int, startDate: Long
): MutableList<AmortizationScheduleEntry> {
    val monthlyInterestRate = (annualInterestRate / 12) / 100
    val monthlyInstallment =
        (principal * monthlyInterestRate * (1 + monthlyInterestRate).pow(months)) / ((1 + monthlyInterestRate).pow(
            months
        ) - 1)
    var remainingBalance = principal
    val amortizationSchedule = mutableListOf<AmortizationScheduleEntry>()

    var initialBalance = monthlyInstallment



    for (month in 1..months) {
        val interestPayment = remainingBalance * monthlyInterestRate
        val principalPayment = monthlyInstallment - interestPayment


        val calendar = Calendar.getInstance().apply {
            timeInMillis = startDate
            add(Calendar.MONTH, month - 1)
        }
        val paymentDate = calendar.timeInMillis

        amortizationSchedule.add(
            AmortizationScheduleEntry(
                month = month,
                principalPayment = principalPayment,
                interestPayment = interestPayment,
                totalPayment = monthlyInstallment,
                remainingBalance = initialBalance,
                paymentDate = paymentDate
            )
        )

        remainingBalance -= principalPayment
        initialBalance += monthlyInstallment
    }

    return amortizationSchedule
}

fun generateHousingAmortizationSchedule_V1(
    principal: Double, annualInterestRate: Double, months: Int, startDate: Long
): MutableList<AmortizationScheduleEntry> {
    val monthlyInterestRate = (annualInterestRate / 12) / 100
    val monthlyPayment =
        (principal * monthlyInterestRate * (1 + monthlyInterestRate).pow(months)) / ((1 + monthlyInterestRate).pow(
            months
        ) - 1)
    var remainingBalance = principal
    val amortizationSchedule = mutableListOf<AmortizationScheduleEntry>()

    for (month in 1..months) {
        val interestPayment = remainingBalance * monthlyInterestRate
        val principalPayment = monthlyPayment - interestPayment

        val calendar = Calendar.getInstance().apply {
            timeInMillis = startDate
            add(Calendar.MONTH, month - 1)
        }
        val paymentDate = calendar.timeInMillis

        amortizationSchedule.add(
            AmortizationScheduleEntry(
                month = month,
                principalPayment = principalPayment,
                interestPayment = interestPayment,
                totalPayment = monthlyPayment,
                remainingBalance = remainingBalance,
                paymentDate = paymentDate
            )
        )

        remainingBalance -= principalPayment
    }

    return amortizationSchedule
}