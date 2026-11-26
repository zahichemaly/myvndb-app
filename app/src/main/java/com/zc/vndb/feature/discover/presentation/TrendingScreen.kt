package com.zc.vndb.feature.discover.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.component.apptopbar.AppTopBarView
import com.zc.vndb.common.ui.component.error.ErrorView
import com.zc.vndb.common.ui.component.platformicon.model.PlatformIcon
import com.zc.vndb.common.ui.component.pulltorefresh.PullToRefreshView
import com.zc.vndb.common.ui.component.skeleton.SkeletonBoxView
import com.zc.vndb.common.ui.component.skeleton.SkeletonLineView
import com.zc.vndb.common.ui.component.skeleton.loadingSemantics
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.theme.cardBackground
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.discover.presentation.component.trendingrelease.TrendingReleaseView
import com.zc.vndb.feature.discover.presentation.component.trendingrelease.model.ReleaseKindTone
import com.zc.vndb.feature.discover.presentation.component.trendingrelease.model.TrendingReleaseUIModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

private val MaxContentWidth = 720.dp

/** Ask for the next page this many rows before the end. */
private const val LOAD_MORE_THRESHOLD = 5

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

@Composable
fun TrendingScreen(
    state: TrendingState,
    onIntent: (TrendingIntent) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        modifier = modifier,
        topBar = { AppTopBarView(onBack = { onIntent(TrendingIntent.BackClicked) }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent,
    ) { padding ->
        val contentModifier = Modifier
            .padding(padding)
            .fillMaxSize()
        when {
            state.error != null -> ErrorView(
                model = state.error,
                onRetry = { onIntent(TrendingIntent.Retry) },
                modifier = contentModifier,
            )

            state.isLoading -> TrendingSkeleton(modifier = contentModifier)

            else -> PullToRefreshView(
                isRefreshing = state.isRefreshing,
                onRefresh = { onIntent(TrendingIntent.Refresh) },
                modifier = contentModifier,
            ) {
                TrendingList(state, onIntent)
            }
        }
    }
}

@Composable
private fun TrendingList(state: TrendingState, onIntent: (TrendingIntent) -> Unit) {
    val listState = rememberLazyListState()
    LoadMoreTrigger(listState) { onIntent(TrendingIntent.EndReached) }
    val shape = MaterialTheme.shapes.medium
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .widthIn(max = MaxContentWidth)
                .fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = VndbTheme.spacing.margin,
                vertical = VndbTheme.spacing.sm,
            ),
            verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
        ) {
            item(key = "header", contentType = "header") { TrendingHeader(state) }
            if (state.isEmpty) {
                item(key = "empty", contentType = "empty") {
                    val empty = UiText.StringResource(StringResource.discover_trending_empty)
                    Text(
                        text = empty.asString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = VndbTheme.spacing.lg),
                    )
                }
            }
            items(items = state.rows, key = { it.releaseId }, contentType = { "row" }) { row ->
                TrendingReleaseView(
                    model = row,
                    onClick = {
                        onIntent(
                            if (row.opensRelease) {
                                TrendingIntent.ReleaseClicked(row.releaseId)
                            } else {
                                TrendingIntent.NovelClicked(row.novelId)
                            },
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .cardBackground()
                        .border(VndbTheme.dimens.borderWidth, VndbTheme.colors.borderSubtle, shape),
                )
            }
            loadMoreFooter(state, onIntent)
        }
    }
}

/** The page title, and the languages it covers (Settings → Trending). */
@Composable
private fun TrendingHeader(state: TrendingState) {
    Column(Modifier.padding(bottom = VndbTheme.spacing.sm)) {
        Text(
            text = UiText.StringResource(StringResource.discover_new_and_trending).asString(),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        state.languagesLabel?.let { languages ->
            Text(
                text = languages.asString(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** The page's shape while its first page loads: the title, its languages and shimmering rows. */
@Composable
private fun TrendingSkeleton(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = MaxContentWidth)
                .fillMaxWidth()
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

private fun LazyListScope.loadMoreFooter(state: TrendingState, onIntent: (TrendingIntent) -> Unit) {
    when {
        state.isLoadingMore -> item(key = "loading-more", contentType = "progress") {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(VndbTheme.spacing.md),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = VndbTheme.colors.accent)
            }
        }

        state.loadMoreFailed -> item(key = "load-more-failed", contentType = "retry") {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TextButton(onClick = { onIntent(TrendingIntent.LoadMoreRetried) }) {
                    Text(
                        text = UiText.StringResource(StringResource.action_retry).asString(),
                        color = VndbTheme.colors.accent,
                    )
                }
            }
        }
    }
}

/** Asks for the next page once the last visible item is near the end of the list. */
@Composable
private fun LoadMoreTrigger(listState: LazyListState, onLoadMore: () -> Unit) {
    val currentOnLoadMore by rememberUpdatedState(onLoadMore)
    val nearEnd by remember(listState) {
        derivedStateOf {
            val info = listState.layoutInfo
            val lastVisible =
                info.visibleItemsInfo.lastOrNull()?.index ?: return@derivedStateOf false
            info.totalItemsCount > 0 && lastVisible >= info.totalItemsCount - LOAD_MORE_THRESHOLD
        }
    }
    LaunchedEffect(nearEnd) {
        if (nearEnd) currentOnLoadMore()
    }
}

@AdaptivePreviews
@Composable
private fun TrendingScreenPreview() {
    val rows = ReleaseKindTone.entries.mapIndexed { index, tone ->
        TrendingReleaseUIModel(
            releaseId = "r$index",
            novelId = "v$index",
            title = UiText.StringValue("Release $index"),
            kindLabel = UiText.StringValue(tone.name),
            kindTone = tone,
            languageLabel = if (tone == ReleaseKindTone.Translation) {
                UiText.StringValue("English")
            } else {
                null
            },
            dateLabel = UiText.StringValue("Nov 2$index"),
            ratingLabel = UiText.StringValue("8.1$index"),
            thumbnailUrl = null,
            isCoverSensitive = false,
            platformIcons = persistentListOf(PlatformIcon.Windows),
            platformsLabel = UiText.StringValue("Windows"),
        )
    }
    VndbTheme {
        TrendingScreen(
            state = TrendingState(
                isLoading = false,
                languagesLabel = UiText.StringValue("English, Japanese"),
                rows = rows.toImmutableList(),
                isLoadingMore = true,
            ),
            onIntent = {},
        )
    }
}

@AdaptivePreviews
@Composable
private fun TrendingScreenLoadingPreview() {
    VndbTheme { TrendingScreen(state = TrendingState(isLoading = true), onIntent = {}) }
}
