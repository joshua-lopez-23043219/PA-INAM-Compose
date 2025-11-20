package com.example.pa_inam.Composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PerfilScreen(name: String, email: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Perfil")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Usuario: $name")
        Text("Correo: $email")
    }
}