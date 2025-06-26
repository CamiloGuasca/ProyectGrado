// app/src/main/java/com/example/proyectogrado/utils/PreferenciasEstudiante.kt
package com.example.proyectogrado.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

// Objeto singleton para manejar las preferencias del estudiante.
// Esta versión requiere que se pase el 'Context' a cada llamada de función pública.
object PreferenciasEstudiante {

    private const val PREFS_FILE_NAME_COMPANERO = "prefs_estudiante"
    private const val KEY_ID_COMPANERO = "id"

    private const val KEY_STUDENT_ID_TUYO = "student_id" // Mantener, pero usaremos KEY_ID_COMPANERO internamente
    private const val TAG = "PreferenciasEstudiante"

    /**
     * Obtiene una instancia de SharedPreferences para el contexto dado.
     * Siempre usa el nombre de archivo de preferencias de tu compañero para asegurar la compatibilidad.
     * @param context El Contexto de la aplicación o actividad.
     * @return Una instancia de SharedPreferences.
     */
    private fun getPrefs(context: Context): SharedPreferences {
        // Usamos applicationContext para evitar posibles fugas de memoria con contextos de actividad.
        return context.applicationContext.getSharedPreferences(PREFS_FILE_NAME_COMPANERO, Context.MODE_PRIVATE)
    }

    // --- Funciones que tu compañero usaba (y que ahora aceptan Context) ---
    /**
     * Guarda el ID del estudiante en SharedPreferences.
     * @param context El Contexto para acceder a SharedPreferences.
     * @param id El ID del estudiante a guardar.
     */
    fun guardarId(context: Context, id: String) {
        val editor = getPrefs(context).edit()
        editor.putString(KEY_ID_COMPANERO, id)
        editor.apply()
        Log.d(TAG, "ID de estudiante '$id' GUARDADO en preferencias (función guardarId de compañero).")
    }

    /**
     * Obtiene el ID del estudiante de SharedPreferences.
     * @param context El Contexto para acceder a SharedPreferences.
     * @return El ID del estudiante o una cadena vacía ("") si no se encuentra.
     */
    fun obtenerId(context: Context): String {
        val id = getPrefs(context).getString(KEY_ID_COMPANERO, "")
        Log.d(TAG, "ID de estudiante '$id' RECUPERADO de preferencias (función obtenerId de compañero).")
        return id ?: ""
    }

    /**
     * Borra el ID del estudiante de SharedPreferences.
     * @param context El Contexto para acceder a SharedPreferences.
     */
    fun borrarId(context: Context) {
        getPrefs(context).edit().remove(KEY_ID_COMPANERO).apply()
        Log.d(TAG, "ID de estudiante BORRADO de preferencias (función borrarId de compañero).")
    }

    // --- Tus funciones originales (adaptadas para usar el Context) ---
    /**
     * Guarda el ID del estudiante en las preferencias.
     * Internamente usa 'guardarId'.
     * @param context Contexto necesario para acceder a las preferencias.
     * @param id El ID del estudiante a guardar.
     */
    fun saveIdEstudiante(context: Context, id: String) {
        // Aseguramos que se use la misma clave que tu compañero para unificar el almacenamiento
        guardarId(context, id)
        Log.d(TAG, "ID de estudiante '$id' GUARDADO en preferencias (tu función saveIdEstudiante).")
    }

    /**
     * Recupera el ID del estudiante de las preferencias.
     * Internamente usa 'obtenerId'.
     * @param context Contexto necesario para acceder a las preferencias.
     * @return El ID del estudiante o una cadena vacía si no se encuentra.
     */
    fun getIdEstudiante(context: Context): String {
        val id = obtenerId(context)
        Log.d(TAG, "ID de estudiante '$id' RECUPERADO de preferencias (tu función getIdEstudiante).")
        return id
    }

    /**
     * Limpia el ID del estudiante de las preferencias.
     * Internamente usa 'borrarId'.
     * @param context Contexto necesario para acceder a las preferencias.
     */
    fun clearIdEstudiante(context: Context) {
        borrarId(context)
        Log.d(TAG, "ID de estudiante LIMPIADO de preferencias (tu función clearIdEstudiante).")
    }
}
