package com.notex.sd.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "folders",
    foreignKeys = [
        ForeignKey(
            entity = FolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["parentId"]),
        Index(value = ["name"]),
        Index(value = ["sortOrder"])
    ]
)
data class FolderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val parentId: Long? = null,
    val color: String? = null,
    val icon: String? = null,
    val sortOrder: Int = 0,
    val isExpanded: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
