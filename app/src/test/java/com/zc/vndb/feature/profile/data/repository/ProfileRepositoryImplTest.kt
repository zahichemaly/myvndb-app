package com.zc.vndb.feature.profile.data.repository

import com.zc.vndb.common.data.local.SessionLocalDataSource
import com.zc.vndb.common.data.local.StoredSession
import com.zc.vndb.common.data.local.sessionStore
import com.zc.vndb.common.data.network.appJson
import com.zc.vndb.common.data.network.createHttpClient
import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.feature.profile.data.remote.ProfileService
import com.zc.vndb.feature.profile.domain.model.ListFilter
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.jsonObject
import org.junit.Rule
import org.junit.rules.TemporaryFolder

class ProfileRepositoryImplTest {

    @get:Rule
    val folder = TemporaryFolder()

    private val requests = mutableListOf<Pair<String, String?>>()
    private val bodies = mutableMapOf<String, String>()
    private var status = HttpStatusCode.OK

    private suspend fun TestScope.setUp(): Pair<ProfileRepositoryImpl, SessionLocalDataSource> {
        val local = sessionStore(File(folder.root, "session.preferences_pb"))
        local.save(StoredSession(TOKEN, "u3", "ayo", setOf("listread")))
        val client = createHttpClient(
            json = appJson,
            engine = MockEngine { request ->
                val path = request.url.encodedPath.removePrefix("/kana/")
                requests += path to request.headers[HttpHeaders.Authorization]
                if (path == "ulist") bodies[path] = request.body.toByteArray().decodeToString()
                if (status == HttpStatusCode.OK) {
                    respond(
                        RESPONSES.getValue(path),
                        status,
                        headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                } else {
                    // VNDB's real error response: a malformed "text" content type.
                    respond("Invalid token.", status, headersOf(HttpHeaders.ContentType, "text"))
                }
            },
        )
        return ProfileRepositoryImpl(ProfileService(client), local) to local
    }

    @Test
    fun `list requests carry the token and map labels and entries`() = runTest {
        val (profile, _) = setUp()

        val listLabels = profile.getLabels("u3").getOrThrow()
        val page = profile.getEntries("u3", ListFilter(setOf(1)), page = 2, pageSize = 25)
            .getOrThrow()

        assertEquals(listOf(1, 7, 10), listLabels.map { it.id })
        assertTrue(listLabels.single { it.id == 10 }.isPrivate)
        assertTrue(requests.all { it.second == "Token $TOKEN" })
        val query = appJson.parseToJsonElement(bodies.getValue("ulist")).jsonObject
        assertEquals("\"u3\"", query["user"].toString())
        assertEquals("[\"label\",\"=\",1]", query["filters"].toString())
        assertEquals("2", query["page"].toString())
        with(page.entries.single()) {
            assertEquals("v58641", novelId)
            assertEquals(85, vote)
            assertEquals("2026-01-07", started.toString())
            assertNull(finished)
            assertNull(notes)
            assertEquals(listOf(1, 7), labels.map { it.id })
        }
        assertTrue(page.hasMore)
    }

    @Test
    fun `a token rejected later signs the user out`() = runTest {
        val (profile, local) = setUp()
        status = HttpStatusCode.Unauthorized

        assertEquals(DomainError.Unauthorized, profile.getLabels("u3").exceptionOrNull())
        assertNull(local.token())
    }

    @Test
    fun `user lookup reads play-time votes without the token`() = runTest {
        val (profile, _) = setUp()

        val user = profile.getUser("u3").getOrThrow()

        assertEquals(9, user.lengthVotes)
        assertEquals(9685, user.lengthVotesMinutes)
        assertNull(requests.single().second)
    }

    private companion object {
        const val TOKEN = "hsoo-ybws4-j8yb9-qxkw-5obay-px8to-bfyk"
        val RESPONSES = mapOf(
            "user" to
                """{"u3":{"id":"u3","username":"ayo","lengthvotes":9,"lengthvotes_sum":9685}}""",
            "ulist_labels" to """{"labels":[
                {"id":1,"label":"Playing","private":false,"count":1},
                {"id":7,"label":"Voted","private":false,"count":5},
                {"id":10,"label":"Secret","private":true,"count":2}]}""",
            "ulist" to """{"more":true,"results":[{"id":"v58641","vote":85,"lastmod":1767815893,
                "started":"2026-01-07","finished":null,"notes":"","labels":[{"id":1,"label":"Playing"},
                {"id":7,"label":"Voted"}],"vn":{"title":"Z.A.T.O.","alttitle":null,"released":"2025-11-11",
                "image":{"url":"https://t.vndb.org/cv/81/119181.jpg","thumbnail":"https://t.vndb.org/cv.t/81/119181.jpg",
                "sexual":0,"violence":0}}}]}""",
        )
    }
}
