package com.example.pushkarskij.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pushkarskij.ui.viewmodels.HabitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: HabitViewModel,
    isDarkTheme: Boolean,
    onNavigateToHome: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val habits by viewModel.habits.collectAsState()
    val habitStats by viewModel.habitStats.collectAsState()
    val bestDay by viewModel.bestDay.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.calculateBestDay()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "СТАТИСТИКА", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = if (isDarkTheme) Color(0xFF1A237E) else Color(0xFF6200EE)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = if (isDarkTheme) Color(0xFF1A1A2E) else Color(0xFFE8E8E8),
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToHome,
                    icon = { Text("🏠", fontSize = 24.sp) },
                    label = { Text("Главная", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = if (isDarkTheme) Color(0xFF64B5F6) else Color(0xFF6200EE),
                        selectedTextColor = if (isDarkTheme) Color(0xFF64B5F6) else Color(0xFF6200EE),
                        unselectedIconColor = if (isDarkTheme) Color(0xFF666666) else Color(0xFF888888),
                        unselectedTextColor = if (isDarkTheme) Color(0xFF666666) else Color(0xFF888888)
                    )
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Text("📊", fontSize = 24.sp) },
                    label = { Text("Статистика", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = if (isDarkTheme) Color(0xFF64B5F6) else Color(0xFF6200EE),
                        selectedTextColor = if (isDarkTheme) Color(0xFF64B5F6) else Color(0xFF6200EE),
                        unselectedIconColor = if (isDarkTheme) Color(0xFF666666) else Color(0xFF888888),
                        unselectedTextColor = if (isDarkTheme) Color(0xFF666666) else Color(0xFF888888)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToSettings,
                    icon = { Text("⚙️", fontSize = 24.sp) },
                    label = { Text("Настройки", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = if (isDarkTheme) Color(0xFF64B5F6) else Color(0xFF6200EE),
                        selectedTextColor = if (isDarkTheme) Color(0xFF64B5F6) else Color(0xFF6200EE),
                        unselectedIconColor = if (isDarkTheme) Color(0xFF666666) else Color(0xFF888888),
                        unselectedTextColor = if (isDarkTheme) Color(0xFF666666) else Color(0xFF888888)
                    )
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) Color(0xFF1A1A2E) else Color(0xFFE8F5E9)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "🏆 Лучший день", fontSize = 14.sp,
                            color = if (isDarkTheme) Color(0xFFB0B0B0) else Color.Gray
                        )

                        if (bestDay != null && bestDay!!.completedCount > 0) {
                            Text(text = bestDay!!.dateString, fontSize = 18.sp, fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                            Text(text = "Выполнено ${viewModel.getDeclension(bestDay!!.completedCount, "привычка")} из ${bestDay!!.totalHabits}", fontSize = 14.sp,
                                color = if (isDarkTheme) Color(0xFFB0B0B0) else Color(0xFF333333)
                            )
                        } else {
                            Text(text = "Нет данных", fontSize = 16.sp,
                                color = if (isDarkTheme) Color(0xFF666666) else Color.Gray
                            )
                        }
                    }
                }
            }

            item {
                Text(text = "ПРИВЫЧКИ", fontSize = 16.sp, fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color(0xFF64B5F6) else Color(0xFF6200EE)
                )
            }

            items(habits) { habit ->
                val stats = habitStats[habit.id]
                val completedDays = stats?.completedDays ?: 0
                val percentage = stats?.percentage ?: 0

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) Color(0xFF252540) else Color.White
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(habit.icon, fontSize = 24.sp)
                            Spacer(Modifier.width(12.dp))
                            Text(text = habit.name, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) Color.White else Color.Black
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(text = "Выполнено: ${viewModel.getDeclension(completedDays, "день")} из 7", fontSize = 12.sp,
                            color = if (isDarkTheme) Color(0xFFB0B0B0) else Color.Gray
                        )
                        Spacer(Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = percentage / 100f,
                            modifier = Modifier.fillMaxWidth().height(6.dp),
                            color = Color(0xFF4CAF50),
                            trackColor = if (isDarkTheme) Color(0xFF3A3A5A) else Color(0xFFE0E0E0)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(text = "$percentage%", fontSize = 12.sp, fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
