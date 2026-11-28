package com.zc.vndb.feature.character.presentation.component.traitgroups.mapper

import com.zc.vndb.common.ui.component.spoilerfilter.model.SpoilerLevel
import com.zc.vndb.common.ui.resource.PluralResource
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.character.domain.model.CharacterTrait
import com.zc.vndb.feature.character.presentation.component.traitgroups.model.TraitGroupUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/** VNDB's trait groups in the order a reader scans them; unknown groups go last. */
private val groupOrder =
    listOf("Hair", "Eyes", "Body", "Clothes", "Items", "Personality", "Role", "Engages in", "Subject of")

/**
 * Traits [level] allows, grouped; sexual traits only when [showSexual] (Settings → Show sensitive
 * content). "Lie" traits are spoilers, so they only show at Major.
 */
fun List<CharacterTrait>.toTraitGroups(
    level: SpoilerLevel,
    showSexual: Boolean = false,
): ImmutableList<TraitGroupUIModel> = filter { it.isAllowed(showSexual) && it.isVisibleAt(level) }
    .groupBy { it.group }
    .toList()
    .sortedBy { (group, _) -> groupOrder.indexOf(group).takeIf { it >= 0 } ?: groupOrder.size }
    .map { (group, traits) ->
        TraitGroupUIModel(
            title = UiText.StringValue(group.uppercase()),
            traits = traits.map { trait ->
                if (trait.isLie) {
                    UiText.StringResource(
                        StringResource.character_trait_lie,
                        listOf(trait.name),
                    )
                } else {
                    UiText.StringValue(trait.name)
                }
            }.toImmutableList(),
        )
    }
    .toImmutableList()

/** How many traits [level] hides; null when none. Sexual traits left out by [showSexual] don't count. */
fun List<CharacterTrait>.toHiddenTraitsLabel(
    level: SpoilerLevel,
    showSexual: Boolean = false,
): UiText? {
    val hidden = count { it.isAllowed(showSexual) && !it.isVisibleAt(level) }
    return if (hidden == 0) {
        null
    } else {
        UiText.StringPlural(
            PluralResource.detail_hidden_by_spoiler,
            hidden,
            listOf(hidden),
        )
    }
}

private fun CharacterTrait.isAllowed(showSexual: Boolean): Boolean = showSexual || !isSexual

private fun CharacterTrait.isVisibleAt(level: SpoilerLevel): Boolean =
    spoilerLevel <= level.maxLevel && (!isLie || level == SpoilerLevel.Major)
