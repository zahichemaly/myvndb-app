package com.zc.vndb.feature.profile.presentation.component.profileskeleton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.component.skeleton.SkeletonBoxView
import com.zc.vndb.common.ui.component.skeleton.SkeletonLineView
import com.zc.vndb.common.ui.component.skeleton.SkeletonRowView
import com.zc.vndb.common.ui.component.skeleton.loadingSemantics
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews

/** Matches ProfileHeaderView's slim card. */
private val AvatarSize = 36.dp
private val UsernameHeight = 16.dp
private val ChipWidth = 80.dp
private val ChipHeight = 28.dp
private const val ENTRY_COUNT = 4
private const val CHIP_COUNT = 3
private const val USERNAME_WIDTH = 0.5f
private const val STATS_WIDTH = 0.8f

/**
 * A placeholder for the Profile list: the entry rows, with the header and label chips above them
 * when [showHeader] is set (before anything has loaded).
 */
@Composable
fun ProfileSkeletonView(modifier: Modifier = Modifier, showHeader: Boolean = false) {
    Column(
        modifier = modifier.loadingSemantics(),
        verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md),
    ) {
        if (showHeader) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SkeletonBoxView(modifier = Modifier.size(AvatarSize), shape = CircleShape)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
                ) {
                    SkeletonLineView(widthFraction = USERNAME_WIDTH, height = UsernameHeight)
                    SkeletonLineView(widthFraction = STATS_WIDTH)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm)) {
                repeat(CHIP_COUNT) {
                    SkeletonBoxView(
                        modifier = Modifier.size(width = ChipWidth, height = ChipHeight),
                    )
                }
            }
        }
        repeat(ENTRY_COUNT) { SkeletonRowView(modifier = Modifier.fillMaxWidth()) }
    }
}

@AdaptivePreviews
@Composable
private fun ProfileSkeletonViewPreview() {
    VndbTheme { ProfileSkeletonView(showHeader = true) }
}
