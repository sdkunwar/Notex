package com.notex.sd.ui.screens.archive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.notex.sd.R
import com.notex.sd.domain.model.Note
import com.notex.sd.ui.components.EmptyState
import com.notex.sd.ui.components.NoteCard
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveScreen(
    onNavigateToEditor: (noteId: Long) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ArchiveViewModel = hiltViewModel()
) {
    val notes by viewModel.archivedNotes.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedNote by remember { mutableStateOf<Note?>(null) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            val message = when (event) {
                ArchiveUiEvent.NoteUnarchived -> "Note unarchived"
                ArchiveUiEvent.NoteMovedToTrash -> "Note moved to trash"
            }
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.archive_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (notes.isEmpty()) {
                EmptyState(
                    icon = Icons.Outlined.Archive,
                    title = stringResource(R.string.archive_empty_title),
                    description = stringResource(R.string.archive_empty_description)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = notes,
                        key = { it.id }
                    ) { note ->
                        NoteCard(
                            note = note,
                            onClick = { onNavigateToEditor(note.id) },
                            onLongClick = { selectedNote = note }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    selectedNote?.let { note ->
        ArchiveActionSheet(
            note = note,
            onDismiss = { selectedNote = null },
            onUnarchive = {
                viewModel.unarchiveNote(note.id)
                selectedNote = null
            },
            onDelete = {
                viewModel.moveToTrash(note.id)
                selectedNote = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArchiveActionSheet(
    note: Note,
    onDismiss: () -> Unit,
    onUnarchive: () -> Unit,
    onDelete: () -> Unit
) {
    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = note.title.ifBlank { "Untitled note" },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            androidx.compose.material3.ListItem(
                headlineContent = { Text(stringResource(R.string.unarchive_note)) },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.Archive,
                        contentDescription = null
                    )
                },
                modifier = Modifier.androidx.compose.foundation.clickable(onClick = onUnarchive)
            )

            androidx.compose.material3.ListItem(
                headlineContent = {
                    Text(
                        stringResource(R.string.action_delete),
                        color = MaterialTheme.colorScheme.error
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                modifier = Modifier.androidx.compose.foundation.clickable(onClick = onDelete)
            )
        }
    }
}
