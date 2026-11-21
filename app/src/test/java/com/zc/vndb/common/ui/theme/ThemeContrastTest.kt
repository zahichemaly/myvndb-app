package com.zc.vndb.common.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.zc.vndb.common.domain.model.AppTheme
import com.zc.vndb.common.ui.mapper.toSpec
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Every theme keeps its main text and accent readable: WCAG AA (4.5:1) for normal-size text.
 * When a theme fails, fix its colors; never lower the threshold or skip the theme.
 */
class ThemeContrastTest {

    @Test
    fun `body text reads on cards in every theme`() = assertEveryTheme("onSurface on surfaceCard") {
        it.colorScheme.onSurface to it.colors.surfaceCard
    }

    @Test
    fun `the accent reads on cards in every theme`() = assertEveryTheme("accent on surfaceCard") {
        it.colors.accent to it.colors.surfaceCard
    }

    @Test
    fun `text on the accent reads in every theme`() = assertEveryTheme("onAccent on accent") {
        it.colors.onAccent to it.colors.accent
    }

    private fun assertEveryTheme(pair: String, colors: (VndbThemeSpec) -> Pair<Color, Color>) {
        val failures = AppTheme.entries.mapNotNull { theme ->
            val (foreground, background) = colors(theme.toSpec())
            val ratio = contrast(foreground, background)
            if (ratio < MIN_CONTRAST) "$theme (${(ratio * 100).roundToInt() / 100f}:1)" else null
        }
        assertTrue(failures.isEmpty(), "$pair is below $MIN_CONTRAST:1 in $failures")
    }

    private fun contrast(first: Color, second: Color): Float {
        val lighter = maxOf(first.luminance(), second.luminance())
        val darker = minOf(first.luminance(), second.luminance())
        return (lighter + LUMINANCE_OFFSET) / (darker + LUMINANCE_OFFSET)
    }

    private companion object {
        const val MIN_CONTRAST = 4.5f

        /** The flare term in the WCAG contrast formula. */
        const val LUMINANCE_OFFSET = 0.05f
    }
}
