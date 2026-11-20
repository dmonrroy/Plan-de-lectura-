package com.planlectura.biblia90.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.planlectura.biblia90.MainUiState
import com.planlectura.biblia90.MainViewModel
import com.planlectura.biblia90.ui.screens.DayDetailScreen
import com.planlectura.biblia90.ui.screens.OnboardingScreen
import com.planlectura.biblia90.ui.screens.PlanScreen
import com.planlectura.biblia90.ui.screens.ProgressScreen
import com.planlectura.biblia90.ui.screens.SettingsScreen
import com.planlectura.biblia90.ui.screens.TodayScreen

private enum class Tab(val route: String, val label: String, val icon: ImageVector) {
    TODAY("hoy", "Hoy", Icons.Outlined.Today),
    PLAN("plan", "Plan", Icons.Outlined.CalendarMonth),
    PROGRESS("progreso", "Progreso", Icons.Outlined.Insights),
    SETTINGS("ajustes", "Ajustes", Icons.Outlined.Settings),
}

private const val DAY_ROUTE = "dia/{day}"

@Composable
fun BibliaRoot(viewModel: MainViewModel = viewModel(factory = MainViewModel.Factory)) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.refreshDate() }

    when (val s = state) {
        MainUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        is MainUiState.Error -> Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Text(s.message, color = MaterialTheme.colorScheme.error)
        }
        is MainUiState.Onboarding -> OnboardingScreen(
            plan = s.plan,
            today = s.today,
            onStart = { viewModel.startPlan(it) },
        )
        is MainUiState.Ready -> MainScaffold(s, viewModel)
    }
}

@Composable
private fun MainScaffold(state: MainUiState.Ready, viewModel: MainViewModel) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val destination = backStack?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                Tab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = destination?.hierarchy?.any { it.route == tab.route } == true,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = null) },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Tab.TODAY.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Tab.TODAY.route) {
                TodayScreen(
                    state = state,
                    onChapterChange = viewModel::setChapterRead,
                    onDayChange = viewModel::setDayRead,
                    onOpenDay = { navController.navigate("dia/$it") },
                )
            }
            composable(Tab.PLAN.route) {
                PlanScreen(state = state, onOpenDay = { navController.navigate("dia/$it") })
            }
            composable(Tab.PROGRESS.route) { ProgressScreen(state = state) }
            composable(Tab.SETTINGS.route) {
                SettingsScreen(
                    state = state,
                    onReminderChange = viewModel::setReminder,
                    onRestartPlan = viewModel::restartPlan,
                )
            }
            composable(DAY_ROUTE, arguments = listOf(navArgument("day") { type = NavType.IntType })) { entry ->
                DayDetailScreen(
                    state = state,
                    dayNumber = entry.arguments?.getInt("day") ?: 1,
                    onBack = { navController.popBackStack() },
                    onChapterChange = viewModel::setChapterRead,
                    onDayChange = viewModel::setDayRead,
                )
            }
        }
    }
}
