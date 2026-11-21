package com.zc.vndb.feature.profile.presentation.mapper

import com.zc.vndb.common.domain.model.ListLabel
import com.zc.vndb.common.domain.model.ListSort
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.profile.presentation.model.LabelChipUIModel
import com.zc.vndb.feature.profile.presentation.model.SortOptionUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/** The labels as chips, in their order, the selected ones marked. */
fun List<ListLabel>.toChips(selectedIds: Set<Int>): ImmutableList<LabelChipUIModel> = map { label ->
    LabelChipUIModel(
        id = label.id,
        label = UiText.StringResource(
            StringResource.profile_label_with_count,
            listOf(label.name, "%,d".format(label.count)),
        ),
        isSelected = label.id in selectedIds,
    )
}.toImmutableList()

/**
 * The selected labels that still exist, in the order they were picked; before anything was picked
 * ([current] null), Playing, else the first label. Nothing selected shows the whole list.
 */
fun List<ListLabel>.pickSelected(current: Set<Int>?): Set<Int> = if (current == null) {
    setOfNotNull(firstOrNull { it.id == ListLabel.PLAYING }?.id ?: firstOrNull()?.id)
} else {
    current.filterTo(linkedSetOf()) { id -> any { it.id == id } }
}

/** The sort menu, with [this] selected. */
fun ListSort.toSortOptions(): ImmutableList<SortOptionUIModel> = ListSort.entries.map { sort ->
    SortOptionUIModel(
        sort = sort,
        label = UiText.StringResource(
            when (sort) {
                ListSort.Title -> StringResource.profile_sort_title
                ListSort.Updated -> StringResource.profile_sort_updated
                ListSort.Vote -> StringResource.profile_sort_vote
            },
        ),
        isSelected = sort == this,
    )
}.toImmutableList()
