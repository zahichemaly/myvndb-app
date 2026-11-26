package com.zc.vndb.feature.discover.presentation.trending.component.trendingskeleton

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.component.skeleton.SkeletonBoxView
import com.zc.vndb.common.ui.component.skeleton.SkeletonLineView
import com.zc.vndb.common.ui.component.skeleton.loadingSemantics
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.theme.cardBackground
import com.zc.vndb.common.ui.util.AdaptivePreviews

/** As `TrendingReleaseView`'s thumbnail, so skeleton rows are as tall as real ones. */
private val RowThumbnailWidth = 44.dp
private val TitleSkeletonHeight = 28.dp
private val RowTitleSkeletonHeight = 16.dp
private val RowLabelSkeletonHeight = 20.dp
private const val TITLE_SKELETON_WIDTH = 0.5f
private const val LANGUAGES_SKELETON_WIDTH = 0.3f
private const val ROW_TITLE_SKELETON_WIDTH = 0.75f
private const val ROW_LABEL_SKELETON_WIDTH = 0.45f

/** Enough rows to fill a phone screen. */
private const val SKELETON_ROWS = 10

/** The Trending page's shape while its first page loads: the title, its languages and shimmering rows. */
@Composable
fun TrendingSkeletonView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .loadingSemantics()
            .padding(horizontal = VndbTheme.spacing.margin, vertical = VndbTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
    ) {
        Column(
            modifier = Modifier.padding(bottom = VndbTheme.spacing.sm),
            verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
        ) {
            SkeletonLineView(widthFraction = TITLE_SKELETON_WIDTH, height = TitleSkeletonHeight)
            SkeletonLineView(widthFraction = LANGUAGES_SKELETON_WIDTH)
        }
        repeat(SKELETON_ROWS) { TrendingRowSkeleton() }
    }
}

/** A trending row's shape (thumbnail, title, release-kind label), with its padding and height. */
@Composable
private fun TrendingRowSkeleton() {
    val shape = MaterialTheme.shapes.medium
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .cardBackground()
            .border(VndbTheme.dimens.borderWidth, VndbTheme.colors.borderSubtle, shape)
            .padding(horizontal = VndbTheme.spacing.md, vertical = VndbTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SkeletonBoxView(
            modifier = Modifier
                .width(RowThumbnailWidth)
                .aspectRatio(VndbTheme.dimens.coverAspectRatio),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
        ) {
            SkeletonLineView(
                widthFraction = ROW_TITLE_SKELETON_WIDTH,
                height = RowTitleSkeletonHeight,
            )
            SkeletonLineView(
                widthFraction = ROW_LABEL_SKELETON_WIDTH,
                height = RowLabelSkeletonHeight,
            )
        }
    }
}

@AdaptivePreviews
@Composable
private fun TrendingSkeletonViewPreview() {
    VndbTheme { TrendingSkeletonView(modifier = Modifier.fillMaxWidth()) }
}
