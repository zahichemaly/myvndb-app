package com.zc.vndb.feature.browse.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object BrowseNavKey : NavKey

/** Browse scoped to one VNDB tag. */
@Serializable
data class TagNavKey(val id: String) : NavKey

/** Browse scoped to one VNDB producer (developer). */
@Serializable
data class ProducerNavKey(val id: String) : NavKey

/** Browse scoped to one VNDB staff member (writer, artist, voice actor…). */
@Serializable
data class StaffNavKey(val id: String) : NavKey

/** Browse limited to the top rated novels (Discover's Top Rated, in full). */
@Serializable
data object TopRatedNavKey : NavKey
