package com.notex.sd.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.notex.sd.domain.model.Note
import com.notex.sd.domain.model.NoteColor

@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    showPreview: Boolean = true,
    showDate: Boolean = true,
    modifier: Modifier = Modifier
) {
    val backgroundColor = note.color?.let {
        Color(it.lightColor)
    } ?: MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (note.title.isNotBlank()) {
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Text(
                        text = note.preview.ifBlank { "New note" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.weight(1f)
                    )
                }

                if (note.isPinned) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Rounded.PushPin,
                        contentDescription = "Pinned",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (showPreview && note.preview.isNotBlank() && note.title.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = note.preview,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            if (showDate || note.folderName != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (note.folderName != null) {
                        Icon(
                            imageVector = Icons.Rounded.Folder,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = note.folderName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        if (showDate) {
                            Text(
                                text = " • ",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                    if (showDate) {
                        Text(
                            text = note.formattedDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NoteGridCard(
    note: Note,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    showPreview: Boolean = true,
    showDate: Boolean = true,
    modifier: Modifier = Modifier
) {
    val backgroundColor = note.color?.let {
        Color(it.lightColor)
    } ?: MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (note.title.isNotBlank()) {
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Text(
                        text = note.preview.ifBlank { "New note" },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.weight(1f)
                    )
                }

                if (note.isPinned) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Rounded.PushPin,
                        contentDescription = "Pinned",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (showPreview && note.preview.isNotBlank() && note.title.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = note.preview,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            if (showDate) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = note.formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun NoteColorIndicator(
    color: NoteColor?,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val indicatorColor = color?.let { Color(it.lightColor) }
        ?: MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(indicatorColor)
            .then(
                if (isSelected) {
                    Modifier.background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        CircleShape
                    )
                } else Modifier
            )
            .combinedClickable(onClick = onClick, onLongClick = {})
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.Center)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
        }
    }
}
