package com.zc.vndb.feature.detail.presentation.component.editionrow.mapper

import androidx.compose.runtime.Composable
import com.zc.vndb.common.domain.model.PlatformFamily
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.VndbTheme

/** Tint of the family's icon in the row's icon box. */
@Composable
fun PlatformFamily.toColors(): TintedColors = when (this) {
    PlatformFamily.Pc -> VndbTheme.colors.releaseTranslation
    PlatformFamily.Console -> VndbTheme.colors.releasePatch
    PlatformFamily.Mobile, PlatformFamily.Web, PlatformFamily.Other -> VndbTheme.colors.chipNeutral
}
