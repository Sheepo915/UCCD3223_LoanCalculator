package com.utar.loancalculator.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utar.loancalculator.R
import com.utar.loancalculator.internal.dataclass.CalculationData
import com.utar.loancalculator.internal.dataclass.CalculationInput
import com.utar.loancalculator.internal.dataclass.db.SavedCalculation
import com.utar.loancalculator.internal.enums.LoanType
import com.utar.loancalculator.internal.utils.toTitleCase
import java.util.Locale

@Composable
fun ListCalculations(
    onClick: (SavedCalculation) -> Unit,
    calculationInputs: List<SavedCalculation>,
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors()
) {
    calculationInputs.forEachIndexed { _, calculation ->
        OutlinedCard(
            onClick = {
                onClick(
                    SavedCalculation(
                        id = calculation.id,
                        type = calculation.type,
                        period = calculation.period,
                        amount = calculation.amount,
                        interest = calculation.interest,
                        startDate = calculation.startDate
                    )
                )
            },
            colors = colors,
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .then(modifier)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (calculation.type == LoanType.PERSONAL_LOAN) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_person_outlined),
                            contentDescription = "Personal Loan"
                        )
                    } else if (calculation.type == LoanType.HOUSING_LOAN) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_house_outlined),
                            contentDescription = "Housing Loan"
                        )
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = calculation.type.name.toTitleCase(),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
                Row {
                    Text(
                        text = "MYR %.2f".format(locale = Locale.getDefault(), calculation.amount),
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
                Row {
                    val years = calculation.period / 12.0

                    Text(
                        text = if (calculation.period > 12) {
                            if (years % 1.0 == 0.0) {
                                "%.0f years".format(locale = Locale.getDefault(), years)
                            } else {
                                "%.1f years".format(locale = Locale.getDefault(), years)
                            }
                        } else {
                            "%d months".format(locale = Locale.getDefault(), calculation.period)
                        }, fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.size(2.dp))
                    Text(text = "•")
                    Spacer(modifier = Modifier.size(2.dp))
                    Text(
                        text = "%.2f per annum".format(
                            locale = Locale.getDefault(), calculation.interest
                        ), fontSize = 14.sp
                    )
                }
            }
        }
    }
}