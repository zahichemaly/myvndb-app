package com.zc.vndb.common.ui.component.spoilerfilter.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.ui.graphics.vector.ImageVector
import com.zc.vndb.common.domain.model.SpoilerPreference
import com.zc.vndb.common.ui.component.spoilerfilter.model.SpoilerLevel

/** The spoiler filter level for a stored preference. */
fun SpoilerPreference.toSpoilerLevel(): SpoilerLevel = when (this) {
    SpoilerPreference.None -> SpoilerLevel.None
    SpoilerPreference.Minor -> SpoilerLevel.Minor
    SpoilerPreference.Major -> SpoilerLevel.Major
}

/** Spoilers hidden (eye crossed out), minor ones shown (eye), all shown (! in a circle). */
fun SpoilerLevel.toIcon(): ImageVector = when (this) {
    SpoilerLevel.None -> Icons.Outlined.VisibilityOff
    SpoilerLevel.Minor -> Icons.Outlined.Visibility
    SpoilerLevel.Major -> Icons.Outlined.ErrorOutline
}
