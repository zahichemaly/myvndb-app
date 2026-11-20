package com.zc.vndb.feature.detail.presentation.mapper

import java.util.Locale

private const val REGIONAL_INDICATOR_A = 0x1F1E6

/**
 * VNDB language code → country whose flag stands for it. English uses the British flag; languages
 * without a national flag (Catalan, Basque, Esperanto, Latin, …) have none.
 */
private val flagCountries = mapOf(
    "ar" to "SA",
    "be" to "BY",
    "bg" to "BG",
    "cs" to "CZ",
    "da" to "DK",
    "de" to "DE",
    "el" to "GR",
    "en" to "GB",
    "es" to "ES",
    "fa" to "IR",
    "fi" to "FI",
    "fr" to "FR",
    "ga" to "IE",
    "he" to "IL",
    "hi" to "IN",
    "hr" to "HR",
    "hu" to "HU",
    "id" to "ID",
    "it" to "IT",
    "ja" to "JP",
    "kk" to "KZ",
    "ko" to "KR",
    "lt" to "LT",
    "lv" to "LV",
    "mk" to "MK",
    "ms" to "MY",
    "ne" to "NP",
    "nl" to "NL",
    "no" to "NO",
    "pl" to "PL",
    "pt-br" to "BR",
    "pt-pt" to "PT",
    "ro" to "RO",
    "ru" to "RU",
    "sk" to "SK",
    "sl" to "SI",
    "sr" to "RS",
    "sv" to "SE",
    "ta" to "IN",
    "th" to "TH",
    "tl" to "PH",
    "tr" to "TR",
    "uk" to "UA",
    "ur" to "PK",
    "vi" to "VN",
    "zh" to "CN",
    "zh-hans" to "CN",
    "zh-hant" to "TW",
)

/** Flag emoji for a VNDB language code ("ja" → 🇯🇵, "en" → 🇬🇧), or null when there is none. */
fun languageFlag(code: String): String? =
    flagCountries[code.lowercase(Locale.ROOT)]?.let { country ->
        buildString { country.forEach { appendCodePoint(REGIONAL_INDICATOR_A + (it - 'A')) } }
    }
