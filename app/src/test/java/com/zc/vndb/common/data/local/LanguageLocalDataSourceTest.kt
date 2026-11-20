package com.zc.vndb.common.data.local

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.zc.vndb.common.data.network.appJson
import com.zc.vndb.common.domain.model.LanguageOption
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.rules.TemporaryFolder

class LanguageLocalDataSourceTest {

    @get:Rule
    val folder = TemporaryFolder()

    private fun TestScope.dataStore() = PreferenceDataStoreFactory.create(
        scope = backgroundScope,
    ) { File(folder.root, "languages.preferences_pb") }

    @Test
    fun `nothing stored is null`() = runTest {
        assertNull(LanguageLocalDataSource(dataStore(), appJson).cached.first())
    }

    @Test
    fun `languages and their order survive a round trip`() = runTest {
        val local = LanguageLocalDataSource(dataStore(), appJson)
        val languages = listOf(
            LanguageOption("zh-Hans", "Chinese (simplified)"),
            LanguageOption("ar", "Arabic"),
            LanguageOption("pt-br", "Portuguese (Brazil)"),
        )
        val fetchedAt = Instant.parse("2026-11-20T10:15:30Z")

        local.save(languages, fetchedAt)

        val cached = local.cached.first()
        assertEquals(languages, cached?.languages)
        assertEquals(fetchedAt, cached?.fetchedAt)
    }

    @Test
    fun `unreadable stored languages are treated as nothing cached`() = runTest {
        val store = dataStore()
        store.edit {
            it[stringPreferencesKey("languages")] = "not json"
            it[longPreferencesKey("fetched_at")] = 1L
        }

        assertNull(LanguageLocalDataSource(store, appJson).cached.first())
    }

    @Test
    fun `a cache without a fetch time is treated as nothing cached`() = runTest {
        val store = dataStore()
        store.edit { it[stringPreferencesKey("languages")] = """{"en":"English"}""" }

        assertNull(LanguageLocalDataSource(store, appJson).cached.first())
    }
}
