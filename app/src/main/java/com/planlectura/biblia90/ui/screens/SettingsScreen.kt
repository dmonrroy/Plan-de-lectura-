package com.planlectura.biblia90.ui.screens

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.planlectura.biblia90.MainUiState
import com.planlectura.biblia90.ui.mediumLabel

@Composable
fun SettingsScreen(
    state: MainUiState.Ready,
    onReminderChange: (enabled: Boolean, hour: Int, minute: Int) -> Unit,
    onRestartPlan: () -> Unit,
) {
    val context = LocalContext.current
    val prefs = state.prefs
    var permissionDenied by remember { mutableStateOf(false) }
    var confirmRestart by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        permissionDenied = !granted
        if (granted) onReminderChange(true, prefs.reminderHour, prefs.reminderMinute)
    }

    fun enableReminder() {
        val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        if (needsPermission) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            onReminderChange(true, prefs.reminderHour, prefs.reminderMinute)
        }
    }

    val timeLabel = "%02d:%02d".format(prefs.reminderHour, prefs.reminderMinute)

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text("Ajustes", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(16.dp))

        ListItem(
            leadingContent = { Icon(Icons.Outlined.Notifications, null) },
            headlineContent = { Text("Recordatorio diario") },
            supportingContent = {
                Text(
                    if (permissionDenied) "Activa las notificaciones en los ajustes del sistema."
                    else "Sólo te avisamos si aún no has leído."
                )
            },
            trailingContent = {
                Switch(
                    checked = prefs.reminderEnabled,
                    onCheckedChange = { on ->
                        if (on) enableReminder() else onReminderChange(false, prefs.reminderHour, prefs.reminderMinute)
                    },
                )
            },
        )
        ListItem(
            modifier = Modifier.clickable(enabled = prefs.reminderEnabled) {
                TimePickerDialog(
                    context,
                    { _, hour, minute -> onReminderChange(true, hour, minute) },
                    prefs.reminderHour,
                    prefs.reminderMinute,
                    true,
                ).show()
            },
            leadingContent = { Icon(Icons.Outlined.Schedule, null) },
            headlineContent = { Text("Hora del recordatorio") },
            supportingContent = { Text(timeLabel) },
        )
        HorizontalDivider()

        ListItem(
            leadingContent = { Icon(Icons.Outlined.AutoStories, null) },
            headlineContent = { Text(state.plan.name) },
            supportingContent = {
                Text("Inicio: ${prefs.startDate?.mediumLabel() ?: "—"} · Fin previsto: ${state.snapshot.plannedFinish.mediumLabel()}")
            },
        )
        ListItem(
            modifier = Modifier.clickable { confirmRestart = true },
            leadingContent = { Icon(Icons.Outlined.RestartAlt, null, tint = MaterialTheme.colorScheme.error) },
            headlineContent = { Text("Reiniciar el plan", color = MaterialTheme.colorScheme.error) },
            supportingContent = { Text("Borra tu progreso y vuelve a elegir fecha de inicio.") },
        )
        HorizontalDivider()
        Text(
            "Biblia en 90 días · versión 0.1. El texto bíblico (Reina-Valera 1909, dominio público) " +
                "se integrará en una próxima versión.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp),
        )
    }

    if (confirmRestart) {
        AlertDialog(
            onDismissRequest = { confirmRestart = false },
            title = { Text("¿Reiniciar el plan?") },
            text = { Text("Se borrarán los ${state.snapshot.chaptersRead} capítulos marcados. No se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { confirmRestart = false; onRestartPlan() }) { Text("Reiniciar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmRestart = false }) { Text("Cancelar") }
            },
        )
    }
}
