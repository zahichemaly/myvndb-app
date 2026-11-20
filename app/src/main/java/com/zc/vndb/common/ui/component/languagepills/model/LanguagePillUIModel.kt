package com.zc.vndb.common.ui.component.languagepills.model

import androidx.compose.runtime.Immutable

/** One language on a card: its [name], with the [flag] emoji when the language has a national flag. */
@Immutable
data class LanguagePillUIModel(val code: String, val flag: String?, val name: String) {
    /** "🇬🇧 English", or just "Catalan" when there is no flag. */
    val label: String get() = flag?.let { "$it $name" } ?: name
}
