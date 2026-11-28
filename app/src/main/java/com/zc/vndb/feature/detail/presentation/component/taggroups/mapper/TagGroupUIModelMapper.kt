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
 * [showSexual] (Settings → Show sensitive content). Each group is capped to its most relevant tags
 * unless [showAll]. Empty groups are dropped.
 */
fun NovelDetail.toTagGroups(
    level: SpoilerLevel,
    showSexual: Boolean = false,
    showAll: Boolean = false,
): ImmutableList<TagGroupUIModel> = listOfNotNull(
    tags.group(
        TagCategory.Content,
        level,
        MAX_THEME_TAGS.takeUnless { showAll },
    )?.let { TagGroupUIModel(UiText.StringResource(StringResource.detail_tags_themes), it) },
    tags.group(TagCategory.Technical, level, MAX_TECHNICAL_TAGS.takeUnless { showAll })?.let {
        TagGroupUIModel(UiText.StringResource(StringResource.detail_tags_technical), it)
    },
    if (showSexual) {
        tags.group(TagCategory.Sexual, level, MAX_THEME_TAGS.takeUnless { showAll })?.let {
            TagGroupUIModel(UiText.StringResource(StringResource.detail_tags_sexual), it)
        }
    } else {
        null
    },
).toImmutableList()

/** How many tags the groups would show at the highest spoiler level but not at [level]; null when none. */
fun NovelDetail.toHiddenTagsLabel(
    level: SpoilerLevel,
    showSexual: Boolean = false,
    showAll: Boolean = false,
): UiText? {
    val shown = toTagGroups(level, showSexual, showAll).flatMap { it.tags }.toSet()
    val hidden = toTagGroups(
        SpoilerLevel.Major,
        showSexual,
        showAll,
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

/**
 * The link that switches the groups between their most relevant tags and every tag: "See all 34"
 * while some are left out, "Show less" while [showAll]; null when the capped groups already hold
 * every tag [level] allows.
 */
fun NovelDetail.toTagsToggleLabel(
    level: SpoilerLevel,
    showSexual: Boolean = false,
    showAll: Boolean = false,
): UiText? {
    val every = toTagGroups(level, showSexual, showAll = true).sumOf { it.tags.size }
    val capped = toTagGroups(level, showSexual, showAll = false).sumOf { it.tags.size }
    return when {
        every == capped -> null
        showAll -> UiText.StringResource(StringResource.detail_read_less)
        else -> UiText.StringResource(StringResource.detail_see_all_count, listOf(every))
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

/** The [category]'s tags [level] allows, most relevant first, at most [max] (null: all of them). */
private fun List<NovelTag>.group(
    category: TagCategory,
    level: SpoilerLevel,
    max: Int?,
): ImmutableList<TagChipUIModel>? = filter {
    it.category == category &&
        it.spoilerLevel <= level.maxLevel
}
    .sortedByDescending { it.rating }
    .take(max ?: Int.MAX_VALUE)
    .map { TagChipUIModel(id = it.id, label = UiText.StringValue(it.name)) }
    .takeIf { it.isNotEmpty() }
    ?.toImmutableList()
