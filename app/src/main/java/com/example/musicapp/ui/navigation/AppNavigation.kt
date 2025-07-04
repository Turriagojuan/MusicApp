package com.example.musicapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.musicapp.ui.lessons.LessonScreen
import com.example.musicapp.ui.main.MainMenuScreen
import com.example.musicapp.ui.main.MainViewModel
import com.example.musicapp.ui.main.SettingsScreen
import com.example.musicapp.ui.main.SettingsViewModel
import com.example.musicapp.ui.rhythm_module.RhythmMenuScreen
import com.example.musicapp.ui.staff_module.StaffMenuScreen

@Composable
fun AppNavigation(
    mainViewModel: MainViewModel,
    settingsViewModel: SettingsViewModel, // <- CAMBIO: Se acepta el ViewModel
    startRhythmGame: () -> Unit,
    startStaffGame: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main_menu") {
        composable("main_menu") {
            MainMenuScreen(
                mainViewModel = mainViewModel,
                onRhythmModuleClicked = { navController.navigate("rhythm_menu") },
                onStaffModuleClicked = { navController.navigate("staff_menu") },
                onSettingsClicked = { navController.navigate("settings") }
            )
        }
        composable("rhythm_menu") {
            RhythmMenuScreen(
                onBackPressed = { navController.popBackStack() },
                // Navega a la lección pasando el ID
                onLessonClick = { lessonId -> navController.navigate("lesson/$lessonId") },
                onGameClicked = startRhythmGame
            )
        }
        composable("staff_menu") {
            StaffMenuScreen(
                onBackPressed = { navController.popBackStack() },
                // Navega a la lección pasando el ID
                onLessonClick = { lessonId -> navController.navigate("lesson/$lessonId") },
                onGameClicked = startStaffGame
            )
        }
        composable("settings") {
            SettingsScreen(
                onLogoutClicked = { mainViewModel.logout() },
                onBackPressed = { navController.popBackStack() },
                settingsViewModel = settingsViewModel // <- CAMBIO: Se pasa la instancia
            )
        }

        // NUEVA RUTA CON ARGUMENTO
        composable(
            route = "lesson/{lessonId}",
            arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId")
            if (lessonId != null) {
                LessonScreen(
                    lessonId = lessonId,
                    onBackPressed = { navController.popBackStack() }
                )
            }
        }
    }
}