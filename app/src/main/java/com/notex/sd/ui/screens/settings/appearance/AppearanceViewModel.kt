package com.notex.sd.ui.screens.settings.appearance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notex.sd.data.preferences.ThemeMode
import com.notex.sd.data.preferences.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppearanceViewModel @Inject constructor(
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = preferencesManager.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

    val dynamicColors: StateFlow<Boolean> = preferencesManager.dynamicColors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferencesManager.setThemeMode(mode)
        }
    }

    fun setDynamicColors(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setDynamicColors(enabled)
        }
    }
}
