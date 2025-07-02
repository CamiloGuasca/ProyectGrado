package com.example.proyectogrado.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectogrado.viewmodel.EnviarUsoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEnviarUso(
    navController: NavController,
    estudianteId: String,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val enviarUsoViewModel: EnviarUsoViewModel = viewModel()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Enviar Uso de Aplicaciones") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .background(Color(0xFFE3F2FD)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📊 Panel de Envío de Uso",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Estudiante ID: ${estudianteId.ifEmpty { "No especificado" }}",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (estudianteId.isNotBlank()) {
                        Log.d("PantallaEnviarUso", "Programando envío de uso para estudiante: $estudianteId")
                        enviarUsoViewModel.programarEnvioDeUso()
                        Toast.makeText(context, "✅ Envío de uso programado.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "⚠️ ID de estudiante no válido.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Enviar Uso Ahora (Simulado)", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cerrar Sesión", color = Color.White)
            }
        }
    }
}
