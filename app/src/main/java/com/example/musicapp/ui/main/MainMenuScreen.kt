package com.example.musicapp.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    mainViewModel: MainViewModel,
    onRhythmModuleClicked: () -> Unit,
    onStaffModuleClicked: () -> Unit,
    onSettingsClicked: () -> Unit
) {
    val uiState by mainViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = onSettingsClicked) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = stringResource(id = R.string.settings_title)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.user != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.welcome_message, uiState.user!!.username),
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(32.dp))

                ModuleButton(
                    title = stringResource(id = R.string.rhythm_module_title),
                    onClick = onRhythmModuleClicked
                )
                Spacer(modifier = Modifier.height(16.dp))
                ModuleButton(
                    title = stringResource(id = R.string.staff_module_title),
                    onClick = onStaffModuleClicked
                )
            }
        }
    }
}

@Composable
fun ModuleButton(title: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}