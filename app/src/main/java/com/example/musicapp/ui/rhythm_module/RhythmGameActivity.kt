package com.example.musicapp.ui.rhythm_module

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicapp.R
import com.example.musicapp.ui.theme.MusicAppTheme
import kotlinx.coroutines.flow.collectLatest

class RhythmGameActivity : ComponentActivity() {

    private val viewModel: RhythmGameViewModel by viewModels()
    private var toneGenerator: ToneGenerator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        } catch (e: Exception) {
            toneGenerator = null
        }

        setContent {
            MusicAppTheme {
                val uiState by viewModel.uiState.collectAsState()

                LaunchedEffect(Unit) {
                    viewModel.soundEvents.collectLatest { event ->
                        toneGenerator?.startTone(event.tone, 150)
                    }
                }

                RhythmGameScreen(
                    uiState = uiState,
                    onStartGame = { viewModel.startGame() },
                    onTap = { viewModel.onBeatTapped() },
                    onFinish = {
                        viewModel.resetGame()
                        finish()
                    },
                    onRetry = { viewModel.resetGame() }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        toneGenerator?.release()
        toneGenerator = null
    }
}

@Composable
fun RhythmGameScreen(
    uiState: RhythmGameState,
    onStartGame: () -> Unit,
    onTap: () -> Unit,
    onFinish: () -> Unit,
    onRetry: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        when (uiState.gameState) {
            GameState.IDLE -> IdleScreen(onStartGame)
            GameState.PLAYING -> PlayingScreen(uiState, onTap)
            GameState.FINISHED -> FinishedScreen(uiState.score, onRetry, onFinish)
        }
    }
}

@Composable
fun PlayingScreen(uiState: RhythmGameState, onTap: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(id = R.string.score, uiState.score), fontSize = 20.sp)
        Text("Ronda: ${uiState.round}", fontSize = 20.sp)
        Spacer(modifier = Modifier.weight(1f))

        Text(uiState.feedback, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(20.dp))

        // La UI ahora solo obedece al ViewModel
        PatternDisplay(pattern = uiState.currentPattern, activeIndex = uiState.activeBeatIndex)

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onTap,
            enabled = uiState.gamePhase == GamePhase.PLAY,
            modifier = Modifier.size(150.dp),
            shape = CircleShape
        ) {
            Text("¡Toca!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun PatternDisplay(pattern: RhythmicPattern?, activeIndex: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        pattern?.sequence?.forEachIndexed { index, step ->
            // La animación depende directamente del activeIndex del ViewModel
            val size by animateDpAsState(if (activeIndex == index) 40.dp else 30.dp, label = "")
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(if (step == 1) MaterialTheme.colorScheme.secondary else Color.LightGray)
            )
        }
    }
}

@Composable
fun IdleScreen(onStartGame: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Imita el Ritmo", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onStartGame) {
            Text("Empezar")
        }
    }
}

@Composable
fun FinishedScreen(score: Int, onRetry: () -> Unit, onFinish: () -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text(stringResource(id = R.string.game_over_title)) },
        text = { Text(stringResource(id = R.string.final_score_message, score), fontSize = 20.sp) },
        confirmButton = {
            Button(onClick = onRetry) {
                Text(stringResource(id = R.string.retry_button))
            }
        },
        dismissButton = {
            Button(onClick = onFinish) {
                Text(stringResource(id = R.string.exit_button))
            }
        }
    )
}