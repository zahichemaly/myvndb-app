package com.zc.vndb.feature.discover.presentation.component.trendingrelease

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.component.chip.TagChipView
import com.zc.vndb.common.ui.component.coverimage.CoverImageView
import com.zc.vndb.common.ui.component.platformicon.PlatformIconsView
import com.zc.vndb.common.ui.component.platformicon.model.PlatformIcon
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.discover.presentation.component.trendingrelease.model.ReleaseKindTone
import com.zc.vndb.feature.discover.presentation.component.trendingrelease.model.TrendingReleaseUIModel
import kotlinx.collections.immutable.persistentListOf

private val ThumbnailWidth = 44.dp
private val StarSize = 12.dp
private const val TITLE_MAX_LINES = 2

/**
 * A "Trending" row, meant to sit in a list with dividers: thumbnail, title, the release-type pill
 * with language and date, and the score above the platform logos.
 */
@Composable
fun TrendingReleaseView(
    model: TrendingReleaseUIModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = VndbTheme.spacing.md, vertical = VndbTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // The title is read out from the text, so the thumbnail stays silent.
        CoverImageView(
            url = model.thumbnailUrl,
            isSensitive = model.isCoverSensitive,
            modifier = Modifier
                .width(ThumbnailWidth)
                .aspectRatio(VndbTheme.dimens.coverAspectRatio),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
        ) {
            Text(
                text = model.title.asString(),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = TITLE_MAX_LINES,
                overflow = TextOverflow.Ellipsis,
            )
            KindRow(model)
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
        ) {
            model.ratingLabel?.let { rating -> RatingText(rating) }
            PlatformIconsView(
                icons = model.platformIcons,
                moreLabel = model.morePlatformsLabel,
                contentDescription = model.platformsLabel,
            )
        }
    }
}

/** The release-type pill, then "English · Nov 12". */
@Composable
private fun KindRow(model: TrendingReleaseUIModel) {
    val separator = " ${UiText.StringResource(StringResource.separator_dot).asString()} "
    Row(
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TagChipView(label = model.kindLabel, colors = model.kindTone.colors())
        Text(
            text = listOfNotNull(model.languageLabel?.asString(), model.dateLabel.asString())
                .joinToString(separator),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun RatingText(rating: UiText) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = VndbTheme.colors.score,
            modifier = Modifier.size(StarSize),
        )
        Text(
            text = rating.asString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

/** Each kind has its own pill colour; a re-release is the least news, so it stays neutral. */
@Composable
private fun ReleaseKindTone.colors(): TintedColors = when (this) {
    ReleaseKindTone.New -> VndbTheme.colors.releaseNew
    ReleaseKindTone.Translation -> VndbTheme.colors.releaseTranslation
    ReleaseKindTone.Patch -> VndbTheme.colors.releasePatch
    ReleaseKindTone.Rerelease -> VndbTheme.colors.chipNeutral
}

@AdaptivePreviews
@Composable
private fun TrendingReleaseViewPreview() {
    VndbTheme {
        TrendingReleaseView(
            model = TrendingReleaseUIModel(
                releaseId = "r1",
                novelId = "v1",
                title = UiText.StringValue("NEVERMORE ~Yongliu Bi'an de Qiyuan~"),
                kindLabel = UiText.StringValue("Translation"),
                kindTone = ReleaseKindTone.Translation,
                languageLabel = UiText.StringValue("English"),
                dateLabel = UiText.StringValue("Nov 12"),
                ratingLabel = UiText.StringValue("8.12"),
                thumbnailUrl = null,
                isCoverSensitive = false,
                platformIcons = persistentListOf(PlatformIcon.Windows, PlatformIcon.Nintendo),
                platformsLabel = UiText.StringValue("Windows, Switch"),
                opensRelease = true,
            ),
            onClick = {},
            modifier = Modifier.fillMaxWidth().padding(VndbTheme.spacing.lg),
        )
    }
}
