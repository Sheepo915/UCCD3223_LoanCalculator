package com.utar.loancalculator.ui.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.utar.loancalculator.R
import com.utar.loancalculator.internal.dataclass.Setting
import com.utar.loancalculator.internal.dataclass.db.SavedCalculation
import com.utar.loancalculator.internal.datastore.DataStoreRepository
import com.utar.loancalculator.internal.enums.SettingDialogContext
import com.utar.loancalculator.ui.component.ListCalculations
import com.utar.loancalculator.ui.viewModel.SavedCalculationsViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun SettingScreen(
    savedCalculationsViewModel: SavedCalculationsViewModel,
    dataStore: DataStoreRepository,
    modifier: Modifier
) {
    val context = LocalContext.current
    val saves by savedCalculationsViewModel.getCalculations.collectAsState(initial = emptyList())

    var isDialogShown by remember {
        mutableStateOf(false)
    }
    var isDeletionConfirmationShown by remember {
        mutableStateOf(false)
    }
    var alertContext by remember {
        mutableStateOf<SettingDialogContext?>(null)
    }
    val selectedList = remember {
        mutableStateListOf<SavedCalculation>()
    }

    val birthYear = dataStore.getBirthYear.collectAsState(initial = 0)
    var temp by remember {
        mutableStateOf("")
    }

    val coroutineScope = rememberCoroutineScope()

    val dataSettings = listOf(Setting(icon = Icons.Outlined.Delete,
        iconColor = MaterialTheme.colorScheme.error,
        title = "Delete Selected Saved Data",
        onClick = {
            isDialogShown = true
            alertContext = SettingDialogContext.DELETE_DATA
        }), Setting(icon = Icons.Outlined.Clear, iconColor = MaterialTheme.colorScheme.onBackground, title = "Delete All Saved Data", onClick = {
        isDialogShown = true
        alertContext = SettingDialogContext.DELETE_ALL_DATA
    })
    )

    val birthDateSetting = listOf(
        Setting(icon = ImageVector.vectorResource(id = R.drawable.ic_calendar_month_outlined),
            iconColor = MaterialTheme.colorScheme.onBackground,
            title = "Change Birth Year",
            onClick = {
                isDialogShown = true
                alertContext = SettingDialogContext.BIRTH_YEAR
            })
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .then(modifier)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                SettingGroup(groupName = "Data", settingItem = dataSettings)
                SettingGroup(groupName = "Data", settingItem = birthDateSetting)
            }
        }
        if (isDialogShown && alertContext == SettingDialogContext.DELETE_ALL_DATA) {
            AlertDialog(onDismissRequest = { isDialogShown = false }, dismissButton = {
                TextButton(onClick = { isDialogShown = false }) {
                    Text(text = "Cancel")
                }
            }, confirmButton = {
                TextButton(onClick = {
                    isDialogShown = false
                    savedCalculationsViewModel.clearCalculation()
                    Toast.makeText(context, "All saved data has been cleared!", Toast.LENGTH_SHORT)
                        .show()
                }) {
                    Text(text = "Confirm")
                }
            }, text = {
                Text(
                    text = "Are you sure you want to delete all saved calculation?"
                )
            }, icon = {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Danger",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.rotate(180f)
                )
            })
        } else if (isDialogShown && alertContext == SettingDialogContext.DELETE_DATA) {
            Dialog(
                onDismissRequest = {
                    isDialogShown = false
                    selectedList.clear()
                },
                properties = DialogProperties(usePlatformDefaultWidth = false),
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Select the saves you wished to delete",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )
                    LazyColumn(
                        modifier = Modifier
                            .padding(8.dp)
                            .heightIn(max = 400.dp)
                    ) {
                        items(saves) { calculation ->
                            ListCalculations(
                                onClick = {
                                    if (!selectedList.contains(it)) {
                                        selectedList.add(it)
                                    } else {
                                        selectedList.remove(it)
                                    }
                                },
                                calculationInputs = listOf(calculation),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedList.contains(calculation)) MaterialTheme.colorScheme.errorContainer else Color.Unspecified
                                )
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = {
                            isDialogShown = false
                            selectedList.clear()
                        }) {
                            Text(text = "Cancel")
                        }
                        Spacer(modifier = Modifier.size(4.dp))
                        TextButton(onClick = {
                            isDeletionConfirmationShown = true
                            isDialogShown = false
                        }) {
                            Text(text = "Continue", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        } else if (isDialogShown && alertContext == SettingDialogContext.BIRTH_YEAR) {
            Dialog(
                onDismissRequest = {
                    isDialogShown = false
                },
                properties = DialogProperties(usePlatformDefaultWidth = false),
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Enter your birth year. Currently set to ${birthYear.value}",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )
                    OutlinedTextField(value = temp,
                        onValueChange = {
                            temp = it
                        },
                        label = {
                            Text(
                                text = "Birth Year"
                            )
                        },
                        placeholder = { Text(text = birthYear.value.toString()) },
                        isError = temp.toIntOrNull() !in 1940..Calendar.getInstance()
                            .get(Calendar.YEAR),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        TextButton(onClick = {
                            isDialogShown = false
                        }) {
                            Text(text = "Cancel")
                        }
                        Spacer(modifier = Modifier.size(4.dp))
                        TextButton(onClick = {
                            isDialogShown = false
                            coroutineScope.launch {
                                dataStore.updateBirthYear(temp.toInt())
                            }
                        }, enabled = temp.toIntOrNull() in 1940..Calendar.getInstance()
                            .get(Calendar.YEAR)) {
                            Text(text = "Save", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        } else if (isDeletionConfirmationShown) {
            AlertDialog(onDismissRequest = { isDeletionConfirmationShown = false },
                dismissButton = {
                    TextButton(onClick = {
                        isDeletionConfirmationShown = false
                        selectedList.clear()
                    }) {
                        Text(text = "Cancel")
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        isDeletionConfirmationShown = false
                        savedCalculationsViewModel.batchDeleteCalculation(selectedList)
                        Toast.makeText(
                            context, "All selected saves has been cleared!", Toast.LENGTH_SHORT
                        ).show()
                    }) {
                        Text(text = "Confirm")
                    }
                },
                text = {
                    Text(
                        text = "Are you sure you want to delete the selected saved calculation?"
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Danger",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.rotate(180f)
                    )
                })
        }
    }
}

@Composable
fun SettingGroup(groupName: String, settingItem: List<Setting>) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            text = groupName,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        )
        HorizontalDivider(
            thickness = 2.dp
        )
        settingItem.forEach { item ->
            SettingItem(item = item)
        }
    }
}

@Composable
fun SettingItem(item: Setting, modifier: Modifier = Modifier) {
    val outlineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)

    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            .clickable { item.onClick() }
            .drawBehind {
                val strokeWidth = 1 * density
                val y = size.height - strokeWidth / 2

                drawLine(
                    outlineColor, Offset(0f, y), Offset(size.width, y), strokeWidth
                )
            }
            .then(modifier)) {
        item.iconColor?.let {
            Icon(
                imageVector = item.icon, contentDescription = item.title, tint = it
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        item.textColor?.let {
            Text(text = item.title, style = MaterialTheme.typography.bodyMedium, color = it)
        }
    }
}