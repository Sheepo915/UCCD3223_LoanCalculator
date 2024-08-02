package com.utar.loancalculator.ui.screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.utar.loancalculator.R
import com.utar.loancalculator.internal.dataclass.AmortizationScheduleEntry
import com.utar.loancalculator.internal.dataclass.db.SavedCalculation
import com.utar.loancalculator.internal.enums.LoanType
import com.utar.loancalculator.internal.utils.generateHousingAmortizationSchedule
import com.utar.loancalculator.internal.utils.generatePersonalAmortizationSchedule
import com.utar.loancalculator.internal.utils.toTitleCase
import com.utar.loancalculator.ui.component.MonthPicker
import com.utar.loancalculator.ui.viewModel.SavedCalculationsViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.ceil

const val PAGE_SIZE = 12

@Composable
fun ResultScreen(
    savedCalculationsViewModel: SavedCalculationsViewModel,
    navController: NavController,
    loanType: LoanType,
    principal: Double,
    annualInterestRate: Double,
    months: Int,
    startDate: Long,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val amortizationSchedule = generateAmortizationSchedule(
        loanType = loanType,
        principal = principal,
        annualInterestRate = annualInterestRate,
        months = months,
        startDate = startDate
    )

    val listState = rememberLazyListState()

    var showFilterDialog by remember { mutableStateOf(false) }
    var isFiltered by remember { mutableStateOf(false) }

    var filterYear by remember { mutableIntStateOf(0) }
    var filterMonth by remember { mutableIntStateOf(0) }
    var filteredSchedule by remember { mutableStateOf(amortizationSchedule) }

    val totalPageBinding: Int = ceil(amortizationSchedule.size.toDouble() / PAGE_SIZE).toInt()
    val totalPage by remember {
        mutableIntStateOf(
            maxOf(
                1, totalPageBinding
            )
        )
    }
    var currentPage by remember { mutableIntStateOf(1) }

    LaunchedEffect(filterYear, filterMonth, isFiltered) {
        filteredSchedule = if (isFiltered) {
            amortizationSchedule.filter { entry ->
                val cal = Calendar.getInstance().apply { timeInMillis = entry.paymentDate }
                val entryYear = cal.get(Calendar.YEAR)
                val entryMonth = cal.get(Calendar.MONTH) + 1
                (filterYear == 0 || entryYear == filterYear) && (filterMonth == 0 || entryMonth == filterMonth)
            }
        } else {
            amortizationSchedule
        }
    }

    LaunchedEffect(currentPage) {
        listState.scrollToItem(0)
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxSize()
            .then(modifier)
    ) {
        when (loanType) {
            LoanType.PERSONAL_LOAN -> Text(
                text = LoanType.PERSONAL_LOAN.name.toTitleCase(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
            )

            LoanType.HOUSING_LOAN -> Text(
                text = LoanType.HOUSING_LOAN.name.toTitleCase(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(text = "Loan Amount: RM %.2f".format(locale = Locale.getDefault(), principal))
            Text(
                text = "Interest Rate Per Annum: %.2f".format(
                    locale = Locale.getDefault(), annualInterestRate
                ).plus("%")
            )
            Text(text = "Number of Repayment: $months")
            HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(vertical = 4.dp))
            Text(
                text = "Last Payment Date: ${
                    SimpleDateFormat(
                        "dd/MM/yyyy", Locale.getDefault()
                    ).format(amortizationSchedule.last().paymentDate)
                }"
            )
            Text(
                text = "Total Repayment: RM %.2f".format(
                    locale = Locale.getDefault(), amortizationSchedule.last().accumulatedRepayment
                )
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (navController.previousBackStackEntry?.destination?.route == Screens.Calculation.route) {
                OutlinedButton(onClick = {
                    savedCalculationsViewModel.addCalculation(
                        calculationData = SavedCalculation(
                            id = 0,
                            amount = principal,
                            interest = annualInterestRate,
                            period = months,
                            startDate = startDate,
                            type = loanType
                        )
                    )
                    Toast.makeText(
                        context, "Calculation saved to local device!", Toast.LENGTH_SHORT
                    ).show()
                }) {
                    Text(text = "Save")
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            if (isFiltered) {
                IconButton(onClick = { isFiltered = false }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(
                            id = R.drawable.ic_filter_alt_outlined
                        ),
                        contentDescription = "Remove Filter",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                IconButton(onClick = { showFilterDialog = true }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(
                            id = R.drawable.ic_filter_outlined
                        ), contentDescription = "Filter", tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
            IconButton(enabled = currentPage != 1, onClick = {
                if (currentPage in 2..totalPage) {
                    currentPage -= 1
                }
            }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Previous Page",
                    modifier = Modifier.rotate(-90f)
                )
            }
            Text(text = "$currentPage/$totalPage")
            IconButton(enabled = currentPage != totalPage, onClick = {
                if (currentPage in 1..<totalPage) {
                    currentPage += 1
                }
            }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Next Page",
                    modifier = Modifier.rotate(90f)
                )
            }
        }
        if (showFilterDialog) {
            FilterDialog(onDismissRequest = {
                showFilterDialog = false
            }, onApply = { year, month ->
                filterYear = year
                filterMonth = month
                isFiltered = true
                showFilterDialog = false
            }, startDate = startDate, endDate = amortizationSchedule.last().paymentDate)
        }
        AmortizationScheduleStackedTable(
            listState = listState,
            amortizationSchedule = filteredSchedule,
            currentPage = currentPage
        )
    }
}

fun generateAmortizationSchedule(
    loanType: LoanType, principal: Double, annualInterestRate: Double, months: Int, startDate: Long
): List<AmortizationScheduleEntry> {
    return when (loanType) {
        LoanType.PERSONAL_LOAN -> generatePersonalAmortizationSchedule(
            principal, annualInterestRate, months, startDate
        )

        LoanType.HOUSING_LOAN -> generateHousingAmortizationSchedule(
            principal, annualInterestRate, months, startDate
        )
    }
}

@Composable
fun FilterDialog(
    onDismissRequest: () -> Unit, onApply: (Int, Int) -> Unit, startDate: Long, endDate: Long
) {
    val selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    val currentYear = selectedDate.get(Calendar.YEAR)
    val currentMonth = selectedDate.get(Calendar.MONTH) + 1

    MonthPicker(
        startDate = startDate,
        endDate = endDate,
        currentMonth = currentMonth,
        currentYear = currentYear,
        onApply = { month, year ->
            onApply(year, month)
            onDismissRequest()
        },
        onDismissRequest = onDismissRequest
    )
}

@Composable
fun AmortizationScheduleStackedTable(
    listState: LazyListState,
    amortizationSchedule: List<AmortizationScheduleEntry>,
    currentPage: Int
) {
    val startIndex = (currentPage - 1) * PAGE_SIZE
    val endIndex = (startIndex + PAGE_SIZE).coerceAtMost(amortizationSchedule.size)

    val currentEntries = amortizationSchedule.subList(startIndex, endIndex)

    LazyColumn(
        state = listState, modifier = Modifier.fillMaxSize()
    ) {
        items(currentEntries) {
            StackedTableRow(entry = it)
        }
    }
}

@Composable
fun StackedTableRow(entry: AmortizationScheduleEntry) {
    val formattedDate =
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(entry.paymentDate)

    OutlinedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            DataRow(label = "Payment Number", value = entry.paymentNumber.toString())
            DataRow(label = "Payment Date", value = formattedDate)
            DataRow(
                label = "Principal Paid (RM)", value = "%.2f".format(entry.principalPayment)
            )
            DataRow(label = "Interest Paid (RM)", value = "%.2f".format(entry.interestPayment))
            DataRow(label = "Monthly Repayment (RM)", value = "%.2f".format(entry.monthlyPayment))
            DataRow(label = "Remaining Balance (RM)", value = "%.2f".format(entry.remainingBalance))
            DataRow(
                label = "Accumulative Repayment (RM)", value = "%.2f".format(entry.accumulatedRepayment)
            )
        }
    }
}

@Composable
fun DataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(0.6f),
            fontSize = 14.sp,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            text = value,
            modifier = Modifier.weight(0.4f),
            fontSize = 14.sp,
            textAlign = TextAlign.Start
        )
    }
}
