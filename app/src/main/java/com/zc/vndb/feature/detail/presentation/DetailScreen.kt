package com.zc.vndb.feature.detail.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.zc.vndb.common.ui.component.apptopbar.AppTopBarView
import com.zc.vndb.common.ui.component.coverimage.CoverImageView
import com.zc.vndb.common.ui.component.error.ErrorView
import com.zc.vndb.common.ui.component.error.model.ErrorUIModel
import com.zc.vndb.common.ui.component.expandabletext.ExpandableTextView
import com.zc.vndb.common.ui.component.linksrow.LinksRowView
import com.zc.vndb.common.ui.component.linksrow.model.LinkUIModel
import com.zc.vndb.common.ui.component.sectioncard.SectionCardView
import com.zc.vndb.common.ui.component.skeleton.PageSkeletonView
import com.zc.vndb.common.ui.component.spoilerfilter.SpoilerFilterView
import com.zc.vndb.common.ui.component.spoilerfilter.model.SpoilerLevel
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.detail.presentation.component.alsoknownas.AlsoKnownAsView
import com.zc.vndb.feature.detail.presentation.component.charactercard.CharacterCardView
import com.zc.vndb.feature.detail.presentation.component.contenttoggles.ContentTogglesView
import com.zc.vndb.feature.detail.presentation.component.detailhero.DetailHeroView
import com.zc.vndb.feature.detail.presentation.component.detailhero.model.DetailHeroUIModel
import com.zc.vndb.feature.detail.presentation.component.detailhero.model.HeroDeveloperUIModel
import com.zc.vndb.feature.detail.presentation.component.detailtabs.DetailTabRowView
import com.zc.vndb.feature.detail.presentation.component.editionrow.EditionRowView
import com.zc.vndb.feature.detail.presentation.component.imageviewer.ImageViewerView
import com.zc.vndb.feature.detail.presentation.component.lazysection.LazySectionView
import com.zc.vndb.feature.detail.presentation.component.mylist.MyListCardView
import com.zc.vndb.feature.detail.presentation.component.quotecard.QuoteCardView
import com.zc.vndb.feature.detail.presentation.component.relationcard.RelationCardView
import com.zc.vndb.feature.detail.presentation.component.screenshotgrid.ScreenshotGridView
import com.zc.vndb.feature.detail.presentation.component.staffrole.StaffRoleView
import com.zc.vndb.feature.detail.presentation.component.taggroups.TagGroupsView
import com.zc.vndb.feature.detail.presentation.model.CastUIModel
import com.zc.vndb.feature.detail.presentation.model.ContentFilter
import com.zc.vndb.feature.detail.presentation.model.DetailSection
import com.zc.vndb.feature.detail.presentation.model.DetailTab
import com.zc.vndb.feature.detail.presentation.model.DetailTabUIModel
import com.zc.vndb.feature.detail.presentation.model.MediaImageUIModel
import com.zc.vndb.feature.detail.presentation.model.PagedListUIState
import com.zc.vndb.feature.detail.presentation.model.SectionState
import com.zc.vndb.feature.detail.presentation.model.TagChipUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/** The page stays readable on wide screens instead of stretching edge to edge. */
private val MaxContentWidth = 720.dp
private val CoverThumbWidth = 104.dp
private val StaffLabelWidth = 112.dp
private const val COMPACT_COLUMNS = 2
private const val WIDE_COLUMNS = 3

/** Callbacks built once from `onIntent`, so rows and cards get stable lambdas. */
@Immutable
private class DetailActions(
    val onIntent: (DetailIntent) -> Unit,
    val onLinkClick: (String) -> Unit,
    val onSpoilerLevelSelect: (SpoilerLevel) -> Unit,
    val onRelationClick: (String) -> Unit,
    val onCharacterClick: (String) -> Unit,
    val onTagClick: (String) -> Unit,
    val onDeveloperClick: (String) -> Unit,
    val onStaffClick: (String) -> Unit,
    val onEditionClick: (String) -> Unit,
    val onTabSelect: (DetailTab) -> Unit,
    val onFilterToggle: (ContentFilter) -> Unit,
    val onScreenshotClick: (Int) -> Unit,
    val onCoverClick: (Int) -> Unit,
) {
    fun needed(section: DetailSection) = onIntent(DetailIntent.SectionNeeded(section))

    fun retry(section: DetailSection) = onIntent(DetailIntent.SectionRetried(section))

    fun loadMore(section: DetailSection) = onIntent(DetailIntent.LoadMoreClicked(section))
}

