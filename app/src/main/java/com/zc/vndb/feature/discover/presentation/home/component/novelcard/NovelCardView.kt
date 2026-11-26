package com.zc.vndb.feature.discover.presentation.home.component.novelcard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.zc.vndb.common.ui.component.coverimage.CoverImageView
import com.zc.vndb.common.ui.component.ratingbadge.RatingBadgeView
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.discover.presentation.home.component.novelcard.model.NovelCardUIModel

private val CardWidth = 120.dp

/** Portrait cover card with a score overlay, title and developer. */
@Composable
fun NovelCardView(model: NovelCardUIModel, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .width(CardWidth)
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick),
    ) {
        Box {
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
                        .align(Alignment.TopEnd)
                        .padding(VndbTheme.spacing.xs),
                )
            }
        }
        Text(
            text = model.title.asString(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = VndbTheme.spacing.xs),
        )
        model.subtitle?.let { subtitle ->
            Text(
                text = subtitle.asString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@AdaptivePreviews
@Composable
private fun NovelCardViewPreview() {
    VndbTheme {
        NovelCardView(
            model = NovelCardUIModel(
                id = "v2002",
                title = UiText.StringValue("STEINS;GATE"),
                subtitle = UiText.StringValue("NITRO PLUS"),
                ratingLabel = UiText.StringValue("9.02"),
                coverUrl = null,
                isCoverSensitive = false,
            ),
            onClick = {},
            modifier = Modifier.padding(VndbTheme.spacing.sm),
        )
    }
}
