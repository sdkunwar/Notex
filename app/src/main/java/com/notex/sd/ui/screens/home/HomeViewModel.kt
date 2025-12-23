package com.notex.sd.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notex.sd.data.preferences.SortBy
import com.notex.sd.data.preferences.SortOrder
import com.notex.sd.data.preferences.UserPreferencesManager
import com.notex.sd.data.preferences.ViewMode
import com.notex.sd.data.repository.FolderRepository
import com.notex.sd.data.repository.NoteRepository
import com.notex.sd.domain.model.Folder
import com.notex.sd.domain.model.FolderTreeNode
import com.notex.sd.domain.model.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val folderRepository: FolderRepository,
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _currentFolderId = MutableStateFlow<Long?>(null)

    private val _uiEvent = MutableSharedFlow<HomeUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val viewMode: StateFlow<ViewMode> = preferencesManager.viewMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ViewMode.LIST)

    val sortBy: StateFlow<SortBy> = preferencesManager.sortBy
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SortBy.DATE_MODIFIED)

    val sortOrder: StateFlow<SortOrder> = preferencesManager.sortOrder
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SortOrder.DESCENDING)

    val showNotePreview: StateFlow<Boolean> = preferencesManager.showNotePreview
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val showNoteDate: StateFlow<Boolean> = preferencesManager.showNoteDate
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    @OptIn(ExperimentalCoroutinesApi::class)
    val notes: StateFlow<List<Note>> = combine(
        _currentFolderId,
        sortBy,
        sortOrder
    ) { folderId, sortBy, sortOrder ->
        Triple(folderId, sortBy, sortOrder)
    }.flatMapLatest { (folderId, sortBy, sortOrder) ->
        val notesFlow = if (folderId != null) {
            noteRepository.getNotesByFolder(folderId)
        } else {
            noteRepository.getAllActiveNotes()
        }

        notesFlow.map { notes ->
            sortNotes(notes, sortBy, sortOrder)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentFolder: StateFlow<Folder?> = _currentFolderId
        .flatMapLatest { folderId ->
            if (folderId != null) {
                folderRepository.getFolderByIdFlow(folderId)
            } else {
                kotlinx.coroutines.flow.flowOf(null)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val folderTree: StateFlow<List<FolderTreeNode>> = folderRepository.getFolderTreeNodes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notesCount: StateFlow<Int> = noteRepository.getActiveNotesCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val archivedNotesCount: StateFlow<Int> = noteRepository.getArchivedNotesCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val trashNotesCount: StateFlow<Int> = noteRepository.getTrashNotesCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setCurrentFolder(folderId: Long?) {
        _currentFolderId.value = folderId
    }

    fun toggleViewMode() {
        viewModelScope.launch {
            val newMode = if (viewMode.value == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST
            preferencesManager.setViewMode(newMode)
        }
    }

    fun setSortBy(sortBy: SortBy) {
        viewModelScope.launch {
            preferencesManager.setSortBy(sortBy)
        }
    }

    fun setSortOrder(order: SortOrder) {
        viewModelScope.launch {
            preferencesManager.setSortOrder(order)
        }
    }

    fun togglePinNote(noteId: Long) {
        viewModelScope.launch {
            noteRepository.togglePinStatus(noteId)
            _uiEvent.emit(HomeUiEvent.NotePinToggled)
        }
    }

    fun archiveNote(noteId: Long) {
        viewModelScope.launch {
            noteRepository.archiveNote(noteId)
            _uiEvent.emit(HomeUiEvent.NoteArchived)
        }
    }

    fun moveToTrash(noteId: Long) {
        viewModelScope.launch {
            noteRepository.moveToTrash(noteId)
            _uiEvent.emit(HomeUiEvent.NoteMovedToTrash)
        }
    }

    fun moveNoteToFolder(noteId: Long, folderId: Long?) {
        viewModelScope.launch {
            noteRepository.moveNoteToFolder(noteId, folderId)
            _uiEvent.emit(HomeUiEvent.NoteMovedToFolder)
        }
    }

    fun duplicateNote(noteId: Long) {
        viewModelScope.launch {
            noteRepository.duplicateNote(noteId)
            _uiEvent.emit(HomeUiEvent.NoteDuplicated)
        }
    }

    fun createFolder(name: String, parentId: Long? = null) {
        viewModelScope.launch {
            val folder = Folder(name = name, parentId = parentId)
            folderRepository.insertFolder(folder)
            _uiEvent.emit(HomeUiEvent.FolderCreated)
        }
    }

    fun renameFolder(folderId: Long, newName: String) {
        viewModelScope.launch {
            val folder = folderRepository.getFolderById(folderId) ?: return@launch
            folderRepository.updateFolder(folder.copy(name = newName))
            _uiEvent.emit(HomeUiEvent.FolderRenamed)
        }
    }

    fun deleteFolder(folderId: Long) {
        viewModelScope.launch {
            folderRepository.deleteFolder(folderId)
            if (_currentFolderId.value == folderId) {
                _currentFolderId.value = null
            }
            _uiEvent.emit(HomeUiEvent.FolderDeleted)
        }
    }

    fun toggleFolderExpanded(folderId: Long) {
        viewModelScope.launch {
            folderRepository.toggleExpandedState(folderId)
        }
    }

    private fun sortNotes(notes: List<Note>, sortBy: SortBy, sortOrder: SortOrder): List<Note> {
        val pinnedNotes = notes.filter { it.isPinned }
        val unpinnedNotes = notes.filter { !it.isPinned }

        val sortedUnpinned = when (sortBy) {
            SortBy.DATE_MODIFIED -> {
                if (sortOrder == SortOrder.DESCENDING) {
                    unpinnedNotes.sortedByDescending { it.updatedAt }
                } else {
                    unpinnedNotes.sortedBy { it.updatedAt }
                }
            }
            SortBy.DATE_CREATED -> {
                if (sortOrder == SortOrder.DESCENDING) {
                    unpinnedNotes.sortedByDescending { it.createdAt }
                } else {
                    unpinnedNotes.sortedBy { it.createdAt }
                }
            }
            SortBy.TITLE -> {
                if (sortOrder == SortOrder.DESCENDING) {
                    unpinnedNotes.sortedByDescending { it.title.lowercase() }
                } else {
                    unpinnedNotes.sortedBy { it.title.lowercase() }
                }
            }
            SortBy.COLOR -> {
                if (sortOrder == SortOrder.DESCENDING) {
                    unpinnedNotes.sortedByDescending { it.color?.name ?: "" }
                } else {
                    unpinnedNotes.sortedBy { it.color?.name ?: "" }
                }
            }
        }

        return pinnedNotes.sortedByDescending { it.updatedAt } + sortedUnpinned
    }
}

sealed class HomeUiEvent {
    data object NotePinToggled : HomeUiEvent()
    data object NoteArchived : HomeUiEvent()
    data object NoteMovedToTrash : HomeUiEvent()
    data object NoteMovedToFolder : HomeUiEvent()
    data object NoteDuplicated : HomeUiEvent()
    data object FolderCreated : HomeUiEvent()
    data object FolderRenamed : HomeUiEvent()
    data object FolderDeleted : HomeUiEvent()
}
