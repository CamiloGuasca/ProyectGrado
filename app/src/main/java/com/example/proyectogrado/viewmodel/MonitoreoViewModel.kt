package com.example.proyectogrado.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.proyectogrado.data.repository.UsoAppRepository
import com.example.proyectogrado.domain.model.AppUso

class MonitoreoViewModel : ViewModel() {
    private val repo = UsoAppRepository()

    var listaApps by mutableStateOf<List<AppUso>>(emptyList())
        private set

    fun cargarDatos(fecha: String) {
        repo.obtenerAppsPorFecha(fecha) {
            listaApps = it
        }
    }

    fun guardarApp(fecha: String, app: AppUso) {
        repo.guardarAppUso(fecha, app)
    }
}
