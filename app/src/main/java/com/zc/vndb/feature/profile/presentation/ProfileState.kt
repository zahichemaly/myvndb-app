package com.zc.vndb.feature.profile.presentation

import androidx.compose.runtime.Immutable
import com.zc.vndb.common.ui.component.error.model.ErrorUIModel
import com.zc.vndb.feature.profile.presentation.component.profileheader.model.ProfileHeaderUIModel
import com.zc.vndb.feature.profile.presentation.model.EntriesUIState
import com.zc.vndb.feature.profile.presentation.model.LabelChipUIModel
import com.zc.vndb.feature.profile.presentation.model.SortOptionUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class ProfileState(
    /** False until the stored session has been read. */
    val isSessionKnown: Boolean = false,
    /** Null for a guest. */
    val header: ProfileHeaderUIModel? = null,
    /** Labels and profile in flight. */
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    /** Labels failed to load. */
    val error: ErrorUIModel? = null,
    /** VNDB's statuses, then the user's labels, then Voted and No label. */
    val labels: ImmutableList<LabelChipUIModel> = persistentListOf(),
    /** Several chips can be on: any of them within a row, and both rows together. */
    val isMultiSelect: Boolean = false,
    /** What is typed in the search field; it is searched once typing pauses. */
    val query: String = "",
    /** Title / Recently updated / My vote, the chosen one selected. */
    val sortOptions: ImmutableList<SortOptionUIModel> = persistentListOf(),
    /** A to Z / oldest / lowest vote first (the direction button shows an up arrow). */
    val isSortAscending: Boolean = true,
    val entries: EntriesUIState = EntriesUIState(),
    /** The token has `listwrite`: entry cards show an edit button. */
    val canEditList: Boolean = false,
) {
    val isSignedIn: Boolean get() = header != null

    /** Signed in, labels loaded, and none has entries. */
    val isListEmpty: Boolean
        get() = isSignedIn && !isLoading && error == null && labels.isEmpty()
}
