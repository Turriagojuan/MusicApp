package com.example.musicapp // O com.example.musicapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.musicapp.auth.LoginScreen
// Importa RegisterScreen cuando lo creemos
// import com.example.musicapp.auth.RegisterScreen
// Importa tu pantalla principal (ej: MainScreen Composable o la actual MainActivity si la adaptas)
// import com.example.musicapp.ui.main.MainScreen


// Define tus rutas como constantes
object AppDestinations {
    const val LOGIN_ROUTE = "login"
    const val REGISTER_ROUTE = "register"
    const val MAIN_APP_ROUTE = "main_app" // Ruta para el contenido principal de la app
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppDestinations.LOGIN_ROUTE) {
        composable(AppDestinations.LOGIN_ROUTE) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppDestinations.MAIN_APP_ROUTE) {
                        // Limpia el backstack para que el usuario no vuelva al login
                        popUpTo(AppDestinations.LOGIN_ROUTE) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(AppDestinations.REGISTER_ROUTE)
                    // Aquí no limpiamos el backstack para que pueda volver al login
                }
            )
        }

        composable(AppDestinations.REGISTER_ROUTE) {
            // Cuando creemos RegisterScreen, irá aquí:
            // RegisterScreen(
            //     onRegisterSuccess = {
            //         navController.navigate(AppDestinations.LOGIN_ROUTE) { // O MAIN_APP_ROUTE directamente
            //             popUpTo(AppDestinations.REGISTER_ROUTE) { inclusive = true }
            //         }
            //     },
            //     onNavigateToLogin = { navController.popBackStack() }
            // )
            // Placeholder mientras no existe RegisterScreen:
            androidx.compose.material3.Text("Pantalla de Registro (Pendiente)")
        }

        composable(AppDestinations.MAIN_APP_ROUTE) {
            // Aquí irá tu pantalla principal después del login.
            // Por ahora, podemos usar el Greeting de tu MainActivity original.
            // O crea un nuevo Composable para ello, ej: MainAppScreen()
            com.example.musicapp.Greeting(name = "Usuario Logueado")
        }
    }
}