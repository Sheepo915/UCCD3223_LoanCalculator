package com.utar.loancalculator.ui.nav

import com.utar.loancalculator.ui.viewModel.SavedCalculationsViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.utar.loancalculator.internal.datastore.DataStoreRepository
import com.utar.loancalculator.internal.enums.LoanType
import com.utar.loancalculator.ui.screen.CALCULATION_SCREEN_ARGUMENT_KEY
import com.utar.loancalculator.ui.screen.CalculationScreen
import com.utar.loancalculator.ui.screen.HomeScreen
import com.utar.loancalculator.ui.screen.InitialScreen
import com.utar.loancalculator.ui.screen.RESULT_SCREEN_ARGUMENT_KEY
import com.utar.loancalculator.ui.screen.ResultScreen
import com.utar.loancalculator.ui.screen.SavedScreen
import com.utar.loancalculator.ui.screen.Screens
import com.utar.loancalculator.ui.screen.SettingScreen

@Composable
fun SetupNavGraph(
    navHostController: NavHostController,
    savedCalculationsViewModel: SavedCalculationsViewModel,
    modifier: Modifier
) {
    val context = LocalContext.current
    val dataStore = DataStoreRepository(context = context)

    val isBirthYearSet = dataStore.getIsBirthYearSet.collectAsState(initial = false)

    NavHost(
        navController = navHostController,
        startDestination = if (isBirthYearSet.value) Screens.Home.route else Screens.Initial.route
    ) {
        composable(route = Screens.Initial.route) {
            InitialScreen(context = context, navController = navHostController, modifier = modifier)
        }
        composable(route = Screens.Home.route) {
            HomeScreen(navHost = navHostController, modifier = modifier)
        }
        composable(route = Screens.Saved.route) {
            SavedScreen(
                navController = navHostController,
                savedCalculationsViewModel = savedCalculationsViewModel
                ,
                modifier = modifier
            )
        }
        composable(route = Screens.Setting.route) {
            SettingScreen(savedCalculationsViewModel = savedCalculationsViewModel, dataStore = dataStore, modifier = modifier)
        }
        composable(
            route = Screens.Calculation.route,
            arguments = listOf(navArgument(name = CALCULATION_SCREEN_ARGUMENT_KEY) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val loanTypeString =
                backStackEntry.arguments?.getString(CALCULATION_SCREEN_ARGUMENT_KEY)
            val loanType = loanTypeString?.let { enumValueOf<LoanType>(it) }

            if (loanType != null) {
                CalculationScreen(
                    navController = navHostController, loanType = loanType, modifier = modifier
                )
            }
        }
        composable(
            route = Screens.Results.route,
            arguments = listOf(navArgument(name = RESULT_SCREEN_ARGUMENT_KEY.TYPE.name) {
                type = NavType.StringType
            }, navArgument(name = RESULT_SCREEN_ARGUMENT_KEY.AMOUNT.name) {
                type = NavType.StringType
            }, navArgument(name = RESULT_SCREEN_ARGUMENT_KEY.PERIOD.name) {
                type = NavType.StringType
            }, navArgument(name = RESULT_SCREEN_ARGUMENT_KEY.INTEREST.name) {
                type = NavType.StringType
            }, navArgument(name = RESULT_SCREEN_ARGUMENT_KEY.START_DATE.name) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val loanTypeString =
                backStackEntry.arguments?.getString(RESULT_SCREEN_ARGUMENT_KEY.TYPE.name)
            val loanType = loanTypeString?.let { enumValueOf<LoanType>(it) }

            val startDate =
                backStackEntry.arguments?.getString(RESULT_SCREEN_ARGUMENT_KEY.START_DATE.name)
                    ?.toLongOrNull() ?: 0L
            val principal =
                backStackEntry.arguments?.getString(RESULT_SCREEN_ARGUMENT_KEY.AMOUNT.name)
                    ?.toDoubleOrNull() ?: 0.0
            val annualInterestRate =
                backStackEntry.arguments?.getString(RESULT_SCREEN_ARGUMENT_KEY.INTEREST.name)
                    ?.toDoubleOrNull() ?: 0.0
            val months = backStackEntry.arguments?.getString(RESULT_SCREEN_ARGUMENT_KEY.PERIOD.name)
                ?.toIntOrNull() ?: 0

            if (loanType != null) {
                ResultScreen(
                    savedCalculationsViewModel = savedCalculationsViewModel,
                    navController = navHostController,
                    loanType = loanType,
                    principal = principal,
                    annualInterestRate = annualInterestRate,
                    months = months,
                    startDate = startDate,
                    modifier = modifier
                )
            }
        }
    }
}