package com.notex.sd.ui.screens.settings

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
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.notex.sd.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAppearance: () -> Unit,
    onNavigateToNotes: () -> Unit,
    onNavigateToEditor: () -> Unit,
    onNavigateToBackup: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
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
            SettingsCategory(title = stringResource(R.string.settings_appearance))

            SettingsItem(
                icon = Icons.Outlined.ColorLens,
                title = stringResource(R.string.settings_appearance),
                subtitle = "Theme, colors, and display",
                onClick = onNavigateToAppearance
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsCategory(title = stringResource(R.string.settings_notes))

            SettingsItem(
                icon = Icons.Outlined.Notes,
                title = stringResource(R.string.settings_notes),
                subtitle = "Preview, sorting, and display options",
                onClick = onNavigateToNotes
            )

            SettingsItem(
                icon = Icons.Outlined.Edit,
                title = stringResource(R.string.settings_editor),
                subtitle = "Font size and auto-save",
                onClick = onNavigateToEditor
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsCategory(title = stringResource(R.string.settings_data))

            SettingsItem(
                icon = Icons.Outlined.Backup,
                title = stringResource(R.string.settings_backup),
                subtitle = "Backup and restore your notes",
                onClick = onNavigateToBackup
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsCategory(title = stringResource(R.string.settings_about))

            SettingsItem(
                icon = Icons.Outlined.Info,
                title = stringResource(R.string.settings_about),
                subtitle = "Version and information",
                onClick = onNavigateToAbout
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsCategory(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(text = title)
        },
        supportingContent = subtitle?.let {
            {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}
