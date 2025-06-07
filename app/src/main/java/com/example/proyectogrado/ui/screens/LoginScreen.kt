package com.example.proyectogrado.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// --- Definición de Colores (Ajusta según tus necesidades) ---
val DarkBackgroundPattern = Color(0xFF0A192F) // Un azul oscuro como ejemplo
val LightGrayBackground = Color(0xFFE0E0E0)
val ButtonDarkColor = Color(0xFF1A1A1A)
val TextGray = Color.Gray

@Composable
fun LoginScreen(onRegisterClick: () -> Unit) {
    // --- State Variables ---
    // Usamos rememberSaveable para que el texto sobreviva a cambios de configuración (como rotación)
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    // --- Main Layout ---
    Column(
        modifier = Modifier
            .fillMaxSize() // Ocupa toda la pantalla
            .background(Color.White) // Fondo principal blanco
    ) {
        // --- Top Section (Dark Background & Logo) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Ocupa una porción del espacio vertical
                .background(DarkBackgroundPattern), // Color de fondo oscuro
            contentAlignment = Alignment.Center // Centra el contenido (logo)
        ) {
            // Aquí podrías poner una Image con el patrón si lo tienes
            // Image(painter = painterResource(id = R.drawable.your_pattern), contentDescription = null, contentScale = ContentScale.Crop)

            // --- Logo ---
            Card(
                shape = RoundedCornerShape(16.dp), // Bordes redondeados para el contenedor del logo
                modifier = Modifier
                    .size(100.dp) // Tamaño del contenedor del logo
                    .offset(y = 50.dp), // Desplaza el logo hacia abajo para que se superponga
                colors = CardDefaults.cardColors(containerColor = Color.White) // Fondo blanco para el logo
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Reemplaza 'R.drawable.your_logo' con el ID de tu recurso de logo
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_share), // Icono de ejemplo
                        contentDescription = "Logo de la App",
                        modifier = Modifier.size(60.dp),
                        tint = DarkBackgroundPattern // Color del icono
                    )
                }
            }
        }

        // --- Bottom Section (Form) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f) // Ocupa más espacio vertical que la sección superior
                .padding(top = 70.dp, start = 32.dp, end = 32.dp, bottom = 32.dp), // Padding (más arriba por el logo)
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- Title ---
            Text(
                text = "Inicio de Sesión",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp)) // Espacio vertical

            // --- Username Field ---
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("USUARIO", style = TextStyle(fontWeight = FontWeight.Normal)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), // Bordes redondeados
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = LightGrayBackground, // Color de fondo cuando está enfocado
                    unfocusedContainerColor = LightGrayBackground, // Color de fondo cuando no está enfocado
                    disabledContainerColor = LightGrayBackground,
                    focusedBorderColor = Color.Transparent, // Sin borde al enfocar
                    unfocusedBorderColor = Color.Transparent // Sin borde normal
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Password Field ---
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("CONTRASEÑA", style = TextStyle(fontWeight = FontWeight.Normal)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), // Bordes redondeados
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(), // Oculta la contraseña
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), // Teclado de contraseña
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = LightGrayBackground,
                    unfocusedContainerColor = LightGrayBackground,
                    disabledContainerColor = LightGrayBackground,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- Login Button ---
            Button(
                onClick = {
                    // TODO: Lógica de inicio de sesión aquí
                    println("Username: $username, Password: $password")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp), // Bordes redondeados
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonDarkColor, // Color de fondo del botón
                    contentColor = Color.White // Color del texto del botón
                )
            ) {
                Text("Iniciar Sesión", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Footer Links ---
            ClickableText(
                text = AnnotatedString("¿Olvidó su contraseña?"),
                onClick = { offset ->
                    // TODO: Lógica para recuperar contraseña
                    println("Clic en Olvidó contraseña")
                },
                style = TextStyle(
                    color = TextGray,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            ClickableText(
                text = AnnotatedString("Regístrese!"),
                onClick = {onRegisterClick()},
                style = TextStyle(
                    color = TextGray,
                    fontWeight = FontWeight.Bold, // Un poco más destacado
                    textAlign = TextAlign.Center
                )
            )
        } // Fin Column (Bottom Section)
    } // Fin Column (Main Layout)
}

// --- Preview ---/**
/**
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LoginScreen()
}**/