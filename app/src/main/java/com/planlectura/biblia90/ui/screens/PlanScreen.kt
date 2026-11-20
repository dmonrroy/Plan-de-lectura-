package com.planlectura.biblia90.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Timelapse
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.planlectura.biblia90.MainUiState
import com.planlectura.biblia90.domain.DayProgress
import com.planlectura.biblia90.domain.DayStatus
import com.planlectura.biblia90.ui.shortLabel

@Composable
fun PlanScreen(state: MainUiState.Ready, onOpenDay: (Int) -> Unit) {
    val s = state.snapshot
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = (s.currentDayNumber - 2).coerceIn(0, s.days.lastIndex),
    )
    LazyColumn(state = listState, contentPadding = PaddingValues(bottom = 16.dp)) {
        item {
            ListItem(
                headlineContent = { Text(state.plan.name, style = MaterialTheme.typography.headlineSmall) },
                supportingContent = {
                    Text("${state.plan.totalDays} días · ${state.plan.totalChapters} capítulos · 66 libros")
                },
            )
        }
        items(s.days, key = { it.day.number }) { progress ->
            DayRow(progress, onClick = { onOpenDay(progress.day.number) })
            HorizontalDivider()
        }
    }
}

@Composable
private fun DayRow(progress: DayProgress, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val (icon, tint, statusText) = when (progress.status) {
        DayStatus.COMPLETED -> Triple(Icons.Filled.CheckCircle, colors.primary, "Leído")
        DayStatus.PARTIAL -> Triple(Icons.Outlined.Timelapse, colors.secondary, "En curso")
        DayStatus.BEHIND -> Triple(Icons.Outlined.ErrorOutline, colors.error, "Pendiente")
        DayStatus.TODAY -> Triple(Icons.Outlined.Today, colors.secondary, "Hoy")
        DayStatus.UPCOMING -> Triple(Icons.Outlined.RadioButtonUnchecked, colors.outline, "Próximo")
    }
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        colors = ListItemDefaults.colors(
            containerColor = if (progress.status == DayStatus.TODAY) colors.secondaryContainer else Color.Transparent,
        ),
        leadingContent = { Icon(icon, contentDescription = statusText, tint = tint) },
        overlineContent = { Text("Día ${progress.day.number} · ${progress.date.shortLabel()} · $statusText") },
        headlineContent = { Text(progress.day.label) },
        supportingContent = {
            Text("${progress.chaptersRead}/${progress.totalChapters} capítulos · ~${progress.day.estimatedMinutes} min")
        },
    )
}
