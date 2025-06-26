// app/src/main/java/com/example/proyectogrado/viewmodel/EnviarUsoViewModel.kt
package com.example.proyectogrado.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectogrado.services.EnvioUsoWorker
import com.example.proyectogrado.utils.PreferenciasEstudiante
import kotlinx.coroutines.launch
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import android.util.Log
import java.util.concurrent.TimeUnit

class EnviarUsoViewModel(application: Application) : AndroidViewModel(application) {

    fun programarEnvioDeUso() {
        Log.d("EnviarUsoViewModel", "Programando envío de uso...")
        // **** CORRECCIÓN: Pasa el contexto a getIdEstudiante() ****
        val idEstudiante = PreferenciasEstudiante.getIdEstudiante(getApplication()) // Usar getApplication()

        if (idEstudiante.isBlank()) { // Usar isBlank() para cadenas vacías o solo espacios en blanco
            Log.e("EnviarUsoViewModel", "ID de estudiante no encontrado para programar envío de uso.")
            return
        }

        val envioUsoRequest = OneTimeWorkRequestBuilder<EnvioUsoWorker>()
            .setInitialDelay(5, TimeUnit.MINUTES)
            .addTag("EnvioUsoDiario_${idEstudiante}")
            .build()

        WorkManager.getInstance(getApplication()).enqueue(envioUsoRequest)
        Log.d("EnviarUsoViewModel", "Tarea de envío de uso programada.")
    }
}
