package com.example.proyectogrado.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

object PreferenciasEstudiante {

    private val KEY_ID_ESTUDIANTE = stringPreferencesKey("id_estudiante")

    fun guardarIdEstudiante(context: Context, id: String) {
        runBlocking {
            context.dataStore.edit { prefs ->
                prefs[KEY_ID_ESTUDIANTE] = id
            }
        }
    }

    fun obtenerIdEstudiante(context: Context): String {
        return runBlocking {
            context.dataStore.data
                .map { prefs -> prefs[KEY_ID_ESTUDIANTE] ?: "" }
                .first()
        }
    }
}
