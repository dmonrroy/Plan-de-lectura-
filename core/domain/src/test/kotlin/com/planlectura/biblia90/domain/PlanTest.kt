package com.planlectura.biblia90.domain

import java.io.File
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PlanTest {

    private val plan: ReadingPlan by lazy {
        val path = System.getProperty("planFile") ?: "../../data/plan_canonico_90.json"
        PlanParser.parse(File(path).readText())
    }

    private val start = LocalDate.of(2027, 1, 1)

    @Test
    fun catalogHas66BooksAnd1189Chapters() {
        assertEquals(66, BibleCatalog.books.size)
        assertEquals(1189, BibleCatalog.totalChapters)
    }

    @Test
    fun canonicalPlanHas90DaysAndCoversWholeBible() {
        assertEquals(90, plan.totalDays)
        val result = PlanValidator.validate(plan)
        assertTrue(result.isComplete, "Faltan: ${result.missing.take(5)} Duplicados: ${result.duplicated.take(5)}")
    }

    @Test
    fun validatorDetectsMissingChapters() {
        val broken = plan.copy(days = plan.days.drop(1))
        val result = PlanValidator.validate(broken)
        assertEquals(plan.days.first().chapters, result.missing)
    }

    @Test
    fun firstDayLabel() {
        assertEquals("Génesis 1–12", plan.days.first().label)
    }

    @Test
    fun beforeStartNothingIsExpected() {
        val s = ProgressCalculator.calculate(plan, start, start.minusDays(3), emptySet())
        assertEquals(0, s.currentDayNumber)
        assertEquals(0, s.expectedChapters)
        assertNull(s.today)
        assertEquals(DayStatus.UPCOMING, s.days.first().status)
    }

    @Test
    fun onDayThreeWithOnlyDayOneReadIsBehind() {
        val read = plan.days[0].chapters.toSet()
        val s = ProgressCalculator.calculate(plan, start, start.plusDays(2), read)
        assertEquals(3, s.currentDayNumber)
        assertEquals(DayStatus.COMPLETED, s.days[0].status)
        assertEquals(DayStatus.BEHIND, s.days[1].status)
        assertEquals(DayStatus.TODAY, s.days[2].status)
        assertEquals(plan.days[1].chapters.size + plan.days[2].chapters.size, s.chaptersBehind)
        assertEquals(plan.days[1], s.nextPending?.day)
    }

    @Test
    fun finishedPlan() {
        val all = BibleCatalog.allChapters().toSet()
        val today = start.plusDays(60)
        val s = ProgressCalculator.calculate(plan, start, today, all)
        assertTrue(s.isFinished)
        assertEquals(today, s.projectedFinish)
        assertTrue(s.books.all { it.fraction == 1f })
    }

    @Test
    fun projectionFollowsPace() {
        // 10 días transcurridos, 100 capítulos leídos => 10 cap/día, faltan 1089 => 109 días.
        val today = start.plusDays(9)
        assertEquals(today.plusDays(109), ProgressCalculator.projectFinish(1189, 100, start, today))
        assertNull(ProgressCalculator.projectFinish(1189, 0, start, today))
    }
}
