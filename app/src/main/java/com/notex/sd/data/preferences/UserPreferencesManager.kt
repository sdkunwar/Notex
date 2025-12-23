package com.notex.sd.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "notex_preferences")

@Singleton
class UserPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
        private val KEY_VIEW_MODE = stringPreferencesKey("view_mode")
        private val KEY_SORT_BY = stringPreferencesKey("sort_by")
        private val KEY_SORT_ORDER = stringPreferencesKey("sort_order")
        private val KEY_SHOW_NOTE_PREVIEW = booleanPreferencesKey("show_note_preview")
        private val KEY_SHOW_NOTE_DATE = booleanPreferencesKey("show_note_date")
        private val KEY_CONFIRM_DELETE = booleanPreferencesKey("confirm_delete")
        private val KEY_AUTO_SAVE = booleanPreferencesKey("auto_save")
        private val KEY_AUTO_SAVE_INTERVAL = intPreferencesKey("auto_save_interval")
        private val KEY_TRASH_AUTO_DELETE_DAYS = intPreferencesKey("trash_auto_delete_days")
        private val KEY_DEFAULT_FOLDER_ID = stringPreferencesKey("default_folder_id")
        private val KEY_EDITOR_FONT_SIZE = intPreferencesKey("editor_font_size")
        private val KEY_SHOW_LINE_NUMBERS = booleanPreferencesKey("show_line_numbers")
        private val KEY_MARKDOWN_PREVIEW = booleanPreferencesKey("markdown_preview")
    }

    val onboardingCompleted: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_ONBOARDING_COMPLETED] ?: false
    }

    val themeMode: Flow<ThemeMode> = dataStore.data.map { preferences ->
        ThemeMode.fromString(preferences[KEY_THEME_MODE])
    }

    val dynamicColorsEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_DYNAMIC_COLORS] ?: true
    }

    val viewMode: Flow<ViewMode> = dataStore.data.map { preferences ->
        ViewMode.fromString(preferences[KEY_VIEW_MODE])
    }

    val sortBy: Flow<SortBy> = dataStore.data.map { preferences ->
        SortBy.fromString(preferences[KEY_SORT_BY])
    }

    val sortOrder: Flow<SortOrder> = dataStore.data.map { preferences ->
        SortOrder.fromString(preferences[KEY_SORT_ORDER])
    }

    val showNotePreview: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_SHOW_NOTE_PREVIEW] ?: true
    }

    val showNoteDate: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_SHOW_NOTE_DATE] ?: true
    }

    val confirmDelete: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_CONFIRM_DELETE] ?: true
    }

    val autoSave: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_AUTO_SAVE] ?: true
    }

    val autoSaveInterval: Flow<Int> = dataStore.data.map { preferences ->
        preferences[KEY_AUTO_SAVE_INTERVAL] ?: 5
    }

    val trashAutoDeleteDays: Flow<Int> = dataStore.data.map { preferences ->
        preferences[KEY_TRASH_AUTO_DELETE_DAYS] ?: 30
    }

    val defaultFolderId: Flow<Long?> = dataStore.data.map { preferences ->
        preferences[KEY_DEFAULT_FOLDER_ID]?.toLongOrNull()
    }

    val editorFontSize: Flow<Int> = dataStore.data.map { preferences ->
        preferences[KEY_EDITOR_FONT_SIZE] ?: 16
    }

    val showLineNumbers: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_SHOW_LINE_NUMBERS] ?: false
    }

    val markdownPreview: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_MARKDOWN_PREVIEW] ?: false
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = mode.name
        }
    }

    suspend fun setDynamicColorsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_DYNAMIC_COLORS] = enabled
        }
    }

    suspend fun setViewMode(mode: ViewMode) {
        dataStore.edit { preferences ->
            preferences[KEY_VIEW_MODE] = mode.name
        }
    }

    suspend fun setSortBy(sortBy: SortBy) {
        dataStore.edit { preferences ->
            preferences[KEY_SORT_BY] = sortBy.name
        }
    }

    suspend fun setSortOrder(order: SortOrder) {
        dataStore.edit { preferences ->
            preferences[KEY_SORT_ORDER] = order.name
        }
    }

    suspend fun setShowNotePreview(show: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_SHOW_NOTE_PREVIEW] = show
        }
    }

    suspend fun setShowNoteDate(show: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_SHOW_NOTE_DATE] = show
        }
    }

    suspend fun setConfirmDelete(confirm: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_CONFIRM_DELETE] = confirm
        }
    }

    suspend fun setAutoSave(autoSave: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_AUTO_SAVE] = autoSave
        }
    }

    suspend fun setAutoSaveInterval(seconds: Int) {
        dataStore.edit { preferences ->
            preferences[KEY_AUTO_SAVE_INTERVAL] = seconds
        }
    }

    suspend fun setTrashAutoDeleteDays(days: Int) {
        dataStore.edit { preferences ->
            preferences[KEY_TRASH_AUTO_DELETE_DAYS] = days
        }
    }

    suspend fun setDefaultFolderId(folderId: Long?) {
        dataStore.edit { preferences ->
            if (folderId != null) {
                preferences[KEY_DEFAULT_FOLDER_ID] = folderId.toString()
            } else {
                preferences.remove(KEY_DEFAULT_FOLDER_ID)
            }
        }
    }

    suspend fun setEditorFontSize(size: Int) {
        dataStore.edit { preferences ->
            preferences[KEY_EDITOR_FONT_SIZE] = size.coerceIn(12, 24)
        }
    }

    suspend fun setShowLineNumbers(show: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_SHOW_LINE_NUMBERS] = show
        }
    }

    suspend fun setMarkdownPreview(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_MARKDOWN_PREVIEW] = enabled
        }
    }
}

enum class ThemeMode {
    SYSTEM, LIGHT, DARK;

    companion object {
        fun fromString(value: String?): ThemeMode {
            return entries.find { it.name == value } ?: SYSTEM
        }
    }
}

enum class ViewMode {
    LIST, GRID;

    companion object {
        fun fromString(value: String?): ViewMode {
            return entries.find { it.name == value } ?: LIST
        }
    }
}

enum class SortBy {
    DATE_MODIFIED, DATE_CREATED, TITLE, COLOR;

    companion object {
        fun fromString(value: String?): SortBy {
            return entries.find { it.name == value } ?: DATE_MODIFIED
        }
    }

    fun displayName(): String = when (this) {
        DATE_MODIFIED -> "Date modified"
        DATE_CREATED -> "Date created"
        TITLE -> "Title"
        COLOR -> "Color"
    }
}

enum class SortOrder {
    ASCENDING, DESCENDING;

    companion object {
        fun fromString(value: String?): SortOrder {
            return entries.find { it.name == value } ?: DESCENDING
        }
    }

    fun displayName(): String = when (this) {
        ASCENDING -> "Ascending"
        DESCENDING -> "Descending"
    }
}
