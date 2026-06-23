package com.example.pushkarskij

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                MyDayApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDayApp() {
    val navController = rememberNavController()
    val viewModel: HabitViewModel = viewModel()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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
                    onNavigateToStatistics = { navController.navigate(Screen.Statistics.route) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyDayAppPreview() {
    MyApplicationTheme {
        MyDayApp()
    }
}