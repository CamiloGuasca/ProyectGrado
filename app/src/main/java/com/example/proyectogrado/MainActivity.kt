package com.example.proyectogrado

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.proyectogrado.ui.navigation.AppNavigation
import com.example.proyectogrado.ui.theme.ProyectoGradoTheme
import com.example.proyectogrado.viewmodel.UsuarioViewModel
import com.example.proyectogrado.viewmodel.VinculacionViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    private val usuarioViewModel: UsuarioViewModel by viewModels()
    private val vinculacionViewModel: VinculacionViewModel by viewModels()
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)
        firebaseAnalytics = Firebase.analytics

        // UI principal
        setContent {
            ProyectoGradoTheme {
                val navController = rememberNavController()

                // Navegación principal
                AppNavigation(
                    navController = navController,
                    usuarioViewModel = usuarioViewModel,
                    vinculacionViewModel = vinculacionViewModel
                )
            }
        }
    }
}