@Composable
fun DetailScreen(
    state: DetailState,
    onIntent: (DetailIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val actions = remember(onIntent) { detailActions(onIntent) }
    Scaffold(
        modifier = modifier,
        topBar = { DetailTopBar(onIntent) },
        containerColor = Color.Transparent,
    ) { padding ->
        val contentModifier = Modifier
            .padding(padding)
            .fillMaxSize()
        when {
            state.error != null -> ErrorView(
                model = state.error,
                onRetry = { onIntent(DetailIntent.Retry) },
                modifier = contentModifier,
            )

            state.hero == null -> PageSkeletonView(
                modifier = contentModifier,
                maxWidth = MaxContentWidth,
            )

            else -> DetailContent(state, actions, contentModifier)
        }
    }
    state.viewer?.let { viewer ->
        ImageViewerView(
            state = viewer,
            onDismiss = { onIntent(DetailIntent.ViewerDismissed) },
        )
    }
}

@Composable
private fun DetailContent(
    state: DetailState,
    actions: DetailActions,
    modifier: Modifier = Modifier,
) {
    val hero = state.hero ?: return
    val isWide = currentWindowAdaptiveInfoV2().windowSizeClass
        .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
    val columns = if (isWide) WIDE_COLUMNS else COMPACT_COLUMNS
    Box(modifier, contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = MaxContentWidth)
                .fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = VndbTheme.spacing.margin,
                vertical = VndbTheme.spacing.sm,
            ),
            verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md),
        ) {
            header(state, hero, actions)
            stickyHeader(key = "tabs", contentType = "tabs") {
                DetailTabRowView(
                    tabs = state.tabs,
                    selected = state.selectedTab,
                    onSelect = actions.onTabSelect,
                )
            }
            when (state.selectedTab) {
                DetailTab.Overview -> overviewTab(state, actions, columns)
                DetailTab.Media -> mediaTab(state, actions, columns)
                DetailTab.Cast -> castTab(state, actions)
                DetailTab.Releases -> releasesTab(state, actions)
                DetailTab.Quotes -> quotesTab(state, actions)
            }
        }
    }
}

private fun detailActions(onIntent: (DetailIntent) -> Unit) = DetailActions(
    onIntent = onIntent,
    onLinkClick = { onIntent(DetailIntent.LinkClicked(it)) },
    onSpoilerLevelSelect = { onIntent(DetailIntent.SpoilerLevelSelected(it)) },
    onRelationClick = { onIntent(DetailIntent.RelationClicked(it)) },
    onCharacterClick = { onIntent(DetailIntent.CharacterClicked(it)) },
    onTagClick = { onIntent(DetailIntent.TagClicked(it)) },
    onDeveloperClick = { onIntent(DetailIntent.DeveloperClicked(it)) },
    onStaffClick = { onIntent(DetailIntent.StaffClicked(it)) },
    onEditionClick = { onIntent(DetailIntent.EditionClicked(it)) },
    onTabSelect = { onIntent(DetailIntent.TabSelected(it)) },
    onFilterToggle = { onIntent(DetailIntent.ContentFilterToggled(it)) },
    onScreenshotClick = { onIntent(DetailIntent.ScreenshotClicked(it)) },
    onCoverClick = { onIntent(DetailIntent.CoverClicked(it)) },
)

@Composable
private fun DetailTopBar(onIntent: (DetailIntent) -> Unit) {
    AppTopBarView(
        onBack = { onIntent(DetailIntent.BackClicked) },
        actions = {
            IconButton(onClick = { onIntent(DetailIntent.ShareClicked) }) {
                Icon(
                    Icons.Outlined.Share,
                    contentDescription = UiText.StringResource(
                        StringResource.detail_share,
                    ).asString(),
                )
            }
            IconButton(onClick = { onIntent(DetailIntent.OpenInBrowserClicked) }) {
                Icon(
                    Icons.AutoMirrored.Outlined.OpenInNew,
                    contentDescription = UiText.StringResource(
                        StringResource.detail_open_in_browser,
                    ).asString(),
                )
            }
        },
    )
}

