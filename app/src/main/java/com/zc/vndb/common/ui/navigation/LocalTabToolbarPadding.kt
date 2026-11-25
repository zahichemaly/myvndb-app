package com.zc.vndb.common.ui.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Room a tab's screen leaves at the bottom of its scrolling content and under its snackbars, so
 * both clear the floating tab toolbar drawn over it. Zero on pages, which cover the toolbar.
 */
val LocalTabToolbarPadding = staticCompositionLocalOf<Dp> { 0.dp }
