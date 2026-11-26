package com.zc.vndb.feature.character.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.component.apptopbar.AppTopBarView
import com.zc.vndb.common.ui.component.chip.TagChipView
import com.zc.vndb.common.ui.component.error.ErrorView
import com.zc.vndb.common.ui.component.error.model.ErrorUIModel
import com.zc.vndb.common.ui.component.expandabletext.ExpandableTextView
import com.zc.vndb.common.ui.component.factline.model.FactUIModel
import com.zc.vndb.common.ui.component.pulltorefresh.PullToRefreshView
import com.zc.vndb.common.ui.component.sectioncard.SectionCardView
import com.zc.vndb.common.ui.component.skeleton.PageSkeletonView
import com.zc.vndb.common.ui.component.spoilerfilter.SpoilerFilterView
import com.zc.vndb.common.ui.component.spoilerfilter.model.SpoilerLevel
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.character.presentation.component.appearancerow.AppearanceRowView
import com.zc.vndb.feature.character.presentation.component.appearancerow.model.AppearanceRowUIModel
import com.zc.vndb.feature.character.presentation.component.characterhero.CharacterHeroView
import com.zc.vndb.feature.character.presentation.component.characterhero.model.CharacterHeroUIModel
import com.zc.vndb.feature.character.presentation.component.traitgroups.TraitGroupsView
import com.zc.vndb.feature.character.presentation.component.traitgroups.model.TraitGroupUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/** Same reading width as Title Detail on wide screens. */
private val MaxContentWidth = 720.dp

/** Callbacks built once from `onIntent`, so rows get stable lambdas. */
@Immutable
private class CharacterActions(
    val onSpoilerLevelSelect: (SpoilerLevel) -> Unit,
    val onDescriptionToggle: () -> Unit,
    val onNovelClick: (String) -> Unit,
    val onStaffClick: (String) -> Unit,
)

@Composable
fun CharacterScreen(
    state: CharacterState,
    onIntent: (CharacterIntent) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val actions = remember(onIntent) {
        CharacterActions(
            onSpoilerLevelSelect = { onIntent(CharacterIntent.SpoilerLevelSelected(it)) },
            onDescriptionToggle = { onIntent(CharacterIntent.DescriptionToggled) },
            onNovelClick = { onIntent(CharacterIntent.NovelClicked(it)) },
            onStaffClick = { onIntent(CharacterIntent.StaffClicked(it)) },
        )
    }
    Scaffold(
        modifier = modifier,
        topBar = { CharacterTopBar(onIntent) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent,
    ) { padding ->
        val contentModifier = Modifier
            .padding(padding)
            .fillMaxSize()
        when {
            state.error != null -> ErrorView(
                model = state.error,
                onRetry = { onIntent(CharacterIntent.Retry) },
                modifier = contentModifier,
            )

            state.hero == null -> PageSkeletonView(
                modifier = contentModifier,
                maxWidth = MaxContentWidth,
            )

            else -> PullToRefreshView(
                isRefreshing = state.isRefreshing,
                onRefresh = { onIntent(CharacterIntent.Refresh) },
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
                        characterSections(state, state.hero, actions)
                    }
                }
            }
        }
    }
}

