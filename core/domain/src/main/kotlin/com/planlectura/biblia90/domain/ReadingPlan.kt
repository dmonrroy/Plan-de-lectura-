package com.planlectura.biblia90.domain

/** Un rango de capítulos consecutivos de un mismo libro, p. ej. "Génesis 1–12". */
data class Reading(
    val bookId: String,
    val bookName: String,
    val fromChapter: Int,
    val toChapter: Int,
) {
    val chapters: List<ChapterRef> get() = (fromChapter..toChapter).map { ChapterRef(bookId, it) }

    val label: String
        get() = if (fromChapter == toChapter) "$bookName $fromChapter" else "$bookName $fromChapter–$toChapter"
}

data class PlanDay(
    val number: Int,
    val readings: List<Reading>,
    val estimatedVerses: Int,
) {
    val chapters: List<ChapterRef> get() = readings.flatMap { it.chapters }

    val label: String get() = readings.joinToString("; ") { it.label }

    /** Minutos estimados de lectura (~7,5 versículos por minuto). */
    val estimatedMinutes: Int get() = (estimatedVerses / 7.5).toInt().coerceAtLeast(1)
}

data class ReadingPlan(
    val id: String,
    val name: String,
    val days: List<PlanDay>,
) {
    val totalDays: Int get() = days.size
    val totalChapters: Int get() = days.sumOf { it.chapters.size }

    fun day(number: Int): PlanDay? = days.getOrNull(number - 1)
}
