package com.zc.vndb.feature.discover.presentation.component.featurednovel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.zc.vndb.common.ui.component.chip.TagChipView
import com.zc.vndb.common.ui.component.coverimage.CoverImageView
import com.zc.vndb.common.ui.component.ratingbadge.RatingBadgeView
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.theme.cardBackground
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.discover.presentation.component.featurednovel.model.FeaturedNovelUIModel
import kotlinx.collections.immutable.persistentListOf

/** Landscape (16:9) crop of the cover art for the hero. */
private const val HERO_ASPECT_RATIO = 16f / 9f

private const val DESCRIPTION_MAX_LINES = 3

/** The "VN of the Week" hero card; the whole card opens the VN. */
@Composable
fun FeaturedNovelView(
    model: FeaturedNovelUIModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = MaterialTheme.shapes.large
    val clickLabel = UiText.StringResource(StringResource.discover_view_details).asString()
    Column(
        modifier = modifier
            .clip(shape)
            .cardBackground()
            .border(VndbTheme.dimens.borderWidth, VndbTheme.colors.borderSubtle, shape)
            .clickable(onClickLabel = clickLabel, onClick = onClick),
    ) {
        HeroArt(model)
        Column(
            modifier = Modifier.padding(VndbTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
        ) {
            CreditsRow(model)
            Text(
                text = model.title.asString(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            model.description?.let { description ->
                Text(
                    text = description.asString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = DESCRIPTION_MAX_LINES,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            TagRow(model)
        }
    }
}

@Composable
private fun HeroArt(model: FeaturedNovelUIModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(HERO_ASPECT_RATIO),
    ) {
        CoverImageView(
            url = model.coverUrl,
            isSensitive = model.isCoverSensitive,
            contentDescription = model.title,
            // Portrait covers cropped to landscape keep their top, where the faces usually are.
            alignment = Alignment.TopCenter,
            modifier = Modifier.matchParentSize(),
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, VndbTheme.colors.coverScrim)),
                ),
        )
        Text(
            text = UiText.StringResource(StringResource.discover_vn_of_the_week).asString(),
            style = MaterialTheme.typography.labelSmall,
            color = VndbTheme.colors.onAccent,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(VndbTheme.spacing.sm)
                .background(VndbTheme.colors.accent, VndbTheme.shapes.pill)
                .padding(horizontal = VndbTheme.spacing.sm, vertical = VndbTheme.spacing.xs),
        )
        model.ratingLabel?.let { rating ->
            RatingBadgeView(
                label = rating,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(VndbTheme.spacing.sm),
            )
        }
    }
}

@Composable
private fun CreditsRow(model: FeaturedNovelUIModel) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        model.developerLabel?.let { developer ->
            Text(
                text = developer.asString(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
        }
        model.writerLabel?.let { writer ->
            Text(
                text = writer.asString(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagRow(model: FeaturedNovelUIModel) {
    if (model.tags.isEmpty() && model.ageLabel == null) return
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
        verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
    ) {
        model.tags.forEach { tag -> TagChipView(label = tag) }
        model.ageLabel?.let { age ->
            TagChipView(
                label = age,
                colors = VndbTheme.colors.spoilerNone,
            )
        }
    }
}

@AdaptivePreviews
@Composable
private fun FeaturedNovelViewPreview() {
    VndbTheme {
        FeaturedNovelView(
            model = FeaturedNovelUIModel(
                id = "v2002",
                title = UiText.StringValue("STEINS;GATE"),
                ratingLabel = UiText.StringValue("9.02 / 10"),
                developerLabel = UiText.StringValue("NITRO PLUS"),
                writerLabel = UiText.StringValue("Tanizaki Ouka"),
                description = UiText.StringValue(
                    "A rag-tag band of tech-savvy students discover a way to change the past via mail.",
                ),
                coverUrl = null,
                isCoverSensitive = false,
                tags = persistentListOf(
                    UiText.StringValue("Suspense"),
                    UiText.StringValue("Time Travel"),
                ),
                ageLabel = UiText.StringValue("All Ages"),
            ),
            onClick = {},
            modifier = Modifier.padding(VndbTheme.spacing.lg),
        )
    }
}
