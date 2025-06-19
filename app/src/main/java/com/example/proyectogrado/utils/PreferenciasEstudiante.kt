package com.example.proyectogrado.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

// ✅ DataStore de preferencias como extensión del Context
val Context.dataStore by preferencesDataStore(name = "configuracion_estudiante")

object PreferenciasEstudiante {
    fun guardarId(context: Context, id: String) {
        val prefs = context.getSharedPreferences("prefs_estudiante", Context.MODE_PRIVATE)
        prefs.edit().putString("id", id).apply()
    }

    fun obtenerId(context: Context): String {
        val prefs = context.getSharedPreferences("prefs_estudiante", Context.MODE_PRIVATE)
        return prefs.getString("id", "") ?: ""
    }

    fun borrarId(context: Context) {
        val prefs = context.getSharedPreferences("prefs_estudiante", Context.MODE_PRIVATE)
        prefs.edit().remove("id").apply()
    }
}
