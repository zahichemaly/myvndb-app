package com.zc.vndb.feature.browse.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.zc.vndb.common.ui.component.apptopbar.AppTopBarView
import com.zc.vndb.common.ui.component.error.ErrorView
import com.zc.vndb.common.ui.component.error.model.ErrorUIModel
import com.zc.vndb.common.ui.component.expandabletext.ExpandableTextView
import com.zc.vndb.common.ui.component.linksrow.LinksRowView
import com.zc.vndb.common.ui.component.pulltorefresh.PullToRefreshView
import com.zc.vndb.common.ui.component.skeleton.SkeletonRowView
import com.zc.vndb.common.ui.component.skeleton.loadingSemantics
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.browse.domain.model.BrowseSort
import com.zc.vndb.feature.browse.domain.model.Genre
import com.zc.vndb.feature.browse.presentation.component.browsenovelcard.BrowseNovelCardView
import com.zc.vndb.feature.browse.presentation.component.browsenovelcard.model.BrowseNovelCardUIModel
import com.zc.vndb.feature.browse.presentation.component.filterbar.ActiveFiltersRowView
import com.zc.vndb.feature.browse.presentation.component.filterbar.FilterButtonView
import com.zc.vndb.feature.browse.presentation.component.filterbar.SortRowView
import com.zc.vndb.feature.browse.presentation.component.filtersheet.FilterSheetView
import com.zc.vndb.feature.browse.presentation.component.scopeheader.ScopeHeaderView
import com.zc.vndb.feature.browse.presentation.component.searchfield.SearchFieldView
import com.zc.vndb.feature.browse.presentation.model.ActiveFilterKey
import com.zc.vndb.feature.browse.presentation.model.ActiveFilterUIModel
import com.zc.vndb.feature.browse.presentation.model.FilterDraft
import com.zc.vndb.feature.browse.presentation.model.SortOptionUIModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/** Start loading the next page this many items before the end. */
private const val LOAD_MORE_THRESHOLD = 5
private const val SKELETON_COUNT = 6
private val SkeletonCoverWidth = 92.dp

/** Callbacks built once from `onIntent`, so rows get stable lambdas. */
@Immutable
private class BrowseActions(
    val onQueryChange: (String) -> Unit,
    val onSearch: () -> Unit,
    val onClear: () -> Unit,
    val onSortSelect: (BrowseSort) -> Unit,
    val onSortDirectionToggle: () -> Unit,
    val onNovelClick: (String) -> Unit,
    val onRetry: () -> Unit,
    val onLoadMoreRetry: () -> Unit,
    val onDescriptionToggle: () -> Unit,
    val onLinkClick: (String) -> Unit,
    val onFiltersClick: () -> Unit,
    val onDraftChange: (FilterDraft) -> Unit,
    val onFiltersReset: () -> Unit,
    val onFiltersApply: () -> Unit,
    val onFiltersDismiss: () -> Unit,
    val onActiveFilterRemove: (ActiveFilterKey) -> Unit,
    val onActiveFiltersClear: () -> Unit,
)

