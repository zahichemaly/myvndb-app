package com.zc.vndb.common.ui.mapper

import com.zc.vndb.common.domain.model.CommonLanguageCodes
import com.zc.vndb.common.domain.model.LanguageOption
import com.zc.vndb.common.ui.text.UiText
import java.util.Locale

private const val LANGUAGES_LISTED = 2

/** VNDB language code → name in the device language ("zh-Hans" → "Chinese (Simplified)"). */
fun languageName(code: String): String =
    Locale.forLanguageTag(code).getDisplayName(Locale.getDefault()).ifBlank { code }
        .replaceFirstChar { it.titlecase(Locale.getDefault()) }

/** "English, Japanese", or "English, Japanese +2" for longer lists. */
fun List<String>.toLanguagesLabel(nameOf: (String) -> String = ::languageName): UiText {
    val names = take(LANGUAGES_LISTED).joinToString(", ") { nameOf(it) }
    val more = size - LANGUAGES_LISTED
    return UiText.StringValue(if (more > 0) "$names +$more" else names)
}

/** The name a picker shows: VNDB's English label, or the device's name for the code when there is none. */
fun LanguageOption.displayName(): String = label.ifBlank { languageName(code) }

/** The languages as a picker lists them: the common ones first in their usual order, then the rest A to Z. */
fun List<LanguageOption>.inPickerOrder(): List<LanguageOption> {
    val common = CommonLanguageCodes.mapNotNull { code -> firstOrNull { it.code == code } }
    val rest = filter { it.code !in CommonLanguageCodes }
        .sortedBy { it.displayName().lowercase() }
    return common + rest
}

/** The name of [code] from this list, or the device's name for a code that is not in it. */
fun List<LanguageOption>.nameOf(code: String): String =
    firstOrNull { it.code == code }?.displayName() ?: languageName(code)

/** [codes] as options carrying this list's labels; a code that is not in the list has no label. */
fun List<LanguageOption>.resolve(codes: List<String>): List<LanguageOption> =
    codes.map { code -> firstOrNull { it.code == code } ?: LanguageOption(code, "") }
