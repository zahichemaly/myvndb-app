package com.zc.vndb.feature.profile.data.remote

import com.zc.vndb.feature.profile.data.dto.UserDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.koin.core.annotation.Single

/** Public user endpoints; no token is sent. */
@Single
class ProfileService(private val client: HttpClient) {

    suspend fun user(userId: String): Map<String, UserDTO?> = client.get("user") {
        parameter("q", userId)
        parameter("fields", "lengthvotes,lengthvotes_sum")
    }.body()
}
