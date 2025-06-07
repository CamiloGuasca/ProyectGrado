package com.example.proyectogrado.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogrado.domain.model.Usuario
import com.example.proyectogrado.ui.viewmodel.UsuarioViewModel
import java.util.Calendar

//val DarkBackgroundPattern = Color(0xFF0A192F)
//val LightGrayBackground = Color(0xFFF5F5F5)
//val ButtonDarkColor = Color(0xFF1A1A1A)
//val TextGray = Color.Gray

@Composable
fun RegisterScreenStyled(onBack: () -> Unit, viewModel: UsuarioViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var documento by rememberSaveable { mutableStateOf("") }
    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var usuario by rememberSaveable { mutableStateOf("") }
    var contraseña by rememberSaveable { mutableStateOf("") }
    var rol by remember { mutableStateOf("Seleccione un rol") }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            fechaNacimiento = "$day/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(DarkBackgroundPattern),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .size(100.dp)
                    .offset(y = 40.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = DarkBackgroundPattern
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Registro", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))

            CustomField("Documento de identidad", documento) { documento = it }
            CustomField("Nombre completo", nombre) { nombre = it }
            CustomField("Correo electrónico", correo, KeyboardType.Email) { correo = it }

            OutlinedTextField(
                value = fechaNacimiento,
                onValueChange = {},
                label = { Text("Fecha de nacimiento") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() },
                enabled = false,
                readOnly = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.Black,
                    disabledContainerColor = LightGrayBackground,
                    disabledBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomField("Usuario", usuario) { usuario = it }

            OutlinedTextField(
                value = contraseña,
                onValueChange = { contraseña = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = LightGrayBackground,
                    unfocusedContainerColor = LightGrayBackground,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            PruebaSelector(
                label = "Rol",
                options = listOf("Profesor", "Padre"),
                selectedOption = rol,
                onOptionSelected = { rol = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (
                        documento.isNotBlank() &&
                        nombre.isNotBlank() &&
                        correo.isNotBlank() &&
                        fechaNacimiento.isNotBlank() &&
                        usuario.isNotBlank() &&
                        contraseña.isNotBlank() &&
                        rol != "Seleccione un rol"
                    ) {
                        val nuevoUsuario = Usuario(
                            documento = documento,
                            nombre = nombre,
                            correo = correo,
                            fechaNacimiento = fechaNacimiento,
                            usuario = usuario,
                            password = contraseña,
                            rol = rol
                        )
                        viewModel.registrarUsuario(nuevoUsuario)
                        Toast.makeText(context, "Usuario registrado", Toast.LENGTH_SHORT).show()
                        onBack()
                    } else {
                        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
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
                Text("Registrarse", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onBack) {
                Text("Volver al inicio de sesión", color = TextGray)
            }
        }
    }
}

@Composable
fun CustomField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = LightGrayBackground,
            unfocusedContainerColor = LightGrayBackground,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        )
    )
    Spacer(modifier = Modifier.height(16.dp))
}
