package com.notex.sd.domain.model

import com.notex.sd.data.local.entity.NoteEntity
import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val plainTextContent: String = "",
    val folderId: Long? = null,
    val folderName: String? = null,
    val color: NoteColor? = null,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isInTrash: Boolean = false,
    val isChecklist: Boolean = false,
    val checklistItems: List<ChecklistItem> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val trashedAt: Long? = null
) {
    val isEmpty: Boolean
        get() = title.isBlank() && content.isBlank() && plainTextContent.isBlank()

    val preview: String
        get() = plainTextContent.take(200).replace("\n", " ").trim()

    val formattedDate: String
        get() {
            val now = System.currentTimeMillis()
            val diff = now - updatedAt
            return when {
                diff < 60_000 -> "Just now"
                diff < 3600_000 -> "${diff / 60_000}m ago"
                diff < 86400_000 -> "${diff / 3600_000}h ago"
                diff < 604800_000 -> "${diff / 86400_000}d ago"
                else -> {
                    val date = java.util.Date(updatedAt)
                    java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault()).format(date)
                }
            }
        }

    fun toEntity(): NoteEntity = NoteEntity(
        id = id,
        title = title,
        content = content,
        plainTextContent = plainTextContent,
        folderId = folderId,
        color = color?.name,
        isPinned = isPinned,
        isArchived = isArchived,
        isInTrash = isInTrash,
        isChecklist = isChecklist,
        checklistItems = checklistItems.toJsonString(),
        createdAt = createdAt,
        updatedAt = updatedAt,
        trashedAt = trashedAt
    )

    companion object {
        fun fromEntity(entity: NoteEntity, folderName: String? = null, tags: List<Tag> = emptyList()): Note = Note(
            id = entity.id,
            title = entity.title,
            content = entity.content,
            plainTextContent = entity.plainTextContent,
            folderId = entity.folderId,
            folderName = folderName,
            color = entity.color?.let { NoteColor.valueOf(it) },
            isPinned = entity.isPinned,
            isArchived = entity.isArchived,
            isInTrash = entity.isInTrash,
            isChecklist = entity.isChecklist,
            checklistItems = entity.checklistItems.parseChecklistItems(),
            tags = tags,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            trashedAt = entity.trashedAt
        )
    }
}

@Serializable
data class ChecklistItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String = "",
    val isChecked: Boolean = false,
    val indent: Int = 0
)

@Serializable
enum class NoteColor(val lightColor: Long, val darkColor: Long) {
    RED(0xFFFFCDD2, 0xFF5C3A3A),
    ORANGE(0xFFFFE0B2, 0xFF5C4A3A),
    YELLOW(0xFFFFF9C4, 0xFF5C5A3A),
    GREEN(0xFFC8E6C9, 0xFF3A5C3A),
    TEAL(0xFFB2DFDB, 0xFF3A5C5A),
    BLUE(0xFFBBDEFB, 0xFF3A4A5C),
    INDIGO(0xFFC5CAE9, 0xFF3A3A5C),
    PURPLE(0xFFE1BEE7, 0xFF4A3A5C),
    PINK(0xFFF8BBD0, 0xFF5C3A4A),
    BROWN(0xFFD7CCC8, 0xFF4A3A3A),
    GRAY(0xFFCFD8DC, 0xFF3A4A4A)
}

private fun List<ChecklistItem>.toJsonString(): String {
    return kotlinx.serialization.json.Json.encodeToString(
        kotlinx.serialization.builtins.ListSerializer(ChecklistItem.serializer()),
        this
    )
}

private fun String.parseChecklistItems(): List<ChecklistItem> {
    return try {
        kotlinx.serialization.json.Json.decodeFromString(
            kotlinx.serialization.builtins.ListSerializer(ChecklistItem.serializer()),
            this
        )
    } catch (e: Exception) {
        emptyList()
    }
}
