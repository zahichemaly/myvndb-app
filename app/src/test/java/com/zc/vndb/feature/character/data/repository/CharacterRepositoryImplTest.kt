package com.zc.vndb.feature.character.data.repository

import com.zc.vndb.common.data.network.appJson
import com.zc.vndb.common.data.network.createHttpClient
import com.zc.vndb.common.domain.model.AppConfigSamples
import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.common.domain.model.VoiceActor
import com.zc.vndb.feature.character.data.remote.CharacterService
import com.zc.vndb.feature.character.domain.model.Birthday
import com.zc.vndb.feature.character.domain.model.Sex
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

class CharacterRepositoryImplTest {

    private val bodies = mutableMapOf<String, JsonElement>()
    private var characterBody = fixture("character.json")
    private var status = HttpStatusCode.OK

    private val repository = CharacterRepositoryImpl(
        CharacterService(
            createHttpClient(
                json = appJson,
                engine = MockEngine { request ->
                    val path = request.url.encodedPath
                    bodies[path] = appJson.parseToJsonElement(
                        request.body.toByteArray().decodeToString(),
                    )
                    val body = if (path == "/kana/character") {
                        characterBody
                    } else {
                        fixture(
                            "voices.json",
                        )
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
    fun `maps the character fixture`() = runTest {
        val profile = repository.getCharacter("c6487").getOrThrow()

        assertEquals(
            appJson.parseToJsonElement("""["id","=","c6487"]"""),
            bodies["/kana/character"]?.jsonObject?.get("filters"),
        )
        assertEquals("Makise Kurisu", profile.name)
        assertEquals(Sex.Female, profile.sex)
        assertEquals(Birthday(7, 25), profile.birthday)
        assertEquals(160, profile.heightCm)
        assertEquals(8, profile.traits.size)
        assertEquals(
            listOf("v2002", "v5883", "v6618", "v9887"),
            profile.appearances.map { it.novelId },
        )
    }

    @Test
    fun `an empty result is NotFound`() = runTest {
        characterBody = """{"more":false,"results":[]}"""

        assertEquals(DomainError.NotFound, repository.getCharacter("c0").exceptionOrNull())
    }

    @Test
    fun `voice actors are matched to this character per VN`() = runTest {
        val actors = repository.getVoiceActors("c6487").getOrThrow()

        assertEquals(
            appJson.parseToJsonElement("""["character","=",["id","=","c6487"]]"""),
            bodies["/kana/vn"]?.jsonObject?.get("filters"),
        )
        assertEquals(
            mapOf(
                "v2002" to VoiceActor("s240", "Imai Asami"),
                "v6618" to VoiceActor("s240", "Imai Asami"),
            ),
            actors,
        )
    }

    @Test
    fun `HTTP failures become domain errors`() = runTest {
        status = HttpStatusCode.TooManyRequests

        assertEquals(DomainError.RateLimited, repository.getCharacter("c6487").exceptionOrNull())
    }

    private companion object {
        fun fixture(name: String): String = checkNotNull(
            CharacterRepositoryImplTest::class.java.getResource("/character/$name"),
        ).readText()
    }
}
