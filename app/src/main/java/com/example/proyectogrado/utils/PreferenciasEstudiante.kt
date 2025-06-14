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

    private val ID_KEY = stringPreferencesKey("id_estudiante")

    fun guardarId(context: Context, id: String) {
        runBlocking {
            context.dataStore.edit { prefs ->
                prefs[ID_KEY] = id
            }
        }
    }

    fun obtenerId(context: Context): String {
        return runBlocking {
            context.dataStore.data.first()[ID_KEY] ?: ""
        }
    }
}
