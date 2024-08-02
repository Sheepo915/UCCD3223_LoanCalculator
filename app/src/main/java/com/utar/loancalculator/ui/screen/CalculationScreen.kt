package com.utar.loancalculator.ui.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.utar.loancalculator.R
import com.utar.loancalculator.internal.dataclass.CalculationInput
import com.utar.loancalculator.internal.datastore.DataStoreRepository
import com.utar.loancalculator.internal.enums.LoanType
import com.utar.loancalculator.internal.utils.toTitleCase
import com.utar.loancalculator.ui.component.CustomDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculationScreen(
    navController: NavController, loanType: LoanType, modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dataStore = DataStoreRepository(context)
    val birthYear =
        dataStore.getBirthYear.collectAsState(initial = Calendar.getInstance().get(Calendar.YEAR))

    LoanCalculationForm(
        navController = navController,
        birthYear = birthYear.value,
        loanType = loanType,
        modifier = modifier
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanCalculationForm(
    navController: NavController, birthYear: Int, loanType: LoanType, modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val timeNow = System.currentTimeMillis()
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val userAge = currentYear - birthYear

    val maxTenureYears = when (loanType) {
        LoanType.PERSONAL_LOAN -> minOf(10, 60 - userAge)
        LoanType.HOUSING_LOAN -> minOf(35, 70 - userAge)
    }
    val maxTenureMonths = minOf(maxTenureYears * 12, 120)

    var interestRate by remember {
        mutableStateOf("")
    }
    var loanAmount by remember {
        mutableStateOf("")
    }
    var numberOfRepayment by remember {
        mutableStateOf("")
    }

    val datePickerState = rememberDatePickerState(
        initialDisplayedMonthMillis = timeNow, yearRange = 1940..2024
    )
    val showDatePicker = remember { mutableStateOf(false) }
    val loanStartDate = remember { mutableStateOf(timeNow.toString()) }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .then(modifier)
    ) {
        Text(text = loanType.name.toTitleCase(), fontSize = 24.sp)
        Spacer(modifier = Modifier.size(16.dp))
        OutlinedTextField(value = loanAmount,
            onValueChange = {
                loanAmount = it
            },
            label = { Text(text = "Loan Amount") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            suffix = { Text(text = "RM") },
            placeholder = { Text("Enter the loan amount") },
            isError = loanAmount.toDoubleOrNull()?.let { it <= 0 } ?: false,
            modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.size(8.dp))
        OutlinedTextField(value = interestRate,
            onValueChange = {
                interestRate = it
            },
            label = { Text(text = "Interest Rate") },
            placeholder = { Text(text = "Enter the interest rate") },
            suffix = { Text(text = "%") },
            isError = interestRate.toDoubleOrNull()?.let { it <= 0.0 || it > 100.0 } ?: false,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.size(8.dp))
        OutlinedTextField(value = numberOfRepayment,
            onValueChange = {
                numberOfRepayment = it
            },
            label = {
                Text(text = "Number of Repayment")
            },
            placeholder = { Text("Enter the number of months (up to $maxTenureMonths months)") },
            trailingIcon = {
                if (numberOfRepayment.isNotEmpty()) {
                    Icon(imageVector = Icons.Default.Info,
                        contentDescription = "Repayment Info",
                        modifier = Modifier.clickable {
                            Toast.makeText(
                                context,
                                "Repayment months should be between 1 and $maxTenureMonths.",
                                Toast.LENGTH_SHORT
                            ).show()
                        })
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            isError = numberOfRepayment.toDoubleOrNull()?.let { it <= 0 || it > maxTenureMonths }
                ?: false,
            modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.size(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(readOnly = true,
                value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                    Date(
                        datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                    )
                ),
                label = {
                    Text(
                        text = "Loan Start Date"
                    )
                },
                onValueChange = {},
                modifier = Modifier.weight(0.8f)
            )
            IconButton(
                onClick = { showDatePicker.value = true }, modifier = Modifier.weight(0.2f)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_calendar_month_outlined),
                    contentDescription = "Calendar"
                )
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        if (showDatePicker.value) {
            CustomDialog(onDismissRequest = { showDatePicker.value = false }, confirmButton = {
                TextButton(
                    onClick = { showDatePicker.value = false },
                    enabled = datePickerState.selectedDateMillis != null
                ) {
                    Text(text = "Confirm")
                }
            }, dismissButton = {
                TextButton(onClick = { showDatePicker.value = false }) {
                    Text(text = "Dismiss")
                }
            }) {
                loanStartDate.value = datePickerState.selectedDateMillis.toString()
                DatePicker(state = datePickerState)
            }
        }
        Button(
            onClick = {
                val amount = loanAmount.toDoubleOrNull()
                val rate = interestRate.toDoubleOrNull()
                val months = numberOfRepayment.toIntOrNull()

                if (amount != null && rate != null && months != null && months in 1..maxTenureMonths) {
                    navController.navigate(
                        Screens.Results.parseResultArgument(
                            CalculationInput(
                                type = loanType,
                                amount = loanAmount.toDouble(),
                                period = numberOfRepayment.toInt(),
                                interest = interestRate.toDouble(),
                                startDate = datePickerState.selectedDateMillis ?: timeNow
                            )
                        )
                    )
                } else {
                    Toast.makeText(
                        context, "Please fill out all fields correctly.", Toast.LENGTH_SHORT
                    ).show()
                }
            }, modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Calculate")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CalculationScreenPreview() {
    CalculationScreen(
        navController = NavController(LocalContext.current), loanType = LoanType.HOUSING_LOAN
    )
}