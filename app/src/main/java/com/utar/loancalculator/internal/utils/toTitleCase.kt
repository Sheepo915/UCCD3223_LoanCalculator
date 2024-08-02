package com.utar.loancalculator.internal.utils

fun String.toTitleCase(): String {
    return split("_", " ").joinToString(" ") { it.lowercase().replaceFirstChar { char -> char.uppercase() } }
}