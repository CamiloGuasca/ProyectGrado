package com.example.proyectogrado

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.example.proyectogrado.domain.model.Usuario
import com.example.proyectogrado.ui.screens.LoginScreen
import com.example.proyectogrado.ui.screens.RegisterScreenStyled
import com.example.proyectogrado.ui.screens.ListaUsuariosScreen
import com.example.proyectogrado.ui.theme.ProyectoGradoTheme
import com.example.proyectogrado.viewmodel.UsuarioViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.FirebaseApp
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val usuarioViewModel: UsuarioViewModel by viewModels()
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)
        firebaseAnalytics = Firebase.analytics

        setContent {
            var currentScreen by remember { mutableStateOf("login") } // login, register, profe, padre

            ProyectoGradoTheme {
                when (currentScreen) {
                    "login" -> LoginScreen(
                        onRegisterClick = { currentScreen = "register" },
                        onLoginSuccess = { rol ->
                            currentScreen = when (rol.lowercase(Locale.ROOT)) {
                                "profesor" -> "profe"
                                "padre" -> "padre"
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

                    "padre" -> Text("Bienvenido padre de familia")
                }
            }
        }
    }
}
