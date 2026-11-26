package com.zc.vndb.feature.release.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.component.apptopbar.AppTopBarView
import com.zc.vndb.common.ui.component.chip.TagChipView
import com.zc.vndb.common.ui.component.coverimage.CoverImageView
import com.zc.vndb.common.ui.component.error.ErrorView
import com.zc.vndb.common.ui.component.error.model.ErrorUIModel
import com.zc.vndb.common.ui.component.expandabletext.ExpandableTextView
import com.zc.vndb.common.ui.component.factline.model.FactUIModel
import com.zc.vndb.common.ui.component.linksrow.LinksRowView
import com.zc.vndb.common.ui.component.linksrow.model.LinkUIModel
import com.zc.vndb.common.ui.component.pulltorefresh.PullToRefreshView
import com.zc.vndb.common.ui.component.sectioncard.SectionCardView
import com.zc.vndb.common.ui.component.skeleton.PageSkeletonView
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.release.presentation.component.releasehero.ReleaseHeroView
import com.zc.vndb.feature.release.presentation.component.releasehero.model.ReleaseFlagTone
import com.zc.vndb.feature.release.presentation.component.releasehero.model.ReleaseFlagUIModel
import com.zc.vndb.feature.release.presentation.component.releasehero.model.ReleaseHeroUIModel
import com.zc.vndb.feature.release.presentation.component.releasenovelrow.ReleaseNovelRowView
import com.zc.vndb.feature.release.presentation.component.releasenovelrow.model.ReleaseNovelRowUIModel
import com.zc.vndb.feature.release.presentation.model.ReleaseImageUIModel
import com.zc.vndb.feature.release.presentation.model.ReleaseLanguageUIModel
import com.zc.vndb.feature.release.presentation.model.ReleaseProducerUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/** Same reading width as Title Detail on wide screens. */
private val MaxContentWidth = 720.dp
private val GalleryImageWidth = 120.dp

/** Callbacks built once from `onIntent`, so rows get stable lambdas. */
@Immutable
private class ReleaseActions(
    val onLinkClick: (String) -> Unit,
    val onNovelClick: (String) -> Unit,
    val onProducerClick: (String) -> Unit,
    val onNotesToggle: () -> Unit,
)

