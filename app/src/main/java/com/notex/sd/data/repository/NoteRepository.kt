package com.notex.sd.data.repository

import com.notex.sd.data.local.dao.FolderDao
import com.notex.sd.data.local.dao.NoteDao
import com.notex.sd.data.local.dao.TagDao
import com.notex.sd.data.local.entity.NoteEntity
import com.notex.sd.data.local.entity.NoteTagCrossRef
import com.notex.sd.domain.model.Note
import com.notex.sd.domain.model.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val folderDao: FolderDao,
    private val tagDao: TagDao
) {

    suspend fun insertNote(note: Note): Long {
        val noteId = noteDao.insertNote(note.toEntity())
        note.tags.forEach { tag ->
            tagDao.insertNoteTag(NoteTagCrossRef(noteId, tag.id))
        }
        return noteId
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note.toEntity())
        tagDao.deleteAllTagsForNote(note.id)
        note.tags.forEach { tag ->
            tagDao.insertNoteTag(NoteTagCrossRef(note.id, tag.id))
        }
    }

    suspend fun deleteNote(noteId: Long) {
        noteDao.deleteNoteById(noteId)
    }

    suspend fun getNoteById(noteId: Long): Note? {
        val entity = noteDao.getNoteById(noteId) ?: return null
        val folderName = entity.folderId?.let { folderDao.getFolderById(it)?.name }
        val tags = tagDao.getTagsForNote(noteId).first().map { Tag.fromEntity(it) }
        return Note.fromEntity(entity, folderName, tags)
    }

    fun getNoteByIdFlow(noteId: Long): Flow<Note?> {
        return noteDao.getNoteByIdFlow(noteId).map { entity ->
            entity?.let {
                val folderName = it.folderId?.let { fId -> folderDao.getFolderById(fId)?.name }
                val tags = tagDao.getTagsForNote(noteId).first().map { tag -> Tag.fromEntity(tag) }
                Note.fromEntity(it, folderName, tags)
            }
        }
    }

    fun getAllActiveNotes(): Flow<List<Note>> {
        return noteDao.getAllActiveNotes().map { entities ->
            entities.map { entity ->
                val folderName = entity.folderId?.let { folderDao.getFolderById(it)?.name }
                Note.fromEntity(entity, folderName)
            }
        }
    }

    fun getNotesWithoutFolder(): Flow<List<Note>> {
        return noteDao.getNotesWithoutFolder().map { entities ->
            entities.map { Note.fromEntity(it) }
        }
    }

    fun getNotesByFolder(folderId: Long): Flow<List<Note>> {
        return combine(
            noteDao.getNotesByFolder(folderId),
            folderDao.getFolderByIdFlow(folderId)
        ) { notes, folder ->
            notes.map { Note.fromEntity(it, folder?.name) }
        }
    }

    fun getArchivedNotes(): Flow<List<Note>> {
        return noteDao.getArchivedNotes().map { entities ->
            entities.map { entity ->
                val folderName = entity.folderId?.let { folderDao.getFolderById(it)?.name }
                Note.fromEntity(entity, folderName)
            }
        }
    }

    fun getTrashNotes(): Flow<List<Note>> {
        return noteDao.getTrashNotes().map { entities ->
            entities.map { entity ->
                val folderName = entity.folderId?.let { folderDao.getFolderById(it)?.name }
                Note.fromEntity(entity, folderName)
            }
        }
    }

    fun getPinnedNotes(): Flow<List<Note>> {
        return noteDao.getPinnedNotes().map { entities ->
            entities.map { entity ->
                val folderName = entity.folderId?.let { folderDao.getFolderById(it)?.name }
                Note.fromEntity(entity, folderName)
            }
        }
    }

    fun searchNotes(query: String): Flow<List<Note>> {
        return noteDao.searchNotes(query).map { entities ->
            entities.map { entity ->
                val folderName = entity.folderId?.let { folderDao.getFolderById(it)?.name }
                Note.fromEntity(entity, folderName)
            }
        }
    }

    suspend fun togglePinStatus(noteId: Long) {
        val note = noteDao.getNoteById(noteId) ?: return
        noteDao.updatePinStatus(noteId, !note.isPinned)
    }

    suspend fun archiveNote(noteId: Long) {
        noteDao.updateArchiveStatus(noteId, true)
    }

    suspend fun unarchiveNote(noteId: Long) {
        noteDao.updateArchiveStatus(noteId, false)
    }

    suspend fun moveToTrash(noteId: Long) {
        noteDao.moveToTrash(noteId)
    }

    suspend fun restoreFromTrash(noteId: Long) {
        noteDao.restoreFromTrash(noteId)
    }

    suspend fun emptyTrash() {
        noteDao.emptyTrash()
    }

    suspend fun deleteOldTrashNotes(daysOld: Int = 30) {
        val threshold = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000L)
        noteDao.deleteOldTrashNotes(threshold)
    }

    suspend fun moveNoteToFolder(noteId: Long, folderId: Long?) {
        noteDao.moveNoteToFolder(noteId, folderId)
    }

    suspend fun updateNoteColor(noteId: Long, color: String?) {
        noteDao.updateNoteColor(noteId, color)
    }

    fun getActiveNotesCount(): Flow<Int> = noteDao.getActiveNotesCount()

    fun getNotesCountInFolder(folderId: Long): Flow<Int> = noteDao.getNotesCountInFolder(folderId)

    fun getArchivedNotesCount(): Flow<Int> = noteDao.getArchivedNotesCount()

    fun getTrashNotesCount(): Flow<Int> = noteDao.getTrashNotesCount()

    suspend fun duplicateNote(noteId: Long): Long = noteDao.duplicateNote(noteId)

    suspend fun getAllNotesForBackup(): List<NoteEntity> = noteDao.getAllNotesForBackup()

    suspend fun insertNoteFromBackup(note: NoteEntity) {
        noteDao.insertNote(note)
    }
}
