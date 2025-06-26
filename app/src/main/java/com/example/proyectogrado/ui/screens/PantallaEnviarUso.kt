// app/src/main/java/com/example/proyectogrado/ui/screens/PantallaEnviarUso.kt
package com.example.proyectogrado.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectogrado.viewmodel.EnviarUsoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import android.util.Log // Importar Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEnviarUso(
    navController: NavController,
    estudianteId: String, // Ahora se espera el ID del estudiante como parámetro
    onLogout: () -> Unit // Ahora se espera la acción de logout como parámetro
) {
    val context = LocalContext.current
    val enviarUsoViewModel: EnviarUsoViewModel = viewModel()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Pantalla para Enviar Uso de la App",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "ID del estudiante: ${estudianteId.ifEmpty { "No especificado" }}",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                if (estudianteId.isNotBlank()) {
                    Log.d("PantallaEnviarUso", "Programando envío de uso para estudiante: $estudianteId")
                    enviarUsoViewModel.programarEnvioDeUso()
                    Toast.makeText(context, "Envío de uso programado.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "ID de estudiante no válido.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar Uso Ahora (Simulado)")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLogout, // Usar el onLogout proporcionado
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Cerrar Sesión")
        }
    }
}
