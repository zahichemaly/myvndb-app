package com.zc.vndb.common.ui.component.platformicon.mapper

import com.zc.vndb.common.ui.component.platformicon.model.PlatformIcon
import com.zc.vndb.common.ui.text.UiText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PlatformIconUIModelMapperTest {

    @Test
    fun `brand platforms get their logo, others their family icon`() {
        assertEquals(
            listOf(
                PlatformIcon.Windows,
                PlatformIcon.Linux,
                PlatformIcon.Apple,
                PlatformIcon.Android,
            ),
            listOf("win", "lin", "ios", "and").toPlatformIcons(),
        )
        assertEquals(listOf(PlatformIcon.Apple), listOf("mac", "ios").toPlatformIcons())
        assertEquals(
            listOf(PlatformIcon.Nintendo, PlatformIcon.Web),
            listOf("swi", "web").toPlatformIcons(),
        )
        assertEquals(
            listOf(PlatformIcon.Pc, PlatformIcon.Console, PlatformIcon.Mobile, PlatformIcon.Other),
            listOf("dos", "sat", "mob", "xyz").toPlatformIcons(),
        )
    }

    @Test
    fun `every Xbox platform shares the Xbox logo`() {
        assertEquals(
            listOf(PlatformIcon.Xbox),
            listOf("xb1", "xb3", "xbo", "xxs").toPlatformIcons(),
        )
    }

    @Test
    fun `every PlayStation platform shares the PlayStation logo`() {
        assertEquals(
            listOf(PlatformIcon.PlayStation),
            listOf("ps1", "ps2", "ps3", "ps4", "ps5", "psp", "psv").toPlatformIcons(),
        )
    }

    @Test
    fun `every Nintendo platform shares the Nintendo logo`() {
        assertEquals(
            listOf(PlatformIcon.Nintendo),
            listOf("nds", "n3d", "wii", "wiu", "swi", "sw2", "gba", "sfc").toPlatformIcons(),
        )
    }

    @Test
    fun `icons are distinct, in first-seen order, and empty codes are skipped`() {
        assertEquals(
            listOf(PlatformIcon.Linux, PlatformIcon.Windows, PlatformIcon.Apple),
            listOf("lin", "", "win", "mac", "ios").toPlatformIcons(),
        )
        assertNull(listOf("lin", "win", "mac").toMorePlatformsLabel())
    }

    @Test
    fun `icons past four collapse into a count`() {
        val platforms = listOf("win", "lin", "ios", "and", "swi", "ps4")

        assertEquals(4, platforms.toPlatformIcons().size)
        assertEquals(UiText.StringValue("+2"), platforms.toMorePlatformsLabel())
    }
}
