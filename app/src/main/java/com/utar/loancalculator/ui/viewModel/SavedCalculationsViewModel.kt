package com.utar.loancalculator.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.utar.loancalculator.internal.dataclass.db.AppDatabase
import com.utar.loancalculator.internal.dataclass.db.SavedCalculation
import com.utar.loancalculator.internal.dataclass.db.SavedCalculationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SavedCalculationsViewModel(application: Application) : AndroidViewModel(application) {
    val getCalculations: Flow<List<SavedCalculation>>
    private val repository: SavedCalculationRepository

    init {
        val savedCalculationDao = AppDatabase.getDatabase(application).savedCalculationDao()
        repository = SavedCalculationRepository(savedCalculationDao)

        getCalculations = repository.getCalculations
    }

    fun addCalculation(calculationData: SavedCalculation) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addCalculation(calculationData)
        }
    }

    fun batchDeleteCalculation(calculationDataList: List<SavedCalculation>) {
        viewModelScope.launch(Dispatchers.IO) {
            calculationDataList.forEach {
                repository.deleteCalculation(it)
            }
        }
    }

    fun clearCalculation() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearCalculation()
        }
    }
}
