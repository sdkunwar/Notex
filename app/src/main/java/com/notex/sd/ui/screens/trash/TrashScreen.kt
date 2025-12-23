package com.notex.sd.ui.screens.trash

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun TrashScreen(
    onNavigateBack: () -> Unit,
    viewModel: TrashViewModel = hiltViewModel()
) {
    val notes by viewModel.trashNotes.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedNote by remember { mutableStateOf<Note?>(null) }
    var showEmptyTrashDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            val message = when (event) {
                TrashUiEvent.NoteRestored -> "Note restored"
                TrashUiEvent.NoteDeletedPermanently -> "Note deleted permanently"
                TrashUiEvent.TrashEmptied -> "Trash emptied"
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
                        text = stringResource(R.string.trash_title),
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
                actions = {
                    if (notes.isNotEmpty()) {
                        IconButton(onClick = { showEmptyTrashDialog = true }) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteForever,
                                contentDescription = stringResource(R.string.trash_empty)
                            )
                        }
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
                    icon = Icons.Outlined.Delete,
                    title = stringResource(R.string.trash_empty_title),
                    description = stringResource(R.string.trash_empty_description)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.trash_auto_delete_info),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(
                        items = notes,
                        key = { it.id }
                    ) { note ->
                        NoteCard(
                            note = note,
                            onClick = { selectedNote = note },
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
        TrashActionSheet(
            note = note,
            onDismiss = { selectedNote = null },
            onRestore = {
                viewModel.restoreNote(note.id)
                selectedNote = null
            },
            onDeletePermanently = {
                viewModel.deleteNotePermanently(note.id)
                selectedNote = null
            }
        )
    }

    if (showEmptyTrashDialog) {
        AlertDialog(
            onDismissRequest = { showEmptyTrashDialog = false },
            title = { Text(stringResource(R.string.trash_empty)) },
            text = { Text(stringResource(R.string.trash_empty_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.emptyTrash()
                        showEmptyTrashDialog = false
                    }
                ) {
                    Text(
                        stringResource(R.string.delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showEmptyTrashDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrashActionSheet(
    note: Note,
    onDismiss: () -> Unit,
    onRestore: () -> Unit,
    onDeletePermanently: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
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
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            ListItem(
                headlineContent = { Text(stringResource(R.string.trash_restore)) },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.Restore,
                        contentDescription = null
                    )
                },
                modifier = Modifier.clickable(onClick = onRestore)
            )

            ListItem(
                headlineContent = {
                    Text(
                        stringResource(R.string.trash_delete_permanently),
                        color = MaterialTheme.colorScheme.error
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.DeleteForever,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                modifier = Modifier.clickable(onClick = onDeletePermanently)
            )
        }
    }
}
