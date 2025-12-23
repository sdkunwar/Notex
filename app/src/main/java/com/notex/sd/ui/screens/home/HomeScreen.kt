package com.notex.sd.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ViewList
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.notex.sd.R
import com.notex.sd.data.preferences.ViewMode
import com.notex.sd.domain.model.Note
import com.notex.sd.ui.components.EmptyState
import com.notex.sd.ui.components.FolderTreeView
import com.notex.sd.ui.components.NoteCard
import com.notex.sd.ui.components.NoteGridCard
import com.notex.sd.ui.theme.Primary50
import com.notex.sd.ui.theme.Primary70
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToEditor: (noteId: Long, folderId: Long) -> Unit,
    onNavigateToFolder: (folderId: Long) -> Unit,
    onNavigateToArchive: () -> Unit,
    onNavigateToTrash: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToSettings: () -> Unit,
    folderId: Long? = null,
    onNavigateBack: (() -> Unit)? = null,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val notes by viewModel.notes.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val currentFolder by viewModel.currentFolder.collectAsState()
    val folderTree by viewModel.folderTree.collectAsState()
    val notesCount by viewModel.notesCount.collectAsState()
    val archivedNotesCount by viewModel.archivedNotesCount.collectAsState()
    val trashNotesCount by viewModel.trashNotesCount.collectAsState()
    val showNotePreview by viewModel.showNotePreview.collectAsState()
    val showNoteDate by viewModel.showNoteDate.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var selectedNoteForAction by remember { mutableStateOf<Note?>(null) }

    LaunchedEffect(folderId) {
        viewModel.setCurrentFolder(folderId)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            val message = when (event) {
                HomeUiEvent.NoteArchived -> "Note archived"
                HomeUiEvent.NoteMovedToTrash -> "Note moved to trash"
                HomeUiEvent.NotePinToggled -> "Pin status updated"
                HomeUiEvent.NoteMovedToFolder -> "Note moved"
                HomeUiEvent.NoteDuplicated -> "Note duplicated"
                HomeUiEvent.FolderCreated -> "Folder created"
                HomeUiEvent.FolderRenamed -> "Folder renamed"
                HomeUiEvent.FolderDeleted -> "Folder deleted"
            }
            snackbarHostState.showSnackbar(message)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HomeDrawerContent(
                folderTree = folderTree,
                selectedFolderId = folderId,
                notesCount = notesCount,
                archivedNotesCount = archivedNotesCount,
                trashNotesCount = trashNotesCount,
                onAllNotesClick = {
                    coroutineScope.launch {
                        drawerState.close()
                        viewModel.setCurrentFolder(null)
                        onNavigateBack?.invoke()
                    }
                },
                onFolderClick = { id ->
                    coroutineScope.launch {
                        drawerState.close()
                        onNavigateToFolder(id)
                    }
                },
                onFolderExpandClick = { id ->
                    viewModel.toggleFolderExpanded(id)
                },
                onArchiveClick = {
                    coroutineScope.launch {
                        drawerState.close()
                        onNavigateToArchive()
                    }
                },
                onTrashClick = {
                    coroutineScope.launch {
                        drawerState.close()
                        onNavigateToTrash()
                    }
                },
                onSettingsClick = {
                    coroutineScope.launch {
                        drawerState.close()
                        onNavigateToSettings()
                    }
                }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text(
                            text = currentFolder?.name ?: stringResource(R.string.home_all_notes),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Menu,
                                contentDescription = stringResource(R.string.cd_menu)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToSearch) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = stringResource(R.string.cd_search)
                            )
                        }
                        IconButton(onClick = { viewModel.toggleViewMode() }) {
                            Icon(
                                imageVector = if (viewMode == ViewMode.LIST) {
                                    Icons.Rounded.GridView
                                } else {
                                    Icons.Rounded.ViewList
                                },
                                contentDescription = stringResource(R.string.cd_view_mode)
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
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        onNavigateToEditor(-1L, folderId ?: -1L)
                    },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = stringResource(R.string.cd_create_note),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
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
                        icon = Icons.AutoMirrored.Rounded.Notes,
                        title = stringResource(R.string.home_empty_title),
                        description = stringResource(R.string.home_empty_description)
                    )
                } else {
                    AnimatedVisibility(
                        visible = viewMode == ViewMode.LIST,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        NoteListView(
                            notes = notes,
                            showPreview = showNotePreview,
                            showDate = showNoteDate,
                            onNoteClick = { note ->
                                onNavigateToEditor(note.id, folderId ?: -1L)
                            },
                            onNoteLongClick = { note ->
                                selectedNoteForAction = note
                            }
                        )
                    }

                    AnimatedVisibility(
                        visible = viewMode == ViewMode.GRID,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        NoteGridView(
                            notes = notes,
                            showPreview = showNotePreview,
                            showDate = showNoteDate,
                            onNoteClick = { note ->
                                onNavigateToEditor(note.id, folderId ?: -1L)
                            },
                            onNoteLongClick = { note ->
                                selectedNoteForAction = note
                            }
                        )
                    }
                }
            }
        }
    }

    selectedNoteForAction?.let { note ->
        NoteActionSheet(
            note = note,
            onDismiss = { selectedNoteForAction = null },
            onPin = {
                viewModel.togglePinNote(note.id)
                selectedNoteForAction = null
            },
            onArchive = {
                viewModel.archiveNote(note.id)
                selectedNoteForAction = null
            },
            onDelete = {
                viewModel.moveToTrash(note.id)
                selectedNoteForAction = null
            },
            onDuplicate = {
                viewModel.duplicateNote(note.id)
                selectedNoteForAction = null
            }
        )
    }
}

