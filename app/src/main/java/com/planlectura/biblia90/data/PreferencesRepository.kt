package com.planlectura.biblia90.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate

data class UserPreferences(
    val planId: String,
    val startDate: LocalDate?,
    val reminderEnabled: Boolean,
    val reminderHour: Int,
    val reminderMinute: Int,
)

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val planId = stringPreferencesKey("plan_id")
        val startDate = longPreferencesKey("start_epoch_day")
        val reminderEnabled = booleanPreferencesKey("reminder_enabled")
        val reminderHour = intPreferencesKey("reminder_hour")
        val reminderMinute = intPreferencesKey("reminder_minute")
    }

    val preferences: Flow<UserPreferences> = context.dataStore.data.map { it.toUserPreferences() }

    suspend fun current(): UserPreferences = preferences.first()

    suspend fun startPlan(planId: String, startDate: LocalDate) {
        context.dataStore.edit {
            it[Keys.planId] = planId
            it[Keys.startDate] = startDate.toEpochDay()
        }
    }

    suspend fun clearPlan() {
        context.dataStore.edit { it.remove(Keys.startDate) }
    }

    suspend fun setReminder(enabled: Boolean, hour: Int, minute: Int) {
        context.dataStore.edit {
            it[Keys.reminderEnabled] = enabled
            it[Keys.reminderHour] = hour
            it[Keys.reminderMinute] = minute
        }
    }

    private fun Preferences.toUserPreferences() = UserPreferences(
        planId = this[Keys.planId] ?: DEFAULT_PLAN_ID,
        startDate = this[Keys.startDate]?.let(LocalDate::ofEpochDay),
        reminderEnabled = this[Keys.reminderEnabled] ?: false,
        reminderHour = this[Keys.reminderHour] ?: 7,
        reminderMinute = this[Keys.reminderMinute] ?: 0,
    )

    companion object {
        const val DEFAULT_PLAN_ID = "canonico-90"
    }
}
