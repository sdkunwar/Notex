package com.notex.sd.domain.model

import com.notex.sd.data.local.entity.TagEntity
import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    val id: Long = 0,
    val name: String,
    val color: String? = null,
    val notesCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toEntity(): TagEntity = TagEntity(
        id = id,
        name = name,
        color = color,
        createdAt = createdAt
    )

    companion object {
        fun fromEntity(entity: TagEntity, notesCount: Int = 0): Tag = Tag(
            id = entity.id,
            name = entity.name,
            color = entity.color,
            notesCount = notesCount,
            createdAt = entity.createdAt
        )
    }
}
