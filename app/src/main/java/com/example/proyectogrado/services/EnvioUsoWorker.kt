package com.example.proyectogrado.services

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.proyectogrado.utils.PreferenciasEstudiante
import com.google.firebase.firestore.FirebaseFirestore
import com.example.proyectogrado.services.ServicioUsoApps

class EnvioUsoWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val firestore = FirebaseFirestore.getInstance()
        val servicio = ServicioUsoApps(context)
        val idEstudiante = PreferenciasEstudiante.obtenerId(context)

        if (idEstudiante.isBlank()) return Result.success()

        val fecha = servicio.obtenerFechaActual()
        val appsUsadas = servicio.obtenerUsoDeHoy()

        if (appsUsadas.isEmpty()) return Result.success()

        val coleccion = firestore.collection("usoApps")
            .document(idEstudiante)
            .collection(fecha)

        appsUsadas.forEach { app ->
            coleccion.add(app)
        }

        return Result.success()
    }
}
