package com.notex.sd.ui.screens.settings.appearance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PhoneAndroid
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.notex.sd.R
import com.notex.sd.data.preferences.ThemeMode
import com.notex.sd.ui.screens.settings.SettingsCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceScreen(
    onNavigateBack: () -> Unit,
    viewModel: AppearanceViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val themeMode by viewModel.themeMode.collectAsState()
    val dynamicColors by viewModel.dynamicColors.collectAsState()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_appearance),
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
            SettingsCategory(title = stringResource(R.string.settings_theme))

            ThemeOption(
                icon = Icons.Outlined.PhoneAndroid,
                title = stringResource(R.string.theme_system),
                subtitle = stringResource(R.string.theme_system_desc),
                selected = themeMode == ThemeMode.SYSTEM,
                onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) }
            )

            ThemeOption(
                icon = Icons.Outlined.LightMode,
                title = stringResource(R.string.theme_light),
                subtitle = stringResource(R.string.theme_light_desc),
                selected = themeMode == ThemeMode.LIGHT,
                onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) }
            )

            ThemeOption(
                icon = Icons.Outlined.DarkMode,
                title = stringResource(R.string.theme_dark),
                subtitle = stringResource(R.string.theme_dark_desc),
                selected = themeMode == ThemeMode.DARK,
                onClick = { viewModel.setThemeMode(ThemeMode.DARK) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsCategory(title = stringResource(R.string.settings_colors))

            ListItem(
                headlineContent = {
                    Text(text = stringResource(R.string.dynamic_colors))
                },
                supportingContent = {
                    Text(
                        text = stringResource(R.string.dynamic_colors_desc),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingContent = {
                    Switch(
                        checked = dynamicColors,
                        onCheckedChange = { viewModel.setDynamicColors(it) }
                    )
                },
                modifier = Modifier.clickable { viewModel.setDynamicColors(!dynamicColors) }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ThemeOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
                imageVector = icon,
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
