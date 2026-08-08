package com.example.interview_ai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.interview_ai.data.preferences.ThemeManager
import com.example.interview_ai.data.preferences.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val themeManager = ThemeManager(application.applicationContext)

    val themeMode: StateFlow<ThemeMode> = themeManager.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ThemeMode.SYSTEM
    )

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch { themeManager.setThemeMode(themeMode) }
    }
}
