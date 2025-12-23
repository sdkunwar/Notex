package com.notex.sd.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.notex.sd.data.local.entity.NoteTagCrossRef
import com.notex.sd.data.local.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: TagEntity): Long

    @Update
    suspend fun updateTag(tag: TagEntity)

    @Delete
    suspend fun deleteTag(tag: TagEntity)

    @Query("DELETE FROM tags WHERE id = :tagId")
    suspend fun deleteTagById(tagId: Long)

    @Query("SELECT * FROM tags WHERE id = :tagId")
    suspend fun getTagById(tagId: Long): TagEntity?

    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchTags(query: String): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags WHERE name = :name LIMIT 1")
    suspend fun getTagByName(name: String): TagEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNoteTag(crossRef: NoteTagCrossRef)

    @Delete
    suspend fun deleteNoteTag(crossRef: NoteTagCrossRef)

    @Query("DELETE FROM note_tags WHERE noteId = :noteId")
    suspend fun deleteAllTagsForNote(noteId: Long)

    @Query("DELETE FROM note_tags WHERE tagId = :tagId")
    suspend fun deleteAllNotesForTag(tagId: Long)

    @Query("""
        SELECT t.* FROM tags t
        INNER JOIN note_tags nt ON t.id = nt.tagId
        WHERE nt.noteId = :noteId
        ORDER BY t.name ASC
    """)
    fun getTagsForNote(noteId: Long): Flow<List<TagEntity>>

    @Query("""
        SELECT COUNT(*) FROM note_tags WHERE tagId = :tagId
    """)
    fun getNotesCountForTag(tagId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM tags")
    fun getTagCount(): Flow<Int>

    @Query("SELECT EXISTS(SELECT 1 FROM tags WHERE name = :name AND id != :excludeId)")
    suspend fun tagNameExists(name: String, excludeId: Long = 0): Boolean

    @Query("SELECT * FROM tags")
    suspend fun getAllTagsForBackup(): List<TagEntity>
}
