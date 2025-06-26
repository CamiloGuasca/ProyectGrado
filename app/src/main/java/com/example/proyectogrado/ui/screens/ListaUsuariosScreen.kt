// app/src/main/java/com/example/proyectogrado/ui/screens/ListaUsuariosScreen.kt
package com.example.proyectogrado.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogrado.viewmodel.ListaUsuariosViewModel

@Composable
fun ListaUsuariosScreen(
    viewModel: ListaUsuariosViewModel = viewModel(),
    onBack: () -> Unit // <-- AÑADIDO: Parámetro onBack
) {
    val listaUsuarios by viewModel.usuarios.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarUsuarios()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Lista de Usuarios", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) { // Añadido weight para que el botón de volver quede abajo
            items(listaUsuarios) { usuario ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nombre: ${usuario.nombre}")
                        Text("Correo: ${usuario.correo}")
                        Text("Rol: ${usuario.rol}")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Botón de volver para salir de esta pantalla de lista
        Button(
            onClick = onBack, // <-- USA el onBack
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}