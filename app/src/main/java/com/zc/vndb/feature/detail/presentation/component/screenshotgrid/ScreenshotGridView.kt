package com.zc.vndb.feature.detail.presentation.component.screenshotgrid

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.zc.vndb.common.ui.component.coverimage.CoverImageView
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.detail.presentation.model.MediaImageUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/** Screenshots are 16:9 on VNDB almost without exception. */
private const val SCREENSHOT_ASPECT_RATIO = 16f / 9f

/** Screenshots in rows of [columns]; tapping one opens it in the viewer. */
@Composable
fun ScreenshotGridView(
    images: ImmutableList<MediaImageUIModel>,
    columns: Int,
    onImageClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm)) {
        images.chunked(columns).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm)) {
                row.forEach { image ->
                    CoverImageView(
                        url = image.thumbnailUrl ?: image.url,
                        isSensitive = image.isSensitive,
                        contentDescription = image.caption,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(SCREENSHOT_ASPECT_RATIO)
                            .clip(MaterialTheme.shapes.small)
                            // Only builds that never reveal a screenshot blur it, so a blurred one stays closed.
                            .clickable(enabled = !image.isSensitive) { onImageClick(image.index) },
                    )
                }
                repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@AdaptivePreviews
@Composable
private fun ScreenshotGridViewPreview() {
    VndbTheme {
        ScreenshotGridView(
            images = persistentListOf(
                MediaImageUIModel("a", null, null, isSensitive = false, index = 0),
                MediaImageUIModel("b", null, null, isSensitive = false, index = 1),
                MediaImageUIModel("c", null, null, isSensitive = false, index = 2),
            ),
            columns = 2,
            onImageClick = {},
            modifier = Modifier.padding(VndbTheme.spacing.lg),
        )
    }
}
