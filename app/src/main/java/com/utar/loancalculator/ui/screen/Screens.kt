package com.utar.loancalculator.ui.screen

import com.utar.loancalculator.internal.dataclass.CalculationInput
import com.utar.loancalculator.internal.enums.LoanType

const val CALCULATION_SCREEN_ARGUMENT_KEY = "type"

enum class RESULT_SCREEN_ARGUMENT_KEY {
    TYPE, AMOUNT, PERIOD, INTEREST, START_DATE
}

sealed class Screens(val route: String) {
    object Initial : Screens(route = "initial")
    object Home : Screens(route = "home")
    object Setting : Screens(route = "setting")
    object Saved : Screens(route = "saved")
    object Calculation : Screens(route = "calculation/{$CALCULATION_SCREEN_ARGUMENT_KEY}") {
        fun parseCalculationArgument(type: LoanType): String {
            return "calculation/${type.name}"
        }
    }
    object Results :
        Screens(route = "results/{${RESULT_SCREEN_ARGUMENT_KEY.TYPE.name}}/{${RESULT_SCREEN_ARGUMENT_KEY.AMOUNT}}/{${RESULT_SCREEN_ARGUMENT_KEY.PERIOD}}/{${RESULT_SCREEN_ARGUMENT_KEY.INTEREST}}/{${RESULT_SCREEN_ARGUMENT_KEY.START_DATE}}") {
        fun parseResultArgument(calculationInput: CalculationInput): String {
            return "results/${calculationInput.type}/${calculationInput.amount}/${calculationInput.period}/${calculationInput.interest}/${calculationInput.startDate}"
        }
    }
}