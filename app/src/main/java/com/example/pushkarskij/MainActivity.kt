package com.example.pushkarskij

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pushkarskij.navigation.Screen
import com.example.pushkarskij.ui.screens.HomeScreen
import com.example.pushkarskij.ui.screens.SettingsScreen
import com.example.pushkarskij.ui.screens.StatisticsScreen
import com.example.pushkarskij.ui.theme.MyApplicationTheme
import com.example.pushkarskij.ui.viewmodels.HabitViewModel
import com.example.pushkarskij.utils.DataStoreManager
import kotlinx.coroutines.runBlocking
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.DisposableEffect


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val viewModel: HabitViewModel = viewModel()

    var isDarkTheme by remember { mutableStateOf(false) }
    var remindersEnabled by remember { mutableStateOf(true) }
    var reminderTime by remember { mutableStateOf("09:00") }
    var isDataLoaded by remember { mutableStateOf(false) }

    // Загрузка сохранённых настроек при запуске
    LaunchedEffect(Unit) {
        dataStoreManager.loadAllData().collect { (_, settings) ->
            isDarkTheme = settings.first
            remindersEnabled = settings.second
            reminderTime = settings.third
            isDataLoaded = true
        }
    }

    // Сохранение настроек при изменении
    fun saveSettings(dark: Boolean, reminders: Boolean, time: String) {
        runBlocking {
            dataStoreManager.saveAllData(
                habits = viewModel.habits.value,
                isDarkTheme = dark,
                remindersEnabled = reminders,
                reminderTime = time
            )
        }
    }

    // Сохранение при закрытии приложения
    DisposableEffect(Unit) {
        onDispose {
            runBlocking {
                dataStoreManager.saveAllData(
                    habits = viewModel.habits.value,
                    isDarkTheme = isDarkTheme,
                    remindersEnabled = remindersEnabled,
                    reminderTime = reminderTime
                )
            }
        }
    }

    MyApplicationTheme(darkTheme = isDarkTheme) {
        val navController = rememberNavController()

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            if (isDataLoaded) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToStatistics = { navController.navigate(Screen.Statistics.route) },
                            onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                        )
                    }
                    composable(Screen.Statistics.route) {
                        StatisticsScreen(
                            viewModel = viewModel,
                            onNavigateToHome = { navController.navigate(Screen.Home.route) },
                            onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                        )
                    }
                    composable(Screen.Settings.route) {
                        SettingsScreen(
                            viewModel = viewModel,
                            onNavigateToHome = { navController.navigate(Screen.Home.route) },
                            onNavigateToStatistics = { navController.navigate(Screen.Statistics.route) },
                            isDarkTheme = isDarkTheme,
                            onThemeChange = {
                                isDarkTheme = it
                                saveSettings(it, remindersEnabled, reminderTime)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    MyApp()
}
