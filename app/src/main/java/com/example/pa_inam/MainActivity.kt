package com.example.pa_inam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.pa_inam.ui.theme.PAINAMTheme
import com.example.pa_inam.Composables.LoginScreen
import com.example.pa_inam.Composables.MenuPrincipalScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PAINAMTheme {

                var showMenu by remember { mutableStateOf(false) }
                var userName by remember { mutableStateOf("Usuari1") }
                var userEmail by remember { mutableStateOf("useradmin3@gmail.com") }

                if (showMenu) {

                    MenuPrincipalScreen(
                        userName = userName,
                        userEmail = userEmail,
                        onLogout = {
                            Toast.makeText(this, "Cerrando sesión", Toast.LENGTH_SHORT).show()
                            showMenu = false   // vuelve al login
                        }
                    )
                } else {

                    LoginScreen(
                        onLogin = { user, pass, remember ->
                            Toast.makeText(
                                this,
                                "Login con $user / $pass (remember=$remember)",
                                Toast.LENGTH_SHORT
                            ).show()

                            // 👉 Aquí validar contra tu backend ASP.NET
                            // Si es correcto:
                            showMenu = true
                        },
                        onRegister = {
                            Toast.makeText(this, "Ir a registro", Toast.LENGTH_SHORT).show()
                        },
                        onForgotPassword = {
                            Toast.makeText(this, "Olvidó contraseña", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

//    @Preview(showBackground = true)
//    @Composable
//    fun LoginScreenPreview() {
//        PAINAMTheme {
//            LoginScreen(
//                onLogin = { _, _, _ -> },
//                onRegister = { },
//                onForgotPassword = { }
//            )
//        }
//    }
//
//    @Preview(showBackground = true)
//    @Composable
//    fun MenuPrincipalScreenPreview() {
//        PAINAMTheme {
//            MenuPrincipalScreen(
//                userName = "Usuari1",
//                userEmail = "useradmin3@gmail.com",
//                onLogout = { }
//            )
//        }
//    }




}







