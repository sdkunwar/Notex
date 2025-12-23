package com.notex.sd.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.notex.sd.data.local.entity.FolderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: FolderEntity): Long

    @Update
    suspend fun updateFolder(folder: FolderEntity)

    @Delete
    suspend fun deleteFolder(folder: FolderEntity)

    @Query("DELETE FROM folders WHERE id = :folderId")
    suspend fun deleteFolderById(folderId: Long)

    @Query("SELECT * FROM folders WHERE id = :folderId")
    suspend fun getFolderById(folderId: Long): FolderEntity?

    @Query("SELECT * FROM folders WHERE id = :folderId")
    fun getFolderByIdFlow(folderId: Long): Flow<FolderEntity?>

    @Query("SELECT * FROM folders ORDER BY sortOrder ASC, name ASC")
    fun getAllFolders(): Flow<List<FolderEntity>>

    @Query("SELECT * FROM folders WHERE parentId IS NULL ORDER BY sortOrder ASC, name ASC")
    fun getRootFolders(): Flow<List<FolderEntity>>

    @Query("SELECT * FROM folders WHERE parentId = :parentId ORDER BY sortOrder ASC, name ASC")
    fun getChildFolders(parentId: Long): Flow<List<FolderEntity>>

    @Query("SELECT * FROM folders WHERE parentId = :parentId ORDER BY sortOrder ASC, name ASC")
    suspend fun getChildFoldersSync(parentId: Long): List<FolderEntity>

    @Query("SELECT * FROM folders WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchFolders(query: String): Flow<List<FolderEntity>>

    @Query("UPDATE folders SET isExpanded = :isExpanded, updatedAt = :updatedAt WHERE id = :folderId")
    suspend fun updateExpandedState(folderId: Long, isExpanded: Boolean, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE folders SET sortOrder = :sortOrder, updatedAt = :updatedAt WHERE id = :folderId")
    suspend fun updateSortOrder(folderId: Long, sortOrder: Int, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE folders SET parentId = :parentId, updatedAt = :updatedAt WHERE id = :folderId")
    suspend fun moveFolder(folderId: Long, parentId: Long?, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM folders")
    fun getFolderCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM folders WHERE parentId = :parentId")
    fun getChildFolderCount(parentId: Long): Flow<Int>

    @Query("""
        WITH RECURSIVE folder_tree AS (
            SELECT id, parentId, 0 AS level
            FROM folders
            WHERE id = :folderId
            UNION ALL
            SELECT f.id, f.parentId, ft.level + 1
            FROM folders f
            INNER JOIN folder_tree ft ON f.parentId = ft.id
        )
        SELECT id FROM folder_tree
    """)
    suspend fun getAllDescendantIds(folderId: Long): List<Long>

    @Query("SELECT EXISTS(SELECT 1 FROM folders WHERE name = :name AND parentId IS :parentId AND id != :excludeId)")
    suspend fun folderNameExists(name: String, parentId: Long?, excludeId: Long = 0): Boolean

    @Query("SELECT * FROM folders")
    suspend fun getAllFoldersForBackup(): List<FolderEntity>
}
