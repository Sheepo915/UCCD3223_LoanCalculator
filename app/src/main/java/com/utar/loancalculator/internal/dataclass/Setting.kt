package com.utar.loancalculator.internal.dataclass

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class Setting(
    val title: String,
    val textColor: Color? = Color.Unspecified,
    val icon: ImageVector,
    val iconColor: Color? = Color.Unspecified,
    val onClick: () -> Unit
)
