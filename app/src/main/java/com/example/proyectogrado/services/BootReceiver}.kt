package com.example.proyectogrado.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    private val TAG = "BootReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Dispositivo reiniciado. Intentando iniciar AccessibilityService.")
            // Aquí puedes añadir lógica para reiniciar tu servicio de accesibilidad si es necesario
            // Por ejemplo, enviar un Intent para iniciar el servicio si no está ya en ejecución.
            // val serviceIntent = Intent(context, AppBlockerAccessibilityService::class.java)
            // context.startService(serviceIntent)
        }
    }
}
