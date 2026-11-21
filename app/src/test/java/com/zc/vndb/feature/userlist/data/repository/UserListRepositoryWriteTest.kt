package com.zc.vndb.feature.userlist.data.repository

import com.zc.vndb.common.data.local.StoredSession
import com.zc.vndb.common.data.local.sessionStore
import com.zc.vndb.common.data.network.appJson
import com.zc.vndb.common.data.network.createHttpClient
import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.common.domain.model.ListLabel
import com.zc.vndb.feature.userlist.data.remote.UserListService
import com.zc.vndb.feature.userlist.domain.model.MyListEntry
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Rule
import org.junit.rules.TemporaryFolder

class UserListRepositoryWriteTest {

    @get:Rule
    val folder = TemporaryFolder()

    private data class Call(
        val method: String,
        val path: String,
        val auth: String?,
        val body: String,
    )

    private val calls = mutableListOf<Call>()
    private var status = HttpStatusCode.NoContent
    private var ulistResponse = """{"results":[],"more":false}"""

    private suspend fun TestScope.repository(): UserListRepositoryImpl {
        val local = sessionStore(File(folder.root, "session.preferences_pb"))
        local.save(StoredSession(TOKEN, "u3", "ayo", setOf("listread", "listwrite")))
        val client = createHttpClient(
            json = appJson,
            engine = MockEngine { request ->
                calls += Call(
                    method = request.method.value,
                    path = request.url.encodedPath.removePrefix("/kana/"),
                    auth = request.headers[HttpHeaders.Authorization],
                    body = request.body.toByteArray().decodeToString(),
                )
                when {
                    request.method.value == "POST" -> respond(
                        ulistResponse,
                        HttpStatusCode.OK,
                        headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )

                    status == HttpStatusCode.NoContent -> respond("", status)

                    else -> respond(
                        "Invalid token.",
                        status,
                        headersOf(HttpHeaders.ContentType, "text"),
                    )
                }
            },
        )
        return UserListRepositoryImpl(UserListService(client), local)
    }

    @Test
    fun `saving patches every field with the token and announces the change`() = runTest {
        val repository = repository()
        val changed = mutableListOf<String>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.listChanges.collect { changed += it }
        }

        val result = repository.saveEntry(
            MyListEntry(
                novelId = "v17",
                labelIds = setOf(ListLabel.FINISHED, ListLabel.WISHLIST),
                vote = 85,
                started = LocalDate(2026, 11, 1),
                notes = "Good",
            ),
            previous = null,
        )

        assertTrue(result.isSuccess)
        val call = calls.single()
        assertEquals("PATCH", call.method)
        assertEquals("ulist/v17", call.path)
        assertEquals("Token $TOKEN", call.auth)
        val body = Json.parseToJsonElement(call.body).jsonObject
        assertEquals(
            listOf(2, 5),
            body.getValue("labels").jsonArray.map { it.jsonPrimitive.content.toInt() },
        )
        assertEquals("85", body.getValue("vote").jsonPrimitive.content)
        assertEquals("2026-11-01", body.getValue("started").jsonPrimitive.content)
        assertTrue("finished" !in body, "a new entry sends only the fields that are set")
        assertEquals("Good", body.getValue("notes").jsonPrimitive.content)
        assertEquals(listOf("v17"), changed)
    }

    @Test
    fun `an edit sends only what changed, with null for cleared fields`() = runTest {
        val repository = repository()
        val previous = MyListEntry(
            novelId = "v17",
            labelIds = setOf(ListLabel.PLAYING),
            vote = 70,
            started = LocalDate(2026, 1, 1),
            notes = "Old",
        )
        repository.saveEntry(previous.copy(vote = null, notes = "  "), previous)

        val body: JsonObject = Json.parseToJsonElement(calls.single().body).jsonObject
        assertEquals(setOf("vote", "notes"), body.keys)
        assertEquals(JsonNull, body.getValue("vote"))
        assertEquals("", body.getValue("notes").jsonPrimitive.content)
    }

    @Test
    fun `removing deletes the entry`() = runTest {
        val repository = repository()

        assertTrue(repository.removeEntry("v17").isSuccess)
        assertEquals(Call("DELETE", "ulist/v17", "Token $TOKEN", ""), calls.single())
    }

    @Test
    fun `a rejected write carries VNDB's reason and announces nothing`() = runTest {
        val repository = repository()
        val changed = mutableListOf<String>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.listChanges.collect { changed += it }
        }
        status = HttpStatusCode.BadRequest

        val result = repository.removeEntry("v17")

        assertEquals(DomainError.Rejected("Invalid token."), result.exceptionOrNull())
        assertTrue(changed.isEmpty())
    }

    @Test
    fun `my entry is read with its labels, and absent when not on the list`() = runTest {
        val repository = repository()
        assertNull(repository.getMyEntry("u3", "v17").getOrThrow())

        ulistResponse = """{"results":[{"id":"v17","vote":70,"started":"2026-01-02","finished":null,
            |"notes":"","labels":[{"id":1,"label":"Playing"},{"id":7,"label":"Voted"}]}],"more":false}
        """.trimMargin()
        val entry = repository.getMyEntry("u3", "v17").getOrThrow()

        assertEquals(
            MyListEntry(
                novelId = "v17",
                labelIds = setOf(ListLabel.PLAYING),
                vote = 70,
                started = LocalDate(2026, 1, 2),
                labelNames = mapOf(ListLabel.PLAYING to "Playing", ListLabel.VOTED to "Voted"),
            ),
            entry,
        )
        val query = Json.parseToJsonElement(calls.last().body).jsonObject
        assertEquals("u3", query.getValue("user").jsonPrimitive.content)
    }

    private companion object {
        const val TOKEN = "test-token"
    }

    @Test
    fun `dates are only sent when set and changed`() = runTest {
        val repository = repository()
        val previous = MyListEntry(novelId = "v17", started = LocalDate(2026, 1, 1))
        repository.saveEntry(previous.copy(finished = LocalDate(2026, 2, 1)), previous)

        val body: JsonObject = Json.parseToJsonElement(calls.single().body).jsonObject
        assertEquals(setOf("finished"), body.keys)
        assertEquals("2026-02-01", body.getValue("finished").jsonPrimitive.content)
    }
}
