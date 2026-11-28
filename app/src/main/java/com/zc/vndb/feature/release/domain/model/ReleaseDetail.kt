package com.zc.vndb.feature.release.domain.model

import com.zc.vndb.common.domain.model.ExternalLink
import com.zc.vndb.common.domain.model.ReleaseDate

/** Everything the release page shows. */
data class ReleaseDetail(
    val id: String,
    val title: String,
    val altTitle: String?,
    val releaseDate: ReleaseDate,
    val minAge: Int?,
    val isOfficial: Boolean,
    val isPatch: Boolean,
    val isFreeware: Boolean,
    /** Null when VNDB does not say. */
    val isUncensored: Boolean?,
    val hasAdultContent: Boolean,
    /** VNDB platform codes. */
    val platforms: List<String>,
    val media: List<ReleaseMedium>,
    val resolution: Resolution?,
    val engine: String?,
    val voicing: Voicing?,
    val catalog: String?,
    /** JAN / EAN / UPC barcode. */
    val gtin: String?,
    /** VNDB BBCode. */
    val notes: String?,
    val languages: List<ReleaseLanguage>,
    val novels: List<ReleaseNovel>,
    val producers: List<ReleaseProducer>,
    val images: List<ReleaseImage>,
    val links: List<ExternalLink>,
) {
    /** Rated 18+ or flagged as having erotic content. */
    val isAdultOnly: Boolean
        get() = (minAge ?: 0) >= ADULT_AGE || hasAdultContent

    private companion object {
        const val ADULT_AGE = 18
    }
}
