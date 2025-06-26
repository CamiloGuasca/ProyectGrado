// app/src/main/java/com/example/proyectogrado/ui/screens/ConfigurarEstudianteScreen.kt
package com.example.proyectogrado.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel // Importa esto para usar viewModel()

import com.example.proyectogrado.services.AppBlockerAccessibilityService
import com.example.proyectogrado.utils.PermissionChecker
import com.example.proyectogrado.utils.PreferenciasEstudiante // Tu PreferenciasEstudiante fusionada
import com.example.proyectogrado.ui.viewmodel.ConfigurarEstudianteViewModel // Tu ViewModel fusionado
import com.example.proyectogrado.ui.navigation.Screen // Tu objeto Screen

import android.util.Log
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurarEstudianteScreen(
    navController: NavController // Ahora solo acepta NavController
) {
    val context = LocalContext.current
    val viewModel: ConfigurarEstudianteViewModel = viewModel() // Instancia el ViewModel aquí

    // Estado para el texto del TextField
    var estudianteIdText by rememberSaveable {
        mutableStateOf(PreferenciasEstudiante.obtenerId(context)) // Usa obtenerId(context)
    }

    // Estados para la UI y permisos
    val showPermissionDialog = remember { mutableStateOf(false) }
    val hasUsageStatsPermission = remember { mutableStateOf(PermissionChecker.hasUsageStatsPermission(context)) }
    val isAccessibilityServiceEnabled = remember { mutableStateOf(PermissionChecker.isAccessibilityServiceEnabled(context, AppBlockerAccessibilityService::class.java.name)) }

    // Observar los estados del ViewModel fusionado
    val mensajeViewModel by viewModel.mensaje.collectAsState() // Mensaje general (compañero)
    val exitoConfiguracion by viewModel.exito.collectAsState() // Éxito general (compañero)

    val isValidating by viewModel.isValidating.collectAsState() // Tu estado de validación
    val validationMessage by viewModel.validationMessage.collectAsState() // Tu mensaje de validación
    val isConfigured by viewModel.isConfigured.collectAsState() // Tu estado de configuración exitosa

    // Actualizar estados de permisos y ID al reanudar la actividad
    DisposableEffect(Unit) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                Log.d("ConfigEstudianteScreen", "ON_RESUME: Verificando permisos y recargando ID.")
                hasUsageStatsPermission.value = PermissionChecker.hasUsageStatsPermission(context)
                isAccessibilityServiceEnabled.value = PermissionChecker.isAccessibilityServiceEnabled(context, AppBlockerAccessibilityService::class.java.name)
                estudianteIdText = PreferenciasEstudiante.obtenerId(context) // Recargar el ID
            }
        }
        (context as? ComponentActivity)?.lifecycle?.addObserver(observer)
        onDispose {
            (context as? ComponentActivity)?.lifecycle?.removeObserver(observer)
        }
    }

    // Mostrar Toast cuando el ViewModel emita un mensaje (usamos el tuyo)
    LaunchedEffect(validationMessage) {
        if (validationMessage.isNotBlank()) {
            Toast.makeText(context, validationMessage, Toast.LENGTH_SHORT).show()
        }
    }

    // Navegar o mostrar diálogo de permisos cuando la configuración sea exitosa
    LaunchedEffect(isConfigured) { // Reaccionar a tu estado de éxito
        if (isConfigured) {
            Log.d("ConfigEstudianteScreen", "Configuración exitosa detectada. Verificando permisos para navegación.")
            if (hasUsageStatsPermission.value && isAccessibilityServiceEnabled.value) {
                Toast.makeText(context, "ID configurado y permisos OK. Redirigiendo...", Toast.LENGTH_SHORT).show()
                navController.navigate(Screen.HomeScreen.route) { // Navega a HomeScreen
                    popUpTo(Screen.ConfigurarEstudiante.route) { // Usar tu ruta Screen.ConfigurarEstudianteScreen
                        inclusive = true
                    }
                }
            } else {
                Toast.makeText(context, "ID guardado, pero se requieren permisos adicionales.", Toast.LENGTH_LONG).show()
                showPermissionDialog.value = true // Muestra el diálogo de permisos si faltan
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurar Estudiante") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Usa navController para volver
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally // Centrar horizontalmente
        ) {
            Text(text = "Configuración del Estudiante", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = estudianteIdText,
                onValueChange = { estudianteIdText = it },
                label = { Text("Introduce tu ID de Estudiante") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (estudianteIdText.isNotBlank()) {
                        Log.d("ConfigEstudianteScreen", "Botón 'Guardar ID' clickeado. ID: $estudianteIdText")
                        // Llama a la función de tu ViewModel para el estudiante
                        viewModel.onEstudianteConfigurarId(estudianteIdText)

                        // Vuelve a verificar el estado de los permisos después del intento de guardar
                        hasUsageStatsPermission.value = PermissionChecker.hasUsageStatsPermission(context)
                        isAccessibilityServiceEnabled.value = PermissionChecker.isAccessibilityServiceEnabled(context, AppBlockerAccessibilityService::class.java.name)

                        // Si los permisos no están listos, muestra el diálogo para guiar al usuario
                        if (!hasUsageStatsPermission.value || !isAccessibilityServiceEnabled.value) {
                            showPermissionDialog.value = true
                            Log.d("ConfigEstudianteScreen", "Faltan permisos. Mostrando diálogo de permisos.")
                        }
                    } else {
                        Toast.makeText(context, "Por favor, ingresa tu ID de estudiante.", Toast.LENGTH_SHORT).show()
                        Log.w("ConfigEstudianteScreen", "Intento de guardar ID vacío.")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isValidating // Deshabilita el botón mientras valida
            ) {
                if (isValidating) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Verificando...")
                } else {
                    Text("Guardar ID y Verificar Permisos")
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(text = "Estado de Permisos Requeridos:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            // Permiso de Uso de Datos
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Permiso de Uso de Datos:")
                Text(if (hasUsageStatsPermission.value) "✅ Concedido" else "❌ No Concedido")
            }
            if (!hasUsageStatsPermission.value) {
                Button(
                    onClick = {
                        Log.d("ConfigEstudianteScreen", "Solicitando permiso de Uso de Datos.")
                        PermissionChecker.requestUsageStatsPermission(context)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Conceder Permiso de Uso de Datos")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Servicio de Accesibilidad
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Servicio de Accesibilidad:")
                Text(if (isAccessibilityServiceEnabled.value) "✅ Habilitado" else "❌ No Habilitado")
            }
            if (!isAccessibilityServiceEnabled.value) {
                Button(
                    onClick = {
                        Log.d("ConfigEstudianteScreen", "Solicitando habilitar Servicio de Accesibilidad.")
                        PermissionChecker.requestAccessibilityService(context)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Habilitar Servicio de Accesibilidad")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Mensajes de validación específicos (tuyos)
            if (validationMessage.isNotEmpty()) {
                Text(text = validationMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Mensajes generales del compañero
            if (mensajeViewModel.isNotEmpty()) {
                Text(text = mensajeViewModel, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Muestra el ID de estudiante actual recuperado de preferencias
            val currentIdFromPrefs = PreferenciasEstudiante.obtenerId(context)
            if (currentIdFromPrefs.isNotEmpty()) {
                Text(text = "ID de estudiante actual (guardado): $currentIdFromPrefs", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Botón de Continuar solo si todo está OK
            if (isConfigured && hasUsageStatsPermission.value && isAccessibilityServiceEnabled.value) {
                Text("¡Todos los permisos concedidos y ID configurado! La aplicación puede monitorear y aplicar restricciones.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        navController.navigate(Screen.HomeScreen.route) {
                            popUpTo(Screen.ConfigurarEstudiante.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continuar a Home")
                }
            }
        }

        // Diálogo de permisos
        if (showPermissionDialog.value) {
            AlertDialog(
                onDismissRequest = { showPermissionDialog.value = false },
                title = { Text("Permisos Necesarios") },
                text = { Text("Para que la aplicación pueda monitorear y restringir el uso, por favor, concede los permisos de 'Acceso a datos de uso' y habilita el 'Servicio de Accesibilidad' para esta app.") },
                confirmButton = {
                    Button(onClick = { showPermissionDialog.value = false }) {
                        Text("Entendido")
                    }
                }
            )
        }
    }
}
