package com.zc.vndb.feature.settings.presentation.home

import com.zc.vndb.feature.settings.presentation.home.model.SettingsDialog

sealed interface SettingsIntent {
    data object BackClicked : SettingsIntent
    data class DialogOpened(val dialog: SettingsDialog) : SettingsIntent
    data object DialogDismissed : SettingsIntent
    data object ThemeClicked : SettingsIntent
    data object LicensesClicked : SettingsIntent

    /** A choice in the title language or spoiler level dialog. */
    data class OptionSelected(val dialog: SettingsDialog, val key: String) : SettingsIntent
    data class LanguagesConfirmed(val codes: Set<String>) : SettingsIntent
    data class CardLanguagesConfirmed(val codes: Set<String>) : SettingsIntent
    data class ShowAllTagsChanged(val isOn: Boolean) : SettingsIntent
    data class SpoilIfFinishedChanged(val isOn: Boolean) : SettingsIntent
    data class ShowSensitiveChanged(val isOn: Boolean) : SettingsIntent
    data object SensitiveConfirmed : SettingsIntent
    data class IncludeAdultChanged(val isOn: Boolean) : SettingsIntent
    data object ClearCacheConfirmed : SettingsIntent
    data object LogOutConfirmed : SettingsIntent
    data class LinkClicked(val url: String) : SettingsIntent
}
