package com.notex.sd.ui.navigation

import kotlinx.serialization.Serializable

sealed interface NavRoute {

    @Serializable
    data object Splash : NavRoute

    @Serializable
    data object Onboarding : NavRoute

    @Serializable
    data object Home : NavRoute

    @Serializable
    data class Editor(
        val noteId: Long = -1L,
        val folderId: Long = -1L
    ) : NavRoute

    @Serializable
    data class Folder(
        val folderId: Long
    ) : NavRoute

    @Serializable
    data object Archive : NavRoute

    @Serializable
    data object Trash : NavRoute

    @Serializable
    data object Search : NavRoute

    @Serializable
    data object Settings : NavRoute

    @Serializable
    data object SettingsAppearance : NavRoute

    @Serializable
    data object SettingsNotes : NavRoute

    @Serializable
    data object SettingsEditor : NavRoute

    @Serializable
    data object SettingsBackup : NavRoute

    @Serializable
    data object SettingsAbout : NavRoute
}

object NavDestinations {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val EDITOR = "editor"
    const val FOLDER = "folder"
    const val ARCHIVE = "archive"
    const val TRASH = "trash"
    const val SEARCH = "search"
    const val SETTINGS = "settings"
}
