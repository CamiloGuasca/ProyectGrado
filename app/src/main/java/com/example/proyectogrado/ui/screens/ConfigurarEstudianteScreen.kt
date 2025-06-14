package com.example.proyectogrado.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.proyectogrado.utils.PreferenciasEstudiante

@Composable
fun ConfigurarEstudianteScreen(
    onConfigurado: () -> Unit
) {
    val context = LocalContext.current
    var estudianteId by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Configurar Estudiante", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = estudianteId,
            onValueChange = { estudianteId = it },
            label = { Text("ID del estudiante") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (estudianteId.isNotBlank()) {
                    PreferenciasEstudiante.guardarId(context, estudianteId)
                    Toast.makeText(context, "ID guardado correctamente", Toast.LENGTH_SHORT).show()
                    onConfigurado()
                } else {
                    Toast.makeText(context, "Por favor ingresa un ID", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar ID")
        }
    }
}