@Composable
fun BrowseScreen(
    state: BrowseState,
    onIntent: (BrowseIntent) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val columns = if (windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND,
        )
    ) {
        2
    } else {
        1
    }
    val margin = if (windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND,
        )
    ) {
        VndbTheme.spacing.marginDesktop
    } else {
        VndbTheme.spacing.margin
    }
    val actions = remember(onIntent) { browseActions(onIntent) }
    val gridState = rememberLazyGridState()
    LoadMoreTrigger(gridState) { onIntent(BrowseIntent.EndReached) }

    Scaffold(
        modifier = modifier,
        topBar = {
            AppTopBarView(
                onBack = if (state.isScopedPage) ({ onIntent(BrowseIntent.BackClicked) }) else null,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent,
    ) { padding ->
        PullToRefreshView(
            isRefreshing = state.isRefreshing,
            onRefresh = { onIntent(BrowseIntent.Refresh) },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            BrowseGrid(state, actions, gridState, columns, margin)
        }
    }
    state.filterSheet?.let { sheet ->
        FilterSheetView(
            sheet = sheet,
            onDraftChange = actions.onDraftChange,
            onReset = actions.onFiltersReset,
            onApply = actions.onFiltersApply,
            onDismiss = actions.onFiltersDismiss,
        )
    }
}

/** Callbacks for [BrowseScreen], each sending one intent. */
private fun browseActions(onIntent: (BrowseIntent) -> Unit) = BrowseActions(

    onQueryChange = { onIntent(BrowseIntent.QueryChanged(it)) },
    onSearch = { onIntent(BrowseIntent.SearchSubmitted) },
    onClear = { onIntent(BrowseIntent.QueryCleared) },
    onSortSelect = { onIntent(BrowseIntent.SortSelected(it)) },
    onSortDirectionToggle = { onIntent(BrowseIntent.SortDirectionToggled) },
    onNovelClick = { onIntent(BrowseIntent.NovelClicked(it)) },
    onRetry = { onIntent(BrowseIntent.Retry) },
    onLoadMoreRetry = { onIntent(BrowseIntent.LoadMoreRetried) },
    onDescriptionToggle = { onIntent(BrowseIntent.DescriptionToggled) },
    onLinkClick = { onIntent(BrowseIntent.LinkClicked(it)) },
    onFiltersClick = { onIntent(BrowseIntent.FiltersOpened) },
    onDraftChange = { onIntent(BrowseIntent.FilterDraftChanged(it)) },
    onFiltersReset = { onIntent(BrowseIntent.FiltersReset) },
    onFiltersApply = { onIntent(BrowseIntent.FiltersApplied) },
    onFiltersDismiss = { onIntent(BrowseIntent.FiltersDismissed) },
    onActiveFilterRemove = { onIntent(BrowseIntent.ActiveFilterRemoved(it)) },
    onActiveFiltersClear = { onIntent(BrowseIntent.ActiveFiltersCleared) },
)

@Composable
private fun BrowseGrid(
    state: BrowseState,
    actions: BrowseActions,
    gridState: LazyGridState,
    columns: Int,
    margin: Dp,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        state = gridState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = margin, vertical = VndbTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.gutter),
        verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md),
    ) {
        filters(state, actions)
        results(state, actions)
    }
}

/** Asks for the next page once the last visible item is near the end of the grid. */
@Composable
private fun LoadMoreTrigger(gridState: LazyGridState, onLoadMore: () -> Unit) {
    val currentOnLoadMore by rememberUpdatedState(onLoadMore)
    val nearEnd by remember(gridState) {
        derivedStateOf {
            val info = gridState.layoutInfo
            val lastVisible =
                info.visibleItemsInfo.lastOrNull()?.index ?: return@derivedStateOf false
            info.totalItemsCount > 0 && lastVisible >= info.totalItemsCount - LOAD_MORE_THRESHOLD
        }
    }
    LaunchedEffect(nearEnd) {
        if (nearEnd) currentOnLoadMore()
    }
}

private fun LazyGridScope.fullWidthItem(key: String, content: @Composable () -> Unit) {
    item(key = key, contentType = key, span = { GridItemSpan(maxLineSpan) }) { content() }
}

