package com.planlectura.biblia90.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.planlectura.biblia90.domain.ChapterRef
import com.planlectura.biblia90.domain.DayProgress
import com.planlectura.biblia90.domain.PlanDay
import com.planlectura.biblia90.ui.longLabel

/** Anillo de progreso con el número de capítulos leídos en el centro. */
@Composable
fun ProgressRing(fraction: Float, centerText: String, modifier: Modifier = Modifier) {
    Box(modifier.size(72.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { fraction },
            modifier = Modifier.size(72.dp),
            strokeWidth = 7.dp,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
        Text(centerText, style = MaterialTheme.typography.labelLarge)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DayReadingCard(
    progress: DayProgress,
    read: Set<ChapterRef>,
    onChapterChange: (ChapterRef, Boolean) -> Unit,
    onDayChange: (PlanDay, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val day = progress.day
    val complete = progress.chaptersRead == progress.totalChapters
    Card(modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProgressRing(
                    fraction = progress.fraction,
                    centerText = "${progress.chaptersRead}/${progress.totalChapters}",
                    modifier = Modifier.semantics {
                        contentDescription = "${progress.chaptersRead} de ${progress.totalChapters} capítulos leídos"
                    },
                )
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text("Día ${day.number}", style = MaterialTheme.typography.headlineSmall)
                    Text(progress.date.longLabel(), style = MaterialTheme.typography.bodyMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Schedule, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "~${day.estimatedMinutes} min · ${day.chapters.size} capítulos",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            day.readings.forEach { reading ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(reading.label, style = MaterialTheme.typography.titleMedium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        reading.chapters.forEach { ref ->
                            val isRead = ref in read
                            FilterChip(
                                selected = isRead,
                                onClick = { onChapterChange(ref, !isRead) },
                                label = { Text(ref.chapter.toString()) },
                                leadingIcon = if (isRead) {
                                    { Icon(Icons.Filled.Check, null, Modifier.size(16.dp)) }
                                } else {
                                    null
                                },
                                modifier = Modifier.semantics {
                                    contentDescription = "${reading.bookName} ${ref.chapter}, " +
                                        if (isRead) "leído" else "pendiente"
                                },
                            )
                        }
                    }
                }
            }

            if (complete) {
                OutlinedButton(onClick = { onDayChange(day, false) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Desmarcar el día")
                }
            } else {
                Button(onClick = { onDayChange(day, true) }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.Check, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Marcar día como leído")
                }
            }
        }
    }
}
