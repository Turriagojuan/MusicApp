package com.example.musicapp.ui.rhythm_module

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicapp.R
import com.example.musicapp.ui.lessons.LessonContentProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RhythmMenuScreen(
    rhythmViewModel: RhythmViewModel = viewModel(),
    onBackPressed: () -> Unit,
    onLessonClick: (String) -> Unit,
    onGameClicked: () -> Unit
) {
    val uiState by rhythmViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.rhythm_menu_header)) },
                navigationIcon = { IconButton(onClick = onBackPressed) { Icon(Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.back_button_desc)) } }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                uiState.progress?.let { progress ->
                    LessonItem(stringResource(id = LessonContentProvider.getLesson("1.1")!!.titleResId), progress.lesson_1_1_completed) { onLessonClick("1.1") }
                    Spacer(Modifier.height(12.dp))
                    LessonItem(stringResource(id = LessonContentProvider.getLesson("1.2")!!.titleResId), progress.lesson_1_2_completed) { onLessonClick("1.2") }
                    Spacer(Modifier.height(12.dp))
                    LessonItem(stringResource(id = LessonContentProvider.getLesson("1.3")!!.titleResId), progress.lesson_1_3_completed) { onLessonClick("1.3") }
                }
                Spacer(Modifier.weight(1f))
                Button(onClick = onGameClicked, modifier = Modifier.fillMaxWidth().height(60.dp)) {
                    Text(stringResource(id = R.string.play_rhythm_game_button))
                }
            }
        }
    }
}

@Composable
fun LessonItem(title: String, isCompleted: Boolean, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth().height(70.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title)
            if (isCompleted) {
                Icon(Icons.Default.CheckCircle, contentDescription = stringResource(id = R.string.lesson_completed_desc), tint = Color(0xFF4CAF50))
            }
        }
    }
}