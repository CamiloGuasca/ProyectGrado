package com.example.proyectogrado.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectogrado.services.AppBlockerAccessibilityService
import com.example.proyectogrado.ui.navigation.Screen
import com.example.proyectogrado.ui.viewmodel.ConfigurarEstudianteViewModel
import com.example.proyectogrado.utils.PermissionChecker
import com.example.proyectogrado.utils.PreferenciasEstudiante
import android.widget.Toast
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurarEstudianteScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: ConfigurarEstudianteViewModel = viewModel()
    val estudianteIdGuardado = PreferenciasEstudiante.obtenerId(context)
    var estudianteIdText by rememberSaveable { mutableStateOf(estudianteIdGuardado) }

    val showPermissionDialog = remember { mutableStateOf(false) }
    val hasUsageStatsPermission = remember { mutableStateOf(PermissionChecker.hasUsageStatsPermission(context)) }
    val isAccessibilityServiceEnabled = remember { mutableStateOf(PermissionChecker.isAccessibilityServiceEnabled(context, AppBlockerAccessibilityService::class.java.name)) }

    val mensaje by viewModel.mensaje.collectAsState()
    val exito by viewModel.exito.collectAsState()
    val isValidating by viewModel.isValidating.collectAsState()
    val validationMessage by viewModel.validationMessage.collectAsState()
    val isConfigured by viewModel.isConfigured.collectAsState()

    DisposableEffect(Unit) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                hasUsageStatsPermission.value = PermissionChecker.hasUsageStatsPermission(context)
                isAccessibilityServiceEnabled.value = PermissionChecker.isAccessibilityServiceEnabled(context, AppBlockerAccessibilityService::class.java.name)
                estudianteIdText = PreferenciasEstudiante.obtenerId(context)
            }
        }
        (context as? ComponentActivity)?.lifecycle?.addObserver(observer)
        onDispose {
            (context as? ComponentActivity)?.lifecycle?.removeObserver(observer)
        }
    }

    LaunchedEffect(isConfigured) {
        if (isConfigured && hasUsageStatsPermission.value && isAccessibilityServiceEnabled.value) {
            Toast.makeText(context, "✅ Configuración exitosa.", Toast.LENGTH_SHORT).show()
            navController.navigate(Screen.HomeScreen.route) {
                popUpTo(Screen.ConfigurarEstudiante.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D47A1)) // Azul fondo institucional
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBar(
                title = { Text("Configurar Estudiante", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = estudianteIdText,
                        onValueChange = { estudianteIdText = it },
                        label = { Text("ID de estudiante") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (estudianteIdText.isNotBlank()) {
                                viewModel.onEstudianteConfigurarId(estudianteIdText)
                                hasUsageStatsPermission.value = PermissionChecker.hasUsageStatsPermission(context)
                                isAccessibilityServiceEnabled.value = PermissionChecker.isAccessibilityServiceEnabled(context, AppBlockerAccessibilityService::class.java.name)
                                if (!hasUsageStatsPermission.value || !isAccessibilityServiceEnabled.value) {
                                    showPermissionDialog.value = true
                                }
                            } else {
                                Toast.makeText(context, "⚠️ ID requerido", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isValidating) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Verificando...")
                        } else {
                            Text("Guardar y Verificar")
                        }
                    }

                    if (validationMessage.isNotBlank()) {
                        Text(validationMessage, color = Color.Red)
                    }

                    if (mensaje.isNotBlank()) {
                        Text(mensaje, color = Color(0xFF0D47A1), fontWeight = FontWeight.Bold)
                    }

                    Divider()

                    Text("Permisos requeridos", fontWeight = FontWeight.SemiBold)

                    Text("Uso de Datos: ${if (hasUsageStatsPermission.value) "✅ Concedido" else "❌ Faltante"}")
                    if (!hasUsageStatsPermission.value) {
                        Button(onClick = {
                            PermissionChecker.requestUsageStatsPermission(context)
                        }) {
                            Text("Conceder permiso de uso")
                        }
                    }

                    Text("Accesibilidad: ${if (isAccessibilityServiceEnabled.value) "✅ Habilitado" else "❌ Faltante"}")
                    if (!isAccessibilityServiceEnabled.value) {
                        Button(onClick = {
                            PermissionChecker.requestAccessibilityService(context)
                        }) {
                            Text("Habilitar accesibilidad")
                        }
                    }
                }
            }

            if (isConfigured && hasUsageStatsPermission.value && isAccessibilityServiceEnabled.value) {
                Button(
                    onClick = {
                        navController.navigate(Screen.HomeScreen.route) {
                            popUpTo(Screen.ConfigurarEstudiante.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("Continuar a Inicio", color = Color(0xFF0D47A1), fontWeight = FontWeight.Bold)
                }
            }
        }

        if (showPermissionDialog.value) {
            AlertDialog(
                onDismissRequest = { showPermissionDialog.value = false },
                title = { Text("Permisos necesarios") },
                text = { Text("Para que la app funcione correctamente, debes conceder los permisos de uso de datos y habilitar el servicio de accesibilidad.") },
                confirmButton = {
                    Button(onClick = { showPermissionDialog.value = false }) {
                        Text("Entendido")
                    }
                }
            )
        }
    }
}
