package com.notex.sd.ui.screens.settings.backup

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notex.sd.data.local.entity.FolderEntity
import com.notex.sd.data.local.entity.NoteEntity
import com.notex.sd.data.local.entity.TagEntity
import com.notex.sd.data.repository.FolderRepository
import com.notex.sd.data.repository.NoteRepository
import com.notex.sd.data.repository.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val folderRepository: FolderRepository,
    private val tagRepository: TagRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uiEvent = MutableSharedFlow<BackupUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun createBackup(context: Context, uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val notes = noteRepository.getAllNotesForBackup()
                val folders = folderRepository.getAllFoldersForBackup()
                val tags = tagRepository.getAllTagsForBackup()

                val backup = BackupData(
                    version = 1,
                    timestamp = System.currentTimeMillis(),
                    notes = notes.map { it.toBackupNote() },
                    folders = folders.map { it.toBackupFolder() },
                    tags = tags.map { it.toBackupTag() }
                )

                val jsonString = json.encodeToString(backup)

                context.contentResolver.openOutputStream(uri)?.use { stream ->
                    stream.write(jsonString.toByteArray())
                }

                _uiEvent.emit(BackupUiEvent.BackupSuccess)
            } catch (e: Exception) {
                _uiEvent.emit(BackupUiEvent.BackupError(e.message ?: "Unknown error"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun restoreBackup(context: Context, uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val jsonString = context.contentResolver.openInputStream(uri)?.use { stream ->
                    stream.bufferedReader().readText()
                } ?: throw Exception("Could not read file")

                val backup = json.decodeFromString<BackupData>(jsonString)

                // Restore folders first (for foreign key relationships)
                backup.folders.forEach { folder ->
                    folderRepository.insertFolder(folder.toFolderEntity())
                }

                // Restore tags
                backup.tags.forEach { tag ->
                    tagRepository.insertTag(tag.toTagEntity())
                }

                // Restore notes
                backup.notes.forEach { note ->
                    noteRepository.insertNoteFromBackup(note.toNoteEntity())
                }

                _uiEvent.emit(BackupUiEvent.RestoreSuccess)
            } catch (e: Exception) {
                _uiEvent.emit(BackupUiEvent.RestoreError(e.message ?: "Unknown error"))
            } finally {
                _isLoading.value = false
            }
        }
    }
}

@Serializable
data class BackupData(
    val version: Int,
    val timestamp: Long,
    val notes: List<BackupNote>,
    val folders: List<BackupFolder>,
    val tags: List<BackupTag>
)

@Serializable
data class BackupNote(
    val id: Long,
    val title: String,
    val content: String,
    val plainTextContent: String = "",
    val folderId: Long?,
    val color: String?,
    val isPinned: Boolean,
    val isArchived: Boolean,
    val isInTrash: Boolean,
    val isChecklist: Boolean = false,
    val checklistItems: String = "[]",
    val createdAt: Long,
    val modifiedAt: Long,
    val trashedAt: Long? = null
)

@Serializable
data class BackupFolder(
    val id: Long,
    val name: String,
    val parentId: Long?,
    val color: String,
    val createdAt: Long
)

@Serializable
data class BackupTag(
    val id: Long,
    val name: String,
    val color: String
)

fun NoteEntity.toBackupNote() = BackupNote(
    id = id,
    title = title,
    content = content,
    plainTextContent = plainTextContent,
    folderId = folderId,
    color = color,
    isPinned = isPinned,
    isArchived = isArchived,
    isInTrash = isInTrash,
    isChecklist = isChecklist,
    checklistItems = checklistItems,
    createdAt = createdAt,
    modifiedAt = updatedAt,
    trashedAt = trashedAt
)

fun BackupNote.toNoteEntity() = NoteEntity(
    id = id,
    title = title,
    content = content,
    plainTextContent = plainTextContent,
    folderId = folderId,
    color = color,
    isPinned = isPinned,
    isArchived = isArchived,
    isInTrash = isInTrash,
    isChecklist = isChecklist,
    checklistItems = checklistItems,
    createdAt = createdAt,
    updatedAt = modifiedAt,
    trashedAt = trashedAt
)

fun FolderEntity.toBackupFolder() = BackupFolder(
    id = id,
    name = name,
    parentId = parentId,
    color = color,
    createdAt = createdAt
)

fun BackupFolder.toFolderEntity() = FolderEntity(
    id = id,
    name = name,
    parentId = parentId,
    color = color,
    createdAt = createdAt
)

fun TagEntity.toBackupTag() = BackupTag(
    id = id,
    name = name,
    color = color
)

fun BackupTag.toTagEntity() = TagEntity(
    id = id,
    name = name,
    color = color
)

sealed class BackupUiEvent {
    data object BackupSuccess : BackupUiEvent()
    data class BackupError(val message: String) : BackupUiEvent()
    data object RestoreSuccess : BackupUiEvent()
    data class RestoreError(val message: String) : BackupUiEvent()
}
