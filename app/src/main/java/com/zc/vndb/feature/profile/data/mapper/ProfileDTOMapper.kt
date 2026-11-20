package com.zc.vndb.feature.profile.data.mapper

import com.zc.vndb.common.data.mapper.toCover
import com.zc.vndb.common.data.mapper.toReleaseDate
import com.zc.vndb.feature.profile.data.dto.ListEntryDTO
import com.zc.vndb.feature.profile.data.dto.ListLabelDTO
import com.zc.vndb.feature.profile.data.dto.MyListEntryDTO
import com.zc.vndb.feature.profile.data.dto.UserDTO
import com.zc.vndb.feature.profile.domain.model.EntryLabel
import com.zc.vndb.feature.profile.domain.model.ListEntry
import com.zc.vndb.feature.profile.domain.model.ListLabel
import com.zc.vndb.feature.profile.domain.model.MyListEntry
import com.zc.vndb.feature.profile.domain.model.UserProfile
import kotlin.time.Instant
import kotlinx.datetime.LocalDate

fun UserDTO.toDomain() = UserProfile(
    id = id,
    username = username,
    lengthVotes = lengthVotes ?: 0,
    lengthVotesMinutes = lengthVotesSum ?: 0,
)

fun ListLabelDTO.toDomain() = ListLabel(
    id = id,
    name = label,
    count = count ?: 0,
    isPrivate = private ?: false,
)

fun ListEntryDTO.toDomain() = ListEntry(
    novelId = id,
    title = vn.title,
    originalTitle = vn.altTitle,
    cover = vn.image?.toCover(),
    releaseDate = vn.released.toReleaseDate(),
    vote = vote,
    started = started.toLocalDateOrNull(),
    finished = finished.toLocalDateOrNull(),
    notes = notes?.takeIf { it.isNotBlank() },
    labels = labels.orEmpty().map { EntryLabel(it.id, it.label) },
    lastModified = Instant.fromEpochSeconds(lastMod),
    languages = vn.languages.orEmpty(),
)

fun MyListEntryDTO.toDomain() = MyListEntry(
    novelId = id,
    labelIds = labels.orEmpty().map {
        it.id
    }.filterNot { it in MyListEntry.VIRTUAL_LABELS }.toSet(),
    vote = vote,
    started = started.toLocalDateOrNull(),
    finished = finished.toLocalDateOrNull(),
    notes = notes?.takeIf { it.isNotBlank() },
    labelNames = labels.orEmpty().associate { it.id to it.label },
)

/** VNDB sends "YYYY-MM-DD"; anything else is treated as unknown. */
private fun String?.toLocalDateOrNull(): LocalDate? = this?.let { value ->
    try {
        LocalDate.parse(value)
    } catch (_: IllegalArgumentException) {
        null
    }
}
