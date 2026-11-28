package com.zc.vndb.feature.settings.presentation.mapper

import com.zc.vndb.common.domain.model.AppConfig
import com.zc.vndb.common.domain.model.AppSettings
import com.zc.vndb.common.domain.model.LanguageOption
import com.zc.vndb.common.domain.model.Session
import com.zc.vndb.common.domain.model.SpoilerPreference
import com.zc.vndb.common.domain.model.TitleLanguage
import com.zc.vndb.common.ui.component.spoilerfilter.mapper.toIcon
import com.zc.vndb.common.ui.component.spoilerfilter.mapper.toSpoilerLevel
import com.zc.vndb.common.ui.mapper.nameOf
import com.zc.vndb.common.ui.mapper.toLabel
import com.zc.vndb.common.ui.mapper.toLanguagesLabel
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.settings.presentation.SettingsState
import com.zc.vndb.feature.settings.presentation.model.SettingOptionUIModel
import kotlinx.collections.immutable.toImmutableList

private const val BYTES_PER_UNIT = 1024.0

/** Copies [settings] into [state]'s rows and dialog options. */
fun SettingsState.withSettings(
    settings: AppSettings,
    languages: List<LanguageOption>,
): SettingsState = copy(
    isLoaded = true,
    themeLabel = settings.theme.toLabel(),
    titleLanguageOptions = TitleLanguage.entries.map {
        SettingOptionUIModel(it.name, it.toLabel(), it == settings.titleLanguage)
    }.toImmutableList(),
    titleLanguageLabel = settings.titleLanguage.toLabel(),
    showAllTags = settings.showAllTags,
    spoilerOptions = SpoilerPreference.entries.map {
        SettingOptionUIModel(
            key = it.name,
            label = it.toLabel(),
            isSelected = it == settings.defaultSpoilerLevel,
            icon = it.toSpoilerLevel().toIcon(),
        )
    }.toImmutableList(),
    spoilerLabel = settings.defaultSpoilerLevel.toLabel(),
    spoilIfFinished = settings.spoilIfFinished,
    showSensitiveContent = settings.showSensitiveContent,
    includeAdultInBrowse = settings.includeAdultInBrowse,
    languages = languages.withChosen(settings.trendingLanguages + settings.cardLanguages)
        .toImmutableList(),
    trendingLanguages = settings.trendingLanguages.toImmutableList(),
    languagesLabel = settings.trendingLanguages.toLanguagesLabel(languages::nameOf),
    cardLanguages = settings.cardLanguages.toImmutableList(),
    cardLanguagesLabel = settings.cardLanguages.takeIf { it.isNotEmpty() }
        ?.toLanguagesLabel(languages::nameOf)
        ?: UiText.StringResource(StringResource.settings_card_languages_off),
)

/** The rows that depend on the build: the version, the "Show sensitive content" switch and the source code. */
fun SettingsState.withAppConfig(appConfig: AppConfig): SettingsState = copy(
    versionLabel = UiText.StringResource(
        StringResource.settings_version,
        listOf(
            UiText.StringResource(StringResource.app_name),
            appConfig.versionName,
            appConfig.versionCode,
        ),
    ),
    canShowSensitiveContent = appConfig.showSensitiveContent,
    showSourceCodeSection = appConfig.isDebug,
)

/** "Yorhel · read & edit access": the Log out row's subtitle, with what the token allows. */
fun Session.toAccountLabel(): UiText = UiText.StringResource(
    StringResource.settings_log_out_account,
    listOf(username, toAccessLabel()),
)

private fun Session.toAccessLabel(): UiText = UiText.StringResource(
    when {
        canReadPrivateList && canWriteList -> StringResource.settings_access_read_edit
        canReadPrivateList -> StringResource.settings_access_read
        canWriteList -> StringResource.settings_access_edit
        else -> StringResource.settings_access_public
    },
)

/** The listed languages, plus any saved choice that is not in the list so it can still be unticked. */
private fun List<LanguageOption>.withChosen(chosen: List<String>): List<LanguageOption> =
    this + chosen.filter { code -> none { it.code == code } }.map { LanguageOption(it, "") }

/** "0 B", "512 KB", "142.3 MB", "1.2 GB". */
fun Long.toSizeLabel(): UiText {
    var value = toDouble()
    val units = listOf(
        StringResource.settings_size_bytes,
        StringResource.settings_size_kb,
        StringResource.settings_size_mb,
        StringResource.settings_size_gb,
    )
    var unit = 0
    while (value >= BYTES_PER_UNIT && unit < units.lastIndex) {
        value /= BYTES_PER_UNIT
        unit++
    }
    val number = if (unit == 0 || value >= 100) "%.0f".format(value) else "%.1f".format(value)
    return UiText.StringResource(units[unit], listOf(number))
}

private fun TitleLanguage.toLabel(): UiText = UiText.StringResource(
    when (this) {
        TitleLanguage.Romanized -> StringResource.settings_title_romanized
        TitleLanguage.Original -> StringResource.settings_title_original
    },
)

private fun SpoilerPreference.toLabel(): UiText = UiText.StringResource(
    when (this) {
        SpoilerPreference.None -> StringResource.settings_spoiler_none
        SpoilerPreference.Minor -> StringResource.settings_spoiler_minor
        SpoilerPreference.Major -> StringResource.settings_spoiler_major
    },
)