@Composable
private fun CharacterTopBar(onIntent: (CharacterIntent) -> Unit) {
    AppTopBarView(
        onBack = { onIntent(CharacterIntent.BackClicked) },
        actions = {
            IconButton(onClick = { onIntent(CharacterIntent.ShareClicked) }) {
                Icon(
                    Icons.Outlined.Share,
                    contentDescription = UiText.StringResource(
                        StringResource.detail_share,
                    ).asString(),
                )
            }
            IconButton(onClick = { onIntent(CharacterIntent.OpenInBrowserClicked) }) {
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

private fun LazyListScope.characterSections(
    state: CharacterState,
    hero: CharacterHeroUIModel,
    actions: CharacterActions,
) {
    item(
        key = "hero",
        contentType = "hero",
    ) {
        CharacterHeroView(
            model = hero,
            onVoiceActorClick = actions.onStaffClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
    if (state.aliases.isNotEmpty()) {
        item(key = "aliases", contentType = "aliases") { AliasesSection(state.aliases) }
    }
    item(key = "spoilers", contentType = "spoilers") {
        SpoilerFilterView(
            level = state.spoilerLevel,
            onLevelSelect = actions.onSpoilerLevelSelect,
            modifier = Modifier.fillMaxWidth(),
        )
    }
    state.description?.let { description ->
        item(key = "description", contentType = "description") {
            ExpandableTextView(
                title = UiText.StringResource(StringResource.character_description),
                text = description,
                isExpanded = state.isDescriptionExpanded,
                onToggle = actions.onDescriptionToggle,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
    if (state.traitGroups.isNotEmpty() || state.hiddenTraitsLabel != null) {
        item(key = "traits", contentType = "traits") {
            TraitGroupsView(
                groups = state.traitGroups,
                hiddenLabel = state.hiddenTraitsLabel,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
    appearancesSection(state, actions)
}

@Composable
private fun AliasesSection(aliases: ImmutableList<UiText>) {
    SectionCardView(
        title = UiText.StringResource(StringResource.character_aliases),
        modifier = Modifier.fillMaxWidth(),
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
            verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
        ) {
            aliases.forEach { alias -> TagChipView(label = alias) }
        }
    }
}

private fun LazyListScope.appearancesSection(state: CharacterState, actions: CharacterActions) {
    if (state.appearances.isEmpty() && state.hiddenAppearancesLabel == null) return
    item(key = "appearances", contentType = "appearances") {
        SectionCardView(
            title = UiText.StringResource(StringResource.character_appears_in),
            modifier = Modifier.fillMaxWidth(),
        ) {
            state.appearances.forEach { appearance ->
                AppearanceRowView(
                    model = appearance,
                    onClick = { actions.onNovelClick(appearance.novelId) },
                    onVoiceActorClick = actions.onStaffClick,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            state.hiddenAppearancesLabel?.let { hidden ->
                Text(
                    text = hidden.asString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private val previewState = CharacterState(
    characterId = "c6487",
    hero = CharacterHeroUIModel(
        name = UiText.StringValue("Makise Kurisu"),
        originalName = UiText.StringValue("牧瀬 紅莉栖"),
        imageUrl = null,
        isImageSensitive = false,
        identity = persistentListOf(UiText.StringValue("Female")),
        facts = persistentListOf(
            FactUIModel(
                UiText.StringResource(StringResource.character_age),
                UiText.StringValue("18"),
            ),
        ),
    ),
    aliases = persistentListOf(UiText.StringValue("Assistant"), UiText.StringValue("Christina")),
    description = UiText.StringValue(
        "Kurisu is a neuroscience researcher at an American university…",
    ),
    traitGroups = persistentListOf(
        TraitGroupUIModel(
            UiText.StringValue("HAIR"),
            persistentListOf(UiText.StringValue("Long"), UiText.StringValue("Red")),
        ),
    ),
    hiddenTraitsLabel = UiText.StringValue("12 hidden by spoiler filter"),
    appearances = persistentListOf(
        AppearanceRowUIModel(
            novelId = "v2002",
            title = UiText.StringValue("STEINS;GATE"),
            yearLabel = UiText.StringValue("2009"),
            roleLabel = UiText.StringValue("PRIMARY"),
            isMajorRole = true,
            voiceActorLabel = UiText.StringValue("CV: Imai Asami"),
            ratingLabel = UiText.StringValue("9.02"),
            thumbnailUrl = null,
            isCoverSensitive = false,
        ),
    ),
)

private class CharacterStateProvider : PreviewParameterProvider<CharacterState> {
    override val values = sequenceOf(
        previewState,
        CharacterState(characterId = "c6487", isLoading = true),
        CharacterState(
            characterId = "c6487",
            error = ErrorUIModel(
                UiText.StringResource(StringResource.error_not_found),
                isRetryable = false,
            ),
        ),
    )
}

@AdaptivePreviews
@Composable
private fun CharacterScreenPreview(
    @PreviewParameter(
        CharacterStateProvider::class,
    ) state: CharacterState,
) {
    VndbTheme {
        CharacterScreen(state = state, onIntent = {})
    }
}
