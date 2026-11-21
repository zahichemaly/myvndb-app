package com.zc.vndb.feature.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.component.apptopbar.AppTopBarView
import com.zc.vndb.common.ui.component.entrylabel.EntryLabelMark
import com.zc.vndb.common.ui.component.error.ErrorView
import com.zc.vndb.common.ui.component.error.model.ErrorUIModel
import com.zc.vndb.common.ui.component.pulltorefresh.PullToRefreshView
import com.zc.vndb.common.ui.component.searchfield.SearchFieldView
import com.zc.vndb.common.ui.component.selectablechip.SelectableChipView
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.profile.presentation.component.guestprompt.GuestPromptView
import com.zc.vndb.feature.profile.presentation.component.listentrycard.ListEntryCardView
import com.zc.vndb.feature.profile.presentation.component.listentrycard.model.ListEntryCardUIModel
import com.zc.vndb.feature.profile.presentation.component.listsortmenu.ListSortMenuView
import com.zc.vndb.feature.profile.presentation.component.profileheader.ProfileHeaderView
import com.zc.vndb.feature.profile.presentation.component.profileheader.model.ProfileHeaderUIModel
import com.zc.vndb.feature.profile.presentation.component.profileskeleton.ProfileSkeletonView
import com.zc.vndb.feature.profile.presentation.model.EntriesUIState
import com.zc.vndb.feature.profile.presentation.model.LabelChipUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private val MultiSelectIconSize = 16.dp
private val MaxContentWidth = 720.dp
private const val LOAD_MORE_THRESHOLD = 4

@Composable
fun ProfileScreen(
    state: ProfileState,
    onIntent: (ProfileIntent) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            AppTopBarView(
                actions = {
                    IconButton(onClick = { onIntent(ProfileIntent.SettingsClicked) }) {
                        Icon(
                            Icons.Outlined.Settings,
                            contentDescription = UiText.StringResource(
                                StringResource.cd_open_settings,
                            ).asString(),
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent,
    ) { padding ->
        val contentModifier = Modifier
            .padding(padding)
            .fillMaxSize()
        when {
            !state.isSessionKnown -> Box(contentModifier, contentAlignment = Alignment.TopCenter) {
                ProfileSkeletonView(
                    modifier = Modifier
                        .widthIn(max = MaxContentWidth)
                        .fillMaxWidth()
                        .padding(
                            horizontal = VndbTheme.spacing.margin,
                            vertical = VndbTheme.spacing.sm,
                        ),
                    showHeader = true,
                )
            }

            !state.isSignedIn -> Box(contentModifier, contentAlignment = Alignment.Center) {
                GuestPromptView(onLogInClick = { onIntent(ProfileIntent.LogInClicked) })
            }

            else -> PullToRefreshView(
                isRefreshing = state.isRefreshing,
                onRefresh = { onIntent(ProfileIntent.Refresh) },
                modifier = contentModifier,
            ) {
                SignedInContent(state, onIntent)
            }
        }
    }
}

@Composable
private fun SignedInContent(state: ProfileState, onIntent: (ProfileIntent) -> Unit) {
    val listState = rememberLazyListState()
    LoadMoreTrigger(listState) { onIntent(ProfileIntent.EndReached) }
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
            verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md),
        ) {
            state.header?.let { header ->
                item(key = "header", contentType = "header") {
                    ProfileHeaderView(model = header, modifier = Modifier.fillMaxWidth())
                }
            }
            when {
                state.isLoading -> item(
                    key = "loading",
                    contentType = "skeleton",
                ) { ProfileSkeletonView(modifier = Modifier.fillMaxWidth()) }

                state.error != null -> item(key = "error", contentType = "error") {
                    ErrorView(model = state.error, onRetry = { onIntent(ProfileIntent.Retry) })
                }

                state.isListEmpty -> item(key = "empty", contentType = "note") {
                    MutedNote(UiText.StringResource(StringResource.profile_no_lists))
                }

                else -> {
                    filters(state, onIntent)
                    entries(state.entries, state.query, state.canEditList, onIntent)
                }
            }
        }
    }
}

