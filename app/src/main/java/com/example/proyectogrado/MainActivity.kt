package com.example.proyectogrado

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.analytics.ktx.analytics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.example.proyectogrado.ui.screens.LoginScreen
import com.example.proyectogrado.ui.screens.Prueba
import com.example.proyectogrado.ui.screens.RegisterScreenStyled
import com.example.proyectogrado.ui.theme.ProyectoGradoTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogrado.ui.viewmodel.UsuarioViewModel
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {

    private val usuarioViewModel: UsuarioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        setContent {
            var isRegisterScreenVisible by remember { mutableStateOf(false) }

            ProyectoGradoTheme { // Si no tienes un tema personalizado, puedes usar MaterialTheme directamente
                if (isRegisterScreenVisible) {
                    RegisterScreenStyled(
                        onBack = { isRegisterScreenVisible = false },
                        viewModel = usuarioViewModel
                    )
                } else {
                    LoginScreen(
                        onRegisterClick = { isRegisterScreenVisible = true }
                    )
                }
            }
        }
    }

}
