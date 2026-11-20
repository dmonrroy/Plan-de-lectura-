package com.planlectura.biblia90

import android.content.Context
import androidx.room.Room
import com.planlectura.biblia90.data.PlanRepository
import com.planlectura.biblia90.data.PreferencesRepository
import com.planlectura.biblia90.data.ProgressRepository
import com.planlectura.biblia90.data.ReadingDatabase
import com.planlectura.biblia90.notifications.ReminderScheduler

/** Inyección de dependencias manual; migrable a Hilt cuando el proyecto crezca. */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    private val database: ReadingDatabase by lazy {
        Room.databaseBuilder(appContext, ReadingDatabase::class.java, "biblia90.db").build()
    }

    val planRepository by lazy { PlanRepository(appContext) }
    val preferencesRepository by lazy { PreferencesRepository(appContext) }
    val progressRepository by lazy { ProgressRepository(database.chapterReadDao()) }
    val reminderScheduler by lazy { ReminderScheduler(appContext) }
}
