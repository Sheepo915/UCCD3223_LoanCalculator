package com.utar.loancalculator.ui.component

import android.util.Log
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MonthPicker(
    startDate: Long,
    endDate: Long,
    currentMonth: Int,
    currentYear: Int,
    onApply: (Int, Int) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val months = listOf(
        "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
    )
    val startCalendar = Calendar.getInstance().apply { timeInMillis = startDate }
    val endCalendar = Calendar.getInstance().apply { timeInMillis = endDate }
    val startYear = startCalendar.get(Calendar.YEAR)
    val startMonth = startCalendar.get(Calendar.MONTH)
    val endYear = endCalendar.get(Calendar.YEAR)
    val endMonth = endCalendar.get(Calendar.MONTH)

    var month by remember {
        mutableStateOf(months[currentMonth])
    }
    var year by remember {
        mutableIntStateOf(currentYear)
    }

    val interactionSource = remember {
        MutableInteractionSource()
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .wrapContentSize()
                .then(modifier)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .size(35.dp)
                        .rotate(90f)
                        .clickable(indication = null,
                            interactionSource = interactionSource,
                            onClick = {
                                year--
                            }),
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    text = year.toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    modifier = Modifier
                        .size(35.dp)
                        .rotate(-90f)
                        .clickable(indication = null,
                            interactionSource = interactionSource,
                            onClick = {
                                year++
                            }),
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null
                )
            }
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.Center,
            ) {
                months.forEachIndexed { index, it ->
                    val isSelectable =
                        (year > startYear || index >= startMonth) && (year < endYear || index <= endMonth) && year >= startYear && year <= endYear

                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clickable(enabled = isSelectable,
                                indication = null,
                                interactionSource = interactionSource,
                                onClick = {
                                    if (isSelectable) {
                                        month = it
                                    }
                                })
                            .background(
                                color = Color.Transparent
                            ), contentAlignment = Alignment.Center
                    ) {
                        val animatedSize by animateDpAsState(
                            targetValue = if (month == it) 60.dp else 0.dp, animationSpec = tween(
                                durationMillis = 500, easing = LinearOutSlowInEasing
                            ), label = "Button animation"
                        )
                        Box(
                            modifier = Modifier
                                .size(animatedSize)
                                .background(
                                    color = if (month == it) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = CircleShape
                                )
                        )
                        Text(
                            text = it,
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (month == it) MaterialTheme.colorScheme.onPrimary else if (!isSelectable) MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                alpha = 0.3f
                            ) else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text(text = "Dismiss", color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Spacer(modifier = Modifier.size(4.dp))
                TextButton(onClick = {
                    onApply(
                        months.indexOf(month) + 1, year
                    )
                }) {
                    Text(text = "Confirm", color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}

@Preview
@Composable
private fun MonthPicker() {
    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH) + 1
    val currentYear = calendar.get(Calendar.YEAR)

    val startDate = 1722155005981
    val endDate = 1769588605981

    MonthPicker(startDate = startDate,
        endDate = endDate,
        currentMonth = currentMonth,
        currentYear = currentYear,
        onApply = { month, year ->
            Log.d("DEBUG", "month: $month, year: $year")
        },
        onDismissRequest = {})
}
