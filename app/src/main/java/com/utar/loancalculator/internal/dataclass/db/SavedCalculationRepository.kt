package com.utar.loancalculator.internal.dataclass.db

import com.utar.loancalculator.internal.dao.SavedCalculationDao

class SavedCalculationRepository(private val savedCalculationDao: SavedCalculationDao) {
    val getCalculations = savedCalculationDao.getCalculations()

    suspend fun addCalculation(calculation: SavedCalculation) {
        savedCalculationDao.insertCalculation(calculation = calculation)
    }

    suspend fun deleteCalculation(calculation: SavedCalculation) {
        savedCalculationDao.deleteCalculation(calculation = calculation)
    }

    suspend fun clearCalculation() {
        savedCalculationDao.clearCalculations()
    }
}