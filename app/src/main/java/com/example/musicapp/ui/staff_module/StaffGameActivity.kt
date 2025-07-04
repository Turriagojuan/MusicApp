package com.example.musicapp.ui.staff_module

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicapp.R
import com.example.musicapp.ui.theme.MusicAppTheme

class StaffGameActivity : ComponentActivity() {

    private val viewModel: StaffGameViewModel by viewModels()
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaPlayer = MediaPlayer.create(this, R.raw.tension_music)
        mediaPlayer?.isLooping = true
        setContent {
            MusicAppTheme {
                val uiState by viewModel.uiState.collectAsState()
                StaffGameScreen(
                    uiState = uiState,
                    onStartGame = { viewModel.startGame() },
                    onAnswer = { viewModel.handleAnswer(it) },
                    onFinish = { viewModel.resetGame(); finish() },
                    onRetry = { viewModel.resetGame() }
                )
            }
        }
    }
    override fun onStart() { super.onStart(); mediaPlayer?.start() }
    override fun onStop() { super.onStop(); mediaPlayer?.pause() }
    override fun onDestroy() { super.onDestroy(); mediaPlayer?.stop(); mediaPlayer?.release(); mediaPlayer = null }
}

@Composable
fun StaffGameScreen(uiState: StaffGameUiState, onStartGame: () -> Unit, onAnswer: (NoteName) -> Unit, onFinish: () -> Unit, onRetry: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        when (uiState.gameState) {
            StaffGameState.IDLE -> IdleScreen(onStartGame)
            StaffGameState.PLAYING -> PlayingScreen(uiState, onAnswer)
            StaffGameState.FINISHED -> FinishedScreen(uiState.score, onRetry, onFinish)
        }
    }
}

@Composable
private fun IdleScreen(onStartGame: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(id = R.string.staff_game_idle_title), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onStartGame) { Text(stringResource(id = R.string.start_button)) }
    }
}

@Composable
private fun PlayingScreen(uiState: StaffGameUiState, onAnswer: (NoteName) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(stringResource(id = R.string.score, uiState.score), fontSize = 20.sp)
            Text(stringResource(id = R.string.lives) + "❤️".repeat(uiState.lives), fontSize = 20.sp)
        }
        LinearProgressIndicator(progress = uiState.timeRemaining, modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp))
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            uiState.currentQuestion?.let { Pentagram(notePosition = it.staffPosition) }
        }
        Text(uiState.feedback, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        NoteButtons(onAnswer = onAnswer)
    }
}

@Composable
fun Pentagram(notePosition: Int) {
    val lineHeight = 20.dp
    val staffHeight = lineHeight * 4
    val noteRadius = lineHeight / 1.5f
    Canvas(modifier = Modifier.fillMaxWidth().height(staffHeight + noteRadius * 2)) {
        val width = size.width
        for (i in 0..4) {
            val y = i * lineHeight.toPx() + noteRadius.toPx()
            drawLine(color = Color.Black, start = Offset(x = 0f, y = y), end = Offset(x = width, y = y), strokeWidth = 4f)
        }
        val noteY = (8 - notePosition) * (lineHeight.toPx() / 2) + noteRadius.toPx()
        drawCircle(color = Color.Black, radius = noteRadius.toPx(), center = Offset(x = width / 2, y = noteY))
    }
}

@Composable
fun NoteButtons(onAnswer: (NoteName) -> Unit) {
    val notes = NoteName.values()
    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            for (i in 0..3) { Button(onClick = { onAnswer(notes[i]) }) { Text(notes[i].name) } }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            for (i in 4..6) { Button(onClick = { onAnswer(notes[i]) }) { Text(notes[i].name) } }
        }
    }
}

@Composable
private fun FinishedScreen(score: Int, onRetry: () -> Unit, onFinish: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(stringResource(id = R.string.game_over_title)) },
        text = { Text(stringResource(id = R.string.final_score_message, score), fontSize = 20.sp) },
        confirmButton = { Button(onClick = onRetry) { Text(stringResource(id = R.string.retry_button)) } },
        dismissButton = { Button(onClick = onFinish) { Text(stringResource(id = R.string.exit_button)) } }
    )
}