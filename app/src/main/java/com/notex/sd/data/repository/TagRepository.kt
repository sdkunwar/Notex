package com.notex.sd.data.repository

import com.notex.sd.data.local.dao.TagDao
import com.notex.sd.data.local.entity.NoteTagCrossRef
import com.notex.sd.domain.model.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagRepository @Inject constructor(
    private val tagDao: TagDao
) {

    suspend fun insertTag(tag: Tag): Long {
        return tagDao.insertTag(tag.toEntity())
    }

    suspend fun updateTag(tag: Tag) {
        tagDao.updateTag(tag.toEntity())
    }

    suspend fun deleteTag(tagId: Long) {
        tagDao.deleteTagById(tagId)
    }

    suspend fun getTagById(tagId: Long): Tag? {
        val entity = tagDao.getTagById(tagId) ?: return null
        val notesCount = tagDao.getNotesCountForTag(tagId).first()
        return Tag.fromEntity(entity, notesCount)
    }

    fun getAllTags(): Flow<List<Tag>> {
        return tagDao.getAllTags().map { entities ->
            entities.map { entity ->
                val notesCount = tagDao.getNotesCountForTag(entity.id).first()
                Tag.fromEntity(entity, notesCount)
            }
        }
    }

    fun getTagsForNote(noteId: Long): Flow<List<Tag>> {
        return tagDao.getTagsForNote(noteId).map { entities ->
            entities.map { Tag.fromEntity(it) }
        }
    }

    suspend fun addTagToNote(noteId: Long, tagId: Long) {
        tagDao.insertNoteTag(NoteTagCrossRef(noteId, tagId))
    }

    suspend fun removeTagFromNote(noteId: Long, tagId: Long) {
        tagDao.deleteNoteTag(NoteTagCrossRef(noteId, tagId))
    }

    suspend fun getOrCreateTag(name: String): Tag {
        val existing = tagDao.getTagByName(name)
        if (existing != null) {
            return Tag.fromEntity(existing)
        }
        val newTag = Tag(name = name)
        val id = tagDao.insertTag(newTag.toEntity())
        return newTag.copy(id = id)
    }

    suspend fun tagNameExists(name: String, excludeId: Long = 0): Boolean {
        return tagDao.tagNameExists(name, excludeId)
    }

    fun searchTags(query: String): Flow<List<Tag>> {
        return tagDao.searchTags(query).map { entities ->
            entities.map { Tag.fromEntity(it) }
        }
    }

    fun getTagCount(): Flow<Int> = tagDao.getTagCount()

    suspend fun getAllTagsForBackup(): List<com.notex.sd.data.local.entity.TagEntity> = tagDao.getAllTagsForBackup()

    suspend fun insertTag(tag: com.notex.sd.data.local.entity.TagEntity): Long {
        return tagDao.insertTag(tag)
    }
}
