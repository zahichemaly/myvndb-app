package com.zc.vndb.feature.userlist.presentation.component.editorskeleton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.component.skeleton.SkeletonBoxView
import com.zc.vndb.common.ui.component.skeleton.SkeletonLineView
import com.zc.vndb.common.ui.component.skeleton.loadingSemantics
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews

private const val FIELD_COUNT = 3
private const val LABEL_WIDTH = 0.3f
private val FieldHeight = 48.dp

/** A placeholder for the list editor's fields while the entry loads. */
@Composable
fun EditorSkeletonView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.loadingSemantics(),
        verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.lg),
    ) {
        repeat(FIELD_COUNT) {
            Column(verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm)) {
                SkeletonLineView(widthFraction = LABEL_WIDTH)
                SkeletonBoxView(modifier = Modifier.fillMaxWidth().height(FieldHeight))
            }
        }
    }
}

@AdaptivePreviews
@Composable
private fun EditorSkeletonViewPreview() {
    VndbTheme { EditorSkeletonView(modifier = Modifier.fillMaxWidth()) }
}
