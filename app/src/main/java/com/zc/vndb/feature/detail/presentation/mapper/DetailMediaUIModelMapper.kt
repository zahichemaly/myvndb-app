package com.zc.vndb.feature.detail.presentation.mapper

import com.zc.vndb.common.ui.resource.PluralResource
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.detail.domain.model.CoverArt
import com.zc.vndb.feature.detail.domain.model.Screenshot
import com.zc.vndb.feature.detail.presentation.model.ContentFilter
import com.zc.vndb.feature.detail.presentation.model.ContentToggleUIState
import com.zc.vndb.feature.detail.presentation.model.ImageViewerUIState
import com.zc.vndb.feature.detail.presentation.model.MediaImageUIModel
import com.zc.vndb.feature.detail.presentation.model.MediaUIModel
import com.zc.vndb.feature.detail.presentation.model.ScreenshotGroupUIModel
import com.zc.vndb.feature.detail.presentation.model.ViewerImageUIModel
import kotlin.math.roundToInt
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

private const val PREVIEW_SIZE = 4
private const val MAX_LEVEL = 2

/**
 * Screenshots allowed by [filters]: a screenshot shows when both its sexual and its violence
 * level (VNDB's averages rounded to 0, 1 or 2) are switched on. Toggles carry per-level counts.
 * With [blursSensitive] (builds that never reveal sensitive content), sensitive ones are blurred.
 */
fun List<Screenshot>.toMediaUIModel(
    filters: Set<ContentFilter>,
    blursSensitive: Boolean,
): MediaUIModel {
    val visible = filter { it.isAllowedBy(filters) }
    var index = 0
    val groups = visible.groupBy { it.releaseId }.map { (_, shots) ->
        ScreenshotGroupUIModel(
            title = shots.first().releaseTitle?.let(UiText::StringValue),
            screenshots = shots
                .map { it.toImageUIModel(index++, blursSensitive) }
                .toImmutableList(),
        )
    }
    val hidden = size - visible.size
    return MediaUIModel(
        totalCount = size,
        sexualToggles = toggles(filters, sexual = true),
        violenceToggles = toggles(filters, sexual = false),
        groups = groups.toImmutableList(),
        preview = groups.flatMap { it.screenshots }.take(PREVIEW_SIZE).toImmutableList(),
        visibleCount = visible.size,
        hiddenLabel = hidden.takeIf { it > 0 }
            ?.let { UiText.StringPlural(PluralResource.detail_hidden_by_filters, it, listOf(it)) },
    )
}

/**
 * The viewer over the screenshots [filters] allow, opened at the [index]th of them. Without
 * [includesSensitive], sensitive ones are left out so swiping never reaches them.
 */
fun List<Screenshot>.toViewer(
    filters: Set<ContentFilter>,
    index: Int,
    includesSensitive: Boolean,
): ImageViewerUIState {
    val allowed = filter { it.isAllowedBy(filters) }
    val isShown = { screenshot: Screenshot -> includesSensitive || !screenshot.image.isSensitive }
    return ImageViewerUIState(
        images = allowed.filter(isShown)
            .map {
                ViewerImageUIModel(
                    url = it.image.url,
                    caption = it.releaseTitle?.let(UiText::StringValue),
                )
            }
            .toImmutableList(),
        startIndex = allowed.take(index).count(isShown),
    )
}

fun List<CoverArt>.toCoverUIModels(): ImmutableList<MediaImageUIModel> = mapIndexed {
        index,
        cover,
    ->
    MediaImageUIModel(
        url = cover.image.url,
        thumbnailUrl = cover.image.thumbnailUrl,
        caption = UiText.StringValue(cover.releaseTitle),
        isSensitive = cover.image.isSensitive,
        index = index,
    )
}.toImmutableList()

/** The viewer over the covers, opened at [index]; without [includesSensitive], sensitive ones are left out. */
fun List<CoverArt>.toViewer(index: Int, includesSensitive: Boolean): ImageViewerUIState {
    val isShown = { cover: CoverArt -> includesSensitive || !cover.image.isSensitive }
    return ImageViewerUIState(
        images = filter(isShown).map {
            ViewerImageUIModel(
                url = it.image.url,
                caption = UiText.StringValue(it.releaseTitle),
            )
        }.toImmutableList(),
        startIndex = take(index).count(isShown),
    )
}

private fun List<Screenshot>.toggles(
    filters: Set<ContentFilter>,
    sexual: Boolean,
): ImmutableList<ContentToggleUIState> =
    ContentFilter.entries.filter { it.isSexual == sexual }.map { filter ->
        val count = count { (if (sexual) it.sexualLevel else it.violenceLevel) == filter.level }
        ContentToggleUIState(
            filter = filter,
            label = filter.toLabel(count),
            isSelected = filter in filters,
        )
    }.toImmutableList()

private fun Screenshot.toImageUIModel(index: Int, blursSensitive: Boolean) = MediaImageUIModel(
    url = image.url,
    thumbnailUrl = image.thumbnailUrl,
    caption = releaseTitle?.let(UiText::StringValue),
    // Listed because the reader allowed its level, so only a build that never reveals it blurs it.
    isSensitive = blursSensitive && image.isSensitive,
    index = index,
)

private fun Screenshot.isAllowedBy(filters: Set<ContentFilter>): Boolean =
    filters.any { it.isSexual && it.level == sexualLevel } &&
        filters.any { !it.isSexual && it.level == violenceLevel }

private val Screenshot.sexualLevel: Int get() = image.sexual.roundToInt().coerceIn(0, MAX_LEVEL)

private val Screenshot.violenceLevel: Int get() = image.violence.roundToInt().coerceIn(0, MAX_LEVEL)

private fun ContentFilter.toLabel(count: Int): UiText = UiText.StringResource(
    when (this) {
        ContentFilter.Safe -> StringResource.detail_filter_safe
        ContentFilter.Suggestive -> StringResource.detail_filter_suggestive
        ContentFilter.Explicit -> StringResource.detail_filter_explicit
        ContentFilter.Tame -> StringResource.detail_filter_tame
        ContentFilter.Violent -> StringResource.detail_filter_violent
        ContentFilter.Brutal -> StringResource.detail_filter_brutal
    },
    listOf(count),
)