/** The search field, the label chips, then the selection mode beside the sort, on the right. */
private fun LazyListScope.filters(state: ProfileState, onIntent: (ProfileIntent) -> Unit) {
    item(key = "search", contentType = "search") {
        SearchFieldView(
            value = state.query,
            placeholder = UiText.StringResource(StringResource.profile_search_hint),
            onValueChange = { onIntent(ProfileIntent.QueryChanged(it)) },
            onSearch = { onIntent(ProfileIntent.SearchSubmitted) },
            onClear = { onIntent(ProfileIntent.QueryCleared) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
    item(key = "labels", contentType = "labels") {
        LabelChips(state.labels) { onIntent(ProfileIntent.LabelSelected(it)) }
    }
    item(key = "controls", contentType = "controls") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm, Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MultiSelectChip(
                isSelected = state.isMultiSelect,
                onClick = { onIntent(ProfileIntent.MultiSelectToggled) },
            )
            if (state.sortOptions.isNotEmpty()) {
                ListSortMenuView(
                    options = state.sortOptions,
                    isAscending = state.isSortAscending,
                    onSelect = { onIntent(ProfileIntent.SortSelected(it)) },
                    onDirectionToggle = { onIntent(ProfileIntent.SortDirectionToggled) },
                )
            }
        }
    }
}

/** Turns multi-select on and off; on, it is filled like a selected filter. */
@Composable
private fun MultiSelectChip(isSelected: Boolean, onClick: () -> Unit) {
    SelectableChipView(
        label = UiText.StringResource(StringResource.profile_multi_select),
        isSelected = isSelected,
        onClick = onClick,
        leading = {
            Icon(
                imageVector = Icons.Outlined.Checklist,
                contentDescription = null,
                tint = if (isSelected) {
                    VndbTheme.colors.chipActiveContent
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(MultiSelectIconSize),
            )
        },
    )
}

@Composable
private fun LabelChips(labels: ImmutableList<LabelChipUIModel>, onSelect: (Int) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm)) {
        items(items = labels, key = { it.id }) { label ->
            SelectableChipView(
                label = label.label,
                isSelected = label.isSelected,
                onClick = { onSelect(label.id) },
                leading = { EntryLabelMark(label.id) },
            )
        }
    }
}

private fun LazyListScope.entries(
    entries: EntriesUIState,
    query: String,
    canEdit: Boolean,
    onIntent: (ProfileIntent) -> Unit,
) {
    when {
        entries.isLoading -> item(
            key = "entries-loading",
            contentType = "skeleton",
        ) { ProfileSkeletonView(modifier = Modifier.fillMaxWidth()) }

        entries.error != null -> item(key = "entries-error", contentType = "error") {
            ErrorView(model = entries.error, onRetry = { onIntent(ProfileIntent.EntriesRetried) })
        }

        entries.items.isEmpty() -> item(key = "entries-empty", contentType = "note") {
            MutedNote(
                if (query.isBlank()) {
                    UiText.StringResource(StringResource.profile_no_entries)
                } else {
                    UiText.StringResource(StringResource.profile_no_matches, listOf(query.trim()))
                },
            )
        }

        else -> {
            items(items = entries.items, key = { it.novelId }, contentType = { "entry" }) { entry ->
                ListEntryCardView(
                    model = entry,
                    onClick = { onIntent(ProfileIntent.EntryClicked(entry.novelId)) },
                    onEdit = if (canEdit) {
                        { onIntent(ProfileIntent.EntryEditClicked(entry.novelId)) }
                    } else {
                        null
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            loadMoreFooter(entries, onIntent)
        }
    }
}

private fun LazyListScope.loadMoreFooter(
    entries: EntriesUIState,
    onIntent: (ProfileIntent) -> Unit,
) {
    when {
        entries.isLoadingMore -> item(
            key = "loading-more",
            contentType = "progress",
        ) { CenteredProgress() }

        entries.loadMoreFailed -> item(key = "load-more-failed", contentType = "retry") {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TextButton(onClick = { onIntent(ProfileIntent.LoadMoreRetried) }) {
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

@Composable
private fun CenteredProgress() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(VndbTheme.spacing.lg),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = VndbTheme.colors.accent)
    }
}

@Composable
private fun MutedNote(text: UiText) {
    Text(
        text = text.asString(),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = VndbTheme.spacing.md),
    )
}

private val previewSignedIn = ProfileState(
    isSessionKnown = true,
    header = ProfileHeaderUIModel(
        username = UiText.StringValue("Yorhel"),
        initial = UiText.StringValue("Y"),
        statsLabel = UiText.StringValue("10 play-time votes · 171h logged"),
    ),
    labels = persistentListOf(
        LabelChipUIModel(1, UiText.StringValue("Playing (1)"), isSelected = true),
        LabelChipUIModel(2, UiText.StringValue("Finished (112)"), isSelected = false),
        LabelChipUIModel(10, UiText.StringValue("Ready to play (44)"), isSelected = false),
    ),
    entries = EntriesUIState(
        items = persistentListOf(
            ListEntryCardUIModel(
                novelId = "v58641",
                title = UiText.StringValue("Z.A.T.O. // I Love the World and Everything In It"),
                coverUrl = null,
                isCoverSensitive = false,
                voteLabel = null,
                dates = persistentListOf(UiText.StringValue("Started 2026-01-07")),
                notes = null,
                updatedLabel = UiText.StringValue("Updated 2 days ago"),
            ),
        ),
    ),
)

private class ProfileStateProvider : PreviewParameterProvider<ProfileState> {
    override val values = sequenceOf(
        ProfileState(isSessionKnown = true),
        previewSignedIn,
        previewSignedIn.copy(
            labels = persistentListOf(),
            error = ErrorUIModel(
                UiText.StringResource(StringResource.error_network),
                isRetryable = true,
            ),
        ),
    )
}

@AdaptivePreviews
@Composable
private fun ProfileScreenPreview(
    @PreviewParameter(
        ProfileStateProvider::class,
    ) state: ProfileState,
) {
    VndbTheme {
        ProfileScreen(state = state, onIntent = {})
    }
}
