package com.zc.vndb.feature.detail.domain.model

import com.zc.vndb.common.domain.model.PlatformFamily
import com.zc.vndb.common.domain.model.ReleaseDate

/** A release in the Releases tab. */
data class ReleaseSummary(
    val id: String,
    val title: String,
    val releaseDate: ReleaseDate,
    /** VNDB platform codes. */
    val platforms: List<String>,
    /** Family of the first platform, for the row icon. */
    val family: PlatformFamily,
    val languageCodes: List<String>,
    val isOfficial: Boolean,
    val isPatch: Boolean,
    val isFreeware: Boolean,
    val minAge: Int?,
)
