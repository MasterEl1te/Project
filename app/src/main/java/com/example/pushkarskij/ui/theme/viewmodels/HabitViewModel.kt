package com.example.pushkarskij.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

data class Habit(
    val id: Int,
    val name: String,
    val icon: String,
    val target: String,
    val completedDates: List<Date> = emptyList()  // список дат выполнения
) {
    // Проверка, выполнена ли привычка в конкретный день
    fun isCompletedOnDate(date: Date, dateFormat: SimpleDateFormat): Boolean {
        val dateStr = dateFormat.format(date)
        return completedDates.any { dateFormat.format(it) == dateStr }
    }
}

// Класс для хранения статистики привычки
data class HabitStats(
    val habitId: Int,
    val completedDays: Int,  // количество уникальных дней выполнения
    val percentage: Int      // процент выполнения (из 7 дней)
)

// Класс для хранения дневной статистики
data class DailyProgress(
    val date: Date,
    val dateString: String,   // для отображения "24.06.2026"
    val completedCount: Int,  // сколько привычек выполнено в этот день
    val totalHabits: Int      // всего привычек
)

class HabitViewModel : ViewModel() {

    private val _habits = MutableStateFlow<List<Habit>>(
        listOf(
            Habit(1, "Выпить воду", "💧", "8 стаканов", getInitialDates(0)),
            Habit(2, "Утренняя зарядка", "💪", "15 минут", getInitialDates(0)),
            Habit(3, "Чтение", "📚", "30 минут", getInitialDates(0)),
            Habit(4, "Медитация", "🧘", "10 минут", getInitialDates(0)),
            Habit(5, "Витамины", "💊", "1 раз", getInitialDates(0))
        )
    )
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    private val _todayProgress = MutableStateFlow(0)
    val todayProgress: StateFlow<Int> = _todayProgress.asStateFlow()

    private val _habitStatsMap = MutableStateFlow<Map<Int, HabitStats>>(emptyMap())
    val habitStats: StateFlow<Map<Int, HabitStats>> = _habitStatsMap.asStateFlow()

    // Лучший день
    private val _bestDay = MutableStateFlow<DailyProgress?>(null)
    val bestDay: StateFlow<DailyProgress?> = _bestDay.asStateFlow()

    // Формат для сравнения дат
    private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private val today = Date()

    init {
        updateProgress()
        updateAllStats()
        calculateBestDay()
    }

    // Создание начальных дат для демонстрации
    private fun getInitialDates(count: Int): List<Date> {
        val dates = mutableListOf<Date>()
        val calendar = Calendar.getInstance()
        for (i in 0 until count) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            dates.add(calendar.time)
        }
        return dates.reversed()
    }

    // Проверка, выполнена ли привычка сегодня
    fun isCompletedToday(habit: Habit): Boolean {
        val todayStr = dateFormat.format(today)
        return habit.completedDates.any { dateFormat.format(it) == todayStr }
    }

    // Добавление сегодняшней даты
    private fun addTodayDate(dates: List<Date>): List<Date> {
        val todayStr = dateFormat.format(today)
        if (dates.any { dateFormat.format(it) == todayStr }) {
            return dates
        }
        return dates + today
    }

    // Удаление сегодняшней даты
    private fun removeTodayDate(dates: List<Date>): List<Date> {
        return dates.filter { dateFormat.format(it) != dateFormat.format(today) }
    }

    // Подсчёт уникальных дней
    private fun countUniqueDays(dates: List<Date>): Int {
        return dates.map { dateFormat.format(it) }.distinct().size
    }

    fun toggleHabit(habitId: Int) {
        _habits.value = _habits.value.map { habit ->
            if (habit.id == habitId) {
                // Если привычка уже выполнена сегодня — снимаем отметку
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
    }

    private fun updateProgress() {
        val completedCount = _habits.value.count { isCompletedToday(it) }
        _todayProgress.value = completedCount
    }

    private fun updateAllStats() {
        val stats = _habits.value.associate { habit ->
            val days = countUniqueDays(habit.completedDates)
            val percentage = (days * 100 / 7).coerceAtMost(100)
            habit.id to HabitStats(habitId = habit.id, completedDays = days, percentage = percentage)
        }
        _habitStatsMap.value = stats
    }

    // Расчёт лучшего дня на основе ежедневного прогресса
    fun calculateBestDay() {
        val totalHabits = _habits.value.size
        if (totalHabits == 0) {
            _bestDay.value = null
            return
        }

        // Собираем все даты, когда были выполнены привычки
        val allDates = mutableMapOf<String, MutableList<Date>>()
        _habits.value.forEach { habit ->
            habit.completedDates.forEach { date ->
                val dateStr = dateFormat.format(date)
                if (!allDates.containsKey(dateStr)) {
                    allDates[dateStr] = mutableListOf()
                }
                allDates[dateStr]?.add(date)
            }
        }

        // Находим день с максимальным количеством выполненных привычек
        var bestDayDate: Date? = null
        var bestDayCount = 0

        allDates.forEach { (dateStr, dates) ->
            // Количество уникальных привычек, выполненных в этот день
            val uniqueHabits = dates.map { date ->
                _habits.value.find { habit ->
                    habit.completedDates.any { dateFormat.format(it) == dateStr }
                }
            }.distinct().count()

            if (uniqueHabits > bestDayCount) {
                bestDayCount = uniqueHabits
                bestDayDate = dates.firstOrNull()
            }
        }

        if (bestDayDate != null && bestDayCount > 0) {
            _bestDay.value = DailyProgress(
                date = bestDayDate!!,  // ← !! означает "я уверен, что не null"
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
    }

    fun deleteHabit(habitId: Int) {
        _habits.value = _habits.value.filter { it.id != habitId }
        _habitStatsMap.value = _habitStatsMap.value.filterKeys { it != habitId }
        updateProgress()
        calculateBestDay()
    }

    // Вспомогательная функция для склонения слов
    fun getDeclension(count: Int, word: String): String {
        return when (word) { "привычка" -> {
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
