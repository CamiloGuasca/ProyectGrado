package com.example.proyectogrado.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.proyectogrado.ui.navigation.Screen
import com.example.proyectogrado.viewmodel.ProfesorMenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaProfesorMenu(
    navController: NavHostController,
    CrearCurso: () -> Unit,
    MisCursos: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfesorMenuViewModel = viewModel()
) {
    val context = LocalContext.current
    val rolValido by viewModel.rolValido.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.validarRol(context, onLogout)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Menú del Profesor",
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF263238)
                )
            )
        },
        containerColor = Color(0xFFECEFF1)
    ) { paddingValues ->
        if (rolValido) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "👨‍🏫 Bienvenido, Profesor",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color(0xFF263238),
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                Button(
                    onClick = MisCursos,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF455A64))
                ) {
                    Text("📚 Ver Mis Cursos", color = Color.White)
                }

                Button(
                    onClick = CrearCurso,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF607D8B))
                ) {
                    Text("➕ Crear Nuevo Curso", color = Color.White)
                }

                Button(
                    onClick = { navController.navigate(Screen.PerfilUsuario.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90A4AE))
                ) {
                    Text("👤 Mi Perfil", color = Color.White)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("🚪 Cerrar Sesión", color = Color.White)
                }
            }
        } else if (mensaje.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(mensaje, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
