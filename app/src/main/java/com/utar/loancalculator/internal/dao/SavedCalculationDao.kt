package com.utar.loancalculator.internal.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.utar.loancalculator.internal.dataclass.db.SavedCalculation
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedCalculationDao {
    @Insert
    suspend fun insertCalculation(calculation: SavedCalculation)

    @Delete
    suspend fun deleteCalculation(calculation: SavedCalculation)

    @Query("DELETE FROM SavedCalculation")
    suspend fun clearCalculations()

    @Query("SELECT * FROM SavedCalculation")
    fun getCalculations(): Flow<List<SavedCalculation>>
}