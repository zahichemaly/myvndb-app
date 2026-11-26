package com.zc.vndb.feature.discover.presentation.home.component.discoverskeleton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.component.skeleton.SkeletonBoxView
import com.zc.vndb.common.ui.component.skeleton.SkeletonLineView
import com.zc.vndb.common.ui.component.skeleton.SkeletonRowView
import com.zc.vndb.common.ui.component.skeleton.loadingSemantics
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews

private const val FEATURED_ASPECT_RATIO = 1.6f
private const val CARD_COUNT = 3
private const val TRENDING_COUNT = 3
private val CardWidth = 120.dp
private const val HEADER_WIDTH = 0.4f

/** A placeholder for Discover while it loads: the featured card, a card row and trending rows. */
@Composable
fun DiscoverSkeletonView(isExpanded: Boolean, modifier: Modifier = Modifier) {
    val margin = if (isExpanded) VndbTheme.spacing.marginDesktop else VndbTheme.spacing.margin
    Column(
        modifier = modifier
            .loadingSemantics()
            .padding(horizontal = margin, vertical = VndbTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md),
    ) {
        SkeletonBoxView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(FEATURED_ASPECT_RATIO),
            shape = MaterialTheme.shapes.large,
        )
        SkeletonLineView(widthFraction = HEADER_WIDTH, height = 20.dp)
        Row(horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md)) {
            repeat(CARD_COUNT) {
                SkeletonBoxView(
                    modifier = Modifier
                        .width(CardWidth)
                        .aspectRatio(VndbTheme.dimens.coverAspectRatio),
                    shape = MaterialTheme.shapes.medium,
                )
            }
        }
        SkeletonLineView(widthFraction = HEADER_WIDTH, height = 20.dp)
        repeat(TRENDING_COUNT) { SkeletonRowView(modifier = Modifier.fillMaxWidth()) }
    }
}

@AdaptivePreviews
@Composable
private fun DiscoverSkeletonViewPreview() {
    VndbTheme {
        DiscoverSkeletonView(isExpanded = false, modifier = Modifier.fillMaxSize())
    }
}
