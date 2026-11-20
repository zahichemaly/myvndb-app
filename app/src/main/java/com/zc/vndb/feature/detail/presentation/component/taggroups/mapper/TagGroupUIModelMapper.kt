package com.zc.vndb.feature.detail.presentation.component.taggroups.mapper

import com.zc.vndb.common.domain.model.NovelTag
import com.zc.vndb.common.domain.model.TagCategory
import com.zc.vndb.common.ui.component.spoilerfilter.model.SpoilerLevel
import com.zc.vndb.common.ui.resource.PluralResource
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.detail.domain.model.NovelDetail
import com.zc.vndb.feature.detail.presentation.component.taggroups.model.ContentNoteTone
import com.zc.vndb.feature.detail.presentation.component.taggroups.model.ContentNoteUIModel
import com.zc.vndb.feature.detail.presentation.component.taggroups.model.TagGroupUIModel
import com.zc.vndb.feature.detail.presentation.model.TagChipUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

private const val MAX_THEME_TAGS = 12
private const val MAX_TECHNICAL_TAGS = 6
private const val VIOLENT_COVER = 1.0

/**
 * Themes and technical tags allowed by [level], most relevant first; sexual tags too when
 * [showSexual] (Settings → Show sensitive content). Empty groups are dropped.
 */
fun NovelDetail.toTagGroups(
    level: SpoilerLevel,
    showSexual: Boolean = false,
): ImmutableList<TagGroupUIModel> = listOfNotNull(
    tags.group(
        TagCategory.Content,
        level,
        MAX_THEME_TAGS,
    )?.let { TagGroupUIModel(UiText.StringResource(StringResource.detail_tags_themes), it) },
    tags.group(TagCategory.Technical, level, MAX_TECHNICAL_TAGS)?.let {
        TagGroupUIModel(UiText.StringResource(StringResource.detail_tags_technical), it)
    },
    if (showSexual) {
        tags.group(TagCategory.Sexual, level, MAX_THEME_TAGS)?.let {
            TagGroupUIModel(UiText.StringResource(StringResource.detail_tags_sexual), it)
        }
    } else {
        null
    },
).toImmutableList()

/** How many tags the groups would show at the highest spoiler level but not at [level]; null when none. */
fun NovelDetail.toHiddenTagsLabel(level: SpoilerLevel, showSexual: Boolean = false): UiText? {
    val shown = toTagGroups(level, showSexual).flatMap { it.tags }.toSet()
    val hidden = toTagGroups(
        SpoilerLevel.Major,
        showSexual,
    ).flatMap { it.tags }.count { it !in shown }
    return if (hidden == 0) {
        null
    } else {
        UiText.StringPlural(
            PluralResource.detail_hidden_by_spoiler,
            hidden,
            listOf(hidden),
        )
    }
}

/** Sexual content from the tags (the tags themselves are not listed) and violent cover art. */
fun NovelDetail.toContentNotes(): ImmutableList<ContentNoteUIModel> = listOfNotNull(
    if (tags.any { it.category == TagCategory.Sexual }) {
        ContentNoteUIModel(
            UiText.StringResource(StringResource.detail_note_sexual),
            ContentNoteTone.Adult,
        )
    } else {
        ContentNoteUIModel(
            UiText.StringResource(StringResource.detail_note_no_sexual),
            ContentNoteTone.Safe,
        )
    },
    if ((cover?.violence ?: 0.0) >= VIOLENT_COVER) {
        ContentNoteUIModel(
            UiText.StringResource(StringResource.detail_note_violent_cover),
            ContentNoteTone.Caution,
        )
    } else {
        null
    },
).toImmutableList()

private fun List<NovelTag>.group(
    category: TagCategory,
    level: SpoilerLevel,
    max: Int,
): ImmutableList<TagChipUIModel>? = filter {
    it.category == category &&
        it.spoilerLevel <= level.maxLevel
}
    .sortedByDescending { it.rating }
    .take(max)
    .map { TagChipUIModel(id = it.id, label = UiText.StringValue(it.name)) }
    .takeIf { it.isNotEmpty() }
    ?.toImmutableList()
