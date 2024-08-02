package com.utar.loancalculator.ui.screen

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.utar.loancalculator.internal.datastore.DataStoreRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun InitialScreen(context: Context, navController: NavController, modifier: Modifier = Modifier) {
    var visible by remember {
        mutableStateOf(false)
    }
    var moveUp by remember {
        mutableStateOf(false)
    }

    val offsetY by animateFloatAsState(
        targetValue = if (moveUp) -100f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "offset"
    )

    val dataStore = DataStoreRepository(context = context)
    var birthYear by remember { mutableStateOf("") }
    var isValidBirthDate by remember {
        mutableStateOf(false)
    }

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        visible = true
        delay(1500)
        moveUp = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .then(modifier),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = offsetY.dp)
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(durationMillis = 1000, delayMillis = 200))
            ) {
                Text(
                    text = "Welcome to Loan Calculator",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(durationMillis = 1000, delayMillis = 600))
            ) {
                Text(
                    text = "First, let's get you set up",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(durationMillis = 1000, delayMillis = 1200))
            ) {
                Text(text = "Enter your birth year", fontSize = 18.sp, textAlign = TextAlign.Center)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        AnimatedVisibility(
            visible = moveUp, enter = fadeIn(animationSpec = tween(durationMillis = 1000))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = birthYear,
                        onValueChange = {
                            birthYear = it
                            isValidBirthDate =
                                if (birthYear.isDigitsOnly() && birthYear.length == 4) {
                                    val year = birthYear.toInt()
                                    year in 1940..currentYear
                                } else {
                                    false
                                }
                        },
                        label = {
                            Text(
                                text = "Birth Year"
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.45f)
                    )
                    Spacer(modifier = Modifier.weight(0.05f))
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                dataStore.updateBirthYear(birthYear.toInt())
                                dataStore.updateIsBirthYearSet(true)
                                navController.navigate(Screens.Home.route)
                            }
                        },
                        enabled = isValidBirthDate,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .weight(0.45f),
                    ) {
                        Text(text = "Get Started")
                    }
                }
                if (!isValidBirthDate && birthYear.isNotEmpty() && birthYear.length > 4) {
                    Text(
                        text = "Please enter a valid birth date",
                        fontSize = 12.sp,
                        color = Color.Red
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun InitialScreenPreview() {
    InitialScreen(context = LocalContext.current, navController = rememberNavController())
}