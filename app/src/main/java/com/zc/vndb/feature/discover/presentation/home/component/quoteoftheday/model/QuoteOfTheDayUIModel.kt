package com.zc.vndb.feature.discover.presentation.home.component.quoteoftheday.model

import androidx.compose.runtime.Immutable
import com.zc.vndb.common.ui.text.UiText

@Immutable
data class QuoteOfTheDayUIModel(
    val text: String,
    /** "— Makise Kurisu, Steins;Gate", or just the title when the character is unknown. */
    val attribution: UiText,
    /** The visual novel the quote is from; tapping the card opens it. */
    val novelId: String,
)
