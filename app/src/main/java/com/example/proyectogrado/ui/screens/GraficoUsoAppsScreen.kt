package com.example.proyectogrado.ui.screens

import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.proyectogrado.domain.model.AppUso
import com.example.proyectogrado.services.ServicioUsoApps
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.firestore.FirebaseFirestore
import android.app.DatePickerDialog
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GraficoUsoAppsScreen(
    estudianteId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Fecha seleccionada por el usuario
    var fechaSeleccionada by remember { mutableStateOf(dateFormat.format(calendar.time)) }
    var appsUso by remember { mutableStateOf<List<AppUso>>(emptyList()) }

    // Cargar los datos cada vez que cambia la fecha
    LaunchedEffect(fechaSeleccionada) {
        val db = FirebaseFirestore.getInstance()
        db.collection("usoApps")
            .document(estudianteId)
            .collection(fechaSeleccionada)
            .get()
            .addOnSuccessListener { result ->
                appsUso = result.mapNotNull { it.toObject(AppUso::class.java) }
            }
            .addOnFailureListener {
                Log.e("GraficoUso", "Error al obtener datos de uso: ${it.message}")
            }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Gráfico de uso de apps del estudiante",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Fecha seleccionada: $fechaSeleccionada")

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    fechaSeleccionada = dateFormat.format(calendar.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }) {
            Text("Seleccionar fecha")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (appsUso.isEmpty()) {
            Text("No hay datos de uso para la fecha seleccionada.")
        } else {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                factory = { ctx ->
                    val barChart = BarChart(ctx)

                    val entries = appsUso.mapIndexed { index, app ->
                        BarEntry(index.toFloat(), app.tiempoMin.toFloat())
                    }

                    val dataSet = BarDataSet(entries, "Minutos de uso")
                    dataSet.color = Color.rgb(70, 130, 180)

                    val barData = BarData(dataSet)
                    barData.barWidth = 0.9f

                    barChart.data = barData
                    barChart.setFitBars(true)
                    barChart.xAxis.valueFormatter = IndexAxisValueFormatter(appsUso.map { it.nombre })
                    barChart.xAxis.granularity = 1f
                    barChart.xAxis.isGranularityEnabled = true
                    barChart.axisLeft.axisMinimum = 0f
                    barChart.axisRight.isEnabled = false
                    barChart.description = Description().apply { text = "" }
                    barChart.invalidate()

                    barChart
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}

