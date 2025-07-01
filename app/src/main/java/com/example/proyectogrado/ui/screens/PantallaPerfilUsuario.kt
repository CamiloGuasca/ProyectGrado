package com.example.proyectogrado.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogrado.viewmodel.PerfilUsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPerfilUsuario(
    viewModel: PerfilUsuarioViewModel = viewModel(),
    onBack: () -> Unit
) {
    val nombre by viewModel.nombre.collectAsState()
    val usuario by viewModel.usuario.collectAsState()
    val documento by viewModel.documento.collectAsState()
    val correo by viewModel.correo.collectAsState()
    val rol by viewModel.rol.collectAsState()
    val fechaNacimiento by viewModel.fechaNacimiento.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    var nombreEditado by remember { mutableStateOf("") }
    var usuarioEditado by remember { mutableStateOf("") }
    var documentoEditado by remember { mutableStateOf("") }

    LaunchedEffect(nombre, usuario, documento) {
        nombreEditado = nombre
        usuarioEditado = usuario
        documentoEditado = documento
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D47A1)) // Azul institucional
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Avatar de perfil
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Avatar de perfil",
                tint = Color.White,
                modifier = Modifier
                    .size(96.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = "👤 Mi Perfil",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📛 Nombre: $nombre")
                    Text("📧 Correo: $correo")
                    Text("🆔 Usuario: $usuario")
                    Text("📄 Documento: $documento")
                    Text("🎂 Nacimiento: $fechaNacimiento")
                    Text("🎓 Rol: $rol")
                }
            }

            Text(
                text = "✏️ Editar información",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)), // gris claro
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = nombreEditado,
                        onValueChange = { nombreEditado = it },
                        label = { Text("Nuevo nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = usuarioEditado,
                        onValueChange = { usuarioEditado = it },
                        label = { Text("Nuevo usuario") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = documentoEditado,
                        onValueChange = { documentoEditado = it },
                        label = { Text("Nuevo documento") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Button(
                onClick = {
                    viewModel.actualizarDatosUsuario(
                        nombreEditado,
                        usuarioEditado,
                        documentoEditado
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("Guardar Cambios", color = Color(0xFF0D47A1), fontWeight = FontWeight.Bold)
            }

            if (mensaje.isNotBlank()) {
                Text(
                    text = mensaje,
                    color = Color.Yellow,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("Volver", color = Color(0xFF0D47A1), fontWeight = FontWeight.Bold)
            }
        }
    }
}
