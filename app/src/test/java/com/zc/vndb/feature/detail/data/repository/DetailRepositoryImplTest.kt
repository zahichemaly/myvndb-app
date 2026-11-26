package com.zc.vndb.feature.detail.data.repository

import com.zc.vndb.common.data.network.appJson
import com.zc.vndb.common.data.network.createHttpClient
import com.zc.vndb.common.domain.model.AppConfigSamples
import com.zc.vndb.common.domain.model.CharacterRole
import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.common.domain.model.NovelLength
import com.zc.vndb.common.domain.model.PlatformFamily
import com.zc.vndb.feature.detail.data.remote.DetailService
import com.zc.vndb.feature.detail.domain.model.RelationType
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

class DetailRepositoryImplTest {

    private val bodies = mutableMapOf<String, JsonElement?>()
    private var status = HttpStatusCode.OK
    private var novelBody = fixture("novel.json")

    private val repository = DetailRepositoryImpl(
        DetailService(
            createHttpClient(
                json = appJson,
                engine = MockEngine { request ->
                    val path = request.url.encodedPath
                    bodies[path] = appJson.parseToJsonElement(
                        request.body.toByteArray().decodeToString(),
                    )
                    val body = when (path) {
                        "/kana/vn" -> novelBody
                        "/kana/character" -> fixture("characters.json")
                        else -> fixture("releases.json")
                    }
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
    fun `maps the novel fixture`() = runTest {
        val novel = repository.getNovel("v17102").getOrThrow()

        assertEquals(
            appJson.parseToJsonElement("""["id","=","v17102"]"""),
            bodies["/kana/vn"]?.jsonObject?.get("filters"),
        )
        assertEquals("STEINS;GATE 0", novel.title)
        assertEquals(NovelLength.Long, novel.length)
        assertEquals(RelationType.ParentStory, novel.relations.single().relation)
        assertEquals("v2002", novel.relations.single().id)
        assertEquals(listOf("ja", "en"), novel.titles.map { it.languageCode })
        assertEquals(listOf("SG0"), novel.aliases)
        assertEquals(412, novel.lengthVotes)
        assertTrue(novel.links.isNotEmpty())
    }

    @Test
    fun `an empty result is NotFound`() = runTest {
        novelBody = """{"more":false,"results":[]}"""

        assertEquals(DomainError.NotFound, repository.getNovel("v0").exceptionOrNull())
    }

    @Test
    fun `characters keep their role in this novel`() = runTest {
        val characters = repository.getCharacters("v17102").getOrThrow()

        assertEquals(
            appJson.parseToJsonElement("""["vn","=",["id","=","v17102"]]"""),
            bodies["/kana/character"]?.jsonObject?.get("filters"),
        )
        assertEquals(5, characters.size)
        assertEquals(CharacterRole.Primary, characters.single { it.id == "c6493" }.role)
        assertEquals(CharacterRole.Appears, characters.single { it.id == "c6487" }.role)
    }

    @Test
    fun `releases become one edition per platform`() = runTest {
        val editions = repository.getEditions("v17102").getOrThrow()

        assertEquals(6, editions.size)
        assertEquals(listOf("ps4", "ps3", "psv"), editions.take(3).map { it.platform })
        assertTrue(editions.all { it.family == PlatformFamily.Console && it.minAge == 15 })
    }

    @Test
    fun `HTTP failures become domain errors`() = runTest {
        status = HttpStatusCode.TooManyRequests

        assertEquals(DomainError.RateLimited, repository.getCharacters("v1").exceptionOrNull())
    }

    private companion object {
        fun fixture(name: String): String = checkNotNull(
            DetailRepositoryImplTest::class.java.getResource("/detail/$name"),
        ).readText()
    }
}
