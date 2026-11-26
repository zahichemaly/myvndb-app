package com.zc.vndb.feature.discover.presentation.trending

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.theme.cardBackground
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.discover.presentation.component.trendingrelease.TrendingReleaseView
import com.zc.vndb.feature.discover.presentation.component.trendingrelease.model.ReleaseKindTone
import com.zc.vndb.feature.discover.presentation.component.trendingrelease.model.TrendingReleaseUIModel
import com.zc.vndb.feature.discover.presentation.trending.component.trendingskeleton.TrendingSkeletonView
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

private val MaxContentWidth = 720.dp

/** Ask for the next page this many rows before the end. */
private const val LOAD_MORE_THRESHOLD = 5

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

            state.isLoading -> Box(contentModifier, contentAlignment = Alignment.TopCenter) {
                TrendingSkeletonView(
                    modifier = Modifier
                        .widthIn(max = MaxContentWidth)
                        .fillMaxWidth(),
                )
            }

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
