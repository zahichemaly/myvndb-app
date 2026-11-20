package com.zc.vndb.feature.release.presentation.component.releasenovelrow

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.zc.vndb.common.ui.component.coverimage.CoverImageView
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.theme.cardBackground
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.release.presentation.component.releasenovelrow.model.ReleaseNovelRowUIModel

private val ThumbnailWidth = 48.dp
private const val TITLE_MAX_LINES = 2

/** A VN this release belongs to: thumbnail, Complete / Partial / Trial, title and year. */
@Composable
fun ReleaseNovelRowView(
    model: ReleaseNovelRowUIModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .cardBackground(VndbTheme.colors.surfaceInset)
            .clickable(onClick = onClick)
            .padding(VndbTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CoverImageView(
            url = model.thumbnailUrl,
            isSensitive = model.isCoverSensitive,
            contentDescription = model.title,
            modifier = Modifier
                .width(ThumbnailWidth)
                .aspectRatio(VndbTheme.dimens.coverAspectRatio),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = model.typeLabel.asString(),
                style = MaterialTheme.typography.labelSmall,
                color = if (model.isComplete) MaterialTheme.colorScheme.onSurfaceVariant else VndbTheme.colors.score,
            )
            Text(
                text = model.title.asString(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = TITLE_MAX_LINES,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = model.yearLabel.asString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@AdaptivePreviews
@Composable
private fun ReleaseNovelRowViewPreview() {
    VndbTheme {
        ReleaseNovelRowView(
            model = ReleaseNovelRowUIModel(
                id = "v2002",
                title = UiText.StringValue("STEINS;GATE"),
                yearLabel = UiText.StringValue("2009"),
                typeLabel = UiText.StringValue("COMPLETE"),
                isComplete = true,
                thumbnailUrl = null,
                isCoverSensitive = false,
            ),
            onClick = {},
            modifier = Modifier.fillMaxWidth().padding(VndbTheme.spacing.lg),
        )
    }
}
