package com.example.proyectogrado.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogrado.viewmodel.EstudianteDataStore
import kotlinx.coroutines.launch

@Composable
fun ConfigurarEstudianteScreen(
    onConfigurado: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = EstudianteDataStore(context)
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
                    scope.launch {
                        dataStore.guardarEstudianteId(estudianteId)
                        Toast.makeText(context, "ID guardado correctamente", Toast.LENGTH_SHORT).show()
                        onConfigurado()
                    }
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