@Composable
private fun NoteListView(
    notes: List<Note>,
    showPreview: Boolean,
    showDate: Boolean,
    onNoteClick: (Note) -> Unit,
    onNoteLongClick: (Note) -> Unit
) {
    val pinnedNotes = notes.filter { it.isPinned }
    val otherNotes = notes.filter { !it.isPinned }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (pinnedNotes.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.home_pinned),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            items(
                items = pinnedNotes,
                key = { it.id }
            ) { note ->
                NoteCard(
                    note = note,
                    onClick = { onNoteClick(note) },
                    onLongClick = { onNoteLongClick(note) },
                    showPreview = showPreview,
                    showDate = showDate
                )
            }

            if (otherNotes.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.home_recent),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }

        items(
            items = otherNotes,
            key = { it.id }
        ) { note ->
            NoteCard(
                note = note,
                onClick = { onNoteClick(note) },
                onLongClick = { onNoteLongClick(note) },
                showPreview = showPreview,
                showDate = showDate
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun NoteGridView(
    notes: List<Note>,
    showPreview: Boolean,
    showDate: Boolean,
    onNoteClick: (Note) -> Unit,
    onNoteLongClick: (Note) -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalItemSpacing = 12.dp
    ) {
        items(
            items = notes,
            key = { it.id }
        ) { note ->
            NoteGridCard(
                note = note,
                onClick = { onNoteClick(note) },
                onLongClick = { onNoteLongClick(note) },
                showPreview = showPreview,
                showDate = showDate
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun HomeDrawerContent(
    folderTree: List<com.notex.sd.domain.model.FolderTreeNode>,
    selectedFolderId: Long?,
    notesCount: Int,
    archivedNotesCount: Int,
    trashNotesCount: Int,
    onAllNotesClick: () -> Unit,
    onFolderClick: (Long) -> Unit,
    onFolderExpandClick: (Long) -> Unit,
    onArchiveClick: () -> Unit,
    onTrashClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Primary50, Primary70)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "N",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "NoteX",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Notes,
                        contentDescription = null
                    )
                },
                label = {
                    Text(stringResource(R.string.home_all_notes))
                },
                badge = {
                    if (notesCount > 0) {
                        Text(
                            text = notesCount.toString(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                selected = selectedFolderId == null,
                onClick = onAllNotesClick,
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            if (folderTree.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.nav_folders),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp)
                )

                FolderTreeView(
                    folders = folderTree,
                    selectedFolderId = selectedFolderId,
                    onFolderClick = onFolderClick,
                    onFolderExpandClick = onFolderExpandClick,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Archive,
                        contentDescription = null
                    )
                },
                label = {
                    Text(stringResource(R.string.nav_archive))
                },
                badge = {
                    if (archivedNotesCount > 0) {
                        Text(
                            text = archivedNotesCount.toString(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                selected = false,
                onClick = onArchiveClick,
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null
                    )
                },
                label = {
                    Text(stringResource(R.string.nav_trash))
                },
                badge = {
                    if (trashNotesCount > 0) {
                        Text(
                            text = trashNotesCount.toString(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                selected = false,
                onClick = onTrashClick,
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = null
                    )
                },
                label = {
                    Text(stringResource(R.string.nav_settings))
                },
                selected = false,
                onClick = onSettingsClick,
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
