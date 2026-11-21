package com.zc.vndb.feature.profile.domain.repository

import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.feature.profile.domain.model.EntryOrder
import com.zc.vndb.feature.profile.domain.model.EntryPage
import com.zc.vndb.feature.profile.domain.model.ListEntry
import com.zc.vndb.feature.profile.domain.model.ListFilter
import com.zc.vndb.feature.profile.domain.model.ListLabel
import com.zc.vndb.feature.profile.domain.model.MyListEntry
import com.zc.vndb.feature.profile.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeProfileRepository : ProfileRepository {

    var user: UserProfile? = null
    var labels: List<ListLabel> = emptyList()
    val entries = mutableMapOf<Int, List<ListEntry>>()
    var labelsError: DomainError? = null
    var entriesError: DomainError? = null

    /** Label ids per novel on the user's list. */
    val entryLabels = mutableMapOf<String, List<Int>>()

    /** The user's own entries, by novel id (what the list editor reads and writes). */
    val myEntries = mutableMapOf<String, MyListEntry>()
    var myEntryError: DomainError? = null
    var writeError: DomainError? = null

    /** Every entry passed to [saveEntry], and every id passed to [removeEntry]. */
    val saved = mutableListOf<MyListEntry>()
    val removed = mutableListOf<String>()

    private val changes = MutableSharedFlow<String>(extraBufferCapacity = 8)
    override val listChanges: Flow<String> = changes

    override suspend fun getMyEntry(userId: String, novelId: String): Result<MyListEntry?> {
        calls += "myEntry:$novelId"
        myEntryError?.let { return Result.failure(it) }
        return Result.success(myEntries[novelId])
    }

    override suspend fun saveEntry(entry: MyListEntry, previous: MyListEntry?): Result<Unit> {
        writeError?.let { return Result.failure(it) }
        saved += entry
        myEntries[entry.novelId] = entry
        changes.tryEmit(entry.novelId)
        return Result.success(Unit)
    }

    override suspend fun removeEntry(novelId: String): Result<Unit> {
        writeError?.let { return Result.failure(it) }
        removed += novelId
        myEntries.remove(novelId)
        changes.tryEmit(novelId)
        return Result.success(Unit)
    }

    /** "labels", "user", "entries:<label ids>:<page>" (then ":<search>" when there is one). */
    val calls = mutableListOf<String>()

    data class EntriesRequest(
        val filter: ListFilter,
        val page: Int,
        val order: EntryOrder,
        val ascending: Boolean,
        val voted: Boolean?,
    )

    val requests = mutableListOf<EntriesRequest>()

    override suspend fun getUser(userId: String): Result<UserProfile> {
        calls += "user"
        return user?.let { Result.success(it) } ?: Result.failure(DomainError.NotFound)
    }

    override suspend fun getLabels(userId: String): Result<List<ListLabel>> {
        calls += "labels"
        return labelsError?.let { Result.failure(it) } ?: Result.success(labels)
    }

    override suspend fun getEntries(
        userId: String,
        filter: ListFilter,
        page: Int,
        pageSize: Int,
        order: EntryOrder,
        ascending: Boolean,
        voted: Boolean?,
    ): Result<EntryPage> {
        val search = filter.search.trim()
        calls += "entries:${filter.labelIds.sorted().joinToString(",")}:$page" +
            if (search.isEmpty()) "" else ":$search"
        requests += EntriesRequest(filter, page, order, ascending, voted)
        entriesError?.let { return Result.failure(it) }
        // Any of the selected labels (all entries when none is); good enough for the tests.
        val selected = if (filter.labelIds.isEmpty()) entries.keys else filter.labelIds
        val all = selected.flatMap { entries[it].orEmpty() }
            .distinctBy { it.novelId }
            .filter { search.isEmpty() || it.title.contains(search, ignoreCase = true) }
            .filter { voted == null || (it.vote != null) == voted }
            .let { list ->
                val ascendingList = when (order) {
                    EntryOrder.Title -> list.sortedBy { it.title }
                    EntryOrder.Updated -> list.sortedBy { it.lastModified }
                    EntryOrder.Vote -> list.sortedBy { it.vote ?: -1 }
                }
                if (ascending) ascendingList else ascendingList.reversed()
            }
        return Result.success(
            EntryPage(
                entries = all.drop((page - 1) * pageSize).take(pageSize),
                hasMore = page * pageSize < all.size,
            ),
        )
    }

    override suspend fun getEntryLabelIds(userId: String, novelId: String): Result<List<Int>> {
        calls += "entryLabels:$novelId"
        return Result.success(entryLabels[novelId].orEmpty())
    }
}
