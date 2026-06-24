package com.example.pushkarskij.utils

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.pushkarskij.ui.viewmodels.Habit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

val Context.dataStore by preferencesDataStore(name = "habit_tracker")

class DataStoreManager(private val context: Context) {

    private val dataStore = context.dataStore

    // Ключи для хранения
    private val HABITS_KEY = stringPreferencesKey("habits")
    private val THEME_KEY = booleanPreferencesKey("dark_theme")
    private val REMINDER_KEY = booleanPreferencesKey("reminders_enabled")
    private val REMINDER_TIME_KEY = stringPreferencesKey("reminder_time")

    private val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    suspend fun saveAllData(
        habits: List<Habit>,
        isDarkTheme: Boolean,
        remindersEnabled: Boolean,
        reminderTime: String
    ) {
        dataStore.edit { preferences ->
            // Сохраняем привычки
            preferences[HABITS_KEY] = habitsToJson(habits)
            // Сохраняем настройки
            preferences[THEME_KEY] = isDarkTheme
            preferences[REMINDER_KEY] = remindersEnabled
            preferences[REMINDER_TIME_KEY] = reminderTime
        }
    }

    fun loadAllData(): Flow<Pair<List<Habit>, Triple<Boolean, Boolean, String>>> {
        return dataStore.data.map { preferences ->
            val habitsJson = preferences[HABITS_KEY] ?: getDefaultHabitsJson()
            val habits = jsonToHabits(habitsJson)

            val isDarkTheme = preferences[THEME_KEY] ?: false
            val remindersEnabled = preferences[REMINDER_KEY] ?: true
            val reminderTime = preferences[REMINDER_TIME_KEY] ?: "09:00"

            Pair(habits, Triple(isDarkTheme, remindersEnabled, reminderTime))
        }
    }

    // ============ ПРЕОБРАЗОВАНИЕ В JSON ============

    private fun habitsToJson(habits: List<Habit>): String {
        if (habits.isEmpty()) return "[]"

        return habits.joinToString(",", "[", "]") { habit ->
            val dates = habit.completedDates.joinToString(",") {
                "\"${dateFormat.format(it)}\""
            }
            """
            {
                "id":${habit.id},
                "name":"${habit.name}",
                "icon":"${habit.icon}",
                "target":"${habit.target}",
                "dates":[$dates]
            }
            """.trimIndent().replace("\n", "")
        }
    }

    private fun jsonToHabits(json: String): List<Habit> {
        if (json.isEmpty() || json == "[]") {
            return getDefaultHabits()
        }

        val habits = mutableListOf<Habit>()
        try {
            // Простой парсинг без внешних библиотек
            val items = json.removePrefix("[").removeSuffix("]").split("},")

            items.forEach { item ->
                val clean = item.trim().removePrefix("{").removeSuffix("}")
                if (clean.isNotEmpty()) {
                    var id = 0
                    var name = ""
                    var icon = ""
                    var target = ""
                    val dates = mutableListOf<Date>()

                    clean.split(",").forEach { part ->
                        val kv = part.split(":")
                        if (kv.size >= 2) {
                            val key = kv[0].trim().removeSurrounding("\"")
                            val value = kv[1].trim().removeSurrounding("\"")
                            when (key) {
                                "id" -> id = value.toIntOrNull() ?: 0
                                "name" -> name = value
                                "icon" -> icon = value
                                "target" -> target = value
                                "dates" -> {
                                    val dateStr = value.removePrefix("[").removeSuffix("]")
                                    if (dateStr.isNotEmpty()) {
                                        dateStr.split(",").forEach { d ->
                                            try {
                                                val date = dateFormat.parse(d.trim().removeSurrounding("\""))
                                                if (date != null) dates.add(date)
                                            } catch (e: Exception) { }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (name.isNotEmpty()) {
                        habits.add(Habit(id, name, icon, target, dates))
                    }
                }
            }
        } catch (e: Exception) {
            return getDefaultHabits()
        }

        return if (habits.isEmpty()) getDefaultHabits() else habits
    }

    // ============ ДЕФОЛТНЫЕ ДАННЫЕ ============

    private fun getDefaultHabits(): List<Habit> {
        return listOf(
            Habit(1, "Выпить воду", "💧", "8 стаканов"),
            Habit(2, "Утренняя зарядка", "💪", "15 минут"),
            Habit(3, "Чтение", "📚", "30 минут"),
            Habit(4, "Медитация", "🧘", "10 минут"),
            Habit(5, "Витамины", "💊", "1 раз")
        )
    }

    private fun getDefaultHabitsJson(): String {
        return habitsToJson(getDefaultHabits())
    }
}