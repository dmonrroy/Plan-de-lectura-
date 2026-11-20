package com.planlectura.biblia90.data

import android.content.Context
import com.planlectura.biblia90.domain.PlanParser
import com.planlectura.biblia90.domain.PlanValidator
import com.planlectura.biblia90.domain.ReadingPlan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Carga los planes empaquetados en assets (100 % offline). En el futuro, los paquetes
 * descargados del CDN tendrán prioridad sobre los incluidos en el APK.
 */
class PlanRepository(private val context: Context) {

    private val mutex = Mutex()
    private val cache = mutableMapOf<String, ReadingPlan>()

    suspend fun plan(id: String): ReadingPlan = mutex.withLock {
        cache.getOrPut(id) {
            val file = ASSETS[id] ?: error("Plan desconocido: $id")
            val plan = withContext(Dispatchers.IO) {
                context.assets.open(file).bufferedReader().use { PlanParser.parse(it.readText()) }
            }
            val validation = PlanValidator.validate(plan)
            check(validation.isComplete) {
                "El plan $id no cubre toda la Biblia: faltan ${validation.missing.size}, " +
                    "duplicados ${validation.duplicated.size}"
            }
            plan
        }
    }

    companion object {
        private val ASSETS = mapOf("canonico-90" to "plan_canonico_90.json")
    }
}
