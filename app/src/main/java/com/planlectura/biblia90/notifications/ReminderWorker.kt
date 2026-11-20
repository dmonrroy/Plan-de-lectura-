package com.planlectura.biblia90.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.planlectura.biblia90.BibliaApp
import com.planlectura.biblia90.domain.ProgressCalculator
import kotlinx.coroutines.flow.first
import java.time.LocalDate

/** Avisa sólo si queda lectura pendiente: si hoy ya leíste, no molesta. */
class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val container = (applicationContext as BibliaApp).container
        val prefs = container.preferencesRepository.current()
        val start = prefs.startDate ?: return Result.success()
        if (!prefs.reminderEnabled) return Result.success()

        val plan = container.planRepository.plan(prefs.planId)
        val read = container.progressRepository.readChapters.first()
        val snapshot = ProgressCalculator.calculate(plan, start, LocalDate.now(), read)

        if (snapshot.currentDayNumber < 1 || snapshot.isFinished) return Result.success()
        if (snapshot.chaptersBehind <= 0) return Result.success()

        val next = snapshot.nextPending ?: return Result.success()
        ReminderNotifier.show(
            applicationContext,
            dayNumber = next.day.number,
            readingLabel = next.day.label,
            minutes = next.day.estimatedMinutes,
        )
        return Result.success()
    }
}
