package com.zc.vndb.feature.detail.presentation.component.detailhero.model

import androidx.compose.runtime.Immutable
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.detail.presentation.model.TagChipUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class DetailHeroUIModel(
    val title: UiText,
    val originalTitle: UiText?,
    val coverUrl: String?,
    val isCoverSensitive: Boolean,
    val developers: ImmutableList<HeroDeveloperUIModel>,
    val releasedLabel: UiText,
    /** Length category, shown as a pill: "Very Long". */
    val lengthName: UiText?,
    /** Average play time from votes, else the category's range: "(73h)", "(>50h)". */
    val lengthValueLabel: UiText?,
    /** "90 length votes", when the play time comes from votes. */
    val lengthVotesLabel: UiText? = null,
    /** "8.69 / 10" (Bayesian rating); null before the VN has a rating. */
    val scoreLabel: UiText? = null,
    /** "12,409 votes" behind the score. */
    val scoreVotesLabel: UiText? = null,
    val ageLabel: UiText? = null,
    /** Top content tags; each opens Browse filtered by it. */
    val tags: ImmutableList<TagChipUIModel> = persistentListOf(),
)
