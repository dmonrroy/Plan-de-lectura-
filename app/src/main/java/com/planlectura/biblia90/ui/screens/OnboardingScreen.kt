package com.planlectura.biblia90.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.planlectura.biblia90.domain.ReadingPlan
import com.planlectura.biblia90.ui.longLabel
import com.planlectura.biblia90.ui.mediumLabel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@Composable
fun OnboardingScreen(plan: ReadingPlan, today: LocalDate, onStart: (LocalDate) -> Unit) {
    val options = listOf(
        "Hoy" to today,
        "Mañana" to today.plusDays(1),
        "Próximo lunes" to today.with(TemporalAdjusters.next(DayOfWeek.MONDAY)),
    )
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val start = options[selected].second
    val avgChapters = plan.totalChapters / plan.totalDays

    Surface(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Icon(
                Icons.Outlined.AutoStories,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(56.dp),
            )
            Text("La Biblia completa en 90 días", style = MaterialTheme.typography.headlineLarge)
            Text(
                "Un plan claro para cada día, de Génesis a Apocalipsis, con tu progreso siempre a la vista.",
                style = MaterialTheme.typography.bodyLarge,
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Feature(Icons.Outlined.Schedule, "Unos $avgChapters capítulos al día (40–50 minutos)")
                Feature(Icons.Outlined.Insights, "Seguimiento por capítulo, por día y por libro")
                Feature(Icons.Outlined.CloudOff, "Funciona sin conexión y sin crear cuenta")
            }

            Text("¿Cuándo quieres empezar?", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEachIndexed { index, (label, _) ->
                    FilterChip(
                        selected = selected == index,
                        onClick = { selected = index },
                        label = { Text(label) },
                    )
                }
            }
            Text(
                "Empiezas el ${start.longLabel().lowercase()} y terminarás el " +
                    "${start.plusDays(plan.totalDays - 1L).mediumLabel()}.",
                style = MaterialTheme.typography.bodyMedium,
            )
            plan.days.firstOrNull()?.let {
                Text(
                    "Día 1: ${it.label}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Button(onClick = { onStart(start) }, modifier = Modifier.fillMaxWidth()) {
                Text("Comenzar el plan")
            }
        }
    }
}

@Composable
private fun Feature(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
        Spacer(Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}
