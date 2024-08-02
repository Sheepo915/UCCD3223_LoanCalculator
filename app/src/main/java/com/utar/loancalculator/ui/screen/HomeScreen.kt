package com.utar.loancalculator.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.utar.loancalculator.R
import com.utar.loancalculator.internal.dataclass.CalculationInput
import com.utar.loancalculator.internal.enums.LoanType
import com.utar.loancalculator.ui.component.ListCalculations
import java.util.Date

@Composable
fun HomeScreen(navHost: NavController, modifier: Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(text = "Calculate", style = MaterialTheme.typography.titleMedium)
                CalculationButton(
                    onClick = {
                        navHost.navigate(Screens.Calculation.parseCalculationArgument(LoanType.HOUSING_LOAN))
                    },
                    imagePainter = painterResource(id = R.drawable.housing_loan),
                    text = "Housing Loan",
                    icon = ImageVector.vectorResource(R.drawable.ic_house_outlined)
                )
                CalculationButton(
                    onClick = {
                        navHost.navigate(
                            Screens.Calculation.parseCalculationArgument(
                                LoanType.PERSONAL_LOAN
                            )
                        )
                    },
                    imagePainter = painterResource(id = R.drawable.personal_loan),
                    text = "Personal Loan",
                    icon = ImageVector.vectorResource(R.drawable.ic_person_outlined)
                )
            }
        }
    }
}


@Composable
fun CalculationButton(
    onClick: () -> Unit, imagePainter: Painter, text: String, icon: ImageVector
) {
    OutlinedCard(
        onClick = onClick, modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Image(
            painter = imagePainter,
            contentDescription = text,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = text)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(navHost = NavHostController(LocalContext.current), modifier = Modifier)
}