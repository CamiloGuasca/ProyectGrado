package com.example.proyectogrado.ui.screens

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogrado.viewmodel.VinculacionViewModel
import com.example.proyectogrado.domain.model.Estudiante
import com.example.proyectogrado.domain.model.AppUso
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.text.font.FontWeight // Import para FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleEstudiante(
    estudianteId: String,
    vinculacionViewModel: VinculacionViewModel = viewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val textMeasurer = rememberTextMeasurer() // Para dibujar texto en Canvas

    var fechaSeleccionada by remember { mutableStateOf(dateFormat.format(calendar.time)) }
    var listaApps by remember { mutableStateOf<List<AppUso>>(emptyList()) }
    var mensajeErrorUso by remember { mutableStateOf<String?>(null) }
    var showGraphDialog by remember { mutableStateOf(false) } // Para controlar el diálogo de la gráfica

    val estudiante by vinculacionViewModel.estudianteSeleccionado.collectAsState()

    fun mapearNombre(nombrePaquete: String): String {
        val mapa = mapOf(
            "com.google.android.youtube" to "YouTube",
            "com.whatsapp" to "WhatsApp",
            "com.instagram.android" to "Instagram",
            "com.google.android.gm" to "Gmail",
            "com.android.chrome" to "Chrome",
            "com.tiktok.android" to "TikTok",
            "com.clarocolombia.miclaro" to "Mi Claro",
            "com.example.proyectogrado" to "Proyecto de Grado",
            "com.google.android.apps.messaging" to "Mensajes",
            "com.google.android.dialer" to "Teléfono",
            "com.google.android.calculator" to "Calculadora",
            "com.google.android.calendar" to "Calendario",
            "com.android.settings" to "Ajustes",
            "com.google.android.contacts" to "Contactos",
            "com.google.android.apps.maps" to "Google Maps",
            "com.google.android.music" to "Play Música",
            "com.google.android.apps.photos" to "Google Fotos",
            "com.google.android.googlequicksearchbox" to "Google Búsqueda",
            "com.google.android.videos" to "Google Play Películas",
            // Nota: "com.spotify.music" es un ejemplo, el nombre de paquete real puede variar
            "com.spotify.music" to "Spotify", // Esto es un placeholder, verifica el nombre real del paquete
            "com.spotify.music" to "Spotify", // Nombre de paquete común para Spotify
            "com.facebook.katana" to "Facebook",
            "com.facebook.lite" to "Facebook Lite",
            "com.twitter.android" to "X (Twitter)",
            "com.snapchat.android" to "Snapchat",
            "com.pinterest" to "Pinterest",
            "com.microsoft.teams" to "Microsoft Teams",
            "com.zoom.us" to "Zoom",
            "com.discord" to "Discord",
            "com.netflix.mediaclient" to "Netflix",
            "com.amazon.mShop.android.shopping" to "Amazon Shopping",
            "com.olx.mobile" to "OLX",
            "com.mercado.libre" to "Mercado Libre",
            "com.rovio.angrybirds" to "Angry Birds",
            "com.supercell.clashofclans" to "Clash of Clans",
            "com.mojang.minecraftpe" to "Minecraft PE",
            "com.pubg.krmobile" to "PUBG Mobile",
            "com.garena.game.kgid" to "Free Fire",
            "com.tencent.ig" to "PUBG Mobile (Global)",
            "com.dts.freefireth" to "Free Fire MAX",
            "com.nianticlabs.pokemongo" to "Pokémon GO",
            "com.kiloo.subwaysurf" to "Subway Surfers"
        )
        return mapa[nombrePaquete] ?: nombrePaquete.substringAfterLast('.').replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
        }.replace("_", " ") // Reemplazar guiones bajos por espacios
    }

    LaunchedEffect(estudianteId) {
        vinculacionViewModel.cargarEstudiantePorId(estudianteId)
        Log.d("DETALLE_ESTUDIANTE", "Cargando estudiante con ID: $estudianteId")
    }

    LaunchedEffect(fechaSeleccionada, estudianteId) {
        if (estudianteId.isNotBlank()) {
            FirebaseFirestore.getInstance()
                .collection("usoApps")
                .document(estudianteId)
                .collection(fechaSeleccionada)
                .get()
                .addOnSuccessListener { result ->
                    val lista = result.mapNotNull { it.toObject(AppUso::class.java) }
                    // Ordenar por tiempo de uso descendente para el gráfico
                    listaApps = lista.sortedByDescending { it.tiempoMin }
                    mensajeErrorUso = null
                    Log.d("DETALLE_ESTUDIANTE", "📦 Apps cargadas para $estudianteId el $fechaSeleccionada: ${lista.size}")
                }
                .addOnFailureListener { e ->
                    mensajeErrorUso = "❌ Error al cargar uso de apps: ${e.message}"
                    Log.e("DETALLE_ESTUDIANTE", "Error al cargar uso de apps", e)
                    listaApps = emptyList()
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(estudiante?.nombre ?: "Cargando Estudiante...") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            estudiante?.let { currentEstudiante ->
                Text(
                    text = "Detalles del Estudiante:",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Nombre: ${currentEstudiante.nombre}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "ID: ${currentEstudiante.id}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Vinculado por: ${currentEstudiante.vinculadoPor ?: "N/A"}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    text = "Monitoreo de Uso de Aplicaciones",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
                )

                Button(onClick = {
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            val fecha = Calendar.getInstance().apply {
                                set(year, month, day)
                            }
                            fechaSeleccionada = dateFormat.format(fecha.time)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Text("Filtrar por fecha")
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Fecha seleccionada: $fechaSeleccionada", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))

                if (mensajeErrorUso != null) {
                    Text(mensajeErrorUso!!, color = Color.Red)
                }

                if (listaApps.isEmpty() && mensajeErrorUso == null) {
                    Text("⚠️ No hay datos de uso para esta fecha.", color = Color.Gray)
                } else if (listaApps.isNotEmpty()) {
                    // Botón para mostrar la gráfica en un diálogo
                    Button(
                        onClick = { showGraphDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ver Gráfica de Uso")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(listaApps) { app ->
                        val nombreBonito = mapearNombre(app.nombre)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Apps,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = nombreBonito, fontWeight = FontWeight.Medium)
                                    Text(text = "Tiempo usado: ${app.tiempoMin} min", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

            } ?: run {
                Text("Estudiante no encontrado o cargando...", modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }

// ... (código anterior en PantallaDetalleEstudiante.kt, antes del AlertDialog)

    // Diálogo para mostrar la gráfica personalizada (Canvas)
    if (showGraphDialog) {
        AlertDialog(
            onDismissRequest = { showGraphDialog = false },
            title = { Text("Uso de Aplicaciones por Fecha: $fechaSeleccionada", textAlign = TextAlign.Center) },
            text = {
                if (listaApps.isNotEmpty()) {
                    // Limitar a las primeras N aplicaciones para que el gráfico no sea demasiado denso
                    val appsParaGraficar = listaApps.take(10) // Grafica las 10 apps más usadas
                    val maxTiempo = appsParaGraficar.maxOfOrNull { it.tiempoMin }?.toFloat() ?: 0f

                    // Obtén el color primario del tema aquí, antes del Canvas
                    val barColor = MaterialTheme.colorScheme.primary

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 300.dp, max = 500.dp)
                            .padding(top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Tiempo de Uso por Aplicación (Minutos)",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Aquí va el Canvas para dibujar el gráfico de barras
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            // Declaraciones de variables dentro del DrawScope
                            val canvasWidth = size.width
                            val canvasHeight = size.height
                            val barWidth = (canvasWidth / appsParaGraficar.size) * 0.7f // Ancho de cada barra
                            val gap = (canvasWidth / appsParaGraficar.size) * 0.3f / 2 // Espacio entre barras

                            val xAxisOffset = 20.dp.toPx() // Espacio para las etiquetas del eje X
                            val yAxisOffset = 40.dp.toPx() // Espacio para las etiquetas del eje Y (Minutos)
                            val chartDrawableHeight = canvasHeight - xAxisOffset - yAxisOffset // Altura real para las barras

                            // Dibujar Eje Y (vertical - Minutos)
                            drawLine(
                                color = Color.Gray,
                                start = Offset(yAxisOffset, chartDrawableHeight),
                                end = Offset(yAxisOffset, 0f),
                                strokeWidth = 2f
                            )

                            // Dibujar marcas del Eje Y
                            val numYLabels = 5
                            for (i in 0..numYLabels) {
                                val yValue = (maxTiempo / numYLabels) * i
                                val yPos = chartDrawableHeight - (yValue / maxTiempo) * chartDrawableHeight
                                drawLine(
                                    color = Color.LightGray,
                                    start = Offset(yAxisOffset, yPos),
                                    end = Offset(canvasWidth, yPos),
                                    strokeWidth = 1f,
                                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                )
                                drawText(
                                    textMeasurer = textMeasurer,
                                    text = yValue.toInt().toString(),
                                    topLeft = Offset(0f, yPos - 10.sp.toPx() / 2),
                                    style = TextStyle(fontSize = 10.sp, color = Color.DarkGray)
                                )
                            }

                            // Dibujar Eje X (horizontal - Aplicaciones)
                            drawLine(
                                color = Color.Gray,
                                start = Offset(yAxisOffset, chartDrawableHeight),
                                end = Offset(canvasWidth, chartDrawableHeight),
                                strokeWidth = 2f
                            )


                            appsParaGraficar.forEachIndexed { index, appUso ->
                                val xPos = yAxisOffset + (gap + barWidth) * index + gap / 2 // Posición X para la barra
                                val barHeight = (appUso.tiempoMin.toFloat() / maxTiempo) * chartDrawableHeight

                                // Dibujar barra
                                drawRect(
                                    color = barColor, // Color de la barra
                                    topLeft = Offset(xPos, chartDrawableHeight - barHeight),
                                    size = Size(barWidth, barHeight)
                                )

                                // Dibujar nombre de la aplicación (etiqueta del eje X)
                                val appName = mapearNombre(appUso.nombre)
                                val textLayoutResult = textMeasurer.measure(
                                    text = appName,
                                    style = TextStyle(fontSize = 10.sp, color = Color.DarkGray),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                // val textWidth = textLayoutResult.size.width.toFloat() // No es directamente necesario aquí si se usa textLayoutResult.size.width
                                // val textHeight = textLayoutResult.size.height.toFloat() // No es directamente necesario aquí si se usa textLayoutResult.size.height

                                withTransform({
                                    rotate(
                                        degrees = -45f,
                                        pivot = Offset(xPos + barWidth / 2, chartDrawableHeight) // El pivote de rotación
                                    )
                                }) {
                                    drawText(
                                        textMeasurer = textMeasurer,
                                        text = appName,
                                        // Ajusta la posición vertical.
                                        // textLayoutResult.size.width/2 se usa para centrar el texto rotado en el eje X.
                                        // textMeasurer.measure(appName, ...).size.height para el alto real del texto rotado.
                                        topLeft = Offset(xPos + barWidth / 2 - (textLayoutResult.size.width / 2), chartDrawableHeight + 5.dp.toPx()),
                                        style = TextStyle(fontSize = 10.sp, color = Color.DarkGray)
                                    )
                                }

                                // Dibujar valor encima de la barra
                                val valueText = "${appUso.tiempoMin}m"
                                val valueLayoutResult = textMeasurer.measure(
                                    text = valueText,
                                    style = TextStyle(fontSize = 10.sp, color = Color.Black),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                drawText(
                                    textMeasurer = textMeasurer,
                                    text = valueText,
                                    topLeft = Offset(xPos + barWidth / 2 - valueLayoutResult.size.width / 2, chartDrawableHeight - barHeight - valueLayoutResult.size.height - 4.dp.toPx()),
                                    style = TextStyle(fontSize = 10.sp, color = Color.Black)
                                )
                            }
                        }
                    }
                } else {
                    Text("No hay datos de uso para generar la gráfica en esta fecha.")
                }
            },
            confirmButton = {
                Button(onClick = { showGraphDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}