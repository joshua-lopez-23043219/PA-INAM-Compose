package com.example.pa_inam.Composables

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pa_inam.network.CategoryValue
import com.example.pa_inam.network.OlapRepository
import com.example.pa_inam.network.PassFailStats

import kotlinx.coroutines.launch

class DashboardCuboViewModel : ViewModel() {

    // Series para los gráficos
    val yearSeries = mutableStateOf<List<CategoryValue>>(emptyList())
    val shiftSeries = mutableStateOf<List<CategoryValue>>(emptyList())
    val passFail = mutableStateOf<PassFailStats?>(null)

    // Estado general
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                // Carga de datos desde el repositorio OLAP
                yearSeries.value = OlapRepository.getYearSeries()
                shiftSeries.value = OlapRepository.getShiftSeries()
                passFail.value = OlapRepository.getPassFailStats()

                // Si luego quieres más gráficos:
                // topSubjects.value = OlapRepository.getTopSubjectsReprob()
                // levelRegSeries.value = OlapRepository.getLevelRegistrationSeries()
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Error al cargar datos del cubo"
            } finally {
                isLoading.value = false
            }
        }
    }
}