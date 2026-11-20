package com.zc.vndb.feature.profile.presentation.component.listentrycard.model

import androidx.compose.runtime.Immutable
import com.zc.vndb.common.ui.component.entrylabel.EntryLabelUIModel
import com.zc.vndb.common.ui.component.languagepills.model.LanguagePillUIModel
import com.zc.vndb.common.ui.text.UiText
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class ListEntryCardUIModel(
    val novelId: String,
    val title: UiText,
    val coverUrl: String?,
    val isCoverSensitive: Boolean,
    /** The vote alone, "8.5", shown after a star. */
    val voteLabel: UiText?,
    /** "Started 2024-10-15", "Finished 2024-11-02". */
    val dates: ImmutableList<UiText> = persistentListOf(),
    /** The entry's other labels. */
    val labels: ImmutableList<EntryLabelUIModel> = persistentListOf(),
    val notes: UiText?,
    /** "Updated 3 days ago". */
    val updatedLabel: UiText,
    /** The languages the user chose to see that this novel is available in. */
    val languagePills: ImmutableList<LanguagePillUIModel> = persistentListOf(),
)
