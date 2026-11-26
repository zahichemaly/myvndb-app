package com.zc.vndb.feature.browse.presentation.component.browsenovelcard

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.component.chip.TagChipView
import com.zc.vndb.common.ui.component.coverimage.CoverImageView
import com.zc.vndb.common.ui.component.languagepills.LanguagePillsView
import com.zc.vndb.common.ui.component.ratingbadge.RatingBadgeView
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.theme.cardBackground
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.browse.presentation.component.browsenovelcard.model.BrowseNovelCardUIModel
import kotlinx.collections.immutable.persistentListOf

private val CoverWidth = 92.dp
private const val TITLE_MAX_LINES = 2

/**
 * A Browse result: cover with score, developers and year, title, up to two tags and the play time
 * pill, then the language pills when enabled.
 */
@Composable
fun BrowseNovelCardView(
    model: BrowseNovelCardUIModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = MaterialTheme.shapes.medium
    Row(
        modifier = modifier
            .clip(shape)
            .cardBackground()
            .border(VndbTheme.dimens.borderWidth, VndbTheme.colors.borderSubtle, shape)
            .clickable(onClick = onClick)
            .padding(VndbTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md),
    ) {
        Box(modifier = Modifier.width(CoverWidth)) {
            CoverImageView(
                url = model.coverUrl,
                isSensitive = model.isCoverSensitive,
                contentDescription = model.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(VndbTheme.dimens.coverAspectRatio),
            )
            model.ratingLabel?.let { rating ->
                RatingBadgeView(
                    label = rating,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(VndbTheme.spacing.xs),
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
        ) {
            MetaRow(model)
            Text(
                text = model.title.asString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = TITLE_MAX_LINES,
                overflow = TextOverflow.Ellipsis,
            )
            TagsAndLength(model)
            LanguagePillsView(model.languagePills)
        }
    }
}

@Composable
private fun MetaRow(model: BrowseNovelCardUIModel) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = model.creditsLabel?.asString().orEmpty(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = model.yearLabel.asString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * One line: the tags, then the length pill ("Very Long (>50h)") right after them. The length always
 * shows; a tag that does not fit beside it is left out.
 */
@Composable
private fun TagsAndLength(model: BrowseNovelCardUIModel) {
    if (model.tags.isEmpty() && model.lengthLabel == null) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = VndbTheme.spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (model.tags.isNotEmpty()) {
            // Only as wide as the tags that fit, so the length pill sits right after them.
            FlowRow(
                modifier = Modifier.weight(1f, fill = false),
                horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
                maxLines = 1,
            ) {
                model.tags.forEach { tag -> TagChipView(label = tag) }
            }
        }
        model.lengthLabel?.let { length -> TagChipView(label = length) }
    }
}

@AdaptivePreviews
@Composable
private fun BrowseNovelCardViewPreview() {
    VndbTheme {
        BrowseNovelCardView(
            model = BrowseNovelCardUIModel(
                id = "v2002",
                title = UiText.StringValue("Steins;Gate"),
                creditsLabel = UiText.StringValue("5pb. / Nitroplus"),
                yearLabel = UiText.StringValue("2009"),
                ratingLabel = UiText.StringValue("8.95"),
                lengthLabel = UiText.StringValue("Long (30-50h)"),
                coverUrl = null,
                isCoverSensitive = false,
                tags = persistentListOf(
                    UiText.StringValue("Sci-Fi"),
                    UiText.StringValue("Mystery"),
                ),
            ),
            onClick = {},
            modifier = Modifier.fillMaxWidth().padding(VndbTheme.spacing.lg),
        )
    }
}
