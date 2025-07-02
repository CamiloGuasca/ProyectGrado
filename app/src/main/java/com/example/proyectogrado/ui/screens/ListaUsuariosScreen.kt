package com.example.proyectogrado.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogrado.viewmodel.ListaUsuariosViewModel

@Composable
fun ListaUsuariosScreen(
    viewModel: ListaUsuariosViewModel = viewModel(),
    onBack: () -> Unit
) {
    val listaUsuarios by viewModel.usuarios.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarUsuarios()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF102027)) // Azul oscuro
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "👥 Lista de Usuarios",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listaUsuarios) { usuario ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF37474F)) // Gris azulado
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("📛 Nombre: ${usuario.nombre}", color = Color.White)
                            Text("📧 Correo: ${usuario.correo}", color = Color.White)
                            Text("🎓 Rol: ${usuario.rol}", color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB0BEC5)) // Gris claro
            ) {
                Text("⬅ Volver", fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}
