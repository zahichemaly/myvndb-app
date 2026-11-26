package com.zc.vndb.feature.browse.presentation.component.filtersheet

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.zc.vndb.common.domain.model.LanguageOption
import com.zc.vndb.common.domain.model.NovelLength
import com.zc.vndb.common.ui.component.bottomsheet.BottomSheetView
import com.zc.vndb.common.ui.component.languagesdialog.LanguagesDialogView
import com.zc.vndb.common.ui.component.selectablechip.SelectableChipView
import com.zc.vndb.common.ui.mapper.nameOf
import com.zc.vndb.common.ui.mapper.toLanguagesLabel
import com.zc.vndb.common.ui.mapper.toNameWithRangeLabel
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.browse.domain.model.AdvancedFilters
import com.zc.vndb.feature.browse.domain.model.AgeRating
import com.zc.vndb.feature.browse.domain.model.Genre
import com.zc.vndb.feature.browse.domain.model.ReleaseEra
import com.zc.vndb.feature.browse.presentation.mapper.toLabel
import com.zc.vndb.feature.browse.presentation.model.FilterDraft
import com.zc.vndb.feature.browse.presentation.model.FilterSheetUIState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * The bottom sheet behind the filter icon: age rating, genres, languages, length and release era.
 * Edits stay in the draft; nothing is loaded until Apply, which closes the sheet.
 */
@Composable
fun FilterSheetView(
    sheet: FilterSheetUIState,
    languages: ImmutableList<LanguageOption>,
    onDraftChange: (FilterDraft) -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomSheetView(
        onDismissRequest = onDismiss,
        modifier = modifier,
        containerColor = VndbTheme.colors.surfaceElevated,
    ) {
        FilterSheetContent(
            draft = sheet.draft,
            languages = languages,
            onDraftChange = onDraftChange,
            onReset = onReset,
            onApply = onApply,
        )
    }
}

@Composable
private fun FilterSheetContent(
    draft: FilterDraft,
    languages: ImmutableList<LanguageOption>,
    onDraftChange: (FilterDraft) -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = VndbTheme.spacing.margin),
        verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = UiText.StringResource(StringResource.browse_filters_title).asString(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onReset) {
                Text(
                    text = UiText.StringResource(StringResource.browse_filters_reset).asString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = VndbTheme.colors.accent,
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
        ) {
            AgeSection(draft, onDraftChange)
            GenreSection(draft, onDraftChange)
            LanguageSection(draft, languages, onDraftChange)
            LengthSection(draft, onDraftChange)
            EraSection(draft, onDraftChange)
        }
        Button(
            onClick = onApply,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = VndbTheme.spacing.md),
            colors = ButtonDefaults.buttonColors(
                containerColor = VndbTheme.colors.accent,
                contentColor = VndbTheme.colors.onAccent,
            ),
        ) {
            Text(
                text = UiText.StringResource(StringResource.browse_filters_apply).asString(),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun SectionLabel(@StringRes text: Int) {
    Text(
        text = UiText.StringResource(text).asString(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = VndbTheme.spacing.sm),
    )
}

@Composable
private fun AgeSection(draft: FilterDraft, onDraftChange: (FilterDraft) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm)) {
        SectionLabel(StringResource.browse_filter_section_age)
        ChipFlow {
            AgeRating.entries.forEach { rating ->
                SelectableChipView(
                    label = rating.toLabel(),
                    isSelected = draft.ageRating == rating,
                    onClick = { onDraftChange(draft.copy(ageRating = rating)) },
                    selectedColors = SelectedColors(),
                )
            }
        }
    }
}

@Composable
private fun GenreSection(draft: FilterDraft, onDraftChange: (FilterDraft) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm)) {
        SectionLabel(StringResource.browse_filter_section_genres)
        ChipFlow {
            Genre.entries.forEach { genre ->
                SelectableChipView(
                    label = genre.toLabel(),
                    isSelected = genre in draft.genres,
                    onClick = {
                        onDraftChange(draft.copy(genres = draft.genres.toggled(genre)))
                    },
                    selectedColors = SelectedColors(),
                )
            }
        }
    }
}

