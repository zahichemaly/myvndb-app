package com.zc.vndb.feature.character.data.mapper

import com.zc.vndb.common.domain.model.CharacterRole
import com.zc.vndb.common.domain.model.SensitiveContentPolicy
import com.zc.vndb.feature.character.data.dto.CharacterAppearanceDTO
import com.zc.vndb.feature.character.data.dto.CharacterProfileDTO
import com.zc.vndb.feature.character.data.dto.CharacterTraitDTO
import com.zc.vndb.feature.character.domain.model.Birthday
import com.zc.vndb.feature.character.domain.model.Gender
import com.zc.vndb.feature.character.domain.model.Sex
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CharacterProfileDTOMapperTest {

    private fun dto(
        sex: List<String?>? = null,
        gender: List<String?>? = null,
        birthday: List<Int>? = null,
        height: Int? = null,
        bust: Int? = null,
        cup: String? = null,
        traits: List<CharacterTraitDTO>? = null,
        vns: List<CharacterAppearanceDTO>? = null,
    ) = CharacterProfileDTO(
        id = "c1",
        name = "Okabe",
        original = "Okabe",
        aliases = listOf("Hououin Kyouma", " ", "Hououin Kyouma"),
        description = " ",
        image = null,
        age = null,
        birthday = birthday,
        bloodType = null,
        height = height,
        weight = 0,
        bust = bust,
        waist = null,
        hips = null,
        cup = cup,
        sex = sex,
        gender = gender,
        traits = traits,
        vns = vns,
    )

    private fun appearance(id: String, role: String?, spoiler: Int?) = CharacterAppearanceDTO(
        id = id,
        title = null,
        released = null,
        rating = null,
        image = null,
        role = role,
        spoiler = spoiler,
    )

    @Test
    fun `spoiler sex and gender are kept only when they differ`() {
        val same = dto(
            sex = listOf("f", "f"),
            gender = listOf("f", "f"),
        ).toDomain(SensitiveContentPolicy.Standard)
        assertEquals(Sex.Female, same.sex)
        assertNull(same.spoilerSex)
        assertNull(same.spoilerGender)

        val twist = dto(
            sex = listOf("f", "m"),
            gender = listOf("m", "o"),
        ).toDomain(SensitiveContentPolicy.Standard)
        assertEquals(Sex.Male, twist.spoilerSex)
        assertEquals(Gender.Male, twist.gender)
        assertEquals(Gender.NonBinary, twist.spoilerGender)

        assertNull(dto(sex = listOf(null, null)).toDomain(SensitiveContentPolicy.Standard).sex)
    }

    @Test
    fun `cleans up names, aliases, description and empty numbers`() {
        val profile = dto(
            birthday = listOf(7, 25),
            height = 0,
        ).toDomain(SensitiveContentPolicy.Standard)

        assertNull(profile.originalName)
        assertEquals(listOf("Hououin Kyouma"), profile.aliases)
        assertNull(profile.description)
        assertEquals(Birthday(7, 25), profile.birthday)
        assertNull(profile.heightCm)
        assertNull(profile.weightKg)
        assertNull(profile.measurements)
        assertNull(dto(birthday = listOf(7)).toDomain(SensitiveContentPolicy.Standard).birthday)
    }

    @Test
    fun `measurements appear when any part is known`() {
        val measurements = dto(
            bust = 79,
            cup = "C",
        ).toDomain(SensitiveContentPolicy.Standard).measurements

        assertEquals(79, measurements?.bust)
        assertEquals("C", measurements?.cup)
        assertNull(measurements?.waist)
    }

    @Test
    fun `traits and appearances default to hidden spoilers, appearances are one per VN`() {
        val profile = dto(
            traits = listOf(
                CharacterTraitDTO(
                    name = "Tsundere",
                    groupName = null,
                    spoiler = null,
                    lie = null,
                    sexual = null,
                ),
            ),
            vns = listOf(
                appearance("v1", "main", 0),
                appearance("v1", "main", 0),
                appearance("v2", "cameo", null),
            ),
        ).toDomain(SensitiveContentPolicy.Standard)

        val trait = profile.traits.single()
        assertEquals(2, trait.spoilerLevel)
        assertEquals("", trait.group)
        assertEquals(false, trait.isLie)
        assertEquals(false, trait.isSexual)
        assertEquals(listOf("v1", "v2"), profile.appearances.map { it.novelId })
        assertEquals(CharacterRole.Main, profile.appearances[0].role)
        assertEquals(CharacterRole.Appears, profile.appearances[1].role)
        assertEquals(2, profile.appearances[1].spoilerLevel)
        assertTrue(profile.appearances.all { it.voiceActor == null })
    }
}
