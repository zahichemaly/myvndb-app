package com.zc.vndb.feature.detail.domain.model

/** Rough grouping of VNDB platform codes, for the edition icon and badge. */
enum class PlatformFamily {
    Pc,
    Console,
    Mobile,
    Web,
    Other,
    ;

    companion object {
        private val pcPlatforms = setOf(
            "win", "lin", "mac", "dos", "p88", "p98", "x1s", "x68", "fm7", "fm8", "fmt", "msx", "pce",
        )
        private val consolePlatforms = setOf(
            "swi", "sw2", "ps1", "ps2", "ps3", "ps4", "ps5", "psp", "psv", "xb1", "xb3", "xbo", "xxs",
            "n3d", "nds", "wii", "wiu", "gba", "gbc", "nes", "sat", "drc", "sfc", "smd", "scd", "pcf",
            "tdo", "vnd",
        )
        private val mobilePlatforms = setOf("and", "ios", "mob")

        /** Rough family of a VNDB platform code ("win" → [Pc], "swi" → [Console]). */
        fun fromCode(code: String): PlatformFamily = when (code) {
            in pcPlatforms -> Pc
            in consolePlatforms -> Console
            in mobilePlatforms -> Mobile
            "web" -> Web
            else -> Other
        }
    }
}
