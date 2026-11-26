package com.zc.vndb.common.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.zc.vndb.common.domain.model.AppConfig
import com.zc.vndb.common.domain.model.AppConfigSamples
import com.zc.vndb.common.domain.model.AppSettings
import com.zc.vndb.common.domain.model.AppTheme
import com.zc.vndb.common.domain.model.ListSort
import com.zc.vndb.common.domain.model.SpoilerPreference
import com.zc.vndb.common.domain.model.TitleLanguage
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.rules.TemporaryFolder

class SettingsLocalDataSourceTest {

    @get:Rule
    val folder = TemporaryFolder()

    private fun TestScope.dataStore() = PreferenceDataStoreFactory.create(
        scope = backgroundScope,
    ) { File(folder.root, "settings.preferences_pb") }

    private fun TestScope.source(
        store: DataStore<Preferences> = dataStore(),
        appConfig: AppConfig = AppConfigSamples.development,
    ) = SettingsLocalDataSource(store, appConfig)

    @Test
    fun `nothing stored means the defaults`() = runTest {
        assertEquals(AppSettings(), source().settings.first())
    }

    @Test
    fun `every setting survives a round trip`() = runTest {
        val local = source()
        val changed = AppSettings(
            titleLanguage = TitleLanguage.Original,
            defaultSpoilerLevel = SpoilerPreference.Minor,
            spoilIfFinished = true,
            showSensitiveContent = true,
            includeAdultInBrowse = true,
            trendingLanguages = listOf("en", "zh-Hans", "ko"),
            cardLanguages = listOf("ja", "en"),
            listSort = ListSort.Vote,
            isListSortReversed = true,
            theme = AppTheme.AngelicSerenade,
        )

        local.update { changed }

        assertEquals(changed, local.settings.first())
    }

    @Test
    fun `every theme survives a round trip`() = runTest {
        val local = source()

        AppTheme.entries.forEach { theme ->
            local.update { it.copy(theme = theme) }

            assertEquals(theme, local.settings.first().theme)
        }
    }

    @Test
    fun `unknown or empty stored values fall back to the defaults`() = runTest {
        val store = dataStore()
        store.edit {
            it[stringPreferencesKey("title_language")] = "Klingon"
            it[stringPreferencesKey("trending_languages")] = " , "
            it[stringPreferencesKey("card_languages")] = " , "
            it[stringPreferencesKey("theme")] = "NotATheme"
        }

        val settings = source(store).settings.first()

        assertEquals(TitleLanguage.Romanized, settings.titleLanguage)
        assertEquals(AppSettings.DEFAULT_TRENDING_LANGUAGES, settings.trendingLanguages)
        assertEquals(emptyList(), settings.cardLanguages)
        assertEquals(AppTheme.Original, settings.theme)
    }

    @Test
    fun `card languages are off by default and capped at two when read`() = runTest {
        val store = dataStore()
        val local = source(store)
        assertEquals(emptyList(), local.settings.first().cardLanguages)

        store.edit { it[stringPreferencesKey("card_languages")] = "en,ja,ko,en" }

        assertEquals(listOf("en", "ja"), local.settings.first().cardLanguages)
    }

    @Test
    fun `a production build reads Show sensitive content as off and keeps the stored choice`() =
        runTest {
            val store = dataStore()
            source(store).update { it.copy(showSensitiveContent = true) }
            val production = source(store, AppConfigSamples.production)

            assertFalse(production.settings.first().showSensitiveContent)
            production.update { it.copy(spoilIfFinished = true) }

            assertTrue(source(store).settings.first().showSensitiveContent)
            assertTrue(source(store).settings.first().spoilIfFinished)
        }
}
