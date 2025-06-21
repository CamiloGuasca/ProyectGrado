package com.example.proyectogrado.services

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object EnvioUsoScheduler {
    fun programarEnvioDiario(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<EnvioUsoWorker>(15, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED) // Solo con internet
                    .build()
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "envioUsoDiario",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }
}
