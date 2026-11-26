package com.zc.vndb.feature.browse.presentation.component.filtersheet

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.zc.vndb.common.domain.model.LanguageOption
import com.zc.vndb.common.domain.model.NovelLength
import com.zc.vndb.common.ui.component.bottomsheet.BottomSheetView
import com.zc.vndb.common.ui.component.languagesdialog.LanguagesDialogView
import com.zc.vndb.common.ui.component.pickerfield.PickerFieldView
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
 * The bottom sheet behind the filter icon: age rating, genres, languages, length and release era,
 * then Reset and Apply. Edits stay in the draft; nothing is loaded until Apply, which closes it.
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
    BottomSheetView(onDismissRequest = onDismiss, modifier = modifier) {
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
    Column(modifier = Modifier.fillMaxWidth()) {
        // Only the filters scroll: Reset and Apply stay pinned below them.
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = VndbTheme.spacing.margin),
            verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.lg),
        ) {
            AgeSection(draft, onDraftChange)
            GenreSection(draft, onDraftChange)
            LanguageSection(draft, languages, onDraftChange)
            LengthSection(draft, onDraftChange)
            EraSection(draft, onDraftChange)
        }
        Footer(onReset = onReset, onApply = onApply)
    }
}

/** Reset on the left, Apply on the right, like the list editor's Remove and Save. */
@Composable
private fun Footer(onReset: () -> Unit, onApply: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = VndbTheme.spacing.margin,
                end = VndbTheme.spacing.margin,
                top = VndbTheme.spacing.sm,
                bottom = VndbTheme.spacing.lg,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(onClick = onReset) {
            Text(UiText.StringResource(StringResource.browse_filters_reset).asString())
        }
        Spacer(Modifier.weight(1f))
        Button(onClick = onApply) {
            Text(UiText.StringResource(StringResource.browse_filters_apply).asString())
        }
    }
}

/** A section: a heading above its [content], spaced like the list editor's. */
@Composable
private fun Section(@StringRes title: Int, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm)) {
        Text(
            text = UiText.StringResource(title).asString(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.semantics { heading() },
        )
        content()
    }
}

@Composable
private fun AgeSection(draft: FilterDraft, onDraftChange: (FilterDraft) -> Unit) {
    Section(StringResource.browse_filter_section_age) {
        ChipFlow {
            AgeRating.entries.forEach { rating ->
                SelectableChipView(
                    label = rating.toLabel(),
                    isSelected = draft.ageRating == rating,
                    onClick = { onDraftChange(draft.copy(ageRating = rating)) },
                    selectedColors = selectedColors(),
                )
            }
        }
    }
}

@Composable
private fun GenreSection(draft: FilterDraft, onDraftChange: (FilterDraft) -> Unit) {
    Section(StringResource.browse_filter_section_genres) {
        ChipFlow {
            Genre.entries.forEach { genre ->
                SelectableChipView(
                    label = genre.toLabel(),
                    isSelected = genre in draft.genres,
                    onClick = {
                        onDraftChange(draft.copy(genres = draft.genres.toggled(genre)))
                    },
                    selectedColors = selectedColors(),
                )
            }
        }
    }
}

/** Supported and original language side by side, each opening its own picker. */
@Composable
private fun LanguageSection(
    draft: FilterDraft,
    languages: ImmutableList<LanguageOption>,
    onDraftChange: (FilterDraft) -> Unit,
) {
    val advanced = draft.advanced
    fun change(new: AdvancedFilters) = onDraftChange(draft.copy(advanced = new))
    var showSupported by remember { mutableStateOf(false) }
    var showOriginal by remember { mutableStateOf(false) }
    Section(StringResource.browse_filter_section_language) {
        Row(horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm)) {
            LanguageField(
                label = UiText.StringResource(StringResource.browse_filter_supported_languages),
                value = advanced.supportedLanguages
                    .takeIf { it.isNotEmpty() }
                    ?.sorted()
                    ?.toLanguagesLabel(languages::nameOf),
                onClick = { showSupported = true },
                modifier = Modifier.weight(1f),
            )
            LanguageField(
                label = UiText.StringResource(StringResource.browse_filter_original_language),
                value = advanced.originalLanguage?.let { UiText.StringValue(languages.nameOf(it)) },
                onClick = { showOriginal = true },
                modifier = Modifier.weight(1f),
            )
        }
    }
    if (showSupported) {
        LanguagesDialogView(
            title = UiText.StringResource(StringResource.browse_filter_supported_languages),
            hint = UiText.StringResource(StringResource.browse_supported_languages_hint),
            options = languages,
            selected = advanced.supportedLanguages,
            onConfirm = {
                showSupported = false
                change(advanced.copy(supportedLanguages = it))
            },
            onDismiss = { showSupported = false },
        )
    }
    if (showOriginal) {
        OriginalLanguageDialogView(
            options = languages,
            selected = advanced.originalLanguage,
            onConfirm = {
                showOriginal = false
                change(advanced.copy(originalLanguage = it))
            },
            onDismiss = { showOriginal = false },
        )
    }
}

/** The choice in the accent color, or "Any" in a quiet one when nothing is chosen. */
@Composable
private fun LanguageField(
    label: UiText,
    value: UiText?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PickerFieldView(
        label = label,
        value = value ?: UiText.StringResource(StringResource.browse_filter_any),
        onClick = onClick,
        modifier = modifier,
        valueColor = if (value == null) {
            MaterialTheme.colorScheme.onSurfaceVariant
        } else {
            VndbTheme.colors.accent
        },
    )
}

@Composable
private fun LengthSection(draft: FilterDraft, onDraftChange: (FilterDraft) -> Unit) {
    val advanced = draft.advanced
    Section(StringResource.browse_filter_section_length) {
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
                    selectedColors = selectedColors(),
                )
            }
        }
    }
}

@Composable
private fun EraSection(draft: FilterDraft, onDraftChange: (FilterDraft) -> Unit) {
    val advanced = draft.advanced
    Section(StringResource.browse_filter_section_released) {
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
                    selectedColors = selectedColors(),
                )
            }
        }
    }
}

private fun <T> Set<T>.toggled(item: T): Set<T> = if (item in this) this - item else this + item

@Composable
private fun ChipFlow(content: @Composable () -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
    ) { content() }
}

@Composable
private fun selectedColors() = TintedColors(
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
