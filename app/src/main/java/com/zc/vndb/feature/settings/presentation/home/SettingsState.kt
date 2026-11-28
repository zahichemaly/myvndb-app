package com.zc.vndb.feature.settings.presentation.home

import androidx.compose.runtime.Immutable
import com.zc.vndb.common.domain.model.LanguageOption
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.settings.presentation.home.model.SettingOptionUIModel
import com.zc.vndb.feature.settings.presentation.home.model.SettingsDialog
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class SettingsState(
    /** False until the stored settings have been read. */
    val isLoaded: Boolean = false,
    /** The current theme's name, shown on the Theme row. */
    val themeLabel: UiText? = null,
    val titleLanguageOptions: ImmutableList<SettingOptionUIModel> = persistentListOf(),
    val titleLanguageLabel: UiText? = null,
    val showAllTags: Boolean = false,
    val spoilerOptions: ImmutableList<SettingOptionUIModel> = persistentListOf(),
    val spoilerLabel: UiText? = null,
    val spoilIfFinished: Boolean = false,
    /** "Spoil me if finished" needs the user's lists. */
    val isSignedIn: Boolean = false,
    /** The username under Log out; null for a guest (no Log out row). */
    val accountLabel: UiText? = null,
    val showSensitiveContent: Boolean = false,
    /** Builds that never show sensitive content (production) hide the "Show sensitive content" row. */
    val canShowSensitiveContent: Boolean = false,
    val includeAdultInBrowse: Boolean = false,
    /** Every language the two language dialogs offer, in picker order. */
    val languages: ImmutableList<LanguageOption> = persistentListOf(),
    val trendingLanguages: ImmutableList<String> = persistentListOf(),
    val languagesLabel: UiText? = null,
    val cardLanguages: ImmutableList<String> = persistentListOf(),
    /** "Off", or the languages flagged on cards ("English, Japanese"). */
    val cardLanguagesLabel: UiText? = null,
    /** "142 MB"; null while measuring. */
    val imageCacheLabel: UiText? = null,
    val isClearingCache: Boolean = false,
    val versionLabel: UiText? = null,
    val dialog: SettingsDialog? = null,
)
