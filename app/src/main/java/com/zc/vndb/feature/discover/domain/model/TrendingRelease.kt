package com.zc.vndb.feature.discover.domain.model

import com.zc.vndb.common.domain.model.Cover
import com.zc.vndb.common.domain.model.ReleaseDate

/** A row in "New & Trending": a recent release, described through its visual novel. */
data class TrendingRelease(
    val releaseId: String,
    val novelId: String,
    val title: String,
    val kind: ReleaseKind,
    val releaseDate: ReleaseDate,
    val platforms: List<Platform>,
    val rating: Double?,
    val cover: Cover?,
    /** Title in the original script, when VNDB has one. */
    val originalTitle: String? = null,
    /** For a new translation: the language it is in, preferring one the user reads. */
    val translationLanguage: String? = null,
)
