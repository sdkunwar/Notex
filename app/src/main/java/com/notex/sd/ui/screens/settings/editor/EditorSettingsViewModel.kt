package com.notex.sd.ui.screens.settings.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notex.sd.data.preferences.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorSettingsViewModel @Inject constructor(
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    val fontSize: StateFlow<Int> = preferencesManager.fontSize
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 16)

    val autoSave: StateFlow<Boolean> = preferencesManager.autoSave
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setFontSize(size: Int) {
        viewModelScope.launch {
            preferencesManager.setFontSize(size)
        }
    }

    fun setAutoSave(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setAutoSave(enabled)
        }
    }
}
