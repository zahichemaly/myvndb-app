package com.zc.vndb.common.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.zc.vndb.common.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.io.IOException
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

/** The `settings` DataStore. Missing or unknown values fall back to [AppSettings] defaults. */
@Single
class SettingsLocalDataSource(
    @Named(
        SETTINGS_DATASTORE,
    ) private val dataStore: DataStore<Preferences>,
) {

    val settings: Flow<AppSettings> = dataStore.data
        .catch { error -> if (error is IOException) emit(emptyPreferences()) else throw error }
        .map { prefs -> prefs.toSettings() }

    suspend fun update(transform: (AppSettings) -> AppSettings) {
        dataStore.edit { prefs ->
            prefs.write(transform(prefs.toSettings()))
        }
    }

    private fun Preferences.toSettings(): AppSettings {
        val defaults = AppSettings()
        return AppSettings(
            titleLanguage = this[TITLE_LANGUAGE].toEnumOr(defaults.titleLanguage),
            defaultSpoilerLevel = this[SPOILER_LEVEL].toEnumOr(defaults.defaultSpoilerLevel),
            spoilIfFinished = this[SPOIL_IF_FINISHED] ?: defaults.spoilIfFinished,
            showSensitiveContent = this[SHOW_SENSITIVE] ?: defaults.showSensitiveContent,
            includeAdultInBrowse = this[INCLUDE_ADULT] ?: defaults.includeAdultInBrowse,
            trendingLanguages = this[TRENDING_LANGUAGES]
                ?.split(SEPARATOR)
                ?.filter { it.isNotBlank() }
                ?.takeIf { it.isNotEmpty() }
                ?: defaults.trendingLanguages,
            cardLanguages = this[CARD_LANGUAGES]
                ?.split(SEPARATOR)
                ?.filter { it.isNotBlank() }
                ?.distinct()
                ?.take(AppSettings.MAX_CARD_LANGUAGES)
                ?: defaults.cardLanguages,
            listSort = this[LIST_SORT].toEnumOr(defaults.listSort),
            isListSortReversed = this[LIST_SORT_REVERSED] ?: defaults.isListSortReversed,
            theme = this[THEME].toEnumOr(defaults.theme),
        )
    }

    private fun MutablePreferences.write(settings: AppSettings) {
        this[TITLE_LANGUAGE] = settings.titleLanguage.name
        this[SPOILER_LEVEL] = settings.defaultSpoilerLevel.name
        this[SPOIL_IF_FINISHED] = settings.spoilIfFinished
        this[SHOW_SENSITIVE] = settings.showSensitiveContent
        this[INCLUDE_ADULT] = settings.includeAdultInBrowse
        this[TRENDING_LANGUAGES] = settings.trendingLanguages.joinToString(SEPARATOR)
        this[CARD_LANGUAGES] = settings.cardLanguages.joinToString(SEPARATOR)
        this[LIST_SORT] = settings.listSort.name
        this[LIST_SORT_REVERSED] = settings.isListSortReversed
        this[THEME] = settings.theme.name
    }

    private inline fun <reified E : Enum<E>> String?.toEnumOr(default: E): E =
        enumValues<E>().firstOrNull { it.name == this } ?: default

    companion object {
        const val SETTINGS_DATASTORE = "settings"
        private const val SEPARATOR = ","
        private val TITLE_LANGUAGE = stringPreferencesKey("title_language")
        private val SPOILER_LEVEL = stringPreferencesKey("default_spoiler_level")
        private val SPOIL_IF_FINISHED = booleanPreferencesKey("spoil_if_finished")
        private val SHOW_SENSITIVE = booleanPreferencesKey("show_sensitive_content")
        private val INCLUDE_ADULT = booleanPreferencesKey("include_adult_in_browse")
        private val TRENDING_LANGUAGES = stringPreferencesKey("trending_languages")
        private val CARD_LANGUAGES = stringPreferencesKey("card_languages")
        private val LIST_SORT = stringPreferencesKey("list_sort")
        private val LIST_SORT_REVERSED = booleanPreferencesKey("list_sort_reversed")
        private val THEME = stringPreferencesKey("theme")
    }
}
