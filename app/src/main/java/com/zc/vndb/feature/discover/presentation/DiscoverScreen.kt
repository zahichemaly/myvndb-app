package com.zc.vndb.feature.discover.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.zc.vndb.common.ui.component.apptopbar.AppTopBarView
import com.zc.vndb.common.ui.component.error.ErrorView
import com.zc.vndb.common.ui.component.error.model.ErrorUIModel
import com.zc.vndb.common.ui.component.platformicon.model.PlatformIcon
import com.zc.vndb.common.ui.component.pulltorefresh.PullToRefreshView
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.theme.cardBackground
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.discover.presentation.component.discoverskeleton.DiscoverSkeletonView
import com.zc.vndb.feature.discover.presentation.component.featurednovel.FeaturedNovelView
import com.zc.vndb.feature.discover.presentation.component.featurednovel.model.FeaturedNovelUIModel
import com.zc.vndb.feature.discover.presentation.component.novelcard.NovelCardView
import com.zc.vndb.feature.discover.presentation.component.novelcard.model.NovelCardUIModel
import com.zc.vndb.feature.discover.presentation.component.quoteoftheday.QuoteOfTheDayView
import com.zc.vndb.feature.discover.presentation.component.sectionheader.SectionHeaderView
import com.zc.vndb.feature.discover.presentation.component.statsfooter.StatsFooterView
import com.zc.vndb.feature.discover.presentation.component.trendingrelease.TrendingReleaseView
import com.zc.vndb.feature.discover.presentation.component.trendingrelease.model.ReleaseKindTone
import com.zc.vndb.feature.discover.presentation.component.trendingrelease.model.TrendingReleaseUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

private val RollProgressSize = 16.dp

/** Callbacks built once from `onIntent`, so rows and cards get stable lambdas. */
@Immutable
private class DiscoverActions(
    val onNovelClick: (String) -> Unit,
    val onReleaseClick: (String) -> Unit,
    val onSeeAllTopRatedClick: () -> Unit,
    val onSeeAllTrendingClick: () -> Unit,
    val onStatsClick: () -> Unit,
    val onRollClick: () -> Unit,
)

