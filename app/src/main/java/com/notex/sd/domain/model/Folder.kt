package com.notex.sd.domain.model

import com.notex.sd.data.local.entity.FolderEntity
import kotlinx.serialization.Serializable

@Serializable
data class Folder(
    val id: Long = 0,
    val name: String,
    val parentId: Long? = null,
    val color: String? = null,
    val icon: String? = null,
    val sortOrder: Int = 0,
    val isExpanded: Boolean = true,
    val notesCount: Int = 0,
    val children: List<Folder> = emptyList(),
    val depth: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val isRoot: Boolean
        get() = parentId == null

    val hasChildren: Boolean
        get() = children.isNotEmpty()

    fun toEntity(): FolderEntity = FolderEntity(
        id = id,
        name = name,
        parentId = parentId,
        color = color,
        icon = icon,
        sortOrder = sortOrder,
        isExpanded = isExpanded,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromEntity(
            entity: FolderEntity,
            notesCount: Int = 0,
            children: List<Folder> = emptyList(),
            depth: Int = 0
        ): Folder = Folder(
            id = entity.id,
            name = entity.name,
            parentId = entity.parentId,
            color = entity.color,
            icon = entity.icon,
            sortOrder = entity.sortOrder,
            isExpanded = entity.isExpanded,
            notesCount = notesCount,
            children = children,
            depth = depth,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}

data class FolderTreeNode(
    val folder: Folder,
    val depth: Int = 0,
    val isLastChild: Boolean = false,
    val parentIsLastList: List<Boolean> = emptyList()
)
