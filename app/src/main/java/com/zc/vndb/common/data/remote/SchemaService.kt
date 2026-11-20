package com.zc.vndb.common.data.remote

import com.zc.vndb.common.data.dto.SchemaDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.koin.core.annotation.Single

/** VNDB's `GET /schema`: the languages, platforms and other values its filters accept. */
@Single
class SchemaService(private val client: HttpClient) {
    suspend fun getSchema(): SchemaDTO = client.get("schema").body()
}
