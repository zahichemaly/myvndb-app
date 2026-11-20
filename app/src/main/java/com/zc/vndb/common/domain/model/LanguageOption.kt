package com.zc.vndb.common.domain.model

/** A language VNDB knows: [code] is what its `lang` and `olang` filters take, [label] its English name. */
data class LanguageOption(val code: String, val label: String)

/**
 * The languages most novels use, in display order. They head every language picker, and they are
 * the whole list until VNDB's own list has been fetched.
 */
val CommonLanguageCodes: List<String> = AppSettings.TRENDING_LANGUAGE_CHOICES

/** The pickers' list before VNDB's has been fetched; the empty label means "name it from the code". */
val BuiltInLanguages: List<LanguageOption> = CommonLanguageCodes.map { LanguageOption(it, "") }
