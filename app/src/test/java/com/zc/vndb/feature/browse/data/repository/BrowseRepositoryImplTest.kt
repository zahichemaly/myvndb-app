package com.zc.vndb.feature.browse.data.repository

import com.zc.vndb.common.data.network.appJson
import com.zc.vndb.common.data.network.createHttpClient
import com.zc.vndb.common.domain.model.AppConfigSamples
import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.common.domain.model.NovelLength
import com.zc.vndb.feature.browse.data.remote.BrowseService
import com.zc.vndb.feature.browse.domain.model.BrowseFilter
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class BrowseRepositoryImplTest {

    private var body = fixture()
    private var status = HttpStatusCode.OK
    private var lastPath: String? = null
    private var lastMethod: HttpMethod? = null

    private val repository = BrowseRepositoryImpl(
        BrowseService(
            createHttpClient(
                json = appJson,
                engine = MockEngine { request ->
                    lastPath = request.url.encodedPath
                    lastMethod = request.method
                    respond(
                        body,
                        status,
                        headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                },
            ),
        ),
        appConfig = AppConfigSamples.development,
    )

    @Test
    fun `maps the search fixture to a page`() = runTest {
        val page = repository.searchNovels(
            BrowseFilter(query = "steins"),
            page = 1,
            pageSize = 2,
            withCount = true,
        )
            .getOrThrow()

        assertEquals(HttpMethod.Post, lastMethod)
        assertEquals("/kana/vn", lastPath)
        assertEquals(9, page.totalCount)
        assertTrue(page.hasMore)
        val first = page.novels.first()
        assertEquals("v17102", first.id)
        assertEquals(listOf("MAGES.", "M2 Co., Ltd."), first.developerNames)
        assertEquals(NovelLength.Long, first.length)
        assertEquals("https://t.vndb.org/cv.t/34/75834.jpg", first.cover?.thumbnailUrl)
        assertTrue(first.tags.isNotEmpty())
    }

    @Test
    fun `HTTP failures become domain errors`() = runTest {
        status = HttpStatusCode.TooManyRequests

        val result = repository.searchNovels(
            BrowseFilter(),
            page = 1,
            pageSize = 20,
            withCount = true,
        )

        assertEquals(DomainError.RateLimited, result.exceptionOrNull())
    }

    private companion object {
        fun fixture(): String = checkNotNull(
            BrowseRepositoryImplTest::class.java.getResource("/browse/search_page.json"),
        ).readText()
    }
}
