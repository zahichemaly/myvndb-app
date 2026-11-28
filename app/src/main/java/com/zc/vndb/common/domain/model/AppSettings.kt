package com.zc.vndb.common.domain.model

/** The user's preferences, from the Settings screen. Defaults apply until something is changed. */
data class AppSettings(
    val titleLanguage: TitleLanguage = TitleLanguage.Romanized,
    /** Spoiler level VN and character pages start with. */
    val defaultSpoilerLevel: SpoilerPreference = SpoilerPreference.None,
    /** VNs on the signed-in user's Finished list open with every spoiler shown. */
    val spoilIfFinished: Boolean = false,
    /** Covers are not blurred, screenshots start with every content level, sexual tags and traits are listed. */
    val showSensitiveContent: Boolean = false,
    /** Browse starts without an age filter instead of All Ages. */
    val includeAdultInBrowse: Boolean = false,
    /** VNDB language codes for Discover's New & Trending; never empty. */
    val trendingLanguages: List<String> = DEFAULT_TRENDING_LANGUAGES,
    /**
     * Up to [MAX_CARD_LANGUAGES] VNDB language codes to flag on Browse and Profile cards; empty (the
     * default) shows none.
     */
    val cardLanguages: List<String> = emptyList(),
    /** Order of the Profile lists. */
    val listSort: ListSort = ListSort.Title,
    /** The Profile lists run opposite to [listSort]'s natural order (Z to A, oldest, lowest vote). */
    val isListSortReversed: Boolean = false,
    /** The app's colors and wallpaper. */
    val theme: AppTheme = AppTheme.Original,
) {
    companion object {
        val DEFAULT_TRENDING_LANGUAGES = listOf("en", "ja")
        const val MAX_CARD_LANGUAGES = 2

        /** The most-used release languages, in display order: the pickers' common and built-in list. */
        val TRENDING_LANGUAGE_CHOICES = listOf(
            "en", "ja", "zh-Hans", "zh-Hant", "ko", "es", "de", "fr", "ru", "pt-br", "it", "pl", "vi", "id", "th",
        )
    }
}

/** Which of a VN's titles comes first. */
enum class TitleLanguage {
    /** VNDB's main title, usually romanized (`title`). */
    Romanized,

    /** The title in its original script (`alttitle`), when there is one. */
    Original,
}

/** A spoiler level stored as a preference (the UI's spoiler filter maps it to its own type). */
enum class SpoilerPreference {
    None,
    Minor,
    Major,
}

/** A look for the whole app: its colors and wallpaper. */
enum class AppTheme {
    /** The Nocturne VN palette on a plain background. */
    Original,

    /** Dark text on a cool off-white, no wallpaper. */
    Light,

    /** Neutral charcoal greys with a periwinkle accent, no wallpaper. */
    Dark,

    /** Retro-terminal neon green on black, no wallpaper. */
    Neon,

    /** Warm parchment and ink with a burnt-sienna accent, no wallpaper. */
    Sepia,

    /** Original's sky blue and amber on true black, no wallpaper. */
    Amoled,

    /** The Nocturne VN palette over the Angelic Serenade wallpaper. */
    AngelicSerenade,

    /** A wine-and-teal palette taken from the Saya no Uta wallpaper. */
    SayaNoUta,

    /** A warm near-black and gold palette over the House in Fata Morgana wallpaper. */
    HouseInFataMorgana,

    /** A midnight-blue and crimson palette over the Tsukihime wallpaper. */
    Tsukihime,

    /** A violet-black, gold and rose-crimson palette over the Meikei no Lupercalia wallpaper. */
    MeikeiNoLupercalia,

    /** An olive-black and Nixie-amber palette over the Steins;Gate wallpaper. */
    SteinsGate,

    /** A deep-sea teal and coral palette over the Ever17 wallpaper. */
    Ever17,

    /** A blush-white and sakura-rose light palette over the Clannad wallpaper. */
    Clannad,

    /** A forest-green and blood-red palette over the Higurashi wallpaper. */
    Higurashi,

    /** A plum, emerald and pink palette over the Doki Doki Literature Club! wallpaper. */
    DokiDokiLiteratureClub,

    /** A gunmetal and hazard-yellow palette over the Muv-Luv Alternative wallpaper. */
    MuvLuvAlternative,

    /** A black, white and shocking-pink palette over the Danganronpa wallpaper. */
    Danganronpa,

    /** A frost-grey and winter-rose palette over the White Album 2 wallpaper. */
    WhiteAlbum2,

    /** A twilight-mauve and apricot palette over the ef - a fairy tale of the two. wallpaper. */
    Ef,

    /** An ash-black and bone-white palette over the Kara no Shoujo wallpaper. */
    KaraNoShoujo,

    /** An ebony, blue-truth and red-truth palette over the Umineko wallpaper. */
    Umineko,

    /** A steel, royal-blue and gold palette over the Fate/stay night wallpaper. */
    FateStayNight,
}
