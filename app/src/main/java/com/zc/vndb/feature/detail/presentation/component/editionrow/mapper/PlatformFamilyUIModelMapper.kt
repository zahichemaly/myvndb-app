package com.zc.vndb.feature.detail.presentation.component.editionrow.mapper

import androidx.compose.runtime.Composable
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.feature.detail.domain.model.PlatformFamily
import com.zc.vndb.feature.detail.presentation.component.editionrow.model.PlatformIcon

/** Generic icon of a platform family, for the row's icon box and platforms without a logo. */
fun PlatformFamily.toPlatformIcon(): PlatformIcon = when (this) {
    PlatformFamily.Pc -> PlatformIcon.Pc
    PlatformFamily.Console -> PlatformIcon.Console
    PlatformFamily.Mobile -> PlatformIcon.Mobile
    PlatformFamily.Web -> PlatformIcon.Web
    PlatformFamily.Other -> PlatformIcon.Other
}

/** Tint of the family's icon in the row's icon box. */
@Composable
fun PlatformFamily.toColors(): TintedColors = when (this) {
    PlatformFamily.Pc -> VndbTheme.colors.releaseTranslation
    PlatformFamily.Console -> VndbTheme.colors.releasePatch
    PlatformFamily.Mobile -> VndbTheme.colors.releaseRerelease
    PlatformFamily.Web, PlatformFamily.Other -> VndbTheme.colors.chipNeutral
}
