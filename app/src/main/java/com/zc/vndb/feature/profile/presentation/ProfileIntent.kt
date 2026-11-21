package com.zc.vndb.feature.profile.presentation

import com.zc.vndb.common.domain.model.ListSort

sealed interface ProfileIntent {
    data object LogInClicked : ProfileIntent
    data object SettingsClicked : ProfileIntent
    data object Retry : ProfileIntent

    /** Pull to refresh: labels, profile and the selected label's first page. */
    data object Refresh : ProfileIntent

    /** Single-select: that chip alone, or none when it was the one on. Multi-select: toggles it. */
    data class LabelSelected(val id: Int) : ProfileIntent

    /** Switches between single- and multi-select; back to single keeps the chip picked last. */
    data object MultiSelectToggled : ProfileIntent
    data class QueryChanged(val query: String) : ProfileIntent

    /** The keyboard's search key: searches now instead of after the pause. */
    data object SearchSubmitted : ProfileIntent
    data object QueryCleared : ProfileIntent
    data class SortSelected(val sort: ListSort) : ProfileIntent

    /** Flips the current sort between ascending and descending. */
    data object SortDirectionToggled : ProfileIntent
    data object EndReached : ProfileIntent
    data object LoadMoreRetried : ProfileIntent
    data object EntriesRetried : ProfileIntent
    data class EntryClicked(val novelId: String) : ProfileIntent

    /** The edit button on an entry card: opens the list editor. */
    data class EntryEditClicked(val novelId: String) : ProfileIntent
}
