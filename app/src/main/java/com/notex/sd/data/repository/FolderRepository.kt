package com.notex.sd.data.repository

import com.notex.sd.data.local.dao.FolderDao
import com.notex.sd.data.local.dao.NoteDao
import com.notex.sd.data.local.entity.FolderEntity
import com.notex.sd.domain.model.Folder
import com.notex.sd.domain.model.FolderTreeNode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FolderRepository @Inject constructor(
    private val folderDao: FolderDao,
    private val noteDao: NoteDao
) {

    suspend fun insertFolder(folder: Folder): Long {
        return folderDao.insertFolder(folder.toEntity())
    }

    suspend fun updateFolder(folder: Folder) {
        folderDao.updateFolder(folder.toEntity())
    }

    suspend fun deleteFolder(folderId: Long) {
        folderDao.deleteFolderById(folderId)
    }

    suspend fun getFolderById(folderId: Long): Folder? {
        val entity = folderDao.getFolderById(folderId) ?: return null
        val notesCount = noteDao.getNotesCountInFolder(folderId).first()
        return Folder.fromEntity(entity, notesCount)
    }

    fun getFolderByIdFlow(folderId: Long): Flow<Folder?> {
        return combine(
            folderDao.getFolderByIdFlow(folderId),
            noteDao.getNotesCountInFolder(folderId)
        ) { entity, count ->
            entity?.let { Folder.fromEntity(it, count) }
        }
    }

    fun getAllFolders(): Flow<List<Folder>> {
        return folderDao.getAllFolders().map { entities ->
            entities.map { Folder.fromEntity(it) }
        }
    }

    fun getRootFolders(): Flow<List<Folder>> {
        return folderDao.getRootFolders().map { entities ->
            entities.map { entity ->
                val notesCount = noteDao.getNotesCountInFolder(entity.id).first()
                Folder.fromEntity(entity, notesCount)
            }
        }
    }

    fun getChildFolders(parentId: Long): Flow<List<Folder>> {
        return folderDao.getChildFolders(parentId).map { entities ->
            entities.map { entity ->
                val notesCount = noteDao.getNotesCountInFolder(entity.id).first()
                Folder.fromEntity(entity, notesCount)
            }
        }
    }

    suspend fun buildFolderTree(): List<Folder> {
        val allFolders = folderDao.getAllFolders().first()
        return buildTree(allFolders, null, 0)
    }

    private suspend fun buildTree(
        allFolders: List<FolderEntity>,
        parentId: Long?,
        depth: Int
    ): List<Folder> {
        return allFolders
            .filter { it.parentId == parentId }
            .sortedWith(compareBy({ it.sortOrder }, { it.name }))
            .map { entity ->
                val notesCount = noteDao.getNotesCountInFolder(entity.id).first()
                val children = buildTree(allFolders, entity.id, depth + 1)
                Folder.fromEntity(entity, notesCount, children, depth)
            }
    }

    fun getFolderTreeNodes(): Flow<List<FolderTreeNode>> {
        return folderDao.getAllFolders().map { entities ->
            val tree = mutableListOf<FolderTreeNode>()
            buildTreeNodes(entities, null, 0, emptyList(), tree)
            tree
        }
    }

    private suspend fun buildTreeNodes(
        allFolders: List<FolderEntity>,
        parentId: Long?,
        depth: Int,
        parentIsLastList: List<Boolean>,
        result: MutableList<FolderTreeNode>
    ) {
        val children = allFolders
            .filter { it.parentId == parentId }
            .sortedWith(compareBy({ it.sortOrder }, { it.name }))

        children.forEachIndexed { index, entity ->
            val isLast = index == children.lastIndex
            val notesCount = noteDao.getNotesCountInFolder(entity.id).first()
            val folder = Folder.fromEntity(entity, notesCount, depth = depth)

            result.add(
                FolderTreeNode(
                    folder = folder,
                    depth = depth,
                    isLastChild = isLast,
                    parentIsLastList = parentIsLastList
                )
            )

            if (entity.isExpanded) {
                buildTreeNodes(
                    allFolders,
                    entity.id,
                    depth + 1,
                    parentIsLastList + isLast,
                    result
                )
            }
        }
    }

    suspend fun toggleExpandedState(folderId: Long) {
        val folder = folderDao.getFolderById(folderId) ?: return
        folderDao.updateExpandedState(folderId, !folder.isExpanded)
    }

    suspend fun moveFolder(folderId: Long, newParentId: Long?) {
        val descendants = folderDao.getAllDescendantIds(folderId)
        if (newParentId != null && descendants.contains(newParentId)) {
            return
        }
        folderDao.moveFolder(folderId, newParentId)
    }

    suspend fun folderNameExists(name: String, parentId: Long?, excludeId: Long = 0): Boolean {
        return folderDao.folderNameExists(name, parentId, excludeId)
    }

    fun searchFolders(query: String): Flow<List<Folder>> {
        return folderDao.searchFolders(query).map { entities ->
            entities.map { Folder.fromEntity(it) }
        }
    }

    fun getFolderCount(): Flow<Int> = folderDao.getFolderCount()

    suspend fun getAllFoldersForBackup(): List<FolderEntity> = folderDao.getAllFoldersForBackup()

    suspend fun insertFolder(folder: FolderEntity): Long {
        return folderDao.insertFolder(folder)
    }
}
