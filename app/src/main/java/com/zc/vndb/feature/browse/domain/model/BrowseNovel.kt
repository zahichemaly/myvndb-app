package com.zc.vndb.feature.browse.domain.model

import com.zc.vndb.common.domain.model.Cover
import com.zc.vndb.common.domain.model.NovelLength
import com.zc.vndb.common.domain.model.NovelTag
import com.zc.vndb.common.domain.model.ReleaseDate

/** A visual novel in Browse results. */
data class BrowseNovel(
    val id: String,
    val title: String,
    /** Title in the original script, when it differs from [title]. */
    val originalTitle: String?,
    val releaseDate: ReleaseDate,
    /** 10–100; null with too few votes. */
    val rating: Double?,
    val cover: Cover?,
    val developerNames: List<String>,
    val tags: List<NovelTag>,
    val length: NovelLength?,
    /** Codes of every language the novel has a release in. */
    val languages: List<String> = emptyList(),
)
