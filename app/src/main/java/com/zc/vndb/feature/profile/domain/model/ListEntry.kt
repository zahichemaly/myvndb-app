package com.zc.vndb.feature.profile.domain.model

import com.zc.vndb.common.domain.model.Cover
import com.zc.vndb.common.domain.model.ReleaseDate
import kotlin.time.Instant
import kotlinx.datetime.LocalDate

/** A visual novel on the user's list. */
data class ListEntry(
    val novelId: String,
    val title: String,
    val originalTitle: String?,
    val cover: Cover?,
    val releaseDate: ReleaseDate,
    /** 10–100. */
    val vote: Int?,
    val started: LocalDate?,
    val finished: LocalDate?,
    val notes: String?,
    /** Every label on this entry (id to name). */
    val labels: List<EntryLabel>,
    val lastModified: Instant,
    /** Codes of every language the novel has a release in. */
    val languages: List<String> = emptyList(),
)

data class EntryLabel(val id: Int, val name: String)
