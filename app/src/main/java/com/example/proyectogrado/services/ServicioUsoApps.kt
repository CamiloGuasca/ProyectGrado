package com.example.proyectogrado.services

import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.proyectogrado.domain.model.AppUso
import java.text.SimpleDateFormat
import java.util.*

class ServicioUsoApps(private val context: Context) {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun obtenerUsoDeHoy(): List<AppUso> {
        val usoApps = mutableListOf<AppUso>()

        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        Log.d("UsoDeApps", "📅 Consultando desde $startTime hasta $endTime")

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        stats?.filter { it.totalTimeInForeground > 0 }?.forEach {
            val nombreApp = it.packageName
            val tiempoMin = (it.totalTimeInForeground / 60000).toInt()
            Log.d("UsoDeApps", "🟢 App: $nombreApp - Min: $tiempoMin")
            usoApps.add(AppUso(nombre = nombreApp, tiempoMin = tiempoMin))
        }

        Log.d("UsoDeApps", "🔍 Total apps detectadas: ${usoApps.size}")
        return usoApps
    }

    fun obtenerFechaActual(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
