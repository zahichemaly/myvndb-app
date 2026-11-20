package com.zc.vndb.common.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.zc.vndb.common.domain.model.LanguageOption
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

/** The languages last fetched from VNDB and when, in the `languages` DataStore. */
data class CachedLanguages(val languages: List<LanguageOption>, val fetchedAt: Instant)

@Single
class LanguageLocalDataSource(
    @Named(LANGUAGE_DATASTORE) private val dataStore: DataStore<Preferences>,
    private val json: Json,
) {

    /** Null when nothing has been cached, or what was cached cannot be read. */
    val cached: Flow<CachedLanguages?> = dataStore.data
        .catch { error -> if (error is IOException) emit(emptyPreferences()) else throw error }
        .map { prefs -> prefs.toCached() }

    suspend fun save(languages: List<LanguageOption>, fetchedAt: Instant) {
        dataStore.edit { prefs ->
            prefs[LANGUAGES] = json.encodeToString(languages.map { it.code to it.label }.toMap())
            prefs[FETCHED_AT] = fetchedAt.toEpochMilliseconds()
        }
    }

    private fun Preferences.toCached(): CachedLanguages? {
        val labels = this[LANGUAGES]?.let(::decodeLabels)
        val fetchedAt = this[FETCHED_AT]
        return if (labels.isNullOrEmpty() || fetchedAt == null) {
            null
        } else {
            CachedLanguages(
                languages = labels.map { (code, label) -> LanguageOption(code, label) },
                fetchedAt = Instant.fromEpochMilliseconds(fetchedAt),
            )
        }
    }

    private fun decodeLabels(stored: String): Map<String, String>? = try {
        json.decodeFromString<Map<String, String>>(stored)
    } catch (_: SerializationException) {
        null
    }

    companion object {
        const val LANGUAGE_DATASTORE = "languages"
        private val LANGUAGES = stringPreferencesKey("languages")
        private val FETCHED_AT = longPreferencesKey("fetched_at")
    }
}
