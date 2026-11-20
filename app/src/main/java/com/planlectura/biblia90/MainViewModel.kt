package com.planlectura.biblia90

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.planlectura.biblia90.data.PreferencesRepository
import com.planlectura.biblia90.data.UserPreferences
import com.planlectura.biblia90.domain.ChapterRef
import com.planlectura.biblia90.domain.PlanDay
import com.planlectura.biblia90.domain.ProgressCalculator
import com.planlectura.biblia90.domain.ProgressSnapshot
import com.planlectura.biblia90.domain.ReadingPlan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed interface MainUiState {
    data object Loading : MainUiState
    data class Error(val message: String) : MainUiState
    data class Onboarding(val plan: ReadingPlan, val today: LocalDate) : MainUiState
    data class Ready(
        val plan: ReadingPlan,
        val prefs: UserPreferences,
        val read: Set<ChapterRef>,
        val snapshot: ProgressSnapshot,
        val today: LocalDate,
    ) : MainUiState
}

class MainViewModel(private val container: AppContainer) : ViewModel() {

    private val prefsRepo = container.preferencesRepository
    private val progressRepo = container.progressRepository
    private val today = MutableStateFlow(LocalDate.now())

    private val planFlow = prefsRepo.preferences
        .map { it.planId }
        .distinctUntilChanged()
        .map { container.planRepository.plan(it) }

    val uiState: StateFlow<MainUiState> =
        combine(prefsRepo.preferences, planFlow, progressRepo.readChapters, today) { prefs, plan, read, date ->
            val start = prefs.startDate
            if (start == null) {
                MainUiState.Onboarding(plan, date)
            } else {
                MainUiState.Ready(
                    plan = plan,
                    prefs = prefs,
                    read = read,
                    snapshot = ProgressCalculator.calculate(plan, start, date, read),
                    today = date,
                )
            }
        }
            .flowOn(Dispatchers.Default)
            .catch { emit(MainUiState.Error(it.message ?: "Error al cargar el plan")) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MainUiState.Loading)

    /** Se llama al volver a la app: si cambió el día, se recalcula "Hoy". */
    fun refreshDate() {
        today.value = LocalDate.now()
    }

    fun startPlan(startDate: LocalDate) = viewModelScope.launch {
        prefsRepo.startPlan(PreferencesRepository.DEFAULT_PLAN_ID, startDate)
    }

    fun setChapterRead(chapter: ChapterRef, read: Boolean) = viewModelScope.launch {
        if (read) progressRepo.markRead(listOf(chapter)) else progressRepo.markUnread(listOf(chapter))
    }

    fun setDayRead(day: PlanDay, read: Boolean) = viewModelScope.launch {
        if (read) progressRepo.markRead(day.chapters) else progressRepo.markUnread(day.chapters)
    }

    fun setReminder(enabled: Boolean, hour: Int, minute: Int) = viewModelScope.launch {
        prefsRepo.setReminder(enabled, hour, minute)
        if (enabled) container.reminderScheduler.schedule(hour, minute) else container.reminderScheduler.cancel()
    }

    fun restartPlan() = viewModelScope.launch {
        progressRepo.reset()
        prefsRepo.clearPlan()
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as BibliaApp
                MainViewModel(app.container)
            }
        }
    }
}
