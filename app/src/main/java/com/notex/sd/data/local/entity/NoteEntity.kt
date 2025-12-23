package com.notex.sd.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = FolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["folderId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["folderId"]),
        Index(value = ["isPinned"]),
        Index(value = ["isArchived"]),
        Index(value = ["isInTrash"]),
        Index(value = ["createdAt"]),
        Index(value = ["updatedAt"])
    ]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val plainTextContent: String = "",
    val folderId: Long? = null,
    val color: String? = null,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isInTrash: Boolean = false,
    val isChecklist: Boolean = false,
    val checklistItems: String = "[]",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val trashedAt: Long? = null
)
