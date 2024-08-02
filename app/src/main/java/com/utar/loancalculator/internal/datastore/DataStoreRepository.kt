package com.utar.loancalculator.internal.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.utar.loancalculator.internal.objects.PreferencesKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

const val PREFERENCES_NAME = "settings"

class DataStoreRepository(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)
    }

    val getBirthYear: Flow<Int> = context.dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        preferences[PreferencesKeys.BIRTH_YEAR] ?: 0
    }


    val getIsBirthYearSet: Flow<Boolean> = context.dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        preferences[PreferencesKeys.IS_BIRTH_YEAR_SET] ?: false
    }

    suspend fun updateBirthYear(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BIRTH_YEAR] = value
        }
    }

    suspend fun updateIsBirthYearSet(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_BIRTH_YEAR_SET] = value
        }
    }
}