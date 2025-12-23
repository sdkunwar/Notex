package com.notex.sd.ui.screens.trash

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
class TrashViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<TrashUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val trashNotes: StateFlow<List<Note>> = noteRepository.getTrashNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun restoreNote(noteId: Long) {
        viewModelScope.launch {
            noteRepository.restoreFromTrash(noteId)
            _uiEvent.emit(TrashUiEvent.NoteRestored)
        }
    }

    fun deleteNotePermanently(noteId: Long) {
        viewModelScope.launch {
            noteRepository.deleteNote(noteId)
            _uiEvent.emit(TrashUiEvent.NoteDeletedPermanently)
        }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            noteRepository.emptyTrash()
            _uiEvent.emit(TrashUiEvent.TrashEmptied)
        }
    }
}

sealed class TrashUiEvent {
    data object NoteRestored : TrashUiEvent()
    data object NoteDeletedPermanently : TrashUiEvent()
    data object TrashEmptied : TrashUiEvent()
}
