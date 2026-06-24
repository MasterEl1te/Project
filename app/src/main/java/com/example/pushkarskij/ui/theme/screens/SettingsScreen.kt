package com.example.pushkarskij.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pushkarskij.ui.viewmodels.HabitViewModel
import android.app.TimePickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: HabitViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val habits by viewModel.habits.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var newHabitName by remember { mutableStateOf("") }
    var newHabitTarget by remember { mutableStateOf("") }

    var remindersEnabled by remember { mutableStateOf(true) }
    var reminderTime by remember { mutableStateOf("09:00") }
    var showTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "УПРАВЛЕНИЕ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
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
                    selected = true,
                    onClick = { },
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
                    color = if (isDarkTheme) Color(0xFF64B5F6) else Color(0xFF6200EE)
                )
            }

            items(habits) { habit ->
                ManageHabitCard(
                    habit = habit,
                    onDelete = { viewModel.deleteHabit(habit.id) },
                    isDarkTheme = isDarkTheme
                )
            }

            item {
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDarkTheme) Color(0xFF1A237E) else Color(0xFF6200EE)
                    )
                ) {
                    Text("+ ДОБАВИТЬ НОВУЮ ПРИВЫЧКУ", fontSize = 14.sp)
                }
            }

            item {
                Text(
                    text = "НАСТРОЙКИ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color(0xFF64B5F6) else Color(0xFF6200EE),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            item {
                SettingsItem(
                    title = "Напоминания",
                    value = if (remindersEnabled) "Включено" else "Выключено",
                    isSwitch = true,
                    switchState = remindersEnabled,
                    onToggle = { remindersEnabled = !remindersEnabled },
                    isDarkTheme = isDarkTheme
                )
            }

            item {
                SettingsItem(
                    title = "Время напоминания",
                    value = reminderTime,
                    isTimePicker = true,
                    onTimeClick = { showTimePicker = true },
                    isDarkTheme = isDarkTheme
                )
            }

            item {
                SettingsItem(
                    title = "Тёмная тема",
                    value = if (isDarkTheme) "Включено" else "Выключено",
                    isSwitch = true,
                    switchState = isDarkTheme,
                    onToggle = { onThemeChange(!isDarkTheme) },
                    isDarkTheme = isDarkTheme
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
            title = {
                Text(
                    text = "Новая привычка",
                    color = if (isDarkTheme) Color.White else Color.Black
                )
            },
            text = {
                Column {
                    TextField(
                        value = newHabitName,
                        onValueChange = { newHabitName = it },
                        label = { Text("Название привычки") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    TextField(
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
                    Text("Добавить", color = if (isDarkTheme) Color(0xFF64B5F6) else Color(0xFF6200EE))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Отмена", color = if (isDarkTheme) Color(0xFFB0B0B0) else Color.Gray)
                }
            }
        )
    }

    if (showTimePicker) {
        val currentParts = reminderTime.split(":")
        val currentHour = currentParts[0].toIntOrNull() ?: 9
        val currentMinute = currentParts[1].toIntOrNull() ?: 0

        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                reminderTime = String.format("%02d:%02d", hourOfDay, minute)
                showTimePicker = false
            },
            currentHour,
            currentMinute,
            true
        ).apply {
            setTitle("Выберите время напоминания")
            show()
        }
        showTimePicker = false
    }
}

@Composable
fun ManageHabitCard(
    habit: com.example.pushkarskij.ui.viewmodels.Habit,
    onDelete: () -> Unit,
    isDarkTheme: Boolean
) {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(habit.icon, fontSize = 24.sp)
                Spacer(Modifier.width(12.dp))
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
            TextButton(
                onClick = onDelete,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (isDarkTheme) Color(0xFFFF6B6B) else Color.Red
                )
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
    onToggle: (() -> Unit)? = null,
    onTimeClick: (() -> Unit)? = null,
    isDarkTheme: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(12.dp)),
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
            Text(
                text = title,
                fontSize = 16.sp,
                color = if (isDarkTheme) Color.White else Color.Black
            )

            when {
                isSwitch -> {
                    Switch(
                        checked = switchState,
                        onCheckedChange = { onToggle?.invoke() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = if (isDarkTheme) Color(0xFF64B5F6) else Color(0xFF6200EE),
                            checkedTrackColor = if (isDarkTheme) Color(0xFF3A3A5A) else Color(0xFFD0BCFF),
                            uncheckedThumbColor = if (isDarkTheme) Color(0xFF888888) else Color(0xFF9E9E9E)
                        )
                    )
                }
                isTimePicker -> {
                    Text(
                        text = value,
                        fontSize = 16.sp,
                        color = if (isDarkTheme) Color(0xFF64B5F6) else Color(0xFF6200EE),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onTimeClick?.invoke() }
                    )
                }
                else -> {
                    Text(
                        text = value,
                        fontSize = 14.sp,
                        color = if (isDarkTheme) Color(0xFFB0B0B0) else Color.Gray
                    )
                }
            }
        }
    }
}
