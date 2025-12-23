package com.notex.sd.ui.screens.editor

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckBox
import androidx.compose.material.icons.rounded.CheckBoxOutlineBlank
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.notex.sd.R
import com.notex.sd.domain.model.ChecklistItem
import com.notex.sd.domain.model.NoteColor
import com.notex.sd.ui.components.NoteColorIndicator
import com.notex.sd.ui.theme.PoppinsFamily
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    noteId: Long?,
    folderId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: EditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val fontSize by viewModel.editorFontSize.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    val titleFocusRequester = remember { FocusRequester() }
    val contentFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.loadNote(noteId, folderId)
    }

    LaunchedEffect(uiState.isNewNote, uiState.isLoading) {
        if (!uiState.isLoading && uiState.isNewNote) {
            titleFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                EditorUiEvent.NoteSaved -> {
                    snackbarHostState.showSnackbar("Note saved")
                }
                EditorUiEvent.NoteDeleted -> {
                    onNavigateBack()
                }
                EditorUiEvent.NoteArchived -> {
                    onNavigateBack()
                }
                EditorUiEvent.NoteMovedToTrash -> {
                    onNavigateBack()
                }
                is EditorUiEvent.NoteDuplicated -> {
                    snackbarHostState.showSnackbar("Note duplicated")
                }
            }
        }
    }

    BackHandler(enabled = uiState.hasUnsavedChanges) {
        viewModel.saveNote()
        onNavigateBack()
    }

    val backgroundColor = uiState.color?.let {
        Color(it.lightColor)
    } ?: MaterialTheme.colorScheme.background

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = {
                        if (uiState.hasUnsavedChanges) {
                            viewModel.saveNote()
                        }
                        onNavigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.togglePin() }) {
                        Icon(
                            imageVector = if (uiState.isPinned) {
                                Icons.Rounded.PushPin
                            } else {
                                Icons.Outlined.PushPin
                            },
                            contentDescription = stringResource(
                                if (uiState.isPinned) R.string.action_unpin else R.string.action_pin
                            ),
                            tint = if (uiState.isPinned) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }

                    IconButton(onClick = { showColorPicker = !showColorPicker }) {
                        Icon(
                            imageVector = Icons.Outlined.ColorLens,
                            contentDescription = stringResource(R.string.action_change_color)
                        )
                    }

                    Box {
                        IconButton(onClick = { showMoreMenu = true }) {
                            Icon(
                                imageVector = Icons.Outlined.MoreVert,
                                contentDescription = stringResource(R.string.cd_more_options)
                            )
                        }

                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.action_archive)) },
                                onClick = {
                                    showMoreMenu = false
                                    viewModel.archive()
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Archive,
                                        contentDescription = null
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.action_duplicate)) },
                                onClick = {
                                    showMoreMenu = false
                                    viewModel.duplicate()
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.ContentCopy,
                                        contentDescription = null
                                    )
                                }
                            )
                            if (!uiState.isNewNote) {
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            stringResource(R.string.action_delete),
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    },
                                    onClick = {
                                        showMoreMenu = false
                                        viewModel.moveToTrash()
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Outlined.Delete,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = backgroundColor
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                ) {
                    AnimatedVisibility(
                        visible = showColorPicker,
                        enter = slideInVertically() + fadeIn(),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        ColorPickerRow(
                            selectedColor = uiState.color,
                            onColorSelected = { color ->
                                viewModel.setColor(color)
                            }
                        )
                    }

                    if (uiState.isChecklist) {
                        ChecklistEditor(
                            items = uiState.checklistItems,
                            fontSize = fontSize,
                            onItemChange = { index, item ->
                                viewModel.updateChecklistItem(index, item)
                            },
                            onItemCheckedChange = { index ->
                                viewModel.toggleChecklistItemChecked(index)
                            },
                            onAddItem = { afterIndex ->
                                viewModel.addChecklistItem(afterIndex)
                            },
                            onRemoveItem = { index ->
                                viewModel.removeChecklistItem(index)
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp)
                        )
                    } else {
                        NoteEditor(
                            title = uiState.title,
                            content = uiState.content,
                            fontSize = fontSize,
                            onTitleChange = { viewModel.updateTitle(it) },
                            onContentChange = { viewModel.updateContent(it) },
                            titleFocusRequester = titleFocusRequester,
                            contentFocusRequester = contentFocusRequester,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorPickerRow(
    selectedColor: NoteColor?,
    onColorSelected: (NoteColor?) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            NoteColorIndicator(
                color = null,
                isSelected = selectedColor == null,
                onClick = { onColorSelected(null) }
            )
        }

        items(NoteColor.entries.size) { index ->
            val color = NoteColor.entries[index]
            NoteColorIndicator(
                color = color,
                isSelected = selectedColor == color,
                onClick = { onColorSelected(color) }
            )
        }
    }
}

@Composable
private fun NoteEditor(
    title: String,
    content: String,
    fontSize: Int,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    titleFocusRequester: FocusRequester,
    contentFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        BasicTextField(
            value = title,
            onValueChange = onTitleChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(titleFocusRequester),
            textStyle = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { contentFocusRequester.requestFocus() }
            ),
            decorationBox = { innerTextField ->
                Box {
                    if (title.isEmpty()) {
                        Text(
                            text = stringResource(R.string.editor_title_hint),
                            style = TextStyle(
                                fontFamily = PoppinsFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 24.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                            )
                        )
                    }
                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(
            value = content,
            onValueChange = onContentChange,
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(contentFocusRequester),
            textStyle = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = fontSize.sp,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = (fontSize * 1.5).sp
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences
            ),
            decorationBox = { innerTextField ->
                Box {
                    if (content.isEmpty()) {
                        Text(
                            text = stringResource(R.string.editor_content_hint),
                            style = TextStyle(
                                fontFamily = PoppinsFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = fontSize.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                            )
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
private fun ChecklistEditor(
    items: List<ChecklistItem>,
    fontSize: Int,
    onItemChange: (Int, ChecklistItem) -> Unit,
    onItemCheckedChange: (Int) -> Unit,
    onAddItem: (Int) -> Unit,
    onRemoveItem: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(
            items = items,
            key = { _, item -> item.id }
        ) { index, item ->
            ChecklistItemRow(
                item = item,
                fontSize = fontSize,
                onCheckedChange = { onItemCheckedChange(index) },
                onTextChange = { text ->
                    onItemChange(index, item.copy(text = text))
                },
                onAddNewItem = { onAddItem(index) },
                onRemove = { onRemoveItem(index) },
                showRemove = items.size > 1
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onAddItem(items.lastIndex) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add item",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add item",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun ChecklistItemRow(
    item: ChecklistItem,
    fontSize: Int,
    onCheckedChange: () -> Unit,
    onTextChange: (String) -> Unit,
    onAddNewItem: () -> Unit,
    onRemove: () -> Unit,
    showRemove: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onCheckedChange,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = if (item.isChecked) {
                    Icons.Rounded.CheckBox
                } else {
                    Icons.Rounded.CheckBoxOutlineBlank
                },
                contentDescription = null,
                tint = if (item.isChecked) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                }
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        BasicTextField(
            value = item.text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            textStyle = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = fontSize.sp,
                color = if (item.isChecked) {
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                } else {
                    MaterialTheme.colorScheme.onBackground
                }
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { onAddNewItem() }
            ),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box {
                    if (item.text.isEmpty()) {
                        Text(
                            text = "List item",
                            style = TextStyle(
                                fontFamily = PoppinsFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = fontSize.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                            )
                        )
                    }
                    innerTextField()
                }
            }
        )

        if (showRemove) {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Remove item",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}
