package com.zc.vndb.feature.browse.presentation.component.scopeheader

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.component.sectioncard.SectionCardView
import com.zc.vndb.common.ui.component.skeleton.SkeletonLineView
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews

private val NameLineHeight = 28.dp
private const val NAME_WIDTH = 0.6f
private const val DETAILS_WIDTH = 0.45f
private const val ALIASES_WIDTH = 0.75f

/** Stands in for [ScopeHeaderView] while the tag, producer or staff member loads. */
@Composable
fun ScopeHeaderSkeletonView(modifier: Modifier = Modifier) {
    SectionCardView(modifier = modifier) {
        SkeletonLineView(widthFraction = NAME_WIDTH, height = NameLineHeight)
        SkeletonLineView(widthFraction = DETAILS_WIDTH)
        SkeletonLineView(widthFraction = ALIASES_WIDTH)
    }
}

@AdaptivePreviews
@Composable
private fun ScopeHeaderSkeletonViewPreview() {
    VndbTheme {
        ScopeHeaderSkeletonView(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VndbTheme.spacing.lg),
        )
    }
}