@Composable
fun ReleaseScreen(
    state: ReleaseState,
    onIntent: (ReleaseIntent) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val actions = remember(onIntent) {
        ReleaseActions(
            onLinkClick = { onIntent(ReleaseIntent.LinkClicked(it)) },
            onNovelClick = { onIntent(ReleaseIntent.NovelClicked(it)) },
            onProducerClick = { onIntent(ReleaseIntent.ProducerClicked(it)) },
            onNotesToggle = { onIntent(ReleaseIntent.NotesToggled) },
        )
    }
    Scaffold(
        modifier = modifier,
        topBar = { ReleaseTopBar(onIntent) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent,
    ) { padding ->
        val contentModifier = Modifier
            .padding(padding)
            .fillMaxSize()
        when {
            state.error != null -> ErrorView(
                model = state.error,
                onRetry = { onIntent(ReleaseIntent.Retry) },
                modifier = contentModifier,
            )

            state.hero == null -> PageSkeletonView(
                modifier = contentModifier,
                maxWidth = MaxContentWidth,
            )

            else -> PullToRefreshView(
                isRefreshing = state.isRefreshing,
                onRefresh = { onIntent(ReleaseIntent.Refresh) },
                modifier = contentModifier,
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
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
                        releaseSections(state, state.hero, actions)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReleaseTopBar(onIntent: (ReleaseIntent) -> Unit) {
    AppTopBarView(
        onBack = { onIntent(ReleaseIntent.BackClicked) },
        actions = {
            IconButton(onClick = { onIntent(ReleaseIntent.ShareClicked) }) {
                Icon(
                    Icons.Outlined.Share,
                    contentDescription = UiText.StringResource(
                        StringResource.detail_share,
                    ).asString(),
                )
            }
            IconButton(onClick = { onIntent(ReleaseIntent.OpenInBrowserClicked) }) {
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

private fun LazyListScope.releaseSections(
    state: ReleaseState,
    hero: ReleaseHeroUIModel,
    actions: ReleaseActions,
) {
    item(
        key = "hero",
        contentType = "hero",
    ) { ReleaseHeroView(model = hero, modifier = Modifier.fillMaxWidth()) }
    if (state.links.isNotEmpty()) {
        item(
            key = "links",
            contentType = "links",
        ) { LinksRowView(links = state.links, onLinkClick = actions.onLinkClick) }
    }
    if (state.novels.isNotEmpty()) {
        item(key = "novels", contentType = "novels") {
            SectionCardView(
                title = UiText.StringResource(StringResource.release_visual_novels),
                modifier = Modifier.fillMaxWidth(),
            ) {
                state.novels.forEach { novel ->
                    ReleaseNovelRowView(
                        model = novel,
                        onClick = { actions.onNovelClick(novel.id) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
    if (state.languages.isNotEmpty()) {
        item(key = "languages", contentType = "languages") { LanguagesSection(state.languages) }
    }
    if (state.producers.isNotEmpty()) {
        item(
            key = "producers",
            contentType = "producers",
        ) { ProducersSection(state.producers, actions.onProducerClick) }
    }
    if (state.images.isNotEmpty()) {
        item(key = "images", contentType = "images") { ImagesSection(state.images) }
    }
    state.notes?.let { notes ->
        item(key = "notes", contentType = "notes") {
            ExpandableTextView(
                title = UiText.StringResource(StringResource.release_notes),
                text = notes,
                isExpanded = state.isNotesExpanded,
                onToggle = actions.onNotesToggle,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun LanguagesSection(languages: ImmutableList<ReleaseLanguageUIModel>) {
    SectionCardView(
        title = UiText.StringResource(StringResource.release_languages),
        modifier = Modifier.fillMaxWidth(),
    ) {
        languages.forEach { language ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = language.name.asString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    language.title?.let { title ->
                        Text(
                            text = title.asString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                if (language.isMachineTranslated) {
                    TagChipView(
                        label = UiText.StringResource(StringResource.release_machine_translation),
                        colors = VndbTheme.colors.spoilerMinor,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProducersSection(
    producers: ImmutableList<ReleaseProducerUIModel>,
    onProducerClick: (String) -> Unit,
) {
    SectionCardView(
        title = UiText.StringResource(StringResource.release_producers),
        modifier = Modifier.fillMaxWidth(),
    ) {
        producers.forEach { producer ->
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
                verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
            ) {
                Text(
                    text = producer.name.asString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = VndbTheme.colors.accent,
                    modifier = Modifier.clickable(
                        role = Role.Button,
                    ) { onProducerClick(producer.id) },
                )
                producer.roles.forEach { role -> TagChipView(label = role) }
            }
        }
    }
}

@Composable
private fun ImagesSection(images: ImmutableList<ReleaseImageUIModel>) {
    SectionCardView(
        title = UiText.StringResource(StringResource.release_images),
        modifier = Modifier.fillMaxWidth(),
    ) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md)) {
            items(items = images, key = { it.url }) { image ->
                Column(
                    modifier = Modifier.width(GalleryImageWidth),
                    verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
                ) {
                    CoverImageView(
                        url = image.thumbnailUrl ?: image.url,
                        isSensitive = image.isSensitive,
                        contentDescription = image.typeLabel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(VndbTheme.dimens.coverAspectRatio),
                    )
                    Text(
                        text = image.typeLabel.asString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

private val previewState = ReleaseState(
    releaseId = "r7668",
    hero = ReleaseHeroUIModel(
        title = UiText.StringValue("Steins;Gate"),
        altTitle = null,
        imageUrl = null,
        isImageSensitive = false,
        flags = persistentListOf(
            ReleaseFlagUIModel(UiText.StringValue("Official"), ReleaseFlagTone.Accent),
        ),
        facts = persistentListOf(
            FactUIModel(
                UiText.StringResource(StringResource.release_released),
                UiText.StringValue("2010-08-26"),
            ),
        ),
    ),
    links = persistentListOf(
        LinkUIModel(UiText.StringValue("vndb.org"), "https://vndb.org/r7668", isVndb = true),
    ),
    novels = persistentListOf(
        ReleaseNovelRowUIModel(
            id = "v2002",
            title = UiText.StringValue("STEINS;GATE"),
            yearLabel = UiText.StringValue("2009"),
            typeLabel = UiText.StringValue("COMPLETE"),
            isComplete = true,
            thumbnailUrl = null,
            isCoverSensitive = false,
        ),
    ),
    languages = persistentListOf(
        ReleaseLanguageUIModel(
            "ja",
            UiText.StringValue("Japanese"),
            null,
            isMachineTranslated = false,
        ),
    ),
    producers = persistentListOf(
        ReleaseProducerUIModel(
            "p1",
            UiText.StringValue("NITRO PLUS"),
            persistentListOf(UiText.StringValue("Developer")),
        ),
    ),
    notes = UiText.StringValue("Comes with 4 trading cards."),
)

private class ReleaseStateProvider : PreviewParameterProvider<ReleaseState> {
    override val values = sequenceOf(
        previewState,
        ReleaseState(releaseId = "r7668", isLoading = true),
        ReleaseState(
            releaseId = "r7668",
            error = ErrorUIModel(
                UiText.StringResource(StringResource.error_not_found),
                isRetryable = false,
            ),
        ),
    )
}

@AdaptivePreviews
@Composable
private fun ReleaseScreenPreview(
    @PreviewParameter(
        ReleaseStateProvider::class,
    ) state: ReleaseState,
) {
    VndbTheme {
        ReleaseScreen(state = state, onIntent = {})
    }
}
