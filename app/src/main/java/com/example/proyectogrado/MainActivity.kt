package com.example.proyectogrado

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectogrado.ui.screens.*
import com.example.proyectogrado.ui.theme.ProyectoGradoTheme
import com.example.proyectogrado.viewmodel.UsuarioViewModel
import com.example.proyectogrado.viewmodel.VinculacionViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val usuarioViewModel: UsuarioViewModel by viewModels()
    private val vinculacionViewModel: VinculacionViewModel by viewModels()
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        firebaseAnalytics = Firebase.analytics

        setContent {
            var currentScreen by remember { mutableStateOf("login") }

            ProyectoGradoTheme {
                when (currentScreen) {

                    // Inicio de sesión
                    "login" -> LoginScreen(
                        onRegisterClick = { currentScreen = "register" },
                        onLoginSuccess = { rol ->
                            currentScreen = when (rol.lowercase(Locale.ROOT)) {
                                "profesor" -> "profe"
                                "padre" -> "padreMenu"
                                "estudiante" -> "configEstudiante"
                                else -> "login"
                            }
                        },
                        viewModel = usuarioViewModel
                    )

                    // Registro
                    "register" -> RegisterScreenStyled(
                        onBack = { currentScreen = "login" },
                        viewModel = usuarioViewModel
                    )

                    // Docente
                    "profe" -> ListaUsuariosScreen()

                    // Menú del padre
                    "padreMenu" -> Column(modifier = Modifier.padding(16.dp)) {
                        Text("Bienvenido padre de familia", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { currentScreen = "vincular" }) {
                            Text("Vincular estudiante")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { currentScreen = "uso" }) {
                            Text("Monitorear uso de apps")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            FirebaseAuth.getInstance().signOut()
                            currentScreen = "login"
                        }) {
                            Text("Cerrar sesión")
                        }
                    }

                    // Pantalla para vincular estudiante (padre/docente)
                    "vincular" -> VincularEstudianteScreen(
                        onBack = { currentScreen = "padreMenu" },
                        viewModel = vinculacionViewModel
                    )

                    // Pantalla de monitoreo del padre
                    "uso" -> PantallaMonitoreoUso(onLogout = {
                        FirebaseAuth.getInstance().signOut()
                        currentScreen = "login"
                    })

                    // Configurar ID del estudiante
                    "configEstudiante" -> ConfigurarEstudianteScreen(
                        onConfigurado = { currentScreen = "enviar" }
                    )

                    // Enviar monitoreo desde celular del niño
                    "enviar" -> PantallaEnviarUso(onFinish = {
                        FirebaseAuth.getInstance().signOut()
                        currentScreen = "login"
                    })

                    else -> Text("Pantalla no encontrada")
                }
            }
        }
    }
}
