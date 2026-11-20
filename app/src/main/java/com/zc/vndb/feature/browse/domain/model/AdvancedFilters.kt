package com.zc.vndb.feature.browse.domain.model

import com.zc.vndb.common.domain.model.NovelLength

/**
 * The filters behind the Browse filter sheet beyond the age rating and genres. Each one narrows
 * the list further; the default (everything off) matches every novel the others allow.
 */
data class AdvancedFilters(
    /** VNDB language codes: only novels with a release in any of them (`lang`). */
    val supportedLanguages: Set<String> = emptySet(),
    /** A VNDB language code such as `ja`: only novels originally written in it (`olang`). */
    val originalLanguage: String? = null,
    /** Play-time ranges; a novel matches any of the chosen ones. */
    val lengths: Set<NovelLength> = emptySet(),
    val era: ReleaseEra? = null,
) {
    /** How many of the four filters are on (the languages and lengths count as one each). */
    val activeCount: Int
        get() = listOf(
            supportedLanguages.isNotEmpty(),
            originalLanguage != null,
            lengths.isNotEmpty(),
            era != null,
        ).count { it }

    val isEmpty: Boolean get() = activeCount == 0

    /** These filters with [kind] switched off. */
    fun without(kind: AdvancedFilterKind): AdvancedFilters = when (kind) {
        AdvancedFilterKind.SupportedLanguages -> copy(supportedLanguages = emptySet())
        AdvancedFilterKind.OriginalLanguage -> copy(originalLanguage = null)
        AdvancedFilterKind.Length -> copy(lengths = emptySet())
        AdvancedFilterKind.Era -> copy(era = null)
    }
}

/** The four filters, for removing one from the active-filter pills. */
enum class AdvancedFilterKind { SupportedLanguages, OriginalLanguage, Length, Era }

/** When a novel was first released. */
enum class ReleaseEra { Since2020, In2010s, In2000s, Before2000 }