private fun LazyListScope.header(
    state: DetailState,
    hero: DetailHeroUIModel,
    actions: DetailActions,
) {
    item(
        key = "hero",
        contentType = "hero",
    ) {
        DetailHeroView(
            model = hero,
            onDeveloperClick = actions.onDeveloperClick,
            onTagClick = actions.onTagClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
    state.myList?.let { myList ->
        item(key = "my-list", contentType = "my-list") {
            MyListCardView(
                model = myList,
                onEdit = { actions.onIntent(DetailIntent.MyListClicked) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// region Overview

private fun LazyListScope.overviewTab(state: DetailState, actions: DetailActions, columns: Int) {
    if (state.links.isNotEmpty()) {
        item(
            key = "links",
            contentType = "links",
        ) { LinksRowView(links = state.links, onLinkClick = actions.onLinkClick) }
    }
    spoilerFilter(state, actions)
    state.synopsis?.let { synopsis ->
        item(key = "synopsis", contentType = "synopsis") {
            ExpandableTextView(
                title = UiText.StringResource(StringResource.detail_synopsis),
                text = synopsis,
                isExpanded = state.isSynopsisExpanded,
                onToggle = { actions.onIntent(DetailIntent.SynopsisToggled) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
    state.alsoKnownAs?.let { aka ->
        item(
            key = "aka",
            contentType = "aka",
        ) { AlsoKnownAsView(model = aka, modifier = Modifier.fillMaxWidth()) }
    }
    item(key = "tags", contentType = "tags") {
        TagGroupsView(
            groups = state.tagGroups,
            hiddenLabel = state.hiddenTagsLabel,
            notes = state.contentNotes,
            onTagClick = actions.onTagClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
    item(
        key = "media-preview",
        contentType = "media-preview",
    ) { MediaPreview(state, actions, columns) }
    item(
        key = "characters-preview",
        contentType = "characters-preview",
    ) { CharactersPreview(state, actions) }
    item(key = "editions", contentType = "editions") { EditionsPreview(state, actions) }
    relationsSection(state, actions)
}

private fun LazyListScope.relationsSection(state: DetailState, actions: DetailActions) {
    if (state.relations.isNotEmpty()) {
        item(key = "relations", contentType = "relations") {
            SectionCardView(
                title = UiText.StringResource(StringResource.detail_relations),
                modifier = Modifier.fillMaxWidth(),
            ) {
                // A plain scrolling row (relations are few) so every card can take the tallest card's height.
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
                ) {
                    state.relations.forEach { relation ->
                        key(relation.id) {
                            RelationCardView(
                                model = relation,
                                onClick = { actions.onRelationClick(relation.id) },
                                modifier = Modifier.fillMaxHeight(),
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun LazyListScope.spoilerFilter(state: DetailState, actions: DetailActions) {
    item(key = "spoilers", contentType = "spoilers") {
        SpoilerFilterView(
            level = state.spoilerLevel,
            onLevelSelect = actions.onSpoilerLevelSelect,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** First screenshots, loaded when this card scrolls into view; "See all" opens the Media tab. */
@Composable
private fun MediaPreview(state: DetailState, actions: DetailActions, columns: Int) {
    val media = (state.screenshots as? SectionState.Loaded)?.data
    if (media != null && media.totalCount == 0) return
    SectionCardView(
        title = UiText.StringResource(StringResource.detail_screenshots),
        trailing = media?.takeIf { it.visibleCount > 0 }?.let {
            {
                SeeAllButton(
                    UiText.StringResource(
                        StringResource.detail_see_all_count,
                        listOf(it.visibleCount),
                    ),
                ) { actions.onTabSelect(DetailTab.Media) }
            }
        },
        modifier = Modifier.fillMaxWidth(),
    ) {
        LazySectionView(
            state = state.screenshots,
            onLoad = { actions.needed(DetailSection.Screenshots) },
            onRetry = { actions.retry(DetailSection.Screenshots) },
        ) { loaded ->
            ScreenshotGridView(
                images = loaded.preview,
                columns = columns,
                onImageClick = actions.onScreenshotClick,
            )
            loaded.hiddenLabel?.let { MutedNote(it) }
        }
    }
}

/** Main and primary characters, loaded when this card scrolls into view. */
@Composable
private fun CharactersPreview(state: DetailState, actions: DetailActions) {
    val cast = (state.cast as? SectionState.Loaded)?.data
    if (cast != null && cast.characterCount == 0) return
    SectionCardView(
        title = UiText.StringResource(StringResource.detail_characters),
        trailing = cast?.mainCountLabel?.let { label ->
            { SeeAllButton(label) { actions.onTabSelect(DetailTab.Cast) } }
        },
        modifier = Modifier.fillMaxWidth(),
    ) {
        LazySectionView(
            state = state.cast,
            onLoad = { actions.needed(DetailSection.Cast) },
            onRetry = { actions.retry(DetailSection.Cast) },
        ) { loaded ->
            LazyRow(horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md)) {
                items(items = loaded.preview, key = { it.id }) { character ->
                    CharacterCardView(
                        model = character,
                        onClick = { actions.onCharacterClick(character.id) },
                        onVoiceActorClick = actions.onStaffClick,
                    )
                }
            }
            loaded.hiddenLabel?.let { MutedNote(it) }
        }
    }
}

/** One edition per platform, loaded when this card scrolls into view (it also brings the age chip). */
@Composable
private fun EditionsPreview(state: DetailState, actions: DetailActions) {
    val editions = (state.editions as? SectionState.Loaded)?.data
    if (editions != null && editions.isEmpty()) return
    SectionCardView(
        title = UiText.StringResource(StringResource.detail_releases),
        trailing = editions?.let {
            {
                SeeAllButton(
                    UiText.StringResource(StringResource.discover_see_all),
                ) { actions.onTabSelect(DetailTab.Releases) }
            }
        },
        modifier = Modifier.fillMaxWidth(),
    ) {
        LazySectionView(
            state = state.editions,
            onLoad = { actions.needed(DetailSection.Editions) },
            onRetry = { actions.retry(DetailSection.Editions) },
        ) { loaded ->
            loaded.forEach { edition ->
                EditionRowView(
                    model = edition,
                    onClick = { actions.onEditionClick(edition.releaseId) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

// endregion

// region Media

private fun LazyListScope.mediaTab(state: DetailState, actions: DetailActions, columns: Int) {
    item(key = "screenshots", contentType = "screenshots") {
        LazySectionView(
            state = state.screenshots,
            onLoad = { actions.needed(DetailSection.Screenshots) },
            onRetry = { actions.retry(DetailSection.Screenshots) },
        ) { media ->
            SectionCardView(
                title = UiText.StringResource(StringResource.detail_screenshots),
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (media.totalCount == 0) {
                    MutedNote(UiText.StringResource(StringResource.detail_no_screenshots))
                } else {
                    ContentTogglesView(
                        sexual = media.sexualToggles,
                        violence = media.violenceToggles,
                        onToggle = actions.onFilterToggle,
                    )
                    media.groups.forEach { group ->
                        group.title?.let { title ->
                            Text(
                                text = title.asString(),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = VndbTheme.spacing.xs),
                            )
                        }
                        ScreenshotGridView(
                            images = group.screenshots,
                            columns = columns,
                            onImageClick = actions.onScreenshotClick,
                        )
                    }
                    media.hiddenLabel?.let { MutedNote(it) }
                }
            }
        }
    }
    item(key = "covers", contentType = "covers") {
        SectionCardView(
            title = UiText.StringResource(StringResource.detail_covers),
            modifier = Modifier.fillMaxWidth(),
        ) {
            LazySectionView(
                state = state.covers,
                onLoad = { actions.needed(DetailSection.Covers) },
                onRetry = { actions.retry(DetailSection.Covers) },
            ) { covers -> CoverCarousel(covers, actions.onCoverClick) }
        }
    }
}

@Composable
private fun CoverCarousel(covers: ImmutableList<MediaImageUIModel>, onCoverClick: (Int) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm)) {
        items(items = covers, key = { it.url }) { cover ->
            CoverImageView(
                url = cover.thumbnailUrl ?: cover.url,
                isSensitive = cover.isSensitive,
                contentDescription = cover.caption,
                modifier = Modifier
                    .width(CoverThumbWidth)
                    .aspectRatio(VndbTheme.dimens.coverAspectRatio)
                    .clickable { onCoverClick(cover.index) },
            )
        }
    }
}

// endregion

// region Cast & Staff

private fun LazyListScope.castTab(state: DetailState, actions: DetailActions) {
    spoilerFilter(state, actions)
    item(key = "cast", contentType = "cast") {
        LazySectionView(
            state = state.cast,
            onLoad = { actions.needed(DetailSection.Cast) },
            onRetry = { actions.retry(DetailSection.Cast) },
        ) { cast -> CastContent(cast, actions) }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CastContent(cast: CastUIModel, actions: DetailActions) {
    Column(verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md)) {
        if (cast.characterGroups.isEmpty()) {
            SectionCardView(
                title = UiText.StringResource(StringResource.detail_characters),
                modifier = Modifier.fillMaxWidth(),
            ) {
                MutedNote(UiText.StringResource(StringResource.detail_no_characters))
            }
        }
        cast.characterGroups.forEach { group ->
            SectionCardView(title = group.title, modifier = Modifier.fillMaxWidth()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md),
                    verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md),
                ) {
                    group.characters.forEach { character ->
                        CharacterCardView(
                            model = character,
                            onClick = { actions.onCharacterClick(character.id) },
                            onVoiceActorClick = actions.onStaffClick,
                        )
                    }
                }
            }
        }
        cast.hiddenLabel?.let { MutedNote(it) }
        cast.staffGroups.forEach { group ->
            SectionCardView(title = group.title, modifier = Modifier.fillMaxWidth()) {
                group.credits.forEach { credit ->
                    StaffRoleView(
                        role = credit,
                        onPersonClick = actions.onStaffClick,
                        labelWidth = StaffLabelWidth,
                    )
                }
            }
        }
    }
}

// endregion

// region Releases & Quotes

private fun LazyListScope.releasesTab(state: DetailState, actions: DetailActions) {
    val releases = (state.releases as? SectionState.Loaded)?.data
    if (releases == null) {
        item(key = "releases-loading", contentType = "section-state") {
            LazySectionView(
                state = state.releases,
                onLoad = { actions.needed(DetailSection.Releases) },
                onRetry = { actions.retry(DetailSection.Releases) },
            ) { }
        }
        return
    }
    if (releases.items.isEmpty()) {
        item(
            key = "releases-empty",
            contentType = "empty",
        ) { MutedNote(UiText.StringResource(StringResource.detail_no_releases)) }
    }
    releases.items.forEach { group ->
        item(key = "releases-${group.languageCode}", contentType = "release-group") {
            SectionCardView(title = group.title, modifier = Modifier.fillMaxWidth()) {
                group.releases.forEach { release ->
                    EditionRowView(
                        model = release,
                        onClick = { actions.onEditionClick(release.releaseId) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
    loadMore(releases, DetailSection.Releases, actions)
}

private fun LazyListScope.quotesTab(state: DetailState, actions: DetailActions) {
    val quotes = (state.quotes as? SectionState.Loaded)?.data
    if (quotes == null) {
        item(key = "quotes-loading", contentType = "section-state") {
            LazySectionView(
                state = state.quotes,
                onLoad = { actions.needed(DetailSection.Quotes) },
                onRetry = { actions.retry(DetailSection.Quotes) },
            ) { }
        }
    } else {
        if (quotes.items.isEmpty()) {
            item(
                key = "quotes-empty",
                contentType = "empty",
            ) { MutedNote(UiText.StringResource(StringResource.detail_no_quotes)) }
        }
        items(items = quotes.items, key = { it.id }, contentType = { "quote" }) { quote ->
            QuoteCardView(
                model = quote,
                onCharacterClick = actions.onCharacterClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        loadMore(quotes, DetailSection.Quotes, actions)
    }
    item(key = "reviews", contentType = "reviews") {
        SectionCardView(
            title = UiText.StringResource(StringResource.detail_reviews_link),
            modifier = Modifier.fillMaxWidth(),
        ) {
            MutedNote(UiText.StringResource(StringResource.detail_reviews_hint))
            // No start padding, so the text lines up with the note above.
            TextButton(
                onClick = { actions.onIntent(DetailIntent.ReviewsClicked) },
                contentPadding = PaddingValues(end = 12.dp, top = 8.dp, bottom = 8.dp),
            ) {
                Text(
                    text = UiText.StringResource(StringResource.detail_open_in_browser).asString(),
                    color = VndbTheme.colors.accent,
                )
            }
        }
    }
}

private fun <T> LazyListScope.loadMore(
    list: PagedListUIState<T>,
    section: DetailSection,
    actions: DetailActions,
) {
    if (!list.hasMore) return
    item(key = "load-more-${section.name}", contentType = "load-more") {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (list.isLoadingMore) {
                CircularProgressIndicator(color = VndbTheme.colors.accent)
            } else {
                TextButton(onClick = { actions.loadMore(section) }) {
                    Text(
                        text = UiText.StringResource(
                            if (list.loadMoreFailed) StringResource.action_retry else StringResource.detail_load_more,
                        ).asString(),
                        color = VndbTheme.colors.accent,
                    )
                }
            }
        }
    }
}

// endregion

@Composable
private fun SeeAllButton(label: UiText, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(
            text = label.asString(),
            style = MaterialTheme.typography.labelLarge,
            color = VndbTheme.colors.accent,
        )
    }
}

@Composable
private fun MutedNote(text: UiText) {
    Text(
        text = text.asString(),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

private val previewState = DetailState(
    novelId = "v2002",
    tabs = DetailTab.entries.map {
        DetailTabUIModel(
            it,
            UiText.StringValue(it.name),
        )
    }.toImmutableList(),
    hero = DetailHeroUIModel(
        title = UiText.StringValue("Steins;Gate"),
        originalTitle = UiText.StringValue("シュタインズ・ゲート"),
        coverUrl = null,
        isCoverSensitive = false,
        developers = persistentListOf(
            HeroDeveloperUIModel(id = "p1", name = "5pb."),
            HeroDeveloperUIModel(id = null, name = "Nitroplus"),
        ),
        releasedLabel = UiText.StringValue("2009-10-15"),
        lengthName = UiText.StringValue("Long"),
        lengthValueLabel = UiText.StringValue("(44h)"),
        lengthVotesLabel = UiText.StringValue("1,021 length votes"),
        ageLabel = UiText.StringValue("All Ages / 15+"),
        tags = persistentListOf(
            TagChipUIModel("g105", UiText.StringValue("Sci-Fi")),
            TagChipUIModel("g230", UiText.StringValue("Time Travel")),
        ),
    ),
    links = persistentListOf(
        LinkUIModel(UiText.StringValue("vndb.org"), "https://vndb.org/v2002", isVndb = true),
    ),
    synopsis = UiText.StringValue(
        "Set in Akihabara in summer 2010, self-proclaimed mad scientist Rintaro Okabe…",
    ),
)

private class DetailStateProvider : PreviewParameterProvider<DetailState> {
    override val values = sequenceOf(
        previewState,
        previewState.copy(selectedTab = DetailTab.Quotes),
        DetailState(novelId = "v2002", isLoading = true),
        DetailState(
            novelId = "v2002",
            error = ErrorUIModel(
                UiText.StringResource(StringResource.error_not_found),
                isRetryable = false,
            ),
        ),
    )
}

@AdaptivePreviews
@Composable
private fun DetailScreenPreview(@PreviewParameter(DetailStateProvider::class) state: DetailState) {
    VndbTheme {
        DetailScreen(state = state, onIntent = {})
    }
}
