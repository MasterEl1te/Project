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
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import androidx.compose.runtime.saveable.rememberSaveable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Загружаем тему ДО создания Compose
        val dataStoreManager = DataStoreManager(this)
        var savedTheme = false
        runBlocking {
            val data = dataStoreManager.loadAllData().first()
            savedTheme = data.second.first
        }
        val initialTheme = savedTheme

        setContent {
            MyApp(initialTheme = initialTheme)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(initialTheme: Boolean) {
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val habitViewModel: HabitViewModel = viewModel()
    val scope = rememberCoroutineScope()

    var isDarkTheme by rememberSaveable { mutableStateOf(initialTheme) }

    // Сохраняем тему при изменении
    fun saveTheme(theme: Boolean) {
        isDarkTheme = theme
        scope.launch {
            dataStoreManager.saveAllData(
                habits = habitViewModel.habits.value,
                isDarkTheme = theme,
                remindersEnabled = true,
                reminderTime = "09:00"
            )
        }
    }

    MyApplicationTheme(darkTheme = isDarkTheme) {
        val navController = rememberNavController()

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = habitViewModel,
                        isDarkTheme = isDarkTheme,
                        onNavigateToStatistics = {
                            navController.navigate(Screen.Statistics.route)
                        },
                        onNavigateToSettings = {
                            navController.navigate(Screen.Settings.route)
                        }
                    )
                }
                composable(Screen.Statistics.route) {
                    StatisticsScreen(
                        viewModel = habitViewModel,
                        isDarkTheme = isDarkTheme,
                        onNavigateToHome = {
                            navController.navigate(Screen.Home.route)
                        },
                        onNavigateToSettings = {
                            navController.navigate(Screen.Settings.route)
                        }
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        viewModel = habitViewModel,
                        isDarkTheme = isDarkTheme,
                        onThemeChange = { newTheme ->
                            saveTheme(newTheme)
                        },
                        onNavigateToHome = {
                            navController.navigate(Screen.Home.route)
                        },
                        onNavigateToStatistics = {
                            navController.navigate(Screen.Statistics.route)
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    MyApp(initialTheme = false)
}