private fun LazyGridScope.filters(state: BrowseState, actions: BrowseActions) {
    header(state, actions)
    fullWidthItem("search") {
        Row(
            horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SearchFieldView(
                value = state.query,
                onValueChange = actions.onQueryChange,
                onSearch = actions.onSearch,
                onClear = actions.onClear,
                modifier = Modifier.weight(1f),
            )
            FilterButtonView(
                activeCount = state.activeFilters.size,
                onClick = actions.onFiltersClick,
            )
        }
    }
    if (state.activeFilters.isNotEmpty()) {
        fullWidthItem("active-filters") {
            ActiveFiltersRowView(
                filters = state.activeFilters,
                onRemove = actions.onActiveFilterRemove,
                onClearAll = actions.onActiveFiltersClear,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
    fullWidthItem("sort") {
        SortRowView(
            resultCountLabel = state.resultCountLabel,
            sort = state.sort,
            isAscending = state.isSortAscending,
            sortOptions = state.sortOptions,
            onSortSelect = actions.onSortSelect,
            onDirectionToggle = actions.onSortDirectionToggle,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** On a tag or producer page: what it is, above the usual filters. */
private fun LazyGridScope.header(state: BrowseState, actions: BrowseActions) {
    val header = state.header ?: return
    fullWidthItem(
        "scope-header",
    ) { ScopeHeaderView(model = header, modifier = Modifier.fillMaxWidth()) }
    if (header.links.isNotEmpty()) {
        fullWidthItem("scope-links") {
            LinksRowView(links = header.links, onLinkClick = actions.onLinkClick)
        }
    }
    header.description?.let { description ->
        fullWidthItem("scope-description") {
            ExpandableTextView(
                title = header.aboutTitle,
                text = description,
                isExpanded = state.isDescriptionExpanded,
                onToggle = actions.onDescriptionToggle,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private fun LazyGridScope.results(state: BrowseState, actions: BrowseActions) {
    when {
        state.isLoading -> repeat(SKELETON_COUNT) { index ->
            fullWidthItem("loading-$index") {
                SkeletonRowView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (index == 0) Modifier.loadingSemantics() else Modifier),
                    coverWidth = SkeletonCoverWidth,
                )
            }
        }

        state.error != null -> fullWidthItem("error") {
            ErrorView(
                model = state.error,
                onRetry = actions.onRetry,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        state.showEmpty -> fullWidthItem("empty") { EmptyResults() }

        else -> {
            items(items = state.results, key = { it.id }, contentType = { "novel" }) { novel ->
                BrowseNovelCardView(
                    model = novel,
                    onClick = { actions.onNovelClick(novel.id) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            when {
                state.isLoadingMore -> fullWidthItem("loading-more") { CenteredProgress() }

                state.loadMoreFailed -> fullWidthItem(
                    "load-more-failed",
                ) { LoadMoreFailed(actions.onLoadMoreRetry) }
            }
        }
    }
}

@Composable
private fun CenteredProgress() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(VndbTheme.spacing.xl),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = VndbTheme.colors.accent)
    }
}

@Composable
private fun EmptyResults() {
    Text(
        text = UiText.StringResource(StringResource.browse_no_results).asString(),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(VndbTheme.spacing.xl),
    )
}

@Composable
private fun LoadMoreFailed(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(VndbTheme.spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = UiText.StringResource(StringResource.browse_load_more_failed).asString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        TextButton(onClick = onRetry) {
            Text(
                text = UiText.StringResource(StringResource.action_retry).asString(),
                color = VndbTheme.colors.accent,
            )
        }
    }
}

private val previewCards = listOf(
    "Steins;Gate",
    "Fate/stay night",
    "CLANNAD",
).mapIndexed { index, title ->
    BrowseNovelCardUIModel(
        id = "v$index",
        title = UiText.StringValue(title),
        creditsLabel = UiText.StringValue("Studio"),
        yearLabel = UiText.StringValue("200$index"),
        ratingLabel = UiText.StringValue("8.9$index"),
        lengthLabel = UiText.StringValue("30-50h"),
        coverUrl = null,
        isCoverSensitive = false,
        tags = persistentListOf(UiText.StringValue("Romance"), UiText.StringValue("Drama")),
    )
}.toImmutableList()

private val previewFilters = BrowseState(
    sortOptions = persistentListOf(
        SortOptionUIModel(BrowseSort.Popularity, UiText.StringValue("Popularity")),
    ),
    activeFilters = persistentListOf(
        ActiveFilterUIModel(ActiveFilterKey.Age, UiText.StringValue("18+")),
        ActiveFilterUIModel(
            ActiveFilterKey.GenreFilter(Genre.Mystery),
            UiText.StringValue("Mystery"),
        ),
    ),
)

private class BrowseStateProvider : PreviewParameterProvider<BrowseState> {
    override val values = sequenceOf(
        previewFilters.copy(
            results = previewCards,
            resultCountLabel = UiText.StringValue("Found 142 titles"),
            canLoadMore = true,
        ),
        previewFilters.copy(isLoading = true),
        previewFilters.copy(
            error = ErrorUIModel(
                UiText.StringResource(StringResource.error_network),
                isRetryable = true,
            ),
        ),
    )
}

@AdaptivePreviews
@Composable
private fun BrowseScreenPreview(@PreviewParameter(BrowseStateProvider::class) state: BrowseState) {
    VndbTheme {
        BrowseScreen(state = state, onIntent = {})
    }
}
