package com.planlectura.biblia90.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.planlectura.biblia90.MainUiState
import com.planlectura.biblia90.domain.ChapterRef
import com.planlectura.biblia90.domain.PlanDay
import com.planlectura.biblia90.ui.components.DayReadingCard
import com.planlectura.biblia90.ui.longLabel
import com.planlectura.biblia90.ui.mediumLabel
import com.planlectura.biblia90.ui.percentLabel

@Composable
fun TodayScreen(
    state: MainUiState.Ready,
    onChapterChange: (ChapterRef, Boolean) -> Unit,
    onDayChange: (PlanDay, Boolean) -> Unit,
    onOpenDay: (Int) -> Unit,
) {
    val s = state.snapshot
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(state.today.longLabel(), style = MaterialTheme.typography.labelLarge)
        val title = when {
            s.isFinished -> "¡Leíste la Biblia completa!"
            s.currentDayNumber == 0 -> "Tu plan está listo"
            s.currentDayNumber > state.plan.totalDays -> "Recta final"
            else -> "Día ${s.currentDayNumber} de ${state.plan.totalDays}"
        }
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Text(
            "Has leído ${s.chaptersRead} de ${s.totalChapters} capítulos (${s.fraction.percentLabel()}).",
            style = MaterialTheme.typography.bodyMedium,
        )

        when {
            s.isFinished -> MessageCard(
                "Completaste los ${s.totalChapters} capítulos. Comparte tu logro y elige tu próximo plan.",
            )
            s.currentDayNumber == 0 -> {
                MessageCard("Empiezas el ${state.prefs.startDate?.longLabel()?.lowercase()}. Aquí tienes un adelanto del día 1.")
                s.days.firstOrNull()?.let {
                    DayReadingCard(it, state.read, onChapterChange, onDayChange)
                }
            }
            else -> {
                PaceMessage(state, onOpenDay)
                val focus = s.today ?: s.nextPending
                focus?.let { DayReadingCard(it, state.read, onChapterChange, onDayChange) }
            }
        }
    }
}

@Composable
private fun PaceMessage(state: MainUiState.Ready, onOpenDay: (Int) -> Unit) {
    val s = state.snapshot
    val behind = s.chaptersBehind
    val todayRemaining = s.today?.let { it.totalChapters - it.chaptersRead } ?: 0
    val text = when {
        behind <= 0 -> "¡Vas al día! Terminarás el ${s.plannedFinish.mediumLabel()}."
        behind <= todayRemaining -> "Te quedan $todayRemaining capítulos para hoy."
        else -> {
            val projected = s.projectedFinish?.let { " A este ritmo terminarías el ${it.mediumLabel()}." } ?: ""
            "Llevas ${behind - todayRemaining} capítulos pendientes de días anteriores. " +
                "No pasa nada: puedes ponerte al día poco a poco.$projected"
        }
    }
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(text, style = MaterialTheme.typography.bodyMedium)
            val pending = s.nextPending
            if (behind > todayRemaining && pending != null && pending.day.number != s.currentDayNumber) {
                TextButton(onClick = { onOpenDay(pending.day.number) }) {
                    Text("Ir al día ${pending.day.number}")
                }
            }
        }
    }
}

@Composable
private fun MessageCard(text: String) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Text(text, Modifier.fillMaxWidth().padding(16.dp), style = MaterialTheme.typography.bodyLarge)
    }
}
