package com.example.interview_ai.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore by preferencesDataStore(name = "app_preferences")

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

class ThemeManager(private val context: Context) {
    private object Keys {
        val ThemeMode = stringPreferencesKey("theme_mode")
    }

    val themeMode: Flow<ThemeMode> = context.themeDataStore.data.map { preferences ->
        preferences[Keys.ThemeMode]
            ?.let { savedValue -> runCatching { ThemeMode.valueOf(savedValue) }.getOrNull() }
            ?: ThemeMode.SYSTEM
    }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        context.themeDataStore.edit { preferences ->
            preferences[Keys.ThemeMode] = themeMode.name
        }
    }
}
