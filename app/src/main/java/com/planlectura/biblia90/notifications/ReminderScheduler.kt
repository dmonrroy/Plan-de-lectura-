package com.planlectura.biblia90.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.planlectura.biblia90.domain.ReminderTime
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/**
 * Programa el recordatorio diario con WorkManager (sobrevive a reinicios del dispositivo).
 * Siguiente paso: AlarmManager con alarma exacta cuando el usuario quiera puntualidad al minuto.
 */
class ReminderScheduler(private val context: Context) {

    fun schedule(hour: Int, minute: Int, now: LocalDateTime = LocalDateTime.now()) {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(ReminderTime.delayUntil(hour, minute, now).toMillis(), TimeUnit.MILLISECONDS)
            .addTag(WORK_NAME)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request,
        )
    }

    fun cancel() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    companion object {
        private const val WORK_NAME = "daily_reading_reminder"
    }
}
