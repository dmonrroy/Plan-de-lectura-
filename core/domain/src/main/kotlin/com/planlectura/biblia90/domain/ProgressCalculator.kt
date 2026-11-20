package com.planlectura.biblia90.domain

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.ceil

enum class DayStatus { COMPLETED, PARTIAL, BEHIND, TODAY, UPCOMING }

data class DayProgress(
    val day: PlanDay,
    val date: LocalDate,
    val chaptersRead: Int,
    val status: DayStatus,
) {
    val totalChapters: Int get() = day.chapters.size
    val fraction: Float get() = if (totalChapters == 0) 1f else chaptersRead.toFloat() / totalChapters
}

data class BookProgress(val book: Book, val chaptersRead: Int) {
    val fraction: Float get() = chaptersRead.toFloat() / book.chapters
}

data class ProgressSnapshot(
    /** Día del plan correspondiente a hoy (0 si aún no empieza, totalDays+1 si ya terminó el calendario). */
    val currentDayNumber: Int,
    val days: List<DayProgress>,
    val books: List<BookProgress>,
    val chaptersRead: Int,
    val totalChapters: Int,
    /** Capítulos que deberían estar leídos al final de hoy según el calendario. */
    val expectedChapters: Int,
    val projectedFinish: LocalDate?,
    val plannedFinish: LocalDate,
) {
    val fraction: Float get() = chaptersRead.toFloat() / totalChapters
    val isFinished: Boolean get() = chaptersRead >= totalChapters

    /** Positivo: capítulos de atraso. Negativo: capítulos de adelanto. */
    val chaptersBehind: Int get() = expectedChapters - chaptersRead

    val today: DayProgress? get() = days.getOrNull(currentDayNumber - 1)

    /** Primer día con lectura pendiente: lo que conviene leer ahora. */
    val nextPending: DayProgress? get() = days.firstOrNull { it.chaptersRead < it.totalChapters }
}

object ProgressCalculator {

    fun calculate(
        plan: ReadingPlan,
        startDate: LocalDate,
        today: LocalDate,
        read: Set<ChapterRef>,
    ): ProgressSnapshot {
        val currentDay = (ChronoUnit.DAYS.between(startDate, today) + 1)
            .coerceIn(0, plan.totalDays + 1L).toInt()

        val days = plan.days.map { day ->
            val readCount = day.chapters.count { it in read }
            val complete = readCount == day.chapters.size
            val status = when {
                complete -> DayStatus.COMPLETED
                day.number == currentDay -> DayStatus.TODAY
                day.number < currentDay -> if (readCount > 0) DayStatus.PARTIAL else DayStatus.BEHIND
                readCount > 0 -> DayStatus.PARTIAL
                else -> DayStatus.UPCOMING
            }
            DayProgress(day, startDate.plusDays(day.number - 1L), readCount, status)
        }

        val planChapters = plan.days.flatMap { it.chapters }.toSet()
        val chaptersRead = read.count { it in planChapters }
        val expected = plan.days.take(currentDay.coerceAtMost(plan.totalDays)).sumOf { it.chapters.size }

        val books = BibleCatalog.books.map { b -> BookProgress(b, read.count { it.bookId == b.id }) }

        return ProgressSnapshot(
            currentDayNumber = currentDay,
            days = days,
            books = books,
            chaptersRead = chaptersRead,
            totalChapters = planChapters.size,
            expectedChapters = expected,
            projectedFinish = projectFinish(planChapters.size, chaptersRead, startDate, today),
            plannedFinish = startDate.plusDays(plan.totalDays - 1L),
        )
    }

    /** Proyecta la fecha de finalización según el ritmo real desde el inicio. */
    internal fun projectFinish(total: Int, read: Int, start: LocalDate, today: LocalDate): LocalDate? {
        if (read >= total) return today
        val elapsed = ChronoUnit.DAYS.between(start, today) + 1
        if (elapsed <= 0 || read == 0) return null
        val pace = read.toDouble() / elapsed
        val remainingDays = ceil((total - read) / pace).toLong()
        return today.plusDays(remainingDays)
    }
}
