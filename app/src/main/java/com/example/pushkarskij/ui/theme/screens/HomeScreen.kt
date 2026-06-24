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
import com.example.pushkarskij.ui.viewmodels.Habit
import com.example.pushkarskij.ui.viewmodels.HabitViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HabitViewModel,
    onNavigateToStatistics: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val habits by viewModel.habits.collectAsState()
    val todayProgress by viewModel.todayProgress.collectAsState()
    val totalHabits = habits.size
    val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("ru"))
    val currentDate = dateFormat.format(Date())

    val isDarkTheme = MaterialTheme.colorScheme.background == Color(0xFF0D0D1A)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "МОЙ ДЕНЬ",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = currentDate,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
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
                    selected = true,
                    onClick = { },
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
                    selected = false,
                    onClick = onNavigateToStatistics,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkTheme) Color(0xFF1A237E) else Color(0xFF6200EE)
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Прогресс: $todayProgress/$totalHabits",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = if (totalHabits > 0) todayProgress.toFloat() / totalHabits else 0f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Color(0xFF4CAF50),
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${if (totalHabits > 0) (todayProgress * 100 / totalHabits) else 0}%",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "ПРИВЫЧКИ НА СЕГОДНЯ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color(0xFF64B5F6) else Color(0xFF6200EE)
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(habits) { habit ->
                    HabitCard(
                        habit = habit,
                        viewModel = viewModel,
                        isDarkTheme = isDarkTheme
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun HabitCard(
    habit: Habit,
    viewModel: HabitViewModel,
    isDarkTheme: Boolean
) {
    val isCompletedToday = viewModel.isCompletedToday(habit)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF252540) else Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(habit.icon, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = habit.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.White else Color.Black
                    )
                    Text(
                        text = habit.target,
                        fontSize = 12.sp,
                        color = if (isDarkTheme) Color(0xFFB0B0B0) else Color.Gray
                    )
                }
            }

            Checkbox(
                checked = isCompletedToday,
                onCheckedChange = { viewModel.toggleHabit(habit.id) },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4CAF50))
            )
        }
    }
}
