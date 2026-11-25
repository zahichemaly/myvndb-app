package com.zc.vndb.feature.userlist.data.repository

import com.zc.vndb.common.data.local.SessionLocalDataSource
import com.zc.vndb.common.data.util.safeApiCall
import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.common.domain.model.ListLabel
import com.zc.vndb.feature.userlist.data.mapper.entriesQuery
import com.zc.vndb.feature.userlist.data.mapper.entryLabelsQuery
import com.zc.vndb.feature.userlist.data.mapper.myEntryQuery
import com.zc.vndb.feature.userlist.data.mapper.toDomain
import com.zc.vndb.feature.userlist.data.mapper.toPatchBody
import com.zc.vndb.feature.userlist.data.remote.UserListService
import com.zc.vndb.feature.userlist.domain.model.EntryOrder
import com.zc.vndb.feature.userlist.domain.model.EntryPage
import com.zc.vndb.feature.userlist.domain.model.ListFilter
import com.zc.vndb.feature.userlist.domain.model.MyListEntry
import com.zc.vndb.feature.userlist.domain.repository.UserListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.annotation.Single

@Single
class UserListRepositoryImpl(
    private val service: UserListService,
    private val local: SessionLocalDataSource,
) : UserListRepository {

    private val changes = MutableSharedFlow<String>(extraBufferCapacity = CHANGES_BUFFER)
    override val listChanges: Flow<String> = changes.asSharedFlow()

    override suspend fun getLabels(userId: String): Result<List<ListLabel>> = authorized { token ->
        service.labels(userId, token).labels.map { it.toDomain() }
    }

    override suspend fun getEntries(
        userId: String,
        filter: ListFilter,
        page: Int,
        pageSize: Int,
        order: EntryOrder,
        ascending: Boolean,
        voted: Boolean?,
    ): Result<EntryPage> = authorized { token ->
        val result = service.entries(
            entriesQuery(userId, filter, page, pageSize, order, ascending, voted),
            token,
        )
        EntryPage(entries = result.results.map { it.toDomain() }, hasMore = result.more)
    }

    override suspend fun getMyEntry(userId: String, novelId: String): Result<MyListEntry?> =
        authorized { token ->
            service.myEntry(myEntryQuery(userId, novelId), token).results.firstOrNull()?.toDomain()
        }

    override suspend fun saveEntry(entry: MyListEntry, previous: MyListEntry?): Result<Unit> =
        authorized { token ->
            service.updateEntry(entry.novelId, entry.toPatchBody(previous), token)
        }.onSuccess { changes.tryEmit(entry.novelId) }

    override suspend fun removeEntry(novelId: String): Result<Unit> = authorized { token ->
        service.removeEntry(novelId, token)
    }.onSuccess { changes.tryEmit(novelId) }

    override suspend fun getEntryLabelIds(userId: String, novelId: String): Result<List<Int>> =
        authorized { token ->
            service.entryLabels(entryLabelsQuery(userId, novelId), token).results
                .firstOrNull()?.labels.orEmpty().map { it.id }
        }

    /** Runs [block] with the stored token; a rejected token signs the user out. */
    private suspend fun <T> authorized(block: suspend (token: String?) -> T): Result<T> {
        val token = local.token()
        return safeApiCall { block(token) }.onFailure { error ->
            if (error == DomainError.Unauthorized && token != null) local.clear()
        }
    }

    private companion object {
        const val CHANGES_BUFFER = 8
    }
}
