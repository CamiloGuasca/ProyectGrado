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
import androidx.compose.ui.unit.sp

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
            .background(Color(0xFF102027)) // Azul oscuro/Gris azulado
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Avatar de perfil",
                tint = Color(0xFF90CAF9),
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = "👤 Mi Perfil",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF37474F))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📛 Nombre: $nombre", color = Color.White)
                    Text("📧 Correo: $correo", color = Color.White)
                    Text("🆔 Usuario: $usuario", color = Color.White)
                    Text("📄 Documento: $documento", color = Color.White)
                    Text("🎂 Nacimiento: $fechaNacimiento", color = Color.White)
                    Text("🎓 Rol: $rol", color = Color.White)
                }
            }

            Text(
                text = "✏️ Editar Información",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFCFD8DC)),
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF29B6F6))
            ) {
                Text("💾 Guardar Cambios", color = Color.White, fontWeight = FontWeight.Bold)
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF546E7A))
            ) {
                Text("🔙 Volver", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
