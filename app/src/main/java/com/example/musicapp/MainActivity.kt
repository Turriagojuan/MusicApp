package com.example.musicapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme // Asegúrate que esté importado
import androidx.compose.material3.Surface // Asegúrate que esté importado
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.musicapp.ui.theme.MusicAppTheme // Tu tema de la app

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Mantén esto si te gusta el edge-to-edge
        setContent {
            MusicAppTheme { // Aplica tu tema general
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation() // Aquí llamas a tu sistema de navegación
                }
            }
        }
    }
}

// Puedes mantener o eliminar este Greeting si no lo usas directamente en MainActivity
// Si lo usas en AppNavigation como placeholder, mantenlo.
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() { // Renombrado para evitar confusión con GreetingPreview original
    MusicAppTheme {
        // Previsualiza tu AppNavigation o una pantalla específica si lo deseas
        // Para previsualizar solo el Greeting:
        Greeting("Android Preview")
        // Para previsualizar la navegación (puede ser complejo en preview):
        // AppNavigation()
    }
}