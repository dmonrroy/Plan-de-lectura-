package com.planlectura.biblia90.ui

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val spanish: Locale = Locale.forLanguageTag("es")
private val longFormat = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", spanish)
private val mediumFormat = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", spanish)
private val shortFormat = DateTimeFormatter.ofPattern("d MMM", spanish)

/** "Lunes 4 de enero" */
fun LocalDate.longLabel(): String = format(longFormat).replaceFirstChar { it.titlecase(spanish) }

/** "4 de enero de 2027" */
fun LocalDate.mediumLabel(): String = format(mediumFormat)

/** "4 ene" */
fun LocalDate.shortLabel(): String = format(shortFormat).trimEnd('.')

fun Float.percentLabel(): String = "${(this * 100).toInt()} %"
