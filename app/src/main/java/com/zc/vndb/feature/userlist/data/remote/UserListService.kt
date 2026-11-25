package com.zc.vndb.feature.userlist.data.remote

import com.zc.vndb.feature.userlist.data.dto.ListEntryLabelsPageDTO
import com.zc.vndb.feature.userlist.data.dto.ListEntryPageDTO
import com.zc.vndb.feature.userlist.data.dto.ListLabelsDTO
import com.zc.vndb.feature.userlist.data.dto.ListQueryDTO
import com.zc.vndb.feature.userlist.data.dto.MyListEntryPageDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.JsonObject
import org.koin.core.annotation.Single

/** List endpoints. A [token] is sent as `Authorization: Token …`, so private data is included. */
@Single
class UserListService(private val client: HttpClient) {

    suspend fun labels(userId: String, token: String?): ListLabelsDTO = client.get("ulist_labels") {
        parameter("user", userId)
        parameter("fields", "count")
        authorize(token)
    }.body()

    suspend fun entries(query: ListQueryDTO, token: String?): ListEntryPageDTO = client.post(
        "ulist",
    ) {
        authorize(token)
        setBody(query)
    }.body()

    suspend fun entryLabels(query: ListQueryDTO, token: String?): ListEntryLabelsPageDTO =
        client.post(
            "ulist",
        ) {
            authorize(token)
            setBody(query)
        }.body()

    suspend fun myEntry(query: ListQueryDTO, token: String?): MyListEntryPageDTO = client.post(
        "ulist",
    ) {
        authorize(token)
        setBody(query)
    }.body()

    /** `PATCH /ulist/<id>`: adds the VN to the list if needed. Answers 204 without a body. */
    suspend fun updateEntry(novelId: String, body: JsonObject, token: String?) {
        client.patch("ulist/$novelId") {
            authorize(token)
            setBody(body)
        }
    }

    /** `DELETE /ulist/<id>`: succeeds even when the VN is not on the list. */
    suspend fun removeEntry(novelId: String, token: String?) {
        client.delete("ulist/$novelId") { authorize(token) }
    }

    private fun HttpRequestBuilder.authorize(token: String?) {
        token?.let { header(HttpHeaders.Authorization, "Token $it") }
    }
}
