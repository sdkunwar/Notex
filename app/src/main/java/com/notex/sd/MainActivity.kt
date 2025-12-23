package com.notex.sd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.notex.sd.data.preferences.UserPreferencesManager
import com.notex.sd.ui.navigation.NavRoute
import com.notex.sd.ui.navigation.NoteXNavHost
import com.notex.sd.ui.theme.NoteXTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: UserPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by preferencesManager.themeMode.collectAsState(
                initial = com.notex.sd.data.preferences.ThemeMode.SYSTEM
            )
            val dynamicColors by preferencesManager.dynamicColors.collectAsState(initial = true)

            NoteXTheme(
                themeMode = themeMode,
                dynamicColor = dynamicColors
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()

                    NoteXNavHost(
                        navController = navController,
                        startDestination = NavRoute.Splash,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
