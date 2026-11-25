package com.zc.vndb.common.ui.component.coverimage

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.common.ui.util.LocalShowSensitiveContent

private val SensitiveBlur = 24.dp
private val OverlayIconSize = 24.dp
private val PreviewCoverWidth = 120.dp

/**
 * A VNDB cover, cropped to fill the size the caller gives it, with the cover shape.
 * Sensitive covers are blurred; before Android 12 (no blur support) they are not loaded at all.
 * A missing or failed image shows a gray placeholder with an exclamation mark.
 */
@Composable
fun CoverImageView(
    url: String?,
    isSensitive: Boolean,
    modifier: Modifier = Modifier,
    contentDescription: UiText? = null,
    /** Which part of the cover stays in view when it is cropped. */
    alignment: Alignment = Alignment.Center,
) {
    // Settings → "Show sensitive content" turns the blur off everywhere.
    val isBlurred = isSensitive && !LocalShowSensitiveContent.current
    val canBlur = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val loads = !url.isNullOrBlank() && (!isBlurred || canBlur)
    var loadFailed by remember(url) { mutableStateOf(false) }
    val showUnavailable = url.isNullOrBlank() || loadFailed
    Box(
        modifier = modifier
            .clip(VndbTheme.shapes.cover)
            .background(
                if (showUnavailable) MaterialTheme.colorScheme.surfaceVariant else VndbTheme.colors.surfaceCard,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (loads && !loadFailed) {
            // No placeholder: the box's own color shows while loading. A placeholder without a
            // size would also make Coil's crossfade center the image, ignoring [alignment].
            AsyncImage(
                model = url,
                contentDescription = contentDescription?.asString(),
                contentScale = ContentScale.Crop,
                alignment = alignment,
                onError = { loadFailed = true },
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (isBlurred) Modifier.blur(SensitiveBlur) else Modifier),
            )
        }
        when {
            showUnavailable -> Icon(
                imageVector = Icons.Filled.PriorityHigh,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(OverlayIconSize),
            )

            isBlurred -> Icon(
                imageVector = Icons.Outlined.VisibilityOff,
                contentDescription = UiText.StringResource(
                    StringResource.cd_sensitive_cover,
                ).asString(),
                tint = VndbTheme.colors.placeholder,
                modifier = Modifier.size(OverlayIconSize),
            )
        }
    }
}

@AdaptivePreviews
@Composable
private fun CoverImageViewPreview() {
    VndbTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm)) {
            CoverImageView(
                url = null,
                isSensitive = false,
                modifier = Modifier.width(
                    PreviewCoverWidth,
                ).aspectRatio(VndbTheme.dimens.coverAspectRatio),
            )
            CoverImageView(
                url = "https://t.vndb.org/cv/19/77819.jpg",
                isSensitive = true,
                modifier = Modifier.width(
                    PreviewCoverWidth,
                ).aspectRatio(VndbTheme.dimens.coverAspectRatio),
            )
        }
    }
}
