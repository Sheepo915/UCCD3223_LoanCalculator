package com.utar.loancalculator.internal.utils

import com.utar.loancalculator.internal.dataclass.AmortizationScheduleEntry
import java.util.Calendar
import kotlin.math.pow

fun generatePersonalAmortizationSchedule(
    principal: Double, annualInterestRate: Double, numberOfInstalments: Int, startDate: Long
): MutableList<AmortizationScheduleEntry> {
    val amortizationSchedule = mutableListOf<AmortizationScheduleEntry>()

    val monthlyInterestRate = (annualInterestRate / 12) / 100
    val monthlyInstallment = (principal * (1 + monthlyInterestRate * numberOfInstalments)) / numberOfInstalments

    val interestPayment = principal * monthlyInterestRate
    val principalPayment = monthlyInstallment - interestPayment

    var initialBalance = principal
    var accumulatedRepayment = monthlyInstallment

    for (instalment in 1..numberOfInstalments) {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = startDate
            add(Calendar.MONTH, instalment - 1)
        }
        val paymentDate = calendar.timeInMillis

        amortizationSchedule.add(
            AmortizationScheduleEntry(
                paymentNumber = instalment,
                principalPayment = principalPayment,
                interestPayment = interestPayment,
                monthlyPayment = monthlyInstallment,
                remainingBalance = initialBalance,
                accumulatedRepayment = accumulatedRepayment,
                paymentDate = paymentDate
            )
        )

        initialBalance -= principalPayment
        accumulatedRepayment += monthlyInstallment
    }

    return amortizationSchedule
}

fun generateHousingAmortizationSchedule(
    principal: Double, annualInterestRate: Double, numberOfInstalments: Int, startDate: Long
): MutableList<AmortizationScheduleEntry> {
    val monthlyInterestRate = (annualInterestRate / 12) / 100
    val monthlyInstallment =
        (principal * monthlyInterestRate * (1 + monthlyInterestRate).pow(numberOfInstalments)) / ((1 + monthlyInterestRate).pow(
            numberOfInstalments
        ) - 1)
    val amortizationSchedule = mutableListOf<AmortizationScheduleEntry>()

    var accumulatedRepayment = monthlyInstallment
    var initialBalance = principal

    for (instalment in 1..numberOfInstalments) {
        val interestPayment = initialBalance * monthlyInterestRate
        val principalPayment = monthlyInstallment - interestPayment


        val calendar = Calendar.getInstance().apply {
            timeInMillis = startDate
            add(Calendar.MONTH, instalment - 1)
        }
        val paymentDate = calendar.timeInMillis

        amortizationSchedule.add(
            AmortizationScheduleEntry(
                paymentNumber = instalment,
                principalPayment = principalPayment,
                interestPayment = interestPayment,
                monthlyPayment = monthlyInstallment,
                remainingBalance = initialBalance,
                accumulatedRepayment = accumulatedRepayment,
                paymentDate = paymentDate
            )
        )

        initialBalance -= principalPayment
        accumulatedRepayment += monthlyInstallment
    }

    return amortizationSchedule
}