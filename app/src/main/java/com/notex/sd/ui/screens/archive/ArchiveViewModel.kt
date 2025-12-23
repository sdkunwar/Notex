package com.notex.sd.ui.screens.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notex.sd.data.repository.NoteRepository
import com.notex.sd.domain.model.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<ArchiveUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val archivedNotes: StateFlow<List<Note>> = noteRepository.getArchivedNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun unarchiveNote(noteId: Long) {
        viewModelScope.launch {
            noteRepository.unarchiveNote(noteId)
            _uiEvent.emit(ArchiveUiEvent.NoteUnarchived)
        }
    }

    fun moveToTrash(noteId: Long) {
        viewModelScope.launch {
            noteRepository.moveToTrash(noteId)
            _uiEvent.emit(ArchiveUiEvent.NoteMovedToTrash)
        }
    }
}

sealed class ArchiveUiEvent {
    data object NoteUnarchived : ArchiveUiEvent()
    data object NoteMovedToTrash : ArchiveUiEvent()
}