@Composable
fun DiscoverScreen(
    state: DiscoverState,
    onIntent: (DiscoverIntent) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val isExpanded = currentWindowAdaptiveInfoV2().windowSizeClass
        .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    val actions = remember(onIntent) {
        DiscoverActions(
            onNovelClick = { id -> onIntent(DiscoverIntent.NovelClicked(id)) },
            onReleaseClick = { id -> onIntent(DiscoverIntent.ReleaseClicked(id)) },
            onSeeAllTopRatedClick = { onIntent(DiscoverIntent.SeeAllTopRatedClicked) },
            onSeeAllTrendingClick = { onIntent(DiscoverIntent.SeeAllTrendingClicked) },
            onStatsClick = { onIntent(DiscoverIntent.StatsClicked) },
            onRollClick = { onIntent(DiscoverIntent.RandomRollClicked) },
        )
    }
    Scaffold(
        modifier = modifier,
        topBar = { AppTopBarView() },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent,
    ) { padding ->
        val contentModifier = Modifier
            .padding(padding)
            .fillMaxSize()
        when {
            state.error != null -> ErrorView(
                model = state.error,
                onRetry = { onIntent(DiscoverIntent.Retry) },
                modifier = contentModifier,
            )

            state.isLoading -> DiscoverSkeletonView(
                isExpanded = isExpanded,
                modifier = contentModifier,
            )

            else -> PullToRefreshView(
                isRefreshing = state.isRefreshing,
                onRefresh = { onIntent(DiscoverIntent.Refresh) },
                modifier = contentModifier,
            ) {
                DiscoverContent(
                    state = state,
                    actions = actions,
                    isExpanded = isExpanded,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun DiscoverContent(
    state: DiscoverState,
    actions: DiscoverActions,
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val margin = if (isExpanded) VndbTheme.spacing.marginDesktop else VndbTheme.spacing.margin
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = margin,
            end = margin,
            bottom = VndbTheme.spacing.lg,
        ),
        verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md),
    ) {
        item(key = "header", contentType = "header") {
            DiscoverHeader(isRolling = state.isRolling, onRollClick = actions.onRollClick)
        }
        if (isExpanded) {
            expandedSections(state, actions)
        } else {
            compactSections(state, actions)
        }
        state.statsLabel?.let { label ->
            item(key = "stats", contentType = "stats") {
                StatsFooterView(
                    label = label,
                    onClick = actions.onStatsClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = VndbTheme.spacing.md),
                )
            }
        }
        state.quote?.let { quote ->
            item(key = "quote", contentType = "quote") {
                QuoteOfTheDayView(
                    model = quote,
                    onClick = { actions.onNovelClick(quote.novelId) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

private fun LazyListScope.compactSections(state: DiscoverState, actions: DiscoverActions) {
    state.featured?.let { featured ->
        item(key = "featured", contentType = "featured") {
            FeaturedNovelView(
                model = featured,
                onClick = { actions.onNovelClick(featured.id) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
    topRatedSection(state.topRated, actions)
    if (state.trending.isNotEmpty()) {
        item(
            key = "trending-header",
            contentType = "section-header",
        ) { TrendingHeader(state.trendingLanguagesLabel, actions.onSeeAllTrendingClick) }
        item(key = "trending", contentType = "trending") {
            TrendingList(state.trending, actions, Modifier.fillMaxWidth())
        }
    }
}

private fun LazyListScope.expandedSections(state: DiscoverState, actions: DiscoverActions) {
    item(key = "featured-and-trending", contentType = "featured-and-trending") {
        Row(horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.gutterDesktop)) {
            state.featured?.let { featured ->
                FeaturedNovelView(
                    model = featured,
                    onClick = { actions.onNovelClick(featured.id) },
                    modifier = Modifier.weight(1f),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md),
            ) {
                if (state.trending.isNotEmpty()) {
                    TrendingHeader(state.trendingLanguagesLabel, actions.onSeeAllTrendingClick)
                    TrendingList(state.trending, actions, Modifier.fillMaxWidth())
                }
            }
        }
    }
    topRatedSection(state.topRated, actions)
}

private fun LazyListScope.topRatedSection(
    cards: ImmutableList<NovelCardUIModel>,
    actions: DiscoverActions,
) {
    if (cards.isEmpty()) return
    item(key = "top-rated", contentType = "top-rated") {
        Column(verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm)) {
            SectionHeaderView(
                title = UiText.StringResource(StringResource.discover_top_rated),
                icon = Icons.Outlined.EmojiEvents,
                modifier = Modifier.padding(top = VndbTheme.spacing.md),
                onSeeAllClick = actions.onSeeAllTopRatedClick,
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md)) {
                items(items = cards, key = { it.id }) { card ->
                    NovelCardView(model = card, onClick = { actions.onNovelClick(card.id) })
                }
            }
        }
    }
}

/** The Trending languages under the title, and See all where they used to be. */
@Composable
private fun TrendingHeader(languagesLabel: UiText?, onSeeAllClick: () -> Unit) {
    SectionHeaderView(
        title = UiText.StringResource(StringResource.discover_new_and_trending),
        icon = Icons.Outlined.Bolt,
        modifier = Modifier.padding(top = VndbTheme.spacing.md),
        subtitle = languagesLabel,
        onSeeAllClick = onSeeAllClick,
    )
}

/** The trending rows in one card, with a divider between rows. */
@Composable
private fun TrendingList(
    rows: ImmutableList<TrendingReleaseUIModel>,
    actions: DiscoverActions,
    modifier: Modifier = Modifier,
) {
    val shape = MaterialTheme.shapes.medium
    Column(
        modifier = modifier
            .clip(shape)
            .cardBackground()
            .border(VndbTheme.dimens.borderWidth, VndbTheme.colors.borderSubtle, shape),
    ) {
        rows.forEachIndexed { index, row ->
            if (index > 0) {
                HorizontalDivider(
                    thickness = VndbTheme.dimens.borderWidth,
                    color = VndbTheme.colors.borderSubtle,
                )
            }
            TrendingReleaseView(
                model = row,
                onClick = {
                    if (row.opensRelease) {
                        actions.onReleaseClick(row.releaseId)
                    } else {
                        actions.onNovelClick(row.novelId)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun DiscoverHeader(isRolling: Boolean, onRollClick: () -> Unit) {
    Row(verticalAlignment = Alignment.Bottom) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = UiText.StringResource(StringResource.discover_title).asString(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        OutlinedButton(
            onClick = onRollClick,
            enabled = !isRolling,
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(VndbTheme.dimens.borderWidth, VndbTheme.colors.borderSubtle),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = VndbTheme.colors.surfaceCard,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        ) {
            if (isRolling) {
                CircularProgressIndicator(
                    modifier = Modifier.size(RollProgressSize),
                    strokeWidth = 2.dp,
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Casino,
                    contentDescription = null,
                    tint = VndbTheme.colors.score,
                    modifier = Modifier.size(RollProgressSize),
                )
            }
            Spacer(Modifier.size(VndbTheme.spacing.sm))
            Text(
                text = UiText.StringResource(StringResource.discover_random_roll).asString(),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

private val previewFeatured = FeaturedNovelUIModel(
    id = "v2002",
    title = UiText.StringValue("STEINS;GATE"),
    ratingLabel = UiText.StringValue("9.02 / 10"),
    developerLabel = UiText.StringValue("NITRO PLUS"),
    writerLabel = UiText.StringValue("Tanizaki Ouka"),
    description = UiText.StringValue(
        "A rag-tag band of students discover a way to change the past via mail.",
    ),
    coverUrl = null,
    isCoverSensitive = false,
    tags = persistentListOf(UiText.StringValue("Suspense"), UiText.StringValue("Time Travel")),
    ageLabel = UiText.StringValue("All Ages"),
)

private val previewCards = listOf(
    "STEINS;GATE",
    "CLANNAD",
    "Muv-Luv Alternative",
).mapIndexed { index, title ->
    NovelCardUIModel(
        id = "v$index",
        title = UiText.StringValue(title),
        subtitle = UiText.StringValue("Studio"),
        ratingLabel = UiText.StringValue("9.0$index"),
        coverUrl = null,
        isCoverSensitive = index == 2,
    )
}.toImmutableList()

private val previewTrending = ReleaseKindTone.entries.mapIndexed { index, tone ->
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
        dateLabel = UiText.StringValue("Nov 1$index"),
        ratingLabel = UiText.StringValue("8.1$index"),
        thumbnailUrl = null,
        isCoverSensitive = false,
        platformIcons = persistentListOf(PlatformIcon.Windows),
        platformsLabel = UiText.StringValue("Windows"),
    )
}.toImmutableList()

private class DiscoverStateProvider : PreviewParameterProvider<DiscoverState> {
    override val values = sequenceOf(
        DiscoverState(
            featured = previewFeatured,
            topRated = previewCards,
            trending = previewTrending,
            statsLabel = UiText.StringValue("68,053 Visual Novels"),
        ),
        DiscoverState(isLoading = true),
        DiscoverState(
            error = ErrorUIModel(
                UiText.StringResource(StringResource.error_network),
                isRetryable = true,
            ),
        ),
    )
}

@AdaptivePreviews
@Composable
private fun DiscoverScreenPreview(
    @PreviewParameter(
        DiscoverStateProvider::class,
    ) state: DiscoverState,
) {
    VndbTheme {
        DiscoverScreen(state = state, onIntent = {})
    }
}
