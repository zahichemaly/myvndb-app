package com.zc.vndb.feature.detail.presentation

import com.zc.vndb.common.ui.component.spoilerfilter.model.SpoilerLevel
import com.zc.vndb.feature.detail.presentation.model.ContentFilter
import com.zc.vndb.feature.detail.presentation.model.DetailSection
import com.zc.vndb.feature.detail.presentation.model.DetailTab

sealed interface DetailIntent {
    data object Load : DetailIntent
    data object Retry : DetailIntent
    data object BackClicked : DetailIntent
    data object ShareClicked : DetailIntent
    data object OpenInBrowserClicked : DetailIntent
    data class LinkClicked(val url: String) : DetailIntent
    data class SpoilerLevelSelected(val level: SpoilerLevel) : DetailIntent
    data object SynopsisToggled : DetailIntent

    /** Add to list / Edit on the My list card. */
    data object MyListClicked : DetailIntent
    data class RelationClicked(val id: String) : DetailIntent
    data class CharacterClicked(val id: String) : DetailIntent
    data class TagClicked(val id: String) : DetailIntent
    data class DeveloperClicked(val id: String) : DetailIntent
    data class StaffClicked(val id: String) : DetailIntent
    data class EditionClicked(val releaseId: String) : DetailIntent

    data class TabSelected(val tab: DetailTab) : DetailIntent

    /** A section's tab was opened or its preview scrolled into view; loads it once. */
    data class SectionNeeded(val section: DetailSection) : DetailIntent
    data class SectionRetried(val section: DetailSection) : DetailIntent
    data class LoadMoreClicked(val section: DetailSection) : DetailIntent

    data class ContentFilterToggled(val filter: ContentFilter) : DetailIntent
    data class ScreenshotClicked(val index: Int) : DetailIntent
    data class CoverClicked(val index: Int) : DetailIntent
    data object ViewerDismissed : DetailIntent

    /** A blurred cover was tapped; [viewerIndex] is its place in the viewer, null for the page's cover. */
    data class CoverRevealRequested(val viewerIndex: Int?) : DetailIntent
    data object CoverRevealConfirmed : DetailIntent
    data object CoverRevealDismissed : DetailIntent
    data object ReviewsClicked : DetailIntent
}
