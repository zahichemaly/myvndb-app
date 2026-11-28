package com.zc.vndb.feature.character.presentation.component.traitgroups.mapper

import com.zc.vndb.common.ui.component.spoilerfilter.model.SpoilerLevel
import com.zc.vndb.common.ui.resource.PluralResource
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.character.domain.model.CharacterSamples.trait
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TraitGroupUIModelMapperTest {

    private val traits = listOf(
        trait("Kind", group = "Personality"),
        trait("Long", group = "Hair"),
        trait("Mystery Group Trait", group = "Something new"),
        trait("Red", group = "Hair"),
        trait("Minor", group = "Role", spoiler = 1),
        trait("Liar", group = "Personality", lie = true),
    )

    @Test
    fun `groups follow VNDB's order with unknown groups last`() {
        val groups = traits.toTraitGroups(SpoilerLevel.None)

        assertEquals(
            listOf("HAIR", "PERSONALITY", "SOMETHING NEW"),
            groups.map { (it.title as UiText.StringValue).value },
        )
        assertEquals(
            listOf<UiText>(UiText.StringValue("Long"), UiText.StringValue("Red")),
            groups[0].traits,
        )
    }

    @Test
    fun `spoiler and lie traits are hidden and counted until allowed`() {
        assertEquals(
            UiText.StringPlural(PluralResource.detail_hidden_by_spoiler, 2, listOf(2)),
            traits.toHiddenTraitsLabel(SpoilerLevel.None),
        )
        assertEquals(
            UiText.StringPlural(PluralResource.detail_hidden_by_spoiler, 1, listOf(1)),
            traits.toHiddenTraitsLabel(SpoilerLevel.Minor),
        )
        assertNull(traits.toHiddenTraitsLabel(SpoilerLevel.Major))

        val personality = traits.toTraitGroups(
            SpoilerLevel.Major,
        ).single { (it.title as UiText.StringValue).value == "PERSONALITY" }
        assertEquals(
            UiText.StringResource(StringResource.character_trait_lie, listOf("Liar")),
            personality.traits.last(),
        )
    }

    @Test
    fun `sexual traits are listed only with sensitive content and never counted as spoilers`() {
        val withSexual = listOf(
            trait("Kind", group = "Personality"),
            trait("Sexual Trait", group = "Engages in (Sexual)", sexual = true),
            trait("Sexual Spoiler", group = "Subject of (Sexual)", spoiler = 2, sexual = true),
        )

        val hidden = withSexual.toTraitGroups(SpoilerLevel.Major)
        assertEquals(listOf("PERSONALITY"), hidden.map { (it.title as UiText.StringValue).value })
        assertNull(withSexual.toHiddenTraitsLabel(SpoilerLevel.None))

        val shown = withSexual.toTraitGroups(SpoilerLevel.Major, showSexual = true)
        assertEquals(
            listOf("PERSONALITY", "ENGAGES IN (SEXUAL)", "SUBJECT OF (SEXUAL)"),
            shown.map { (it.title as UiText.StringValue).value },
        )
        assertEquals(
            UiText.StringPlural(PluralResource.detail_hidden_by_spoiler, 1, listOf(1)),
            withSexual.toHiddenTraitsLabel(SpoilerLevel.None, showSexual = true),
        )
    }
}
