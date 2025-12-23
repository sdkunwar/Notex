package com.notex.sd.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.notex.sd.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: Long)

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Long): NoteEntity?

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getNoteByIdFlow(noteId: Long): Flow<NoteEntity?>

    @Query("""
        SELECT * FROM notes
        WHERE isInTrash = 0 AND isArchived = 0
        ORDER BY isPinned DESC, updatedAt DESC
    """)
    fun getAllActiveNotes(): Flow<List<NoteEntity>>

    @Query("""
        SELECT * FROM notes
        WHERE isInTrash = 0 AND isArchived = 0 AND folderId IS NULL
        ORDER BY isPinned DESC, updatedAt DESC
    """)
    fun getNotesWithoutFolder(): Flow<List<NoteEntity>>

    @Query("""
        SELECT * FROM notes
        WHERE folderId = :folderId AND isInTrash = 0 AND isArchived = 0
        ORDER BY isPinned DESC, updatedAt DESC
    """)
    fun getNotesByFolder(folderId: Long): Flow<List<NoteEntity>>

    @Query("""
        SELECT * FROM notes
        WHERE isArchived = 1 AND isInTrash = 0
        ORDER BY updatedAt DESC
    """)
    fun getArchivedNotes(): Flow<List<NoteEntity>>

    @Query("""
        SELECT * FROM notes
        WHERE isInTrash = 1
        ORDER BY trashedAt DESC
    """)
    fun getTrashNotes(): Flow<List<NoteEntity>>

    @Query("""
        SELECT * FROM notes
        WHERE isPinned = 1 AND isInTrash = 0 AND isArchived = 0
        ORDER BY updatedAt DESC
    """)
    fun getPinnedNotes(): Flow<List<NoteEntity>>

    @Query("""
        SELECT * FROM notes
        WHERE (title LIKE '%' || :query || '%' OR plainTextContent LIKE '%' || :query || '%')
        AND isInTrash = 0
        ORDER BY isPinned DESC, updatedAt DESC
    """)
    fun searchNotes(query: String): Flow<List<NoteEntity>>

    @Query("UPDATE notes SET isPinned = :isPinned, updatedAt = :updatedAt WHERE id = :noteId")
    suspend fun updatePinStatus(noteId: Long, isPinned: Boolean, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE notes SET isArchived = :isArchived, isPinned = 0, updatedAt = :updatedAt WHERE id = :noteId")
    suspend fun updateArchiveStatus(noteId: Long, isArchived: Boolean, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE notes SET isInTrash = 1, trashedAt = :trashedAt, isPinned = 0, isArchived = 0, updatedAt = :updatedAt WHERE id = :noteId")
    suspend fun moveToTrash(noteId: Long, trashedAt: Long = System.currentTimeMillis(), updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE notes SET isInTrash = 0, trashedAt = NULL, updatedAt = :updatedAt WHERE id = :noteId")
    suspend fun restoreFromTrash(noteId: Long, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM notes WHERE isInTrash = 1")
    suspend fun emptyTrash()

    @Query("DELETE FROM notes WHERE isInTrash = 1 AND trashedAt < :threshold")
    suspend fun deleteOldTrashNotes(threshold: Long)

    @Query("UPDATE notes SET folderId = :folderId, updatedAt = :updatedAt WHERE id = :noteId")
    suspend fun moveNoteToFolder(noteId: Long, folderId: Long?, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE notes SET color = :color, updatedAt = :updatedAt WHERE id = :noteId")
    suspend fun updateNoteColor(noteId: Long, color: String?, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM notes WHERE isInTrash = 0 AND isArchived = 0")
    fun getActiveNotesCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM notes WHERE folderId = :folderId AND isInTrash = 0 AND isArchived = 0")
    fun getNotesCountInFolder(folderId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM notes WHERE isArchived = 1 AND isInTrash = 0")
    fun getArchivedNotesCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM notes WHERE isInTrash = 1")
    fun getTrashNotesCount(): Flow<Int>

    @Transaction
    suspend fun duplicateNote(noteId: Long): Long {
        val note = getNoteById(noteId) ?: return -1
        val newNote = note.copy(
            id = 0,
            title = "${note.title} (Copy)",
            isPinned = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return insertNote(newNote)
    }

    @Query("SELECT * FROM notes")
    suspend fun getAllNotesForBackup(): List<NoteEntity>
}
