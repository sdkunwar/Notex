package com.notex.sd.ui.screens.settings.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notex.sd.data.preferences.SortOrder
import com.notex.sd.data.preferences.UserPreferencesManager
import com.notex.sd.data.preferences.ViewMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesSettingsViewModel @Inject constructor(
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    val viewMode: StateFlow<ViewMode> = preferencesManager.viewMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ViewMode.GRID)

    val sortOrder: StateFlow<SortOrder> = preferencesManager.sortOrder
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SortOrder.MODIFIED_NEWEST)

    val showPreview: StateFlow<Boolean> = preferencesManager.showPreview
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setViewMode(mode: ViewMode) {
        viewModelScope.launch {
            preferencesManager.setViewMode(mode)
        }
    }

    fun setSortOrder(order: SortOrder) {
        viewModelScope.launch {
            preferencesManager.setSortOrder(order)
        }
    }

    fun setShowPreview(show: Boolean) {
        viewModelScope.launch {
            preferencesManager.setShowPreview(show)
        }
    }
}
