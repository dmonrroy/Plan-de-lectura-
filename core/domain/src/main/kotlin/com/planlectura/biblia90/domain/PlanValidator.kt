package com.planlectura.biblia90.domain

data class ValidationResult(
    val missing: List<ChapterRef>,
    val duplicated: List<ChapterRef>,
    val unknown: List<ChapterRef>,
) {
    /** El plan cubre cada capítulo de la Biblia exactamente una vez. */
    val isComplete: Boolean get() = missing.isEmpty() && duplicated.isEmpty() && unknown.isEmpty()
}

/** Garantiza que un plan cubre los 1.189 capítulos de la Biblia. */
object PlanValidator {
    fun validate(plan: ReadingPlan): ValidationResult {
        val all = BibleCatalog.allChapters()
        val valid = all.toSet()
        val counts = plan.days.flatMap { it.chapters }.groupingBy { it }.eachCount()
        return ValidationResult(
            missing = all.filter { it !in counts },
            duplicated = counts.filter { it.value > 1 && it.key in valid }.keys.toList(),
            unknown = counts.keys.filter { it !in valid },
        )
    }
}
