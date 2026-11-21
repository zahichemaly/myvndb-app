package com.zc.vndb.feature.detail.domain.model

import com.zc.vndb.common.domain.model.PlatformFamily
import com.zc.vndb.common.domain.model.ReleaseDate

/** One release of the VN on one platform. */
data class Edition(
    val id: String,
    val title: String,
    val releaseDate: ReleaseDate,
    /** VNDB platform code, e.g. "win", "swi". */
    val platform: String,
    val family: PlatformFamily,
    val languageCodes: List<String>,
    val isOfficial: Boolean,
    val isPatch: Boolean,
    val minAge: Int?,
)
