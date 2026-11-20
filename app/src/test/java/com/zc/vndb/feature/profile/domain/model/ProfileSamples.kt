package com.zc.vndb.feature.profile.domain.model

import com.zc.vndb.common.domain.model.ReleaseDate
import com.zc.vndb.common.domain.model.Session
import kotlin.time.Instant
import kotlinx.datetime.LocalDate

object ProfileSamples {

    fun session(
        userId: String = "u2",
        canReadPrivateList: Boolean = true,
        canWriteList: Boolean = true,
    ) = Session(
        userId = userId,
        username = "Yorhel",
        canReadPrivateList = canReadPrivateList,
        canWriteList = canWriteList,
    )

    fun label(id: Int, count: Int = 3, name: String = "Label $id") =
        ListLabel(id = id, name = name, count = count, isPrivate = false)

    fun entry(
        id: String,
        labels: List<EntryLabel> = listOf(EntryLabel(ListLabel.PLAYING, "Playing")),
        vote: Int? = null,
        languages: List<String> = listOf("ja", "en", "ko"),
    ) = ListEntry(
        novelId = id,
        title = "Novel $id",
        originalTitle = null,
        cover = null,
        releaseDate = ReleaseDate.Tba,
        vote = vote,
        started = LocalDate(2026, 1, 7),
        finished = null,
        notes = null,
        labels = labels,
        lastModified = Instant.parse("2026-11-14T00:00:00Z"),
        languages = languages,
    )
}
