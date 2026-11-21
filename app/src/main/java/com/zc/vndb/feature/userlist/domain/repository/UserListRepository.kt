package com.zc.vndb.feature.userlist.domain.repository

import com.zc.vndb.common.domain.model.ListLabel
import com.zc.vndb.feature.userlist.domain.model.EntryOrder
import com.zc.vndb.feature.userlist.domain.model.EntryPage
import com.zc.vndb.feature.userlist.domain.model.ListFilter
import com.zc.vndb.feature.userlist.domain.model.MyListEntry
import kotlinx.coroutines.flow.Flow

/**
 * A user's visual novel list (VNDB's `ulist` and `ulist_labels`). Requests carry the stored token,
 * so private labels show; a rejected token (`DomainError.Unauthorized`) also signs the user out.
 */
interface UserListRepository {

    /** Ids of the visual novels whose entry was saved or removed through this repository. */
    val listChanges: Flow<String>

    suspend fun getLabels(userId: String): Result<List<ListLabel>>

    /** [userId]'s own entry for [novelId], or null when it is not on their list. */
    suspend fun getMyEntry(userId: String, novelId: String): Result<MyListEntry?>

    /**
     * Adds [entry] to the signed-in user's list, or updates the existing entry. Only what differs
     * from [previous] (the entry as loaded; null when it was not on the list) is sent. Needs the
     * `listwrite` permission.
     */
    suspend fun saveEntry(entry: MyListEntry, previous: MyListEntry?): Result<Unit>

    /** Removes [novelId] (and its releases) from the signed-in user's list. */
    suspend fun removeEntry(novelId: String): Result<Unit>

    /** Label ids [userId] gave [novelId]; empty when it is not on their list. */
    suspend fun getEntryLabelIds(userId: String, novelId: String): Result<List<Int>>

    /**
     * One page (1-based) of the entries matching [filter] in [order], [ascending] or not. [voted]
     * keeps only the entries the user voted on (true) or did not (false); null keeps all.
     */
    suspend fun getEntries(
        userId: String,
        filter: ListFilter,
        page: Int,
        pageSize: Int,
        order: EntryOrder = EntryOrder.Updated,
        ascending: Boolean = false,
        voted: Boolean? = null,
    ): Result<EntryPage>
}
