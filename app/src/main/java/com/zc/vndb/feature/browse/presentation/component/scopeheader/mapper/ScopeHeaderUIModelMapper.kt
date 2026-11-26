package com.zc.vndb.feature.browse.presentation.component.scopeheader.mapper

import androidx.annotation.StringRes
import com.zc.vndb.common.domain.model.TOP_RATED_MIN_VOTES
import com.zc.vndb.common.domain.model.TagCategory
import com.zc.vndb.common.ui.component.linksrow.model.LinkUIModel
import com.zc.vndb.common.ui.mapper.bbCodeToPlainText
import com.zc.vndb.common.ui.resource.PluralResource
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.browse.domain.model.ProducerInfo
import com.zc.vndb.feature.browse.domain.model.ProducerType
import com.zc.vndb.feature.browse.domain.model.StaffGender
import com.zc.vndb.feature.browse.domain.model.StaffInfo
import com.zc.vndb.feature.browse.domain.model.TagInfo
import com.zc.vndb.feature.browse.presentation.component.scopeheader.model.ScopeHeaderUIModel
import java.util.Locale
import kotlinx.collections.immutable.toImmutableList

fun TagInfo.toHeaderUIModel(): ScopeHeaderUIModel = ScopeHeaderUIModel(
    name = UiText.StringValue(name),
    originalName = null,
    typeLabel = category?.let {
        UiText.StringResource(
            when (it) {
                TagCategory.Content -> StringResource.tag_category_content
                TagCategory.Sexual -> StringResource.tag_category_sexual
                TagCategory.Technical -> StringResource.tag_category_technical
            },
        )
    },
    isSexualCategory = category == TagCategory.Sexual,
    countLabel = UiText.StringPlural(
        PluralResource.tag_vn_count,
        novelCount,
        listOf("%,d".format(novelCount)),
    ),
    aliasesLabel = aliases.toAliasesLabel(),
    aboutTitle = UiText.StringResource(StringResource.tag_about),
    description = description.toPlainDescription(),
)

fun ProducerInfo.toHeaderUIModel(locale: Locale = Locale.getDefault()): ScopeHeaderUIModel =
    ScopeHeaderUIModel(
        name = UiText.StringValue(name),
        originalName = originalName?.let(UiText::StringValue),
        typeLabel = toTypeLabel(locale),
        isSexualCategory = false,
        countLabel = null,
        aliasesLabel = aliases.toAliasesLabel(),
        aboutTitle = UiText.StringResource(StringResource.producer_about),
        description = description.toPlainDescription(),
    )

fun StaffInfo.toHeaderUIModel(locale: Locale = Locale.getDefault()): ScopeHeaderUIModel =
    ScopeHeaderUIModel(
        name = UiText.StringValue(name),
        originalName = originalName?.let(UiText::StringValue),
        typeLabel = labelWithLanguage(
            typeRes = when (gender) {
                StaffGender.Male -> StringResource.staff_gender_male
                StaffGender.Female -> StringResource.staff_gender_female
                null -> null
            },
            language = language,
            locale = locale,
        ),
        isSexualCategory = false,
        countLabel = null,
        aliasesLabel = aliases.toAliasesLabel(),
        aboutTitle = UiText.StringResource(StringResource.staff_about),
        description = description.toPlainDescription(),
        links = buildList {
            add(
                LinkUIModel(
                    label = UiText.StringResource(StringResource.staff_vndb_link),
                    url = "https://vndb.org/$id",
                    isVndb = true,
                ),
            )
            links.forEach { add(LinkUIModel(UiText.StringValue(it.label), it.url, isVndb = false)) }
        }.toImmutableList(),
    )

/** "Company · Japanese", or whichever of the two VNDB has. */
private fun ProducerInfo.toTypeLabel(locale: Locale): UiText? = labelWithLanguage(
    typeRes = when (type) {
        ProducerType.Company -> StringResource.producer_type_company
        ProducerType.Individual -> StringResource.producer_type_individual
        ProducerType.AmateurGroup -> StringResource.producer_type_amateur
        null -> null
    },
    language = language,
    locale = locale,
)

/** [typeRes] and the display name of [language] joined by a dot, or whichever one exists. */
private fun labelWithLanguage(
    @StringRes typeRes: Int?,
    language: String?,
    locale: Locale,
): UiText? {
    val languageName = language
        ?.let { Locale.forLanguageTag(it).getDisplayLanguage(locale) }
        ?.takeIf { it.isNotBlank() }
    return when {
        typeRes != null && languageName != null ->
            UiText.StringResource(
                StringResource.scope_type_with_language,
                listOf(UiText.StringResource(typeRes), languageName),
            )

        typeRes != null -> UiText.StringResource(typeRes)

        languageName != null -> UiText.StringValue(languageName)

        else -> null
    }
}

/** Top Rated has nothing to load: its header names the list and says what gets into it. */
fun topRatedHeaderUIModel(): ScopeHeaderUIModel = ScopeHeaderUIModel(
    name = UiText.StringResource(StringResource.browse_top_rated_title),
    originalName = null,
    typeLabel = null,
    isSexualCategory = false,
    countLabel = UiText.StringPlural(
        PluralResource.browse_top_rated_rule,
        TOP_RATED_MIN_VOTES,
        listOf(TOP_RATED_MIN_VOTES),
    ),
    aliasesLabel = null,
    // Unused without a description.
    aboutTitle = UiText.StringResource(StringResource.browse_top_rated_title),
    description = null,
)

private fun List<String>.toAliasesLabel(): UiText? = takeIf { it.isNotEmpty() }
    ?.let { UiText.StringResource(StringResource.tag_aliases, listOf(it.joinToString(", "))) }

private fun String?.toPlainDescription(): UiText? = this
    ?.bbCodeToPlainText(keepParagraphs = true, showSpoilers = true)
    ?.takeIf { it.isNotEmpty() }
    ?.let(UiText::StringValue)
