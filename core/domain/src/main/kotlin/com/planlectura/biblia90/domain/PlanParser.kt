package com.planlectura.biblia90.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** Convierte los paquetes de plan (JSON generados por tools/generar_plan.py) en el modelo de dominio. */
object PlanParser {
    private val json = Json { ignoreUnknownKeys = true }

    fun parse(text: String): ReadingPlan {
        val dto = json.decodeFromString(PlanDto.serializer(), text)
        return ReadingPlan(
            id = dto.id,
            name = dto.name,
            days = dto.days.sortedBy { it.day }.map { d ->
                PlanDay(
                    number = d.day,
                    readings = d.readings.map { Reading(it.bookId, it.bookName, it.from, it.to) },
                    estimatedVerses = d.estimatedVerses,
                )
            },
        )
    }

    @Serializable
    private data class PlanDto(
        val id: String,
        @SerialName("nombre") val name: String,
        @SerialName("dias") val days: List<DayDto>,
    )

    @Serializable
    private data class DayDto(
        @SerialName("dia") val day: Int,
        @SerialName("lecturas") val readings: List<ReadingDto>,
        @SerialName("versiculos_estimados") val estimatedVerses: Int = 0,
    )

    @Serializable
    private data class ReadingDto(
        @SerialName("libro") val bookId: String,
        @SerialName("nombre") val bookName: String,
        @SerialName("desde") val from: Int,
        @SerialName("hasta") val to: Int,
    )
}
