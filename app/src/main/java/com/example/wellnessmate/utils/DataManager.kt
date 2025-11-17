package com.example.wellnessmate.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.wellnessmate.models.Habit
import com.example.wellnessmate.models.MoodEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID
import com.example.wellnessmate.widget.HabitProgressWidget

class DataManager(private val context: Context) {

    private val sharedPref: SharedPreferences = context.getSharedPreferences("pulsepath_data", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Habit data operations
    fun saveHabits(habits: List<Habit>) {
        val json = gson.toJson(habits)
        sharedPref.edit().putString("habits", json).apply()

        // Calculate and save overall habit progress for dashboard
        val totalProgress = habits.sumOf { it.progress }
        val totalTarget = habits.sumOf { it.target }
        val overallProgress = if (totalTarget > 0) (totalProgress * 100) / totalTarget else 0
        saveHabitProgress(overallProgress)

        // Notify home-screen widget to refresh
        HabitProgressWidget.updateAllWidgets(context)
    }

    fun loadHabits(): List<Habit> {
        val json = sharedPref.getString("habits", null)
        return if (json != null) {
            val type = object : TypeToken<List<Habit>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            // Return sample data if no habits exist
            listOf(
                Habit(UUID.randomUUID().toString(), "Drink water", "Hydration", 8, 4),
                Habit(UUID.randomUUID().toString(), "Meditate", "Mental Health", 1, 0),
                Habit(UUID.randomUUID().toString(), "Exercise", "Fitness", 1, 1),
                Habit(UUID.randomUUID().toString(), "Read", "Personal Growth", 30, 15)
            )
        }
    }

    // Mood data operations
    fun saveMoodEntries(entries: List<MoodEntry>) {
        val json = gson.toJson(entries)
        sharedPref.edit().putString("mood_entries", json).apply()

        // Save last mood for dashboard
        if (entries.isNotEmpty()) {
            val lastMood = entries.first()
            sharedPref.edit().putString("last_mood", "${lastMood.emoji} ${lastMood.mood}").apply()
        }
    }

    fun loadMoodEntries(): List<MoodEntry> {
        val json = sharedPref.getString("mood_entries", null)
        return if (json != null) {
            val type = object : TypeToken<List<MoodEntry>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun addMoodEntry(entry: MoodEntry) {
        val currentEntries = loadMoodEntries().toMutableList()
        currentEntries.add(0, entry) // Add to beginning for reverse chronological order
        saveMoodEntries(currentEntries)
    }

    // Water intake operations
    fun saveWaterIntake(glasses: Int) {
        sharedPref.edit().putInt("water_intake", glasses).apply()
    }

    fun getWaterIntake(): Int {
        return sharedPref.getInt("water_intake", 0)
    }

    fun setWaterTarget(target: Int) {
        sharedPref.edit().putInt("water_target", target).apply()
    }

    fun getWaterTarget(): Int {
        return sharedPref.getInt("water_target", 8) // Default target: 8 glasses
    }

    // Habit progress operations
    fun saveHabitProgress(progress: Int) {
        sharedPref.edit().putInt("habit_progress", progress).apply()
    }

    fun getHabitProgress(): Int {
        return sharedPref.getInt("habit_progress", 0)
    }

    // Get last mood for dashboard
    fun getLastMood(): String {
        return sharedPref.getString("last_mood", "Feeling good today!") ?: "Feeling good today!"
    }

    // Hydration reminder settings
    fun saveReminderEnabled(enabled: Boolean) {
        sharedPref.edit().putBoolean("reminder_enabled", enabled).apply()
    }

    fun getReminderEnabled(): Boolean {
        return sharedPref.getBoolean("reminder_enabled", false)
    }

    fun saveReminderInterval(interval: Int) {
        sharedPref.edit().putInt("reminder_interval", interval).apply()
    }

    fun getReminderInterval(): Int {
        return sharedPref.getInt("reminder_interval", 2) // Default: 2 hours
    }

    // Settings operations
    fun saveNotificationSetting(enabled: Boolean) {
        sharedPref.edit().putBoolean("notifications_enabled", enabled).apply()
    }

    fun getNotificationSetting(): Boolean {
        return sharedPref.getBoolean("notifications_enabled", true)
    }

    fun saveDarkModeSetting(enabled: Boolean) {
        sharedPref.edit().putBoolean("dark_mode_enabled", enabled).apply()
    }

    fun getDarkModeSetting(): Boolean {
        return sharedPref.getBoolean("dark_mode_enabled", false)
    }

    // User profile
    fun saveUserName(name: String) {
        sharedPref.edit().putString("user_name", name).apply()
    }

    fun getUserName(): String {
        return sharedPref.getString("user_name", "User") ?: "User"
    }

    // Clear all data
    fun clearAllData() {
        sharedPref.edit().clear().apply()
    }
}