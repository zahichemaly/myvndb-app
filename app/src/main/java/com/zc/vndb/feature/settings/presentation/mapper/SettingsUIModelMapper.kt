package com.zc.vndb.feature.settings.presentation.mapper

import com.zc.vndb.common.domain.model.AppSettings
import com.zc.vndb.common.domain.model.LanguageOption
import com.zc.vndb.common.domain.model.SpoilerPreference
import com.zc.vndb.common.domain.model.TitleLanguage
import com.zc.vndb.common.ui.mapper.displayName
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
    spoilerOptions = SpoilerPreference.entries.map {
        SettingOptionUIModel(it.name, it.toLabel(), it == settings.defaultSpoilerLevel)
    }.toImmutableList(),
    spoilerLabel = settings.defaultSpoilerLevel.toLabel(),
    spoilIfFinished = settings.spoilIfFinished,
    showSensitiveContent = settings.showSensitiveContent,
    includeAdultInBrowse = settings.includeAdultInBrowse,
    languageOptions = languages.withChosen(settings.trendingLanguages).map {
        SettingOptionUIModel(
            it.code,
            UiText.StringValue(it.displayName()),
            it.code in settings.trendingLanguages,
        )
    }.toImmutableList(),
    languagesLabel = settings.trendingLanguages.toLanguagesLabel(),
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
