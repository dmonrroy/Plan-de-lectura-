package com.planlectura.biblia90.domain

import java.time.Duration
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class ReminderTimeTest {
    private val now = LocalDateTime.of(2027, 1, 1, 8, 30)

    @Test
    fun laterToday() = assertEquals(Duration.ofMinutes(30), ReminderTime.delayUntil(9, 0, now))

    @Test
    fun alreadyPassedGoesToTomorrow() = assertEquals(Duration.ofHours(23), ReminderTime.delayUntil(7, 30, now))

    @Test
    fun exactlyNowGoesToTomorrow() = assertEquals(Duration.ofDays(1), ReminderTime.delayUntil(8, 30, now))
}
