package com.zc.vndb.feature.discover.domain.usecase

import com.zc.vndb.feature.discover.domain.model.Release
import com.zc.vndb.feature.discover.domain.model.ReleaseKind
import com.zc.vndb.feature.discover.domain.model.ReleaseNovel
import com.zc.vndb.feature.discover.domain.model.TrendingRelease

/**
 * "New & Trending" rows from releases in the user's [userLanguages], in the given order: one per
 * visual novel, each labelled with why it is new. Machine-translation-only releases are left out.
 * Shared by Discover's short list and the full page so both show the same rows.
 */
internal fun List<Release>.toTrendingReleases(userLanguages: List<String>): List<TrendingRelease> =
    filterNot { it.isMachineTranslatedOnly }
        .mapNotNull { it.toTrendingOrNull(userLanguages) }
        .distinctBy { it.novelId }

private val Release.isMachineTranslatedOnly: Boolean
    get() = languages.isNotEmpty() && languages.all { it.isMachineTranslated }

private fun Release.toTrendingOrNull(userLanguages: List<String>): TrendingRelease? {
    val novel = novels.firstOrNull() ?: return null
    val kind = kindFor(novel)
    return TrendingRelease(
        releaseId = id,
        novelId = novel.id,
        title = novel.title,
        originalTitle = novel.originalTitle,
        kind = kind,
        releaseDate = releaseDate,
        platforms = platforms,
        rating = novel.rating,
        cover = novel.cover,
        translationLanguage = if (kind == ReleaseKind.NewTranslation) {
            translationLanguage(novel, userLanguages)
        } else {
            null
        },
    )
}

/** The language of a human translation: one the user reads if there is one, in their order. */
private fun Release.translationLanguage(
    novel: ReleaseNovel,
    userLanguages: List<String>,
): String? {
    val translated = languages
        .filter { !it.isMachineTranslated && it.code != novel.originalLanguage }
        .map { it.code }
    return userLanguages.firstOrNull { it in translated } ?: translated.firstOrNull()
}

private fun Release.kindFor(novel: ReleaseNovel): ReleaseKind = when {
    isPatch -> ReleaseKind.RecentPatch

    novel.releaseDate >= releaseDate -> ReleaseKind.NewRelease

    languages.any {
        !it.isMachineTranslated && it.code != novel.originalLanguage
    } -> ReleaseKind.NewTranslation

    else -> ReleaseKind.ReRelease
}
