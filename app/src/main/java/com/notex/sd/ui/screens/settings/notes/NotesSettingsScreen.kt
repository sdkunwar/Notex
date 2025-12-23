package com.notex.sd.ui.screens.settings.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Preview
import androidx.compose.material.icons.outlined.ViewList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.notex.sd.R
import com.notex.sd.data.preferences.SortOrder
import com.notex.sd.data.preferences.ViewMode
import com.notex.sd.ui.screens.settings.SettingsCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotesSettingsViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val viewMode by viewModel.viewMode.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val showPreview by viewModel.showPreview.collectAsState()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_notes),
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsCategory(title = stringResource(R.string.settings_view_mode))

            ListItem(
                headlineContent = {
                    Text(text = stringResource(R.string.view_mode_grid))
                },
                supportingContent = {
                    Text(
                        text = stringResource(R.string.view_mode_grid_desc),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.GridView,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingContent = {
                    RadioButton(
                        selected = viewMode == ViewMode.GRID,
                        onClick = { viewModel.setViewMode(ViewMode.GRID) }
                    )
                },
                modifier = Modifier.clickable { viewModel.setViewMode(ViewMode.GRID) }
            )

            ListItem(
                headlineContent = {
                    Text(text = stringResource(R.string.view_mode_list))
                },
                supportingContent = {
                    Text(
                        text = stringResource(R.string.view_mode_list_desc),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.ViewList,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingContent = {
                    RadioButton(
                        selected = viewMode == ViewMode.LIST,
                        onClick = { viewModel.setViewMode(ViewMode.LIST) }
                    )
                },
                modifier = Modifier.clickable { viewModel.setViewMode(ViewMode.LIST) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsCategory(title = stringResource(R.string.settings_sort_order))

            SortOption(
                title = stringResource(R.string.sort_modified_newest),
                selected = sortOrder == SortOrder.MODIFIED_NEWEST,
                onClick = { viewModel.setSortOrder(SortOrder.MODIFIED_NEWEST) }
            )

            SortOption(
                title = stringResource(R.string.sort_modified_oldest),
                selected = sortOrder == SortOrder.MODIFIED_OLDEST,
                onClick = { viewModel.setSortOrder(SortOrder.MODIFIED_OLDEST) }
            )

            SortOption(
                title = stringResource(R.string.sort_created_newest),
                selected = sortOrder == SortOrder.CREATED_NEWEST,
                onClick = { viewModel.setSortOrder(SortOrder.CREATED_NEWEST) }
            )

            SortOption(
                title = stringResource(R.string.sort_created_oldest),
                selected = sortOrder == SortOrder.CREATED_OLDEST,
                onClick = { viewModel.setSortOrder(SortOrder.CREATED_OLDEST) }
            )

            SortOption(
                title = stringResource(R.string.sort_title_az),
                selected = sortOrder == SortOrder.TITLE_AZ,
                onClick = { viewModel.setSortOrder(SortOrder.TITLE_AZ) }
            )

            SortOption(
                title = stringResource(R.string.sort_title_za),
                selected = sortOrder == SortOrder.TITLE_ZA,
                onClick = { viewModel.setSortOrder(SortOrder.TITLE_ZA) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsCategory(title = stringResource(R.string.settings_display))

            ListItem(
                headlineContent = {
                    Text(text = stringResource(R.string.show_preview))
                },
                supportingContent = {
                    Text(
                        text = stringResource(R.string.show_preview_desc),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.Preview,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingContent = {
                    Switch(
                        checked = showPreview,
                        onCheckedChange = { viewModel.setShowPreview(it) }
                    )
                },
                modifier = Modifier.clickable { viewModel.setShowPreview(!showPreview) }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SortOption(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(text = title)
        },
        leadingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Sort,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            RadioButton(
                selected = selected,
                onClick = onClick
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}
