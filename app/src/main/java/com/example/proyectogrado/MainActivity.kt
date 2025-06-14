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
            var currentScreen by remember { mutableStateOf("inicio") }

            ProyectoGradoTheme {
                when (currentScreen) {
                    "inicio" -> PantallaInicioModo(
                        onSeleccionarPadre = { currentScreen = "login" },
                        onSeleccionarEstudiante = { currentScreen = "configEstudiante" }
                    )

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

                    "register" -> RegisterScreenStyled(
                        onBack = { currentScreen = "login" },
                        viewModel = usuarioViewModel
                    )

                    "profe" -> ListaUsuariosScreen()

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

                    "vincular" -> VincularEstudianteScreen(
                        onBack = { currentScreen = "padreMenu" },
                        viewModel = vinculacionViewModel
                    )

                    "uso" -> PantallaMonitoreoUso(onLogout = {
                        FirebaseAuth.getInstance().signOut()
                        currentScreen = "login"
                    })

                    "configEstudiante" -> ConfigurarEstudianteScreen(
                        onConfigurado = { currentScreen = "enviar" }
                    )

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
