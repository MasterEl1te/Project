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
fun SettingsScreen(
    viewModel: HabitViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToStatistics: () -> Unit
) {
    val habits by viewModel.habits.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var newHabitName by remember { mutableStateOf("") }
    var newHabitTarget by remember { mutableStateOf("") }

    var remindersEnabled by remember { mutableStateOf(true) }
    var reminderTime by remember { mutableStateOf("09:00") }
    var darkThemeEnabled by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("УПРАВЛЕНИЕ", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xFF6200EE)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToHome,
                    icon = { Text("🏠", fontSize = 24.sp) },
                    label = { Text("Главная", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToStatistics,
                    icon = { Text("📊", fontSize = 24.sp) },
                    label = { Text("Статистика", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Text("⚙️", fontSize = 24.sp) },
                    label = { Text("Настройки", fontSize = 10.sp) }
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "МОИ ПРИВЫЧКИ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6200EE)
                )
            }

            items(habits) { habit ->
                ManageHabitCard(habit = habit, onDelete = { viewModel.deleteHabit(habit.id) })
            }

            item {
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                ) {
                    Text("+ ДОБАВИТЬ НОВУЮ ПРИВЫЧКУ", fontSize = 14.sp)
                }
            }

            item {
                Text(
                    text = "НАСТРОЙКИ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6200EE),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            item {
                SettingsItem(
                    title = "Напоминания",
                    value = if (remindersEnabled) "Включено" else "Выключено",
                    isSwitch = true,
                    switchState = remindersEnabled,
                    onToggle = { remindersEnabled = !remindersEnabled }
                )
            }
            item {
                SettingsItem(
                    title = "Время напоминания",
                    value = reminderTime,
                    isTimePicker = true
                )
            }
            item {
                SettingsItem(
                    title = "Тёмная тема",
                    value = if (darkThemeEnabled) "Включено" else "Выключено",
                    isSwitch = true,
                    switchState = darkThemeEnabled,
                    onToggle = { darkThemeEnabled = !darkThemeEnabled }
                )
            }

            item {
                Spacer(Modifier.height(32.dp))
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Новая привычка") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newHabitName,
                        onValueChange = { newHabitName = it },
                        label = { Text("Название привычки") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newHabitTarget,
                        onValueChange = { newHabitTarget = it },
                        label = { Text("Цель (например: 8 стаканов)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newHabitName.isNotBlank() && newHabitTarget.isNotBlank()) {
                            viewModel.addHabit(newHabitName, newHabitTarget)
                            newHabitName = ""
                            newHabitTarget = ""
                            showDialog = false
                        }
                    }
                ) {
                    Text("Добавить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun ManageHabitCard(
    habit: com.example.pushkarskij.ui.viewmodels.Habit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(habit.icon, fontSize = 24.sp)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(habit.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(habit.target, fontSize = 12.sp, color = Color.Gray)
                }
            }
            TextButton(
                onClick = onDelete,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
            ) {
                Text("Удалить")
            }
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    value: String,
    isSwitch: Boolean = false,
    isTimePicker: Boolean = false,
    switchState: Boolean = false,
    onToggle: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontSize = 16.sp)
            when {
                isSwitch -> {
                    Switch(
                        checked = switchState,
                        onCheckedChange = { onToggle?.invoke() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF6200EE))
                    )
                }
                else -> {
                    Text(value, fontSize = 14.sp, color = Color.Gray)
                }
            }
        }
    }
}