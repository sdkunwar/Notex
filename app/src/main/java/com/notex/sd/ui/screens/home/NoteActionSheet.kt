package com.notex.sd.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.notex.sd.R
import com.notex.sd.domain.model.Note

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteActionSheet(
    note: Note,
    onDismiss: () -> Unit,
    onPin: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = note.title.ifBlank { "Untitled note" },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(8.dp))

            ActionItem(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.PushPin,
                        contentDescription = null
                    )
                },
                title = if (note.isPinned) {
                    stringResource(R.string.action_unpin)
                } else {
                    stringResource(R.string.action_pin)
                },
                onClick = onPin
            )

            ActionItem(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Archive,
                        contentDescription = null
                    )
                },
                title = stringResource(R.string.action_archive),
                onClick = onArchive
            )

            ActionItem(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = null
                    )
                },
                title = stringResource(R.string.action_duplicate),
                onClick = onDuplicate
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(8.dp))

            ActionItem(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                title = stringResource(R.string.action_delete),
                titleColor = MaterialTheme.colorScheme.error,
                onClick = onDelete
            )
        }
    }
}

@Composable
private fun ActionItem(
    icon: @Composable () -> Unit,
    title: String,
    onClick: () -> Unit,
    titleColor: Color = MaterialTheme.colorScheme.onSurface
) {
    ListItem(
        headlineContent = {
            Text(
                text = title,
                color = titleColor
            )
        },
        leadingContent = icon,
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .let { modifier ->
                androidx.compose.foundation.clickable(onClick = onClick).let { modifier }
            }
    )
}
