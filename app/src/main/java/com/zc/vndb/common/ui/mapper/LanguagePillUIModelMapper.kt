package com.zc.vndb.common.ui.mapper

import com.zc.vndb.common.domain.model.LanguageOption
import com.zc.vndb.common.ui.component.languagepills.model.LanguagePillUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * The pills for a novel available in [novelLanguages]: one per language in [cardLanguages] that the
 * novel has, in the order the user picked them. Empty when none is picked or none matches.
 */
fun toLanguagePills(
    novelLanguages: List<String>,
    cardLanguages: List<LanguageOption>,
): ImmutableList<LanguagePillUIModel> = cardLanguages
    .filter { it.code in novelLanguages }
    .map {
        LanguagePillUIModel(
            code = it.code,
            flag = languageFlag(it.code),
            name = it.displayName(),
        )
    }
    .toImmutableList()
