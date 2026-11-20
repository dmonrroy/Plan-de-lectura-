package com.planlectura.biblia90.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.planlectura.biblia90.MainUiState
import com.planlectura.biblia90.domain.ChapterRef
import com.planlectura.biblia90.domain.PlanDay
import com.planlectura.biblia90.ui.components.DayReadingCard

@Composable
fun DayDetailScreen(
    state: MainUiState.Ready,
    dayNumber: Int,
    onBack: () -> Unit,
    onChapterChange: (ChapterRef, Boolean) -> Unit,
    onDayChange: (PlanDay, Boolean) -> Unit,
) {
    val progress = state.snapshot.days.getOrNull(dayNumber - 1)
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
            Text("Día $dayNumber", style = MaterialTheme.typography.titleLarge)
        }
        if (progress == null) {
            Text("Este día no existe en el plan.")
        } else {
            DayReadingCard(progress, state.read, onChapterChange, onDayChange)
        }
    }
}
