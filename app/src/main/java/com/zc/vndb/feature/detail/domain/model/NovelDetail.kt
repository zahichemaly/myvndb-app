package com.zc.vndb.feature.detail.domain.model

import com.zc.vndb.common.domain.model.Cover
import com.zc.vndb.common.domain.model.ExternalLink
import com.zc.vndb.common.domain.model.NovelLength
import com.zc.vndb.common.domain.model.NovelTag
import com.zc.vndb.common.domain.model.ReleaseDate

/** Everything the detail page shows from `POST /vn`. */
data class NovelDetail(
    val id: String,
    val title: String,
    val originalTitle: String?,
    val releaseDate: ReleaseDate,
    /** Bayesian rating, 10–100. */
    val rating: Double?,
    /** Plain mean of all votes, 10–100. */
    val average: Double?,
    val voteCount: Int,
    val cover: Cover?,
    val developers: List<NovelDeveloper>,
    /** VNDB BBCode. */
    val description: String?,
    val length: NovelLength?,
    /** Average play time from votes, in minutes. */
    val averagePlayMinutes: Int?,
    val tags: List<NovelTag>,
    val links: List<ExternalLink>,
    val relations: List<RelatedNovel>,
    /** The VN's title in other languages. */
    val titles: List<NovelTitle>,
    val aliases: List<String>,
    /** How many users submitted a play time. */
    val lengthVotes: Int,
    /** Code of the language the novel was first written in. */
    val originalLanguage: String? = null,
    /** Codes of every language the novel has a release in, original included. */
    val languages: List<String> = emptyList(),
)
