package com.example.musicapp.ui.staff_module

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicapp.R
import com.example.musicapp.ui.rhythm_module.LessonItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffMenuScreen(
    staffViewModel: StaffViewModel = viewModel(),
    onBackPressed: () -> Unit,
    onLessonClick: (String) -> Unit,
    onGameClicked: () -> Unit
) {
    val uiState by staffViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.staff_menu_header)) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_button_desc)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                uiState.progress?.let { progress ->
                    LessonItem("Lección 2.1: ¿Qué es el Pentagrama?", progress.lesson_2_1_completed) { onLessonClick("2.1") }
                    Spacer(Modifier.height(12.dp))
                    LessonItem("Lección 2.2: La Clave de Sol", progress.lesson_2_2_completed) { onLessonClick("2.2") }
                    Spacer(Modifier.height(12.dp))
                    LessonItem("Lección 2.3: Notas en las Líneas", progress.lesson_2_3_completed) { onLessonClick("2.3") }
                    Spacer(Modifier.height(12.dp))
                    LessonItem("Lección 2.4: Notas en los Espacios", progress.lesson_2_4_completed) { onLessonClick("2.4") }
                }

                Spacer(Modifier.weight(1f))

                Button(
                    onClick = onGameClicked,
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                ) {
                    Text(stringResource(id = R.string.play_staff_game_button))
                }
            }
        }
    }
}