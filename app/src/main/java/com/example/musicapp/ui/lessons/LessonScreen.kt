package com.example.musicapp.ui.lessons

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    lessonId: String,
    onBackPressed: () -> Unit,
    lessonViewModel: LessonViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val savedStateHandle = androidx.lifecycle.SavedStateHandle(mapOf("lessonId" to lessonId))
                return LessonViewModel(savedStateHandle) as T
            }
        }
    )
) {
    val uiState by lessonViewModel.uiState.collectAsState()

    if (uiState.isLessonComplete) {
        LaunchedEffect(Unit) {
            onBackPressed()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.lessonContent?.title ?: stringResource(id = R.string.loading_lesson)) },
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
        val pageContent = uiState.lessonContent?.pages?.getOrNull(uiState.currentPage)

        if (pageContent != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = pageContent.text,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
                LessonNavigation(
                    isFirstPage = uiState.currentPage == 0,
                    isLastPage = uiState.currentPage == (uiState.lessonContent?.pages?.size ?: 0) - 1,
                    onPreviousClicked = { lessonViewModel.previousPage() },
                    onNextClicked = { lessonViewModel.nextPage() },
                    onFinishClicked = { lessonViewModel.completeLesson() },
                    isLoading = uiState.isLoading
                )
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun LessonNavigation(
    isFirstPage: Boolean,
    isLastPage: Boolean,
    onPreviousClicked: () -> Unit,
    onNextClicked: () -> Unit,
    onFinishClicked: () -> Unit,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onPreviousClicked, enabled = !isFirstPage) {
            Text(stringResource(id = R.string.previous_button))
        }

        if (isLoading) {
            CircularProgressIndicator()
        }

        if (isLastPage) {
            Button(
                onClick = onFinishClicked,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(stringResource(id = R.string.finish_lesson_button))
            }
        } else {
            Button(onClick = onNextClicked) {
                Text(stringResource(id = R.string.next_button))
            }
        }
    }
}