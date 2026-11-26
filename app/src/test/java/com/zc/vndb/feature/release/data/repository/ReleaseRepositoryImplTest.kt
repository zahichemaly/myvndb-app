package com.zc.vndb.feature.release.data.repository

import com.zc.vndb.common.data.network.appJson
import com.zc.vndb.common.data.network.createHttpClient
import com.zc.vndb.common.domain.model.AppConfigSamples
import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.feature.release.data.remote.ReleaseService
import com.zc.vndb.feature.release.domain.model.ReleaseImageType
import com.zc.vndb.feature.release.domain.model.Voicing
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

class ReleaseRepositoryImplTest {

    private var sent: JsonElement? = null
    private var path: String? = null
    private var body = fixture()
    private var status = HttpStatusCode.OK

    private val repository = ReleaseRepositoryImpl(
        ReleaseService(
            createHttpClient(
                json = appJson,
                engine = MockEngine { request ->
                    path = request.url.encodedPath
                    sent = appJson.parseToJsonElement(request.body.toByteArray().decodeToString())
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
    fun `maps the release fixture`() = runTest {
        val release = repository.getRelease("r21541").getOrThrow()

        assertEquals("/kana/release", path)
        assertEquals(
            appJson.parseToJsonElement("""["id","=","r21541"]"""),
            sent?.jsonObject?.get("filters"),
        )
        assertEquals("Steins;Gate Platinum collection", release.title)
        assertEquals(Voicing.Full, release.voicing)
        assertEquals(
            listOf(ReleaseImageType.PackageFront, ReleaseImageType.PackageBack),
            release.images.map { it.type },
        )
        assertEquals(listOf("NITRO PLUS", "MAGES."), release.producers.map { it.name })
        assertEquals("v2002", release.novels.single().id)
        assertEquals(2, release.links.size)
    }

    @Test
    fun `an empty result is NotFound and HTTP failures become domain errors`() = runTest {
        body = """{"more":false,"results":[]}"""
        assertEquals(DomainError.NotFound, repository.getRelease("r0").exceptionOrNull())

        status = HttpStatusCode.TooManyRequests
        assertEquals(DomainError.RateLimited, repository.getRelease("r1").exceptionOrNull())
    }

    private companion object {
        fun fixture(): String = checkNotNull(
            ReleaseRepositoryImplTest::class.java.getResource("/release/release.json"),
        ).readText()
    }
}
