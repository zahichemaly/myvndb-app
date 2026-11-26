package com.zc.vndb.feature.detail.presentation.component.imageviewer

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.detail.presentation.model.ImageViewerUIState
import com.zc.vndb.feature.detail.presentation.model.ViewerImageUIModel
import kotlinx.collections.immutable.persistentListOf

private const val MAX_ZOOM = 4f

/**
 * Full-screen viewer: swipe between images, pinch to zoom (pan while zoomed), double-tap to reset.
 */
@Composable
fun ImageViewerView(
    state: ImageViewerUIState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        val pagerState = rememberPagerState(initialPage = state.startIndex) { state.images.size }
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black),
        ) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                ZoomableImage(state.images[page])
            }
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .safeDrawingPadding(),
            ) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = UiText.StringResource(
                        StringResource.detail_close_viewer,
                    ).asString(),
                    tint = Color.White,
                )
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(VndbTheme.colors.coverScrim)
                    .safeDrawingPadding()
                    .padding(VndbTheme.spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                state.images.getOrNull(pagerState.currentPage)?.caption?.let { caption ->
                    Text(
                        text = caption.asString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                }
                Text(
                    text = UiText.StringResource(
                        StringResource.detail_viewer_position,
                        listOf(pagerState.currentPage + 1, state.images.size),
                    ).asString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
private fun ZoomableImage(image: ViewerImageUIModel) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    // The pinch centroid is ignored: the image zooms around its centre.
    val transformState = rememberTransformableState { _, zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, MAX_ZOOM)
        offset = if (scale == 1f) Offset.Zero else offset + panChange
    }
    AsyncImage(
        model = image.url,
        contentDescription = image.caption?.asString(),
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        scale = 1f
                        offset = Offset.Zero
                    },
                )
            }
            // Panning is only taken while zoomed, so the pager still gets swipes at 1x.
            .transformable(state = transformState, canPan = { scale > 1f })
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationX = offset.x
                translationY = offset.y
            },
    )
}

@AdaptivePreviews
@Composable
private fun ImageViewerViewPreview() {
    VndbTheme {
        ImageViewerView(
            state = ImageViewerUIState(
                images = persistentListOf(
                    ViewerImageUIModel("a", UiText.StringValue("First Press Edition")),
                ),
                startIndex = 0,
            ),
            onDismiss = {},
        )
    }
}
