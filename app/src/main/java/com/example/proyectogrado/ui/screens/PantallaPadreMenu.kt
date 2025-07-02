package com.example.proyectogrado.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .background(Color(0xFFECEFF1)) // Fondo gris claro
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "👨‍👧 Menú del Padre",
            style = MaterialTheme.typography.headlineMedium.copy(color = Color(0xFF263238)),
            modifier = Modifier.padding(bottom = 40.dp)
        )

        Button(
            onClick = onVincular,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0277BD))
        ) {
            Text("🔗 Vincular Estudiante", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onMonitorear,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
        ) {
            Text("📊 Monitorear Uso de Apps", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onConfigurarHorarioEstudiante,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF039BE5))
        ) {
            Text("⏰ Configurar Horarios", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate(Screen.PerfilUsuario.route) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF546E7A))
        ) {
            Text("👤 Mi Perfil", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
        ) {
            Text("🔒 Cerrar Sesión", color = Color.White, fontSize = 16.sp)
        }
    }
}
