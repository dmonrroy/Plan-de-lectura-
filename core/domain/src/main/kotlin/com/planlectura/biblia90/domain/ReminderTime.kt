package com.planlectura.biblia90.domain

import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

object ReminderTime {
    /** Tiempo hasta la próxima ocurrencia de [hour]:[minute] (hoy si aún no pasó, si no mañana). */
    fun delayUntil(hour: Int, minute: Int, now: LocalDateTime): Duration {
        var next = now.toLocalDate().atTime(LocalTime.of(hour, minute))
        if (!next.isAfter(now)) next = next.plusDays(1)
        return Duration.between(now, next)
    }
}
