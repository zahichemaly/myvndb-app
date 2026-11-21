package com.zc.vndb.feature.discover.presentation.component.trendingrelease.model

import androidx.compose.runtime.Immutable
import com.zc.vndb.common.ui.component.platformicon.model.PlatformIcon
import com.zc.vndb.common.ui.text.UiText
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class TrendingReleaseUIModel(
    val releaseId: String,
    val novelId: String,
    val title: UiText,
    /** The pill: "New release", "Translation", "Patch", "Re-release". */
    val kindLabel: UiText,
    val kindTone: ReleaseKindTone,
    /** "English" for a new translation, shown after the pill. */
    val languageLabel: UiText?,
    /** "Nov 18". */
    val dateLabel: UiText,
    val ratingLabel: UiText?,
    val thumbnailUrl: String?,
    val isCoverSensitive: Boolean,
    val platformIcons: ImmutableList<PlatformIcon> = persistentListOf(),
    /** "+2" after the logos when some were left out. */
    val morePlatformsLabel: UiText? = null,
    /** "Windows, Linux": what screen readers hear for the logos. */
    val platformsLabel: UiText? = null,
    /** A translation or patch opens its release; the others open the visual novel. */
    val opensRelease: Boolean = false,
)
