package com.zc.vndb.common.data.repository

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.zc.vndb.common.data.local.LanguageLocalDataSource
import com.zc.vndb.common.data.network.appJson
import com.zc.vndb.common.data.network.createHttpClient
import com.zc.vndb.common.data.remote.SchemaService
import com.zc.vndb.common.domain.model.BuiltInLanguages
import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.common.domain.model.LanguageOption
import com.zc.vndb.common.domain.util.FixedClock
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Instant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.rules.TemporaryFolder

class LanguageRepositoryImplTest {

    @get:Rule
    val folder = TemporaryFolder()

    private var body = fixture()
    private var status = HttpStatusCode.OK
    private var requests = 0
    private val clock = FixedClock(Instant.parse("2026-11-20T00:00:00Z"))

    private fun TestScope.local() = LanguageLocalDataSource(
        PreferenceDataStoreFactory.create(scope = backgroundScope) {
            File(folder.root, "languages.preferences_pb")
        },
        appJson,
    )

    private fun service() = SchemaService(
        createHttpClient(
            json = appJson,
            engine = MockEngine {
                requests++
                respond(
                    body,
                    status,
                    headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            },
        ),
    )

    private fun TestScope.repository() = LanguageRepositoryImpl(local(), service(), clock)

    @Test
    fun `before any fetch the built-in languages are offered`() = runTest {
        assertEquals(BuiltInLanguages, repository().languages.first())
        assertEquals(0, requests)
    }

    @Test
    fun `refresh fetches VNDB's list and the list then comes from the cache`() = runTest {
        val repository = repository()

        repository.refreshIfStale().getOrThrow()

        val languages = repository.languages.first()
        assertEquals(57, languages.size)
        assertEquals(LanguageOption("ar", "Arabic"), languages.first())
        assertEquals(
            LanguageOption("pt-br", "Portuguese (Brazil)"),
            languages.single { it.code == "pt-br" },
        )
        assertEquals(1, requests)
    }

    @Test
    fun `a fresh cache is not fetched again`() = runTest {
        val repository = repository()
        repository.refreshIfStale().getOrThrow()

        repository.refreshIfStale().getOrThrow()

        assertEquals(1, requests)
    }

    @Test
    fun `a cache older than 30 days is refreshed`() = runTest {
        val local = local()
        local.save(listOf(LanguageOption("en", "English")), Instant.parse("2026-10-01T00:00:00Z"))
        val repository = LanguageRepositoryImpl(local, service(), clock)

        repository.refreshIfStale().getOrThrow()

        assertEquals(1, requests)
        assertEquals(57, repository.languages.first().size)
    }

    @Test
    fun `a cache under 30 days old is kept`() = runTest {
        val local = local()
        val cached = listOf(LanguageOption("en", "English"))
        local.save(cached, Instant.parse("2026-11-01T00:00:00Z"))
        val repository = LanguageRepositoryImpl(local, service(), clock)

        repository.refreshIfStale().getOrThrow()

        assertEquals(0, requests)
        assertEquals(cached, repository.languages.first())
    }

    @Test
    fun `a failed fetch leaves the built-in list and can be retried`() = runTest {
        val repository = repository()
        status = HttpStatusCode.InternalServerError

        val failed = repository.refreshIfStale()

        assertTrue(failed.isFailure)
        assertEquals(BuiltInLanguages, repository.languages.first())

        status = HttpStatusCode.OK
        repository.refreshIfStale().getOrThrow()
        assertEquals(57, repository.languages.first().size)
    }

    @Test
    fun `a failed refresh keeps a stale cache`() = runTest {
        val local = local()
        val cached = listOf(LanguageOption("en", "English"))
        local.save(cached, Instant.parse("2026-01-01T00:00:00Z"))
        val repository = LanguageRepositoryImpl(local, service(), clock)
        status = HttpStatusCode.InternalServerError

        assertTrue(repository.refreshIfStale().isFailure)

        assertEquals(cached, repository.languages.first())
    }

    @Test
    fun `an empty language list is rejected instead of replacing the cache`() = runTest {
        val repository = repository()
        body = """{"enums":{"language":[]},"api_fields":{},"extlinks":{}}"""

        assertEquals(DomainError.InvalidData, repository.refreshIfStale().exceptionOrNull())
        assertEquals(BuiltInLanguages, repository.languages.first())
    }

    @Test
    fun `an unexpected response shape is InvalidData`() = runTest {
        body = """{"unexpected":true}"""

        assertEquals(DomainError.InvalidData, repository().refreshIfStale().exceptionOrNull())
    }

    private fun fixture(): String = checkNotNull(
        LanguageRepositoryImplTest::class.java.getResource("/schema/schema.json"),
    ).readText()
}
