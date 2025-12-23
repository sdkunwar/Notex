package com.notex.sd.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.notex.sd.domain.model.Folder
import com.notex.sd.domain.model.FolderTreeNode

@Composable
fun FolderTreeView(
    folders: List<FolderTreeNode>,
    selectedFolderId: Long?,
    onFolderClick: (Long) -> Unit,
    onFolderExpandClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        folders.forEach { node ->
            FolderTreeItem(
                node = node,
                isSelected = selectedFolderId == node.folder.id,
                onClick = { onFolderClick(node.folder.id) },
                onExpandClick = { onFolderExpandClick(node.folder.id) }
            )
        }
    }
}

@Composable
private fun FolderTreeItem(
    node: FolderTreeNode,
    isSelected: Boolean,
    onClick: () -> Unit,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val folder = node.folder
    val hasChildren = folder.notesCount > 0 || node.depth < 2
    val rotationAngle by animateFloatAsState(
        targetValue = if (folder.isExpanded) 90f else 0f,
        label = "chevron_rotation"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = (node.depth * 20).dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    Color.Transparent
                }
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (hasChildren) {
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = if (folder.isExpanded) "Collapse" else "Expand",
                modifier = Modifier
                    .size(20.dp)
                    .rotate(rotationAngle)
                    .clickable(onClick = onExpandClick),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        } else {
            Spacer(modifier = Modifier.width(20.dp))
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = if (folder.isExpanded && hasChildren) {
                Icons.Rounded.FolderOpen
            } else {
                Icons.Rounded.Folder
            },
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            }
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = folder.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        if (folder.notesCount > 0) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = folder.notesCount.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun FolderChip(
    folder: Folder,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Folder,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = folder.name,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}