@Composable
private fun LanguageSection(
    draft: FilterDraft,
    languages: ImmutableList<LanguageOption>,
    onDraftChange: (FilterDraft) -> Unit,
) {
    val advanced = draft.advanced
    fun change(new: AdvancedFilters) = onDraftChange(draft.copy(advanced = new))
    var showDialog by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs)) {
        SectionLabel(StringResource.browse_filter_section_language)
        PickerRow(
            label = UiText.StringResource(StringResource.browse_filter_supported_languages),
            value = advanced.supportedLanguages
                .takeIf { it.isNotEmpty() }
                ?.sorted()
                ?.toLanguagesLabel(languages::nameOf)
                ?.asString(),
            onClick = { showDialog = true },
        )
        OriginalLanguageRow(
            selected = advanced.originalLanguage,
            languages = languages,
            onSelect = { change(advanced.copy(originalLanguage = it)) },
        )
    }
    if (showDialog) {
        LanguagesDialogView(
            title = UiText.StringResource(StringResource.browse_filter_supported_languages),
            hint = UiText.StringResource(StringResource.browse_supported_languages_hint),
            options = languages,
            selected = advanced.supportedLanguages,
            onConfirm = {
                showDialog = false
                change(advanced.copy(supportedLanguages = it))
            },
            onDismiss = { showDialog = false },
        )
    }
}

/** A label on the left and the current choice (or "Any") on the right; tapping opens the choice. */
@Composable
private fun PickerRow(label: UiText, value: String?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = VndbTheme.dimens.filterChipHeight)
            .clickable(role = Role.Button, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label.asString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value ?: UiText.StringResource(StringResource.browse_filter_any).asString(),
            style = MaterialTheme.typography.labelLarge,
            color = if (value == null) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                VndbTheme.colors.accent
            },
        )
        Icon(
            Icons.Filled.ArrowDropDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun OriginalLanguageRow(
    selected: String?,
    languages: ImmutableList<LanguageOption>,
    onSelect: (String?) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    PickerRow(
        label = UiText.StringResource(StringResource.browse_filter_original_language),
        value = selected?.let(languages::nameOf),
        onClick = { showDialog = true },
    )
    if (showDialog) {
        OriginalLanguageDialogView(
            options = languages,
            selected = selected,
            onConfirm = {
                showDialog = false
                onSelect(it)
            },
            onDismiss = { showDialog = false },
        )
    }
}

@Composable
private fun LengthSection(draft: FilterDraft, onDraftChange: (FilterDraft) -> Unit) {
    val advanced = draft.advanced
    Column(verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm)) {
        SectionLabel(StringResource.browse_filter_section_length)
        ChipFlow {
            NovelLength.entries.forEach { length ->
                SelectableChipView(
                    label = length.toNameWithRangeLabel(),
                    isSelected = length in advanced.lengths,
                    onClick = {
                        onDraftChange(
                            draft.copy(
                                advanced = advanced.copy(
                                    lengths = advanced.lengths.toggled(length),
                                ),
                            ),
                        )
                    },
                    selectedColors = SelectedColors(),
                )
            }
        }
    }
}

@Composable
private fun EraSection(draft: FilterDraft, onDraftChange: (FilterDraft) -> Unit) {
    val advanced = draft.advanced
    Column(verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm)) {
        SectionLabel(StringResource.browse_filter_section_released)
        ChipFlow {
            ReleaseEra.entries.forEach { era ->
                SelectableChipView(
                    label = era.toLabel(),
                    isSelected = advanced.era == era,
                    // Tapping the chosen era again turns the filter off.
                    onClick = {
                        onDraftChange(
                            draft.copy(
                                advanced = advanced.copy(
                                    era = era.takeUnless { it == advanced.era },
                                ),
                            ),
                        )
                    },
                    selectedColors = SelectedColors(),
                )
            }
        }
    }
}

private fun <T> Set<T>.toggled(item: T): Set<T> = if (item in this) this - item else this + item

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipFlow(content: @Composable () -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
    ) { content() }
}

@Composable
private fun SelectedColors() = TintedColors(
    container = VndbTheme.colors.accent,
    content = VndbTheme.colors.onAccent,
    border = VndbTheme.colors.accent,
)

@AdaptivePreviews
@Composable
private fun FilterSheetContentPreview() {
    VndbTheme {
        FilterSheetContent(
            languages = persistentListOf(LanguageOption("en", "English")),
            draft = FilterDraft(
                ageRating = AgeRating.AllAges,
                genres = setOf(Genre.Mystery),
                advanced = AdvancedFilters(
                    supportedLanguages = setOf("en", "ja"),
                    lengths = setOf(NovelLength.Short),
                ),
            ),
            onDraftChange = {},
            onReset = {},
            onApply = {},
        )
    }
}
