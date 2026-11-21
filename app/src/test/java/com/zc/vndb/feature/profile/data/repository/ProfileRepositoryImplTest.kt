package com.zc.vndb.feature.profile.data.repository

import com.zc.vndb.common.data.network.appJson
import com.zc.vndb.common.data.network.createHttpClient
import com.zc.vndb.feature.profile.data.remote.ProfileService
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest

class ProfileRepositoryImplTest {

    private val requests = mutableListOf<Pair<String, String?>>()

    private fun repository(): ProfileRepositoryImpl {
        val client = createHttpClient(
            json = appJson,
            engine = MockEngine { request ->
                requests += request.url.encodedPath.removePrefix("/kana/") to
                    request.headers[HttpHeaders.Authorization]
                respond(
                    USER_RESPONSE,
                    HttpStatusCode.OK,
                    headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            },
        )
        return ProfileRepositoryImpl(ProfileService(client))
    }

    @Test
    fun `user lookup reads play-time votes without the token`() = runTest {
        val user = repository().getUser("u3").getOrThrow()

        assertEquals(9, user.lengthVotes)
        assertEquals(9685, user.lengthVotesMinutes)
        assertEquals("user", requests.single().first)
        assertNull(requests.single().second)
    }

    private companion object {
        const val USER_RESPONSE =
            """{"u3":{"id":"u3","username":"ayo","lengthvotes":9,"lengthvotes_sum":9685}}"""
    }
}
