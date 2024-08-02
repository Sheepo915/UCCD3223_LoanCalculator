package com.utar.loancalculator.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.utar.loancalculator.internal.dataclass.CalculationInput
import com.utar.loancalculator.ui.component.ListCalculations
import com.utar.loancalculator.ui.viewModel.SavedCalculationsViewModel

@Composable
fun SavedScreen(
    navController: NavController,
    savedCalculationsViewModel: SavedCalculationsViewModel = viewModel(),
    modifier: Modifier
) {
    val saves by savedCalculationsViewModel.getCalculations.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .then(modifier)
    ) {
        item {
            if (saves.isNotEmpty()) {
                ListCalculations(
                    onClick = { calculation ->
                        navController.navigate(
                            Screens.Results.parseResultArgument(
                                CalculationInput(
                                    type = calculation.type,
                                    interest = calculation.interest,
                                    period = calculation.period,
                                    amount = calculation.amount,
                                    startDate = calculation.startDate
                                )
                            )
                        )
                    },
                    calculationInputs = saves,
                )
            } else {
                Text(
                    text = "No saved calculations yet. Go back to home page to start calculation!",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
