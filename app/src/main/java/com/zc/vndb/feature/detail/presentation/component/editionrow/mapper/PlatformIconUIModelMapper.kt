package com.zc.vndb.feature.detail.presentation.component.editionrow.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.zc.vndb.common.ui.resource.DrawableResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.detail.domain.model.PlatformFamily
import com.zc.vndb.feature.detail.presentation.component.editionrow.model.PlatformIcon
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

private const val MAX_PLATFORM_ICONS = 4

private val nintendoPlatforms =
    setOf("nes", "sfc", "n64", "gbc", "gba", "nds", "n3d", "wii", "wiu", "swi", "sw2")
private val playStationPlatforms = setOf("ps1", "ps2", "ps3", "ps4", "ps5", "psp", "psv")
private val xboxPlatforms = setOf("xb1", "xb3", "xbo", "xxs")

/**
 * Distinct icons for VNDB platform codes in first-seen order, at most [MAX_PLATFORM_ICONS]:
 * "win", "lin", "mac" → Windows, Linux, Apple.
 */
fun List<String>.toPlatformIcons(): ImmutableList<PlatformIcon> =
    distinctIcons().take(MAX_PLATFORM_ICONS).toImmutableList()

/** "+2" when the platforms need more icons than [toPlatformIcons] shows, else null. */
fun List<String>.toMorePlatformsLabel(): UiText? {
    val more = distinctIcons().size - MAX_PLATFORM_ICONS
    return if (more > 0) UiText.StringValue("+$more") else null
}

private fun List<String>.distinctIcons(): List<PlatformIcon> =
    filter { it.isNotEmpty() }.map { it.toPlatformIcon() }.distinct()

private fun String.toPlatformIcon(): PlatformIcon = when (this) {
    "win" -> PlatformIcon.Windows
    "ios", "mac" -> PlatformIcon.Apple
    "lin" -> PlatformIcon.Linux
    "and" -> PlatformIcon.Android
    "web" -> PlatformIcon.Web
    in nintendoPlatforms -> PlatformIcon.Nintendo
    in playStationPlatforms -> PlatformIcon.PlayStation
    in xboxPlatforms -> PlatformIcon.Xbox
    else -> PlatformFamily.fromCode(this).toPlatformIcon()
}

/** Brand logos are vector drawables (Simple Icons); generic icons come from Material. */
@Composable
fun PlatformIcon.toImageVector(): ImageVector = when (this) {
    PlatformIcon.Windows -> ImageVector.vectorResource(DrawableResource.ic_platform_windows)
    PlatformIcon.Apple -> ImageVector.vectorResource(DrawableResource.ic_platform_apple)
    PlatformIcon.Linux -> ImageVector.vectorResource(DrawableResource.ic_platform_linux)
    PlatformIcon.Android -> ImageVector.vectorResource(DrawableResource.ic_platform_android)
    PlatformIcon.Nintendo -> ImageVector.vectorResource(DrawableResource.ic_platform_nintendo)
    PlatformIcon.PlayStation -> ImageVector.vectorResource(DrawableResource.ic_platform_playstation)
    PlatformIcon.Xbox -> ImageVector.vectorResource(DrawableResource.ic_platform_xbox)
    PlatformIcon.Web -> Icons.Outlined.Language
    PlatformIcon.Pc -> Icons.Outlined.Computer
    PlatformIcon.Console -> Icons.Outlined.SportsEsports
    PlatformIcon.Mobile -> Icons.Outlined.PhoneAndroid
    PlatformIcon.Other -> Icons.Outlined.Devices
}
