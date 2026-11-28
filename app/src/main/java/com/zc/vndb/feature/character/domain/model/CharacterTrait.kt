package com.zc.vndb.feature.character.domain.model

data class CharacterTrait(
    val name: String,
    /** VNDB trait group, e.g. "Hair", "Personality". */
    val group: String,
    /** 0 = no spoiler, 1 = minor, 2 = major. */
    val spoilerLevel: Int,
    /** The character only pretends to have this trait. */
    val isLie: Boolean,
    /** A sexual trait, shown only with Settings → Show sensitive content. */
    val isSexual: Boolean,
)
