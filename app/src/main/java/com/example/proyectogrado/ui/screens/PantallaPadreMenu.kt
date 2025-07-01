// app/src/main/java/com/example/proyectogrado/ui/screens/PantallaPadreMenu.kt
package com.example.proyectogrado.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.proyectogrado.ui.navigation.Screen

@Composable
fun PantallaPadreMenu(
    navController: NavHostController,
    onVincular: () -> Unit,
    onMonitorear: () -> Unit,
    onConfigurarHorarioEstudiante: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Menú del Padre",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = onVincular,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Vincular Estudiante")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onMonitorear,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Monitorear Uso de Apps")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onConfigurarHorarioEstudiante,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Configurar Horarios Estudiante")
        }
        Button(
            onClick = { navController.navigate(Screen.PerfilUsuario.route) },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("Mi Perfil")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Cerrar Sesión")
        }
    }
}