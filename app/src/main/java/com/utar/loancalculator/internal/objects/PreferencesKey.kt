package com.utar.loancalculator.internal.objects

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey

object PreferencesKeys {
    val IS_BIRTH_YEAR_SET: Preferences.Key<Boolean> = booleanPreferencesKey("is_birth_year_set")
    val BIRTH_YEAR: Preferences.Key<Int> = intPreferencesKey("birth_year")
}