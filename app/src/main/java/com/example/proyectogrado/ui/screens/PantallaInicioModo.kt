package com.example.proyectogrado.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectogrado.R
import com.example.proyectogrado.ui.navigation.Screen
import androidx.navigation.NavController
import androidx.compose.ui.text.style.TextAlign

@Composable
fun PantallaInicioModo(
    navController: NavController,
    onSeleccionarPadre: () -> Unit = {},
    onSeleccionarEstudiante: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFCFD8DC), // Gris azulado claro
                        Color(0xFFB3E5FC), // Azul claro más saturado
                        Color(0xFFECEFF1)  // Gris muy claro casi blanco
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.colegio),
                contentDescription = "Bienvenida",
                modifier = Modifier.size(160.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "¡Bienvenid@!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF01579B)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "¿Cómo deseas ingresar a la mejor app de cuidado infantil?",
                fontSize = 18.sp,
                color = Color(0xFF0277BD),
                modifier = Modifier.padding(horizontal = 16.dp),
                lineHeight = 22.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    onSeleccionarPadre()
                    navController.navigate(Screen.Login.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("👨‍👩‍👧 Soy padre o profesor", fontSize = 16.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onSeleccionarEstudiante()
                    navController.navigate(Screen.ConfigurarEstudiante.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("🎓 Soy estudiante", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}
