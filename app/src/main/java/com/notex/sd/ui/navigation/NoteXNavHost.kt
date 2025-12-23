package com.notex.sd.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.notex.sd.ui.screens.archive.ArchiveScreen
import com.notex.sd.ui.screens.editor.EditorScreen
import com.notex.sd.ui.screens.home.HomeScreen
import com.notex.sd.ui.screens.onboarding.OnboardingScreen
import com.notex.sd.ui.screens.search.SearchScreen
import com.notex.sd.ui.screens.settings.SettingsScreen
import com.notex.sd.ui.screens.settings.about.AboutScreen
import com.notex.sd.ui.screens.settings.appearance.AppearanceScreen
import com.notex.sd.ui.screens.settings.backup.BackupScreen
import com.notex.sd.ui.screens.settings.editor.EditorSettingsScreen
import com.notex.sd.ui.screens.settings.notes.NotesSettingsScreen
import com.notex.sd.ui.screens.splash.SplashScreen
import com.notex.sd.ui.screens.trash.TrashScreen

private const val TRANSITION_DURATION = 300

@Composable
fun NoteXNavHost(
    navController: NavHostController,
    startDestination: NavRoute,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(TRANSITION_DURATION)) +
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(TRANSITION_DURATION)
                )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(TRANSITION_DURATION)) +
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(TRANSITION_DURATION)
                )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(TRANSITION_DURATION)) +
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(TRANSITION_DURATION)
                )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(TRANSITION_DURATION)) +
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(TRANSITION_DURATION)
                )
        }
    ) {
        composable<NavRoute.Splash> {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(NavRoute.Onboarding) {
                        popUpTo(NavRoute.Splash) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(NavRoute.Home) {
                        popUpTo(NavRoute.Splash) { inclusive = true }
                    }
                }
            )
        }

        composable<NavRoute.Onboarding> {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(NavRoute.Home) {
                        popUpTo(NavRoute.Onboarding) { inclusive = true }
                    }
                }
            )
        }

        composable<NavRoute.Home>(
            enterTransition = { fadeIn(animationSpec = tween(TRANSITION_DURATION)) },
            exitTransition = { fadeOut(animationSpec = tween(TRANSITION_DURATION)) }
        ) {
            HomeScreen(
                onNavigateToEditor = { noteId, folderId ->
                    navController.navigate(NavRoute.Editor(noteId, folderId))
                },
                onNavigateToFolder = { folderId ->
                    navController.navigate(NavRoute.Folder(folderId))
                },
                onNavigateToArchive = {
                    navController.navigate(NavRoute.Archive)
                },
                onNavigateToTrash = {
                    navController.navigate(NavRoute.Trash)
                },
                onNavigateToSearch = {
                    navController.navigate(NavRoute.Search)
                },
                onNavigateToSettings = {
                    navController.navigate(NavRoute.Settings)
                }
            )
        }

        composable<NavRoute.Editor> { backStackEntry ->
            val route: NavRoute.Editor = backStackEntry.toRoute()
            EditorScreen(
                noteId = route.noteId.takeIf { it != -1L },
                folderId = route.folderId.takeIf { it != -1L },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<NavRoute.Folder> { backStackEntry ->
            val route: NavRoute.Folder = backStackEntry.toRoute()
            HomeScreen(
                folderId = route.folderId,
                onNavigateToEditor = { noteId, fId ->
                    navController.navigate(NavRoute.Editor(noteId, fId))
                },
                onNavigateToFolder = { folderId ->
                    navController.navigate(NavRoute.Folder(folderId))
                },
                onNavigateToArchive = {
                    navController.navigate(NavRoute.Archive)
                },
                onNavigateToTrash = {
                    navController.navigate(NavRoute.Trash)
                },
                onNavigateToSearch = {
                    navController.navigate(NavRoute.Search)
                },
                onNavigateToSettings = {
                    navController.navigate(NavRoute.Settings)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<NavRoute.Archive> {
            ArchiveScreen(
                onNavigateToEditor = { noteId ->
                    navController.navigate(NavRoute.Editor(noteId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<NavRoute.Trash> {
            TrashScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<NavRoute.Search> {
            SearchScreen(
                onNavigateToEditor = { noteId ->
                    navController.navigate(NavRoute.Editor(noteId))
                },
                onNavigateToFolder = { folderId ->
                    navController.navigate(NavRoute.Folder(folderId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<NavRoute.Settings> {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAppearance = {
                    navController.navigate(NavRoute.SettingsAppearance)
                },
                onNavigateToNotes = {
                    navController.navigate(NavRoute.SettingsNotes)
                },
                onNavigateToEditor = {
                    navController.navigate(NavRoute.SettingsEditor)
                },
                onNavigateToBackup = {
                    navController.navigate(NavRoute.SettingsBackup)
                },
                onNavigateToAbout = {
                    navController.navigate(NavRoute.SettingsAbout)
                }
            )
        }

        composable<NavRoute.SettingsAppearance> {
            AppearanceScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<NavRoute.SettingsNotes> {
            NotesSettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<NavRoute.SettingsEditor> {
            EditorSettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<NavRoute.SettingsBackup> {
            BackupScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<NavRoute.SettingsAbout> {
            AboutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
