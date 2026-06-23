package com.example.pushkarskij.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Habit(
    val id: Int,
    val name: String,
    val icon: String,
    val target: String,
    val isCompleted: Boolean = false
)

class HabitViewModel : ViewModel() {

    private val _habits = MutableStateFlow<List<Habit>>(
        listOf(
            Habit(1, "Выпить воду", "💧", "8 стаканов"),
            Habit(2, "Утренняя зарядка", "💪", "15 минут"),
            Habit(3, "Чтение", "📚", "30 минут"),
            Habit(4, "Медитация", "🧘", "10 минут"),
            Habit(5, "Витамины", "💊", "1 раз")
        )
    )
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    private val _todayProgress = MutableStateFlow(0)
    val todayProgress: StateFlow<Int> = _todayProgress.asStateFlow()

    private val _habitStats = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val habitStats: StateFlow<Map<Int, Int>> = _habitStats.asStateFlow()

    init {
        updateProgress()
    }

    fun toggleHabit(habitId: Int) {
        _habits.value = _habits.value.map { habit ->
            if (habit.id == habitId) {
                habit.copy(isCompleted = !habit.isCompleted)
            } else {
                habit
            }
        }
        updateProgress()
    }

    private fun updateProgress() {
        val completedCount = _habits.value.count { it.isCompleted }
        _todayProgress.value = completedCount

        val stats = _habits.value.associate { habit ->
            habit.id to (if (habit.isCompleted) (5..7).random() else (1..4).random())
        }
        _habitStats.value = stats
    }

    fun addHabit(name: String, target: String) {
        val newId = (_habits.value.maxOfOrNull { it.id } ?: 0) + 1
        val newHabit = Habit(
            id = newId,
            name = name,
            icon = getIconForHabit(name),
            target = target
        )
        _habits.value = _habits.value + newHabit
        updateProgress()
    }

    fun deleteHabit(habitId: Int) {
        _habits.value = _habits.value.filter { it.id != habitId }
        updateProgress()
    }

    private fun getIconForHabit(name: String): String = when {
        name.contains("вода", ignoreCase = true) -> "💧"
        name.contains("зарядка", ignoreCase = true) -> "💪"
        name.contains("чтение", ignoreCase = true) -> "📚"
        name.contains("медитация", ignoreCase = true) -> "🧘"
        name.contains("витамины", ignoreCase = true) -> "💊"
        else -> "✅"
    }
}