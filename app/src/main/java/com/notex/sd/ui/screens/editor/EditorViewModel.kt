package com.notex.sd.ui.screens.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notex.sd.data.preferences.UserPreferencesManager
import com.notex.sd.data.repository.FolderRepository
import com.notex.sd.data.repository.NoteRepository
import com.notex.sd.domain.model.ChecklistItem
import com.notex.sd.domain.model.Folder
import com.notex.sd.domain.model.Note
import com.notex.sd.domain.model.NoteColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val folderRepository: FolderRepository,
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<EditorUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val autoSave: StateFlow<Boolean> = preferencesManager.autoSave
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val autoSaveInterval: StateFlow<Int> = preferencesManager.autoSaveInterval
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 5)

    val editorFontSize: StateFlow<Int> = preferencesManager.editorFontSize
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 16)

    private var autoSaveJob: Job? = null
    private var originalNote: Note? = null
    private var noteId: Long? = null

    fun loadNote(noteId: Long?, folderId: Long?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val folder = folderId?.let { folderRepository.getFolderById(it) }

            if (noteId != null && noteId > 0) {
                val note = noteRepository.getNoteById(noteId)
                if (note != null) {
                    this@EditorViewModel.noteId = note.id
                    originalNote = note
                    _uiState.value = EditorUiState(
                        title = note.title,
                        content = note.content,
                        plainTextContent = note.plainTextContent,
                        isPinned = note.isPinned,
                        color = note.color,
                        folder = note.folderId?.let { folderRepository.getFolderById(it) },
                        isChecklist = note.isChecklist,
                        checklistItems = note.checklistItems,
                        isLoading = false,
                        isNewNote = false
                    )
                } else {
                    _uiState.value = EditorUiState(
                        folder = folder,
                        isLoading = false,
                        isNewNote = true
                    )
                }
            } else {
                _uiState.value = EditorUiState(
                    folder = folder,
                    isLoading = false,
                    isNewNote = true
                )
            }

            startAutoSave()
        }
    }

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(
            title = title,
            hasUnsavedChanges = true
        )
        scheduleAutoSave()
    }

    fun updateContent(content: String) {
        val plainText = extractPlainText(content)
        _uiState.value = _uiState.value.copy(
            content = content,
            plainTextContent = plainText,
            hasUnsavedChanges = true
        )
        scheduleAutoSave()
    }

    fun togglePin() {
        _uiState.value = _uiState.value.copy(
            isPinned = !_uiState.value.isPinned,
            hasUnsavedChanges = true
        )
    }

    fun setColor(color: NoteColor?) {
        _uiState.value = _uiState.value.copy(
            color = color,
            hasUnsavedChanges = true
        )
    }

    fun setFolder(folder: Folder?) {
        _uiState.value = _uiState.value.copy(
            folder = folder,
            hasUnsavedChanges = true
        )
    }

    fun toggleChecklist() {
        val isChecklist = !_uiState.value.isChecklist
        _uiState.value = _uiState.value.copy(
            isChecklist = isChecklist,
            checklistItems = if (isChecklist && _uiState.value.checklistItems.isEmpty()) {
                listOf(ChecklistItem())
            } else {
                _uiState.value.checklistItems
            },
            hasUnsavedChanges = true
        )
    }

    fun updateChecklistItem(index: Int, item: ChecklistItem) {
        val items = _uiState.value.checklistItems.toMutableList()
        if (index < items.size) {
            items[index] = item
            _uiState.value = _uiState.value.copy(
                checklistItems = items,
                hasUnsavedChanges = true
            )
        }
        scheduleAutoSave()
    }

    fun addChecklistItem(afterIndex: Int = -1) {
        val items = _uiState.value.checklistItems.toMutableList()
        val newItem = ChecklistItem()
        if (afterIndex >= 0 && afterIndex < items.size) {
            items.add(afterIndex + 1, newItem)
        } else {
            items.add(newItem)
        }
        _uiState.value = _uiState.value.copy(
            checklistItems = items,
            hasUnsavedChanges = true
        )
    }

    fun removeChecklistItem(index: Int) {
        val items = _uiState.value.checklistItems.toMutableList()
        if (index < items.size && items.size > 1) {
            items.removeAt(index)
            _uiState.value = _uiState.value.copy(
                checklistItems = items,
                hasUnsavedChanges = true
            )
        }
    }

    fun toggleChecklistItemChecked(index: Int) {
        val items = _uiState.value.checklistItems.toMutableList()
        if (index < items.size) {
            items[index] = items[index].copy(isChecked = !items[index].isChecked)
            _uiState.value = _uiState.value.copy(
                checklistItems = items,
                hasUnsavedChanges = true
            )
        }
        scheduleAutoSave()
    }

    fun saveNote() {
        viewModelScope.launch {
            val state = _uiState.value

            if (state.title.isBlank() && state.plainTextContent.isBlank() && state.checklistItems.all { it.text.isBlank() }) {
                if (noteId != null) {
                    noteRepository.deleteNote(noteId!!)
                }
                _uiEvent.emit(EditorUiEvent.NoteDeleted)
                return@launch
            }

            val note = Note(
                id = noteId ?: 0,
                title = state.title,
                content = state.content,
                plainTextContent = state.plainTextContent,
                folderId = state.folder?.id,
                color = state.color,
                isPinned = state.isPinned,
                isChecklist = state.isChecklist,
                checklistItems = state.checklistItems,
                createdAt = originalNote?.createdAt ?: System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            if (noteId != null) {
                noteRepository.updateNote(note)
            } else {
                noteId = noteRepository.insertNote(note)
            }

            originalNote = note.copy(id = noteId ?: 0)
            _uiState.value = _uiState.value.copy(
                hasUnsavedChanges = false,
                isNewNote = false
            )
            _uiEvent.emit(EditorUiEvent.NoteSaved)
        }
    }

    fun archive() {
        viewModelScope.launch {
            saveNote()
            noteId?.let {
                noteRepository.archiveNote(it)
                _uiEvent.emit(EditorUiEvent.NoteArchived)
            }
        }
    }

    fun moveToTrash() {
        viewModelScope.launch {
            noteId?.let {
                noteRepository.moveToTrash(it)
                _uiEvent.emit(EditorUiEvent.NoteMovedToTrash)
            } ?: run {
                _uiEvent.emit(EditorUiEvent.NoteDeleted)
            }
        }
    }

    fun duplicate() {
        viewModelScope.launch {
            saveNote()
            noteId?.let {
                val newId = noteRepository.duplicateNote(it)
                _uiEvent.emit(EditorUiEvent.NoteDuplicated(newId))
            }
        }
    }

    private fun startAutoSave() {
        viewModelScope.launch {
            val autoSaveEnabled = autoSave.first()
            if (!autoSaveEnabled) return@launch

            val interval = autoSaveInterval.first() * 1000L
            while (true) {
                delay(interval)
                if (_uiState.value.hasUnsavedChanges) {
                    saveNote()
                }
            }
        }
    }

    private fun scheduleAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            val autoSaveEnabled = autoSave.first()
            if (!autoSaveEnabled) return@launch

            delay(3000)
            if (_uiState.value.hasUnsavedChanges) {
                saveNote()
            }
        }
    }

    private fun extractPlainText(content: String): String {
        return content
            .replace(Regex("\\*\\*(.+?)\\*\\*"), "$1")
            .replace(Regex("\\*(.+?)\\*"), "$1")
            .replace(Regex("~~(.+?)~~"), "$1")
            .replace(Regex("__(.+?)__"), "$1")
            .replace(Regex("_(.+?)_"), "$1")
            .replace(Regex("`(.+?)`"), "$1")
            .replace(Regex("#{1,6}\\s+"), "")
            .replace(Regex("\\[(.+?)]\\(.+?\\)"), "$1")
            .replace(Regex("^[\\-*+]\\s+", RegexOption.MULTILINE), "")
            .replace(Regex("^\\d+\\.\\s+", RegexOption.MULTILINE), "")
            .replace(Regex("^>\\s+", RegexOption.MULTILINE), "")
            .trim()
    }

    override fun onCleared() {
        super.onCleared()
        autoSaveJob?.cancel()
    }
}

data class EditorUiState(
    val title: String = "",
    val content: String = "",
    val plainTextContent: String = "",
    val isPinned: Boolean = false,
    val color: NoteColor? = null,
    val folder: Folder? = null,
    val isChecklist: Boolean = false,
    val checklistItems: List<ChecklistItem> = emptyList(),
    val isLoading: Boolean = true,
    val isNewNote: Boolean = true,
    val hasUnsavedChanges: Boolean = false
)

sealed class EditorUiEvent {
    data object NoteSaved : EditorUiEvent()
    data object NoteDeleted : EditorUiEvent()
    data object NoteArchived : EditorUiEvent()
    data object NoteMovedToTrash : EditorUiEvent()
    data class NoteDuplicated(val newNoteId: Long) : EditorUiEvent()
}
