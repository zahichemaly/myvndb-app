package com.zc.vndb.feature.settings.presentation.home

import com.zc.vndb.common.ui.text.UiText

sealed interface SettingsEffect {
    data object NavigateBack : SettingsEffect
    data object OpenTheme : SettingsEffect
    data object OpenLicenses : SettingsEffect
    data class OpenUrl(val url: String) : SettingsEffect
    data class ShowMessage(val message: UiText) : SettingsEffect
}
