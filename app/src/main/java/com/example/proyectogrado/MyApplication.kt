// app/src/main/java/com/example/proyectogrado/MyApplication.kt
package com.example.proyectogrado

import android.app.Application
import android.util.Log
import com.jakewharton.threetenabp.AndroidThreeTen // Esta importación funcionará si la dependencia está bien
// No necesitas importar PreferenciasEstudiante aquí si no la inicializas globalmente.
// Si tus funciones de PreferenciasEstudiante requieren contexto, lo pasarán directamente.
// import com.example.proyectogrado.utils.PreferenciasEstudiante

class MyApplication : Application() {
    private val TAG = "MyApplication"

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "MyApplication onCreate llamado.")

        // Inicializa ThreeTenABP para las APIs de tiempo (si minSdk < 26).
        AndroidThreeTen.init(this)

        // IMPORTANTE: La versión fusionada de PreferenciasEstudiante NO TIENE un método init().
        // Sus funciones (guardarId, obtenerId, etc.) ahora esperan el 'Context' directamente.
        // Por lo tanto, ELIMINAMOS esta línea.
        // PreferenciasEstudiante.init(this) // <--- ¡ESTA LÍNEA DEBE SER ELIMINADA!
        // Log.d(TAG, "PreferenciasEstudiante inicializado desde MyApplication.onCreate") // <--- Y esta también
    }
}
