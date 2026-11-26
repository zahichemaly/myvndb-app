package com.zc.vndb.feature.detail.data.mapper

import com.zc.vndb.common.data.mapper.toCover
import com.zc.vndb.common.domain.model.CharacterRole
import com.zc.vndb.common.domain.model.SensitiveContentPolicy
import com.zc.vndb.feature.detail.data.dto.CharacterDTO
import com.zc.vndb.feature.detail.domain.model.NovelCharacter

/** Unknown spoiler levels are treated as major so they stay hidden. */
private const val MAJOR_SPOILER = 2

/** The character as they appear in [novelId]; null if VNDB does not list them in it. */
fun CharacterDTO.toDomain(novelId: String, policy: SensitiveContentPolicy): NovelCharacter? {
    val appearance = vns.orEmpty().firstOrNull { it.id == novelId } ?: return null
    return NovelCharacter(
        id = id,
        name = name,
        originalName = original?.takeIf { it.isNotBlank() && it != name },
        image = image?.toCover(policy),
        role = appearance.role.toCharacterRole(),
        spoilerLevel = appearance.spoiler ?: MAJOR_SPOILER,
        voiceActor = null,
    )
}

private fun String?.toCharacterRole(): CharacterRole = when (this) {
    "main" -> CharacterRole.Main
    "primary" -> CharacterRole.Primary
    "side" -> CharacterRole.Side
    else -> CharacterRole.Appears
}
