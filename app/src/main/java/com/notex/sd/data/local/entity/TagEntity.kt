package com.notex.sd.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tags",
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class TagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val color: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "note_tags",
    primaryKeys = ["noteId", "tagId"],
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        ),
        androidx.room.ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["noteId"]),
        Index(value = ["tagId"])
    ]
)
data class NoteTagCrossRef(
    val noteId: Long,
    val tagId: Long
)
