package com.example.proyectogrado.viewmodel

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "configuracion_estudiante")

class EstudianteDataStore(private val context: Context) {

    companion object {
        val ESTUDIANTE_ID_KEY = stringPreferencesKey("estudiante_id")
    }

    suspend fun guardarEstudianteId(id: String) {
        context.dataStore.edit { preferences ->
            preferences[ESTUDIANTE_ID_KEY] = id
        }
    }

    suspend fun obtenerEstudianteId(): String? {
        val flow = context.dataStore.data.map { prefs ->
            prefs[ESTUDIANTE_ID_KEY]
        }
        return flow.first()
    }
}
