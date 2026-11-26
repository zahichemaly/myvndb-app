package com.zc.vndb.feature.character.data.mapper

import com.zc.vndb.common.data.mapper.toCover
import com.zc.vndb.common.data.mapper.toReleaseDate
import com.zc.vndb.common.domain.model.CharacterRole
import com.zc.vndb.common.domain.model.SensitiveContentPolicy
import com.zc.vndb.common.domain.model.VoiceActor
import com.zc.vndb.feature.character.data.dto.CharacterAppearanceDTO
import com.zc.vndb.feature.character.data.dto.CharacterNovelVoicesPageDTO
import com.zc.vndb.feature.character.data.dto.CharacterProfileDTO
import com.zc.vndb.feature.character.data.dto.CharacterTraitDTO
import com.zc.vndb.feature.character.domain.model.Birthday
import com.zc.vndb.feature.character.domain.model.CharacterAppearance
import com.zc.vndb.feature.character.domain.model.CharacterProfile
import com.zc.vndb.feature.character.domain.model.CharacterTrait
import com.zc.vndb.feature.character.domain.model.Gender
import com.zc.vndb.feature.character.domain.model.Measurements
import com.zc.vndb.feature.character.domain.model.Sex

/** Unknown spoiler levels are treated as major so they stay hidden. */
private const val MAJOR_SPOILER = 2
private const val BIRTHDAY_PARTS = 2

fun CharacterProfileDTO.toDomain(policy: SensitiveContentPolicy): CharacterProfile {
    val apparentSex = sex?.getOrNull(0).toSex()
    val apparentGender = gender?.getOrNull(0).toGender()
    return CharacterProfile(
        id = id,
        name = name,
        originalName = original?.takeIf { it.isNotBlank() && it != name },
        aliases = aliases.orEmpty().filter { it.isNotBlank() }.distinct(),
        description = description?.takeIf { it.isNotBlank() },
        image = image?.toCover(policy),
        age = age,
        birthday = birthday?.takeIf { it.size == BIRTHDAY_PARTS }?.let { (month, day) ->
            Birthday(
                month = month,
                day = day,
            )
        },
        bloodType = bloodType,
        heightCm = height?.takeIf { it > 0 },
        weightKg = weight?.takeIf { it > 0 },
        measurements = measurements(),
        sex = apparentSex,
        spoilerSex = sex?.getOrNull(1).toSex()?.takeIf { it != apparentSex },
        gender = apparentGender,
        spoilerGender = gender?.getOrNull(1).toGender()?.takeIf { it != apparentGender },
        traits = traits.orEmpty().map { it.toDomain() },
        appearances = vns.orEmpty().distinctBy { it.id }.map { it.toDomain(policy) },
    )
}

/** VN id → voice actor of [characterId] in it. */
fun CharacterNovelVoicesPageDTO.toVoiceActors(characterId: String): Map<String, VoiceActor> =
    results
        .mapNotNull { novel ->
            novel.va.orEmpty().firstOrNull { it.character.id == characterId }?.let {
                novel.id to VoiceActor(id = it.staff.id, name = it.staff.name)
            }
        }
        .toMap()

private fun CharacterProfileDTO.measurements(): Measurements? =
    Measurements(bust = bust, waist = waist, hips = hips, cup = cup)
        .takeIf { listOf(bust, waist, hips, cup).any { part -> part != null } }

private fun CharacterTraitDTO.toDomain() = CharacterTrait(
    name = name,
    group = groupName.orEmpty(),
    spoilerLevel = spoiler ?: MAJOR_SPOILER,
    isLie = lie ?: false,
)

private fun CharacterAppearanceDTO.toDomain(policy: SensitiveContentPolicy) = CharacterAppearance(
    novelId = id,
    novelTitle = title.orEmpty(),
    novelOriginalTitle = altTitle,
    releaseDate = released.toReleaseDate(),
    rating = rating,
    cover = image?.toCover(policy),
    role = when (role) {
        "main" -> CharacterRole.Main
        "primary" -> CharacterRole.Primary
        "side" -> CharacterRole.Side
        else -> CharacterRole.Appears
    },
    spoilerLevel = spoiler ?: MAJOR_SPOILER,
    voiceActor = null,
)

private fun String?.toSex(): Sex? = when (this) {
    "m" -> Sex.Male
    "f" -> Sex.Female
    "b" -> Sex.Both
    "n" -> Sex.Sexless
    else -> null
}

private fun String?.toGender(): Gender? = when (this) {
    "m" -> Gender.Male
    "f" -> Gender.Female
    "o" -> Gender.NonBinary
    "a" -> Gender.Ambiguous
    else -> null
}
