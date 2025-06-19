package com.example.proyectogrado.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogrado.viewmodel.UsuarioViewModel
import java.util.Locale
import com.example.proyectogrado.ui.theme.DarkBackgroundPattern
import com.example.proyectogrado.ui.theme.LightGrayBackground
import com.example.proyectogrado.ui.theme.ButtonDarkColor
import com.example.proyectogrado.ui.theme.TextGray
import com.example.proyectogrado.utils.PreferenciasEstudiante
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit,
    onLoginSuccess: (String) -> Unit,
    viewModel: UsuarioViewModel = viewModel()
) {
    val context = LocalContext.current
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(DarkBackgroundPattern),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .size(100.dp)
                    .offset(y = 50.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Logo",
                        modifier = Modifier.size(60.dp),
                        tint = DarkBackgroundPattern
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f)
                .padding(top = 70.dp, start = 32.dp, end = 32.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Inicio de Sesión", fontSize = 28.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("USUARIO") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = LightGrayBackground,
                    unfocusedContainerColor = LightGrayBackground,
                    disabledContainerColor = LightGrayBackground,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("CONTRASEÑA") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = LightGrayBackground,
                    unfocusedContainerColor = LightGrayBackground,
                    disabledContainerColor = LightGrayBackground,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (username.isNotBlank() && password.isNotBlank()) {
                        viewModel.login(username, password) { usuario ->
                            if (usuario != null) {
                                Toast.makeText(context, "Bienvenido, ${usuario.nombre}", Toast.LENGTH_SHORT).show()

                                // ✅ Limpieza según el rol
                                when (usuario.rol.lowercase(Locale.ROOT)) {
                                    "padre", "profesor" -> {
                                        PreferenciasEstudiante.borrarId(context) // elimina estado anterior
                                        onLoginSuccess(usuario.rol.lowercase(Locale.ROOT))
                                    }
                                    "estudiante" -> {
                                        onLoginSuccess("estudiante")
                                    }
                                    else -> {
                                        Toast.makeText(context, "Rol no reconocido", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonDarkColor,
                    contentColor = Color.White
                )
            ) {
                Text("Iniciar Sesión", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = {
                if (username.isNotBlank()) {
                    FirebaseAuth.getInstance()
                        .sendPasswordResetEmail(username)
                        .addOnSuccessListener {
                            Toast.makeText(context, "📧 Revisa tu correo para restablecer la contraseña", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "❌ Error: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                } else {
                    Toast.makeText(context, "⚠️ Ingresa tu correo electrónico primero", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("¿Olvidó su contraseña?", color = TextGray, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            ClickableText(
                text = AnnotatedString("Regístrese!"),
                onClick = { onRegisterClick() },
                style = TextStyle(
                    color = TextGray,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}
