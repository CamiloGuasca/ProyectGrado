package com.example.proyectogrado.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogrado.viewmodel.VinculacionViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun VincularEstudianteScreen(
    onBack: () -> Unit,
    viewModel: VinculacionViewModel = viewModel()
) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Vincular Estudiante", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del estudiante") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = id,
            onValueChange = { id = it },
            label = { Text("ID del estudiante (único)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val padreUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            if (padreUid.isNotEmpty()) {
                viewModel.vincular(nombre, id, padreUid)
                Toast.makeText(context, "Estudiante vinculado", Toast.LENGTH_SHORT).show()
                onBack()
            }
        }) {
            Text("Vincular")
        }
    }
}
