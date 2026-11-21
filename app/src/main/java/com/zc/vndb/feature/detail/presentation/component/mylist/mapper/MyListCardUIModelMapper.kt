package com.zc.vndb.feature.detail.presentation.component.mylist.mapper

import com.zc.vndb.common.ui.component.entrylabel.EntryLabelUIModel
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.detail.presentation.component.mylist.model.MyListCardUIModel
import com.zc.vndb.feature.userlist.domain.model.MyListEntry
import kotlinx.collections.immutable.toImmutableList

private const val VOTE_SCALE = 10.0

/** [this] is the user's entry, or null when the VN is not on their list. */
fun MyListEntry?.toMyListCard(canEdit: Boolean): MyListCardUIModel {
    if (this == null) return MyListCardUIModel(isOnList = false, canEdit = canEdit)
    val status = status
    val ordered = listOfNotNull(status) + labelIds.filter { it != status }.sorted()
    return MyListCardUIModel(
        isOnList = true,
        canEdit = canEdit,
        labels = ordered.mapNotNull { id ->
            labelNames[id]?.let { EntryLabelUIModel(id, UiText.StringValue(it)) }
        }.toImmutableList(),
        voteLabel = vote?.let {
            UiText.StringResource(
                StringResource.detail_score_value,
                listOf("%.1f".format(it / VOTE_SCALE)),
            )
        },
        dates = listOfNotNull(
            started?.let {
                UiText.StringResource(
                    StringResource.profile_started,
                    listOf(it.toString()),
                )
            },
            finished?.let {
                UiText.StringResource(
                    StringResource.profile_finished,
                    listOf(it.toString()),
                )
            },
        ).toImmutableList(),
        notes = notes?.let(UiText::StringValue),
    )
}
