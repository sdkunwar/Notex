package com.notex.sd.ui.screens.settings.editor

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
import androidx.compose.material.icons.outlined.FormatSize
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
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
import com.notex.sd.ui.screens.settings.SettingsCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditorSettingsViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val fontSize by viewModel.fontSize.collectAsState()
    val autoSave by viewModel.autoSave.collectAsState()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_editor),
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
            SettingsCategory(title = stringResource(R.string.settings_font_size))

            FontSizeOption(
                title = stringResource(R.string.font_size_small),
                subtitle = "14sp",
                selected = fontSize == 14,
                onClick = { viewModel.setFontSize(14) }
            )

            FontSizeOption(
                title = stringResource(R.string.font_size_medium),
                subtitle = "16sp",
                selected = fontSize == 16,
                onClick = { viewModel.setFontSize(16) }
            )

            FontSizeOption(
                title = stringResource(R.string.font_size_large),
                subtitle = "18sp",
                selected = fontSize == 18,
                onClick = { viewModel.setFontSize(18) }
            )

            FontSizeOption(
                title = stringResource(R.string.font_size_extra_large),
                subtitle = "20sp",
                selected = fontSize == 20,
                onClick = { viewModel.setFontSize(20) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsCategory(title = stringResource(R.string.settings_saving))

            ListItem(
                headlineContent = {
                    Text(text = stringResource(R.string.auto_save))
                },
                supportingContent = {
                    Text(
                        text = stringResource(R.string.auto_save_desc),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.Save,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingContent = {
                    Switch(
                        checked = autoSave,
                        onCheckedChange = { viewModel.setAutoSave(it) }
                    )
                },
                modifier = Modifier.clickable { viewModel.setAutoSave(!autoSave) }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun FontSizeOption(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(text = title)
        },
        supportingContent = {
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.FormatSize,
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
