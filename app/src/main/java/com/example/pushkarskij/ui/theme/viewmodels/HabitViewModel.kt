package com.example.pushkarskij.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pushkarskij.utils.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class Habit(
    val id: Int,
    val name: String,
    val icon: String,
    val target: String,
    val completedDates: List<Date> = emptyList()
)

data class HabitStats(
    val habitId: Int,
    val completedDays: Int,
    val percentage: Int
)

data class DailyProgress(
    val date: Date,
    val dateString: String,
    val completedCount: Int,
    val totalHabits: Int
)

class HabitViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStoreManager = DataStoreManager(application)

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    private val _todayProgress = MutableStateFlow(0)
    val todayProgress: StateFlow<Int> = _todayProgress.asStateFlow()

    private val _habitStatsMap = MutableStateFlow<Map<Int, HabitStats>>(emptyMap())
    val habitStats: StateFlow<Map<Int, HabitStats>> = _habitStatsMap.asStateFlow()

    private val _bestDay = MutableStateFlow<DailyProgress?>(null)
    val bestDay: StateFlow<DailyProgress?> = _bestDay.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private val today = Date()

    init {
        loadDataFromStorage()
    }

    private fun loadDataFromStorage() {
        viewModelScope.launch {
            dataStoreManager.loadAllData().collect { (habits, settings) ->
                _habits.value = habits
                updateProgress()
                updateAllStats()
                calculateBestDay()
            }
        }
    }

    private fun saveData() {
        viewModelScope.launch {
            // Настройки сохраняются отдельно через MainActivity
        }
    }

    fun saveHabitsToStorage() {
        viewModelScope.launch {
            dataStoreManager.saveAllData(
                habits = _habits.value,
                isDarkTheme = false,  // будет перезаписано в MainActivity
                remindersEnabled = true,
                reminderTime = "09:00"
            )
        }
    }


    fun isCompletedToday(habit: Habit): Boolean {
        val todayStr = dateFormat.format(today)
        return habit.completedDates.any { dateFormat.format(it) == todayStr }
    }

    private fun addTodayDate(dates: List<Date>): List<Date> {
        val todayStr = dateFormat.format(today)
        if (dates.any { dateFormat.format(it) == todayStr }) {
            return dates
        }
        return dates + today
    }

    private fun removeTodayDate(dates: List<Date>): List<Date> {
        return dates.filter { dateFormat.format(it) != dateFormat.format(today) }
    }

    private fun countUniqueDays(dates: List<Date>): Int {
        return dates.map { dateFormat.format(it) }.distinct().size
    }

    fun toggleHabit(habitId: Int) {
        _habits.value = _habits.value.map { habit ->
            if (habit.id == habitId) {
                if (isCompletedToday(habit)) {
                    val newDates = removeTodayDate(habit.completedDates)
                    habit.copy(completedDates = newDates)
                } else {
                    val newDates = addTodayDate(habit.completedDates)
                    habit.copy(completedDates = newDates)
                }
            } else {
                habit
            }
        }
        updateProgress()
        updateAllStats()
        calculateBestDay()
        saveHabitsToStorage()
    }

    private fun updateProgress() {
        val completedCount = _habits.value.count { isCompletedToday(it) }
        _todayProgress.value = completedCount
    }

    private fun updateAllStats() {
        val stats = _habits.value.associate { habit ->
            val days = countUniqueDays(habit.completedDates)
            val percentage = (days * 100 / 7).coerceAtMost(100)
            habit.id to HabitStats(
                habitId = habit.id,
                completedDays = days,
                percentage = percentage
            )
        }
        _habitStatsMap.value = stats
    }

    fun calculateBestDay() {
        val totalHabits = _habits.value.size
        if (totalHabits == 0) {
            _bestDay.value = null
            return
        }

        val allDates = mutableMapOf<String, MutableList<Int>>()
        _habits.value.forEach { habit ->
            habit.completedDates.forEach { date ->
                val dateStr = dateFormat.format(date)
                if (!allDates.containsKey(dateStr)) {
                    allDates[dateStr] = mutableListOf()
                }
                allDates[dateStr]?.add(habit.id)
            }
        }

        if (allDates.isEmpty()) {
            _bestDay.value = null
            return
        }

        var bestDayDate: Date? = null
        var bestDayCount = 0

        allDates.forEach { (dateStr, habitIds) ->
            val uniqueHabits = habitIds.distinct().size
            if (uniqueHabits > bestDayCount) {
                bestDayCount = uniqueHabits
                _habits.value.forEach { habit ->
                    habit.completedDates.forEach { date ->
                        if (dateFormat.format(date) == dateStr) {
                            bestDayDate = date
                        }
                    }
                }
            }
        }

        if (bestDayDate != null && bestDayCount > 0) {
            _bestDay.value = DailyProgress(
                date = bestDayDate!!,
                dateString = displayDateFormat.format(bestDayDate),
                completedCount = bestDayCount,
                totalHabits = totalHabits
            )
        } else {
            _bestDay.value = null
        }
    }

    fun addHabit(name: String, target: String) {
        val newId = (_habits.value.maxOfOrNull { it.id } ?: 0) + 1
        val newHabit = Habit(
            id = newId,
            name = name,
            icon = getIconForHabit(name),
            target = target,
            completedDates = emptyList()
        )
        _habits.value = _habits.value + newHabit
        updateAllStats()
        updateProgress()
        calculateBestDay()
        saveHabitsToStorage()
    }

    fun deleteHabit(habitId: Int) {
        _habits.value = _habits.value.filter { it.id != habitId }
        _habitStatsMap.value = _habitStatsMap.value.filterKeys { it != habitId }
        updateProgress()
        calculateBestDay()
        saveHabitsToStorage()
    }

    fun getDeclension(count: Int, word: String): String {
        return when (word) {
            "привычка" -> {
                when {
                    count % 10 == 1 && count % 100 != 11 -> "$count привычка"
                    count % 10 in 2..4 && count % 100 !in 12..14 -> "$count привычки"
                    else -> "$count привычек"
                }
            }
            "день" -> {
                when {
                    count % 10 == 1 && count % 100 != 11 -> "$count день"
                    count % 10 in 2..4 && count % 100 !in 12..14 -> "$count дня"
                    else -> "$count дней"
                }
            }
            else -> "$count $word"
        }
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
