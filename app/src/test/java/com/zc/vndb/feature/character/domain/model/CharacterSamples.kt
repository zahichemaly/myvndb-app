package com.zc.vndb.feature.character.domain.model

import com.zc.vndb.common.domain.model.CharacterRole
import com.zc.vndb.common.domain.model.ReleaseDate
import kotlinx.datetime.LocalDate

object CharacterSamples {

    fun appearance(
        novelId: String,
        role: CharacterRole = CharacterRole.Primary,
        year: Int = 2009,
        spoiler: Int = 0,
    ) = CharacterAppearance(
        novelId = novelId,
        novelTitle = "Novel $novelId",
        releaseDate = ReleaseDate.Full(LocalDate(year, 1, 1)),
        rating = 90.2,
        cover = null,
        role = role,
        spoilerLevel = spoiler,
        voiceActor = null,
    )

    fun trait(
        name: String,
        group: String = "Hair",
        spoiler: Int = 0,
        lie: Boolean = false,
        sexual: Boolean = false,
    ) = CharacterTrait(
        name = name,
        group = group,
        spoilerLevel = spoiler,
        isLie = lie,
        isSexual = sexual,
    )

    fun profile(
        appearances: List<CharacterAppearance> = emptyList(),
        traits: List<CharacterTrait> = emptyList(),
        sex: Sex? = Sex.Female,
        spoilerSex: Sex? = null,
        gender: Gender? = Gender.Female,
        spoilerGender: Gender? = null,
        description: String? = "Hello",
    ) = CharacterProfile(
        id = "c6487",
        name = "Makise Kurisu",
        originalName = "牧瀬 紅莉栖",
        aliases = listOf("Assistant", "Christina"),
        description = description,
        image = null,
        age = 18,
        birthday = Birthday(month = 7, day = 25),
        bloodType = "a",
        heightCm = 160,
        weightKg = 45,
        measurements = Measurements(bust = 79, waist = 56, hips = 83, cup = null),
        sex = sex,
        spoilerSex = spoilerSex,
        gender = gender,
        spoilerGender = spoilerGender,
        traits = traits,
        appearances = appearances,
    )
}
