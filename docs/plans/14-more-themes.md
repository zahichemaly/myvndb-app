# Plan 14 — More themes (9 → 23)

**Status: Done. All six steps are in, plus four changes made afterwards: the theme card's check mark moved so long names don't break mid-word (§6), Tsukihime's accent was recolored to pass contrast (§9), `ThemeContrastTest` has no exceptions, and Summer Pockets and FLOWERS were removed on request.**

Goal: add 14 themes to the Theme screen, taking it from 9 to 23: 2 plain themes (no wallpaper) and 12 visual novel themes. Every new theme has its own color scheme. The wallpapers come from VNDB; a small Python tool downloads them, crops them to the phone shape, scales them to 1080×2400 and saves them as WebP in `drawable-nodpi`. There are no new screens and no behaviour change: the theme system from [plan 10](10-themes.md) already handles any number of themes.

Rules: `.claude/skills/*` (architecture, structure, conventions, best-practices). Builds on plan 10, "Adding a theme".

> **Count:** the request was 2 plain + 13 visual novel themes, which makes 24 with the 9 themes there were; Fate/stay night was added as a 25th. After the work was done, Summer Pockets and FLOWERS were removed on request (scheme files, wallpapers, strings, enum entries and manifest entries), so the app now has 23 themes. This plan describes the 23-theme result.

---

## 1. Themes

| # | `AppTheme` | Display name | String | Scheme file | Mode | Wallpaper |
|---|---|---|---|---|---|---|
| 1 | `Sepia` | Sepia | `theme_sepia` | `SepiaScheme.kt` | light | none |
| 2 | `Amoled` | AMOLED | `theme_amoled` | `AmoledScheme.kt` | dark | none |
| 3 | `SteinsGate` | Steins;Gate | `theme_steins_gate` | `SteinsGateScheme.kt` | dark | `wallpaper_steins_gate.webp` |
| 4 | `Ever17` | Ever17 | `theme_ever17` | `Ever17Scheme.kt` | dark | `wallpaper_ever17.webp` |
| 5 | `Clannad` | Clannad | `theme_clannad` | `ClannadScheme.kt` | light | `wallpaper_clannad.webp` |
| 6 | `Higurashi` | Higurashi | `theme_higurashi` | `HigurashiScheme.kt` | dark | `wallpaper_higurashi.webp` |
| 7 | `DokiDokiLiteratureClub` | Doki Doki Literature Club! | `theme_doki_doki_literature_club` | `DokiDokiLiteratureClubScheme.kt` | dark | `wallpaper_doki_doki_literature_club.webp` |
| 8 | `MuvLuvAlternative` | Muv-Luv Alternative | `theme_muv_luv_alternative` | `MuvLuvAlternativeScheme.kt` | dark | `wallpaper_muv_luv_alternative.webp` |
| 9 | `Danganronpa` | Danganronpa | `theme_danganronpa` | `DanganronpaScheme.kt` | dark | `wallpaper_danganronpa.webp` |
| 10 | `WhiteAlbum2` | White Album 2 | `theme_white_album_2` | `WhiteAlbum2Scheme.kt` | dark | `wallpaper_white_album_2.webp` |
| 11 | `Ef` | ef - a fairy tale of the two. | `theme_ef` | `EfScheme.kt` | dark | `wallpaper_ef.webp` |
| 12 | `KaraNoShoujo` | Kara no Shoujo | `theme_kara_no_shoujo` | `KaraNoShoujoScheme.kt` | dark | `wallpaper_kara_no_shoujo.webp` |
| 13 | `Umineko` | Umineko | `theme_umineko` | `UminekoScheme.kt` | dark | `wallpaper_umineko.webp` |
| 14 | `FateStayNight` | Fate/stay night | `theme_fate_stay_night` | `FateStayNightScheme.kt` | dark | `wallpaper_fate_stay_night.webp` |

**List order.** The Theme screen shows `AppTheme.entries` in declaration order: Original, Light, Dark, Neon, **Sepia, AMOLED**, Angelic Serenade, Saya no Uta, House in Fata Morgana, Tsukihime, Meikei no Lupercalia, then rows 3–14 in table order. The plain themes stay together right after Original. The saved choice is the enum *name*, so the new order does not affect saved settings.

**Light themes.** Sepia and Clannad are light. `AppRoute` already gives a theme dark system-bar icons when its background luminance is above 0.5, so they need nothing extra.

---

## 2. Color schemes

14 new files in `common/ui/theme/schemes/`, one per theme. No new theme reuses an existing scheme.

**File shape.** Dark schemes follow the layout of `TsukihimeScheme.kt` (`darkColorScheme`); light schemes follow `LightScheme.kt` (`lightColorScheme`). Each file has, in order: a header comment with the theme's idea and anchor colors, the Material 3 roles as `private val`s, the brand and semantic colors, the release-pill colors, the `ColorScheme`, the `VndbColors`, and a KDoc'd `internal val <Name>Scheme = VndbScheme(...)`.

**Values.** Every hex value is in [Appendix A](#appendix-a--color-tokens). This is how the Appendix A keys map to code (the same mapping the current schemes use):

| Appendix A key | Code |
|---|---|
| M3 rows | the same-named argument of `darkColorScheme(...)` / `lightColorScheme(...)` |
| `surfaceCard`, `surfaceElevated`, `surfaceInset`, `borderSubtle` | the same-named `VndbColors` tokens. Also `chipInactiveContainer = surfaceCard` and `chipNeutral = TintedColors(surfaceElevated, onSurfaceVariant, borderSubtle)` |
| `surfaceGlass` | `surfaceGlass = <color>.copy(alpha = TokenAlpha.GLASS)`: the inset color on dark schemes, the card color on light ones |
| `hairline` | `glassHairline = Color.White` (dark) or `Color.Black` (light) `.copy(alpha = TokenAlpha.HAIRLINE)` |
| `accent` | `accent`; `borderFocused = accent.copy(alpha = TokenAlpha.FOCUS_BORDER)`; `focusRing = accent.copy(alpha = TokenAlpha.FOCUS_RING)` |
| `accentPressed`, `onAccent`, `score`, `placeholder` | the same-named tokens |
| `accentDeep` | `accentDeep` and `chipActiveContainer` |
| `chipActiveContent` | `chipActiveContent` |
| `glow` | `glow = <color>.copy(alpha = TokenAlpha.GLOW)` |
| (none) | `coverScrim = Surface.copy(alpha = TokenAlpha.GLASS)` |
| `playing` | `statusPlaying = tinted(playing)` |
| `finished` | `statusFinished = tinted(finished)`, `spoilerNone = tinted(finished)` |
| `stalled` | `statusStalled = tinted(stalled)`, `spoilerMinor = tinted(stalled)` |
| `dropped` | `statusDropped = tintedStrong(dropped)`, `spoilerMajor = tintedStrong(dropped)` |
| `nsfw` | `tagNsfw = tinted(nsfw)` |
| `wishlist` | `labelWishlist = TintedColors(Color.Transparent, wishlist, wishlist)` |
| (none) | `labelBlacklist = TintedColors(Color.Transparent, OnSurfaceVariant, placeholder)`, `labelOwn = TintedColors(Color.Transparent, OnSurfaceVariant, borderSubtle)` |
| `releaseNew`, `releaseTranslation`, `releasePatch` | `releaseNew/Translation/Patch = tinted(...)` |

**How the values were made.** This is here so the values can be reviewed, and so future themes can follow the same rules. Each scheme starts from six anchors (background, card, text, accent, secondary, score) plus its semantic colors. The rest is derived. "x% toward y" means a linear RGB mix.

- *Dark:* lowest container = background 35% toward black; low container = halfway between background and card; container = card; high / highest / bright = card 5% / 10% / 16% toward the text color (surfaceVariant = highest). Secondary text (`onSurfaceVariant`) = text 25% toward background; `outline` = 45%. `primary` = accent 45% toward white and `primaryContainer` = accent. `secondary` = secondary 35% toward white. Tertiary comes from the score color. `surfaceElevated` / `borderSubtle` = card 7% / 9% toward text. `accentPressed` = accent 22% toward black, `accentDeep` = accent 72% toward background, `chipActiveContent` = accent 35% toward white.
- *Light:* low / container / high / highest = background 2.5% / 5% / 8% / 11% toward text. `primary` = accent and `primaryContainer` = accent 82% toward white. `surfaceElevated` (10%) is darker than `surfaceInset` (4%), as in `LightScheme`, so the selected tab pill stays visible on the inset nav bar. `accentDeep` = accent 86% toward white, `chipActiveContent` = `accentPressed`.
- *Semantic colors* (status, spoiler, NSFW, wishlist, release pills) start from Nocturne's set on dark themes and Light's darker set on light themes. They move off a hue when it would clash with the accent or score; each scheme's note in Appendix A says what moved.
- *Error roles* are the Material 3 baseline: dark `#FFB4AB` / `#690005` / `#93000A` / `#FFDAD6`, light `#BA1A1A` / `#FFFFFF` / `#FFDAD6` / `#410002`.

**Contrast.** Every pair below is at least 4.5:1 in all 14 schemes; Appendix A gives each scheme's lowest value.
- text and secondary text on the card;
- accent on the card and on the background;
- `onAccent` on the accent;
- `primary` on the surface and `onPrimary` on `primary`;
- the on-colors on the secondary and tertiary containers;
- `secondary` on the surface;
- active chip text on its container;
- the score on the card;
- every status, label and release color on its own 13% tint over the card (the pill look).

---

## 3. Wallpapers

### 3.1 Sources

Every source is a VNDB cover or release image rated safe (sexual < 0.5, violence < 0.5). The current wallpapers are clean art with no logos, so each source has a **region**: the part of the image with no logo, console bar or rating mark. The tool fits the largest 9:20 window it can inside that region, centred on a **focus** point. The crop and scale columns are the tool's final output; the exact fractions are in [Appendix B](#appendix-b--wallpapersjson).

| Theme | VNDB | Source | Size | Crop (W×H+X+Y) | Scale | Art |
|---|---|---|---|---|---|---|
| Steins;Gate | [v2002](https://vndb.org/v2002) | [85365.jpg](https://t.vndb.org/cv/65/85365.jpg) | 1620×2160 | 699×1555+687+0 | ×1.55 | Digital cover: Okabe, Kurisu, Mayuri. Stops above the logo |
| Ever17 | [v17](https://vndb.org/v17) | [85892.jpg](https://t.vndb.org/cv/92/85892.jpg) | 1518×2164 | 647×1438+436+249 | ×1.67 | PS2 cover: Tsugumi, Coco and You underwater. Skips the PS2 bar and the logo |
| Clannad | [v4](https://vndb.org/v4) | [95864.jpg](https://t.vndb.org/cv/64/95864.jpg) | 1200×1995 | 448×997+376+200 | ×2.41 | PC cover: the heroines, Nagisa in the middle. Stops above the logo |
| Higurashi | [v67](https://vndb.org/v67) | [83484.jpg](https://t.vndb.org/cv/84/83484.jpg) | 1517×2160 | 403×896+481+227 | ×2.68 | PS2 cover: Rena under a red moon. Skips the PS2 bar, stops above the logo |
| Doki Doki Literature Club! | [v21905](https://vndb.org/v21905) | [116477.jpg](https://t.vndb.org/cv/77/116477.jpg) | 2729×4437 | 1067×2373+1431+289 | ×1.01 | Switch cover (Plus!): Monika. Skips the Switch bar, stops above the logo |
| Muv-Luv Alternative | [v92](https://vndb.org/v92) | [83217.jpg](https://t.vndb.org/cv/17/83217.jpg) | 1536×2160 | 614×1364+768+398 | ×1.76 | Xbox 360 cover: Meiya and the Takemikazuchi. Skips the bar, the logo (left) and the rating marks |
| Danganronpa | [v7014](https://vndb.org/v7014) | [75815.jpg](https://t.vndb.org/cv/15/75815.jpg) | 600×900 | 405×900+195+0 | ×2.67 | **Placeholder.** Pink-and-black lettering with Monokuma: the art *is* the logo, so the crop keeps part of it (see §9) |
| White Album 2 | [v7771](https://vndb.org/v7771) | [97306.jpg](https://t.vndb.org/cv/06/97306.jpg) | 1915×2715 | 684×1520+118+0 | ×1.58 | Extended Edition cover: Setsuna with her pink scarf in the snow. Stops above the logo |
| ef - a fairy tale of the two. | [v88](https://vndb.org/v88) | [120033.jpg](https://t.vndb.org/cv/33/120033.jpg) | 1955×2824 | 648×1440+654+113 | ×1.67 | English package: the heroines under a blue sky. Skips the frame, stops above the logo |
| Kara no Shoujo | [v810](https://vndb.org/v810) | [120088.jpg](https://t.vndb.org/cv/88/120088.jpg) | 1999×2854 | 834×1854+682+372 | ×1.29 | English package: Toko in an eggshell, black and white. Skips both logos and the 18+ mark |
| Umineko | [v24](https://vndb.org/v24) | [94328.jpg](https://t.vndb.org/cv/28/94328.jpg) | 6090×7646 | 1290×2867+3679+956 | ×0.84 | PS4 cover (Saku): Beatrice and golden butterflies. Skips the PS4 bar, stops above the logo |
| Fate/stay night | [v11](https://vndb.org/v11) | [89274.jpg](https://t.vndb.org/cv/74/89274.jpg) | 1945×2364 | 701×1559+622+284 | ×1.54 | Fate/stay night + hollow ataraxia package: Saber between Rin and Sakura. Skips both logos and the title |

A scale above ×1 means the image is enlarged. Up to about ×2 it is not noticeable at the 20% wallpaper opacity. Clannad and Higurashi go above ×2 and look a little soft (see §9). Every crop was checked with `--preview` (§3.3), including strips along its edges, and none shows a logo, console bar or rating mark (except the Danganronpa placeholder).

### 3.2 The tool

```text
tools/wallpapers/
├── fetch_wallpapers.py    # Python 3.9+, standard library only; calls ImageMagick and cwebp
└── wallpapers.json        # the manifest (Appendix B)
```

- **Why Python, and why `tools/`:** Python was the requested language for this asset tooling. The script runs by hand and never runs in the Gradle build, CI or the app. The tech-stack skill's "Kotlin only" rule covers app code, tests and build scripts; §7 adds a one-line exception for `tools/`.
- **Requirements:** ImageMagick 7 (`magick`) and `cwebp` (`brew install imagemagick webp`); both are already installed on this Mac. No pip packages, so no virtualenv is needed.
- **Manifest:** top-level `output_size` (`[1080, 2400]`) and `webp_quality` (`80`, as in plan 10), plus one entry per wallpaper:
  - `name`: the resource suffix, which gives `wallpaper_<name>.webp`;
  - `vndb`: the VN id, for reference;
  - `source`: the image URL;
  - `region`: `[x0, y0, x1, y1]` as fractions of the source, the clean part of the image;
  - `focus`: `[x, y]` as fractions, where to centre the crop;
  - `note`: what the art is and what the region leaves out.

**For each entry, the script:**
1. **Downloads** `source` into a new temporary directory (`tempfile.mkdtemp()`). Only `https://` URLs on hosts in `ALLOWED_HOSTS = {"t.vndb.org"}` are accepted; to use official art from another site, add that host to the set. It uses a 60 s timeout, a 50 MB size cap and a generic `User-Agent: MyVNDB-wallpaper-tool`.
2. **Checks the file type** from its first bytes (JPEG `FF D8 FF`, PNG `89 50 4E 47`, WebP `RIFF....WEBP`) and rejects anything else. The file is passed to ImageMagick with an explicit format prefix (`jpeg:`, `png:` or `webp:`), so a mislabeled download is never read as another format.
3. **Reads the size** with `magick identify -format "%w %h" <fmt>:<file>[0]`.
4. **Computes the crop:** the largest window with the output's aspect ratio (1080:2400 = 0.45) that fits inside `region`, centred on `focus`, then moved back inside `region` if it sticks out. Box values are rounded to whole pixels.
5. **Crops and scales:** `magick <fmt>:<src> -crop WxH+X+Y +repage -colorspace sRGB -filter Lanczos -resize 1080x2400! -strip png:<tmp>/out.png`
6. **Encodes:** `cwebp -q 80 -m 6 -metadata none <tmp>/out.png -o <tmp>/out.webp`, then checks the result is exactly 1080×2400.
7. **Moves** the WebP to `app/src/main/res/drawable-nodpi/wallpaper_<name>.webp` with `os.replace`. If the file already exists, the script refuses unless `--force` is given, so the five current wallpapers can never be overwritten by accident.
8. **Reports** the source size, crop box, scale factor (with a warning above ×2.0) and output size in KB. The temporary directory is removed in a `finally` block, even when a step fails.

The script stops with a clear message if `magick` or `cwebp` is missing, if the manifest is invalid (unknown keys, region not inside `[0, 1]`, `x0 >= x1`), or if a download fails. It never runs anything from the download directory.

**Commands:**

```bash
python3 -I tools/wallpapers/fetch_wallpapers.py --preview
```
Crops every entry into `build/wallpapers/<name>.webp` and builds a contact sheet at `build/wallpapers/preview.png`, 7 crops per row, each labeled with its name. ImageMagick has no default font on macOS, so the labels use the first system font found (Arial); with no font, the sheet is built without labels, in manifest order. It changes nothing in `res/`. The root `/build` folder is already git-ignored.

```bash
python3 -I tools/wallpapers/fetch_wallpapers.py
```
Writes every wallpaper in the manifest into `drawable-nodpi`.

```bash
python3 -I tools/wallpapers/fetch_wallpapers.py --only umineko --force
```
Redoes one wallpaper after its region or focus was changed. `--only` can be repeated.

### 3.3 Review loop

1. Run `--preview` and open `build/wallpapers/preview.png`. Check each crop: no logo, console bar or rating mark; faces are not cut off; it still reads behind a 70%-opaque card.
2. Adjust `region` / `focus` in `wallpapers.json` and repeat.
3. Run the script without `--preview` to write the 12 files. They came out at 85–249 KB each, about 1.9 MB in total (the current five are 76–127 KB each).

Umineko's source (6090×7646) is by far the largest download; the others are under 3000 px on the long side.

---

## 4. Code changes

**Schemes** (`common/ui/theme/schemes/`): the 14 files from §1, with values from Appendix A.

**`common/ui/theme/Themes.kt`:** 14 specs, each with a one-line KDoc like the existing ones.
```kotlin
/** Warm paper and ink with a burnt-sienna accent, on a plain background. */
val SepiaThemeSpec = SepiaScheme.toSpec(
    name = UiText.StringResource(StringResource.theme_sepia),
)

/** Ebony, blue truth and red truth over the Umineko wallpaper. */
val UminekoThemeSpec = UminekoScheme.toSpec(
    name = UiText.StringResource(StringResource.theme_umineko),
    wallpaper = wallpaper(DrawableResource.wallpaper_umineko),
)
```

**`common/domain/model/AppSettings.kt`:** 14 `AppTheme` entries with KDoc, in the order of §1 (Sepia and Amoled right after Neon, the visual novel themes after `MeikeiNoLupercalia`).

**`common/ui/mapper/AppThemeUIModelMapper.kt`:** 14 branches in `toSpec()`. The `when` is exhaustive, so the build fails until all are there. `toLabel()` needs no change. With 23 branches, `toSpec()` goes over Detekt's `CyclomaticComplexMethod` limit (14), so it carries a scoped `@Suppress` with a same-line reason, like the intent dispatchers in the ViewModels. A map lookup would pass Detekt but lose the exhaustive check.

**`res/values/strings.xml`:** the 14 `theme_*` strings from §1, next to the existing ones.

**`res/drawable-nodpi/`:** the 12 WebPs written by the tool.

**`tools/wallpapers/`:** the script and the manifest.

Nothing else changes. The Theme screen lists `AppTheme.entries`, the settings DataStore saves the name, and the root reads the spec.

---

## 5. Tests

- **`AppThemeUIModelMapperTest`:**
  - add 14 assertions to `every theme maps to its own spec`;
  - in both plain-theme tests, the plain list becomes Original, Light, Dark, Neon, Sepia, Amoled, and "follow Original" uses `take(6)`;
  - `every theme has a distinct name and a distinct look` covers the new themes with no change.
- **New `ThemeContrastTest`** (`app/src/test/.../common/ui/theme/`): for every `AppTheme`, checks that `onSurface` on `surfaceCard`, `accent` on `surfaceCard` and `onAccent` on `accent` are at least 4.5:1, computed with `Color.luminance()`. This guards future themes too. If one of the current nine fails, fix its color in the same change, or list it as a named exception with the reason. Never lower the threshold.
  - *Result:* all 14 new themes pass. One existing theme, Tsukihime, failed accent on card (3.54:1). It was first listed as a named exception, then recolored (§9), so the test now runs on every theme with no exceptions.
- **`SettingsLocalDataSourceTest`:** one round-trip with a new entry (`AppTheme.Umineko`), to show new names save and load.
- `ThemeViewModelTest` and `AppViewModelTest` need no change; they read `AppTheme.entries`.
- Run `./gradlew testDevelopmentDebugUnitTest detekt`.

---

## 6. On-device checks

Run `./gradlew installDevelopmentDebug`. For each new theme, open Discover, Browse, a VN detail page, Profile, Settings, the Theme screen, one dialog and the filter bottom sheet. Also check:
- the light themes get dark status-bar and nav-bar icons;
- wallpapers on a phone: faces visible, no logo showing (except the Danganronpa placeholder). On a tablet or in landscape, `ContentScale.Crop` shows the middle band, as it already does for the current five;
- long names on the Theme screen ("Doki Doki Literature Club!", "ef - a fairy tale of the two.") wrap without breaking the grid. If they look bad, limit the name in `ThemePreviewView` to 2 lines with an ellipsis;
- a cold start with a new visual novel theme saved shows no frame of Original.

**Result** (phone emulator, 1080×2400):
- all the new themes apply at once and repaint the Theme screen with their wallpaper;
- Discover was checked under Clannad, Sepia, AMOLED and Umineko;
- the light themes get dark status-bar icons;
- a cold start with Umineko saved goes splash → window color → Umineko, with no frame of Original.
- **Fixed during the pass:** a selected card put its check mark next to the name, which left too little width, so long names broke mid-word ("Steins;Gat / e", "Alternativ / e", "Danganro / npa"). The check now sits at the end of the swatch row in `ThemePreviewView`, and that row is always check-height, so the name gets the full card width and selecting a card does not shift it against its row. Names now wrap only at spaces.

---

## 7. Docs

- **Plan 10, "Adding a theme":** step 2 now points to `tools/wallpapers` (add a manifest entry, run `--preview`, then write). The test sentence's list of plain themes gains Sepia and AMOLED.
- **`tech-stack` skill, "Kotlin only":** add "`tools/` may hold dev-only Python scripts that never run in the Gradle build (for example the wallpaper tool)."
- **`structure` skill:** one line saying the repo-root `tools/` folder holds dev-only scripts and is outside the package tree.
- **This plan:** update the status line when done.

---

## 8. Order of work

1. **Wallpapers:** write the tool and manifest, run `--preview`, review, then write the 12 files. This step needs network access.
2. **Schemes:** the 14 scheme files from Appendix A.
3. **Wiring:** specs, enum entries, mapper branches and strings.
4. **Tests:** mapper test updates, `ThemeContrastTest`, the settings round-trip.
5. **Checks:** unit tests, Detekt, install, the on-device pass from §6.
6. **Docs:** §7.

Steps 1 and 2 don't depend on each other. Step 3 needs both, because the specs reference the schemes and the drawables.

---

## 9. Open points

- **Danganronpa wallpaper:** VNDB has no clean Trigger Happy Havoc art; every cover is mostly logo. The manifest uses the digital cover as a placeholder, which keeps part of the lettering. Before release, replace it with official art (a Monokuma key visual), add that site's host to `ALLOWED_HOSTS`, and update `source`, `region` and `focus`.
- **Soft wallpapers:** Higurashi (×2.68) and Clannad (×2.41) are enlarged more than ×2. They are acceptable at 20% opacity; swap in official high-res art if you find it.
- **Tsukihime accent (existing theme), resolved:** the old crimson `#D92B4B` was 3.54:1 on its navy cards `#141C33`, and the accent is drawn as text in many places (links like "See all", tappable tag chips, section titles). It is now `#EC5570` (4.9:1 on cards) with the dark background `#0A0E1A` as the text on it (5.6:1) instead of white. The pressed state went to `#D44C65` (dark text 4.6:1), and "dropped" / major spoilers to `#FF7D95`, so they stay a lighter crimson than the accent; the old `#F0617F` was also just under 4.5:1 on its pill. `InversePrimary`, `PrimaryContainer` and `OnPrimaryContainer` follow. Checked on the emulator: Discover's button, pill and links read clearly and the theme still looks crimson.
- **Copyright:** all wallpapers are the publishers' official art, the same situation as the current five.
- **Out of scope** (as in plan 10): per-theme wallpaper opacity, user-supplied wallpapers, dynamic color.

---

## Appendix A — Color tokens

Every value for the 14 schemes, made with the rules in §2 and contrast-checked. `@ n%` means `.copy(alpha = …)` with the named `TokenAlpha` constant.

### SepiaScheme (light)

Warm paper and ink. Burnt-sienna accent, olive secondary, old-gold score. Release patch is plum, since orange would sit next to the sienna accent.

| Group | Values |
|---|---|
| M3 · Surfaces | background `#F4ECDD` · surface `#F4ECDD` · surfaceDim `#E0D7C9` · surfaceBright `#FBF6EC` · surfaceContainerLowest `#FBF6EC` · surfaceContainerLow `#EFE7D8` · surfaceContainer `#EBE2D4` · surfaceContainerHigh `#E5DDCE` · surfaceContainerHighest `#E0D7C9` · surfaceVariant `#E4DCCD` |
| M3 · Content | onSurface `#3A2E25` · onBackground `#3A2E25` · onSurfaceVariant `#72675C` · inverseSurface `#493D34` · inverseOnSurface `#FAF6EE` · outline `#978D81` · outlineVariant `#D4CCBE` · scrim `#000000` |
| M3 · Primary | primary `#9C4A22` · onPrimary `#FFFFFF` · primaryContainer `#EDDED7` · onPrimaryContainer `#46210F` · inversePrimary `#CEA490` · surfaceTint `#9C4A22` |
| M3 · Secondary | secondary `#5E6B2E` · onSecondary `#FFFFFF` · secondaryContainer `#DFE1D5` · onSecondaryContainer `#2A3015` |
| M3 · Tertiary | tertiary `#8A6510` · onTertiary `#FFFFFF` · tertiaryContainer `#E8E0CF` · onTertiaryContainer `#3E2D07` |
| M3 · Error | error `#BA1A1A` · onError `#FFFFFF` · errorContainer `#FFDAD6` · onErrorContainer `#410002` |
| Vndb · Surfaces | surfaceCard `#FBF6EC` · surfaceElevated `#E1D9CB` · surfaceInset `#EDE4D6` · borderSubtle `#E1D9CB` · surfaceGlass `#FBF6EC @ 85%` · hairline `Black @ 8%` |
| Vndb · Accent | accent `#9C4A22` · accentPressed `#7D3B1B` · onAccent `#FFFFFF` · accentDeep `#F1E6E0` · chipActiveContent `#7D3B1B` · glow `#9C4A22 @ 15%` · score `#8A6510` · placeholder `#978D81` |
| Vndb · Status / labels | playing `#2A527A` · finished `#045C42` · stalled `#74550C` · dropped `#A01818` · nsfw `#9F1239` · wishlist `#5B21B6` |
| Vndb · Release pills | releaseNew `#0B5A55` · releaseTranslation `#5B21B6` · releasePatch `#9D174D` |

Lowest checked contrast: 4.9:1 (score/card).

### AmoledScheme (dark)

Original's sky blue and amber on true black. Semantic colors are Nocturne's.

| Group | Values |
|---|---|
| M3 · Surfaces | background `#000000` · surface `#000000` · surfaceDim `#000000` · surfaceBright `#333438` · surfaceContainerLowest `#000000` · surfaceContainerLow `#070709` · surfaceContainer `#0F1013` · surfaceContainerHigh `#1A1B1E` · surfaceContainerHighest `#26272A` · surfaceVariant `#26272A` |
| M3 · Content | onSurface `#F2F4F8` · onBackground `#F2F4F8` · onSurfaceVariant `#B6B7BA` · inverseSurface `#F2F4F8` · inverseOnSurface `#18191C` · outline `#858688` · outlineVariant `#2D2E31` · scrim `#000000` |
| M3 · Primary | primary `#92DBFB` · onPrimary `#11394A` · primaryContainer `#38BDF8` · onPrimaryContainer `#000000` · inversePrimary `#2D97C6` · surfaceTint `#74D1FA` |
| M3 · Secondary | secondary `#FFD297` · onSecondary `#47341B` · secondaryContainer `#805C30` · onSecondaryContainer `#FFF1DF` |
| M3 · Tertiary | tertiary `#F8B648` · onTertiary `#452C03` · tertiaryContainer `#674205` · onTertiaryContainer `#FEF0DA` |
| M3 · Error | error `#FFB4AB` · onError `#690005` · errorContainer `#93000A` · onErrorContainer `#FFDAD6` |
| Vndb · Surfaces | surfaceCard `#0F1013` · surfaceElevated `#1F2023` · surfaceInset `#050607` · borderSubtle `#232528` · surfaceGlass `#050607 @ 85%` · hairline `White @ 8%` |
| Vndb · Accent | accent `#38BDF8` · accentPressed `#2C93C1` · onAccent `#000000` · accentDeep `#103545` · chipActiveContent `#7ED4FA` · glow `#2C93C1 @ 15%` · score `#F59E0B` · placeholder `#797A7C` |
| Vndb · Status / labels | playing `#38BDF8` · finished `#10B981` · stalled `#F59E0B` · dropped `#F05252` · nsfw `#F43F5E` · wishlist `#A78BFA` |
| Vndb · Release pills | releaseNew `#2DD4BF` · releaseTranslation `#A78BFA` · releasePatch `#FB923C` |

Lowest checked contrast: 4.6:1 (nsfw).

### SteinsGateScheme (dark)

Olive-black lab, Nixie-tube amber accent, Kurisu auburn secondary, IBN-gold score. Playing is a cool steel blue and the patch pill is pink, to stay clear of the orange accent.

| Group | Values |
|---|---|
| M3 · Surfaces | background `#12110D` · surface `#12110D` · surfaceDim `#12110D` · surfaceBright `#403D35` · surfaceContainerLowest `#0C0B08` · surfaceContainerLow `#181611` · surfaceContainer `#1F1D16` · surfaceContainerHigh `#292720` · surfaceContainerHighest `#343129` · surfaceVariant `#343129` |
| M3 · Content | onSurface `#EDE7D6` · onBackground `#EDE7D6` · onSurfaceVariant `#B6B2A4` · inverseSurface `#EDE7D6` · inverseOnSurface `#27251E` · outline `#8A877C` · outlineVariant `#3A372F` · scrim `#000000` |
| M3 · Primary | primary `#FFBF84` · onPrimary `#4D2909` · primaryContainer `#FF8A1F` · onPrimaryContainer `#0B0A08` · inversePrimary `#CC6E19` · surfaceTint `#FFAD62` |
| M3 · Secondary | secondary `#E09B8B` · onSecondary `#3A1C15` · secondaryContainer `#683226` · onSecondaryContainer `#F6E0DB` |
| M3 · Tertiary | tertiary `#EED38F` · onTertiary `#41371E` · tertiaryContainer `#62522D` · onTertiaryContainer `#FCF6E9` |
| M3 · Error | error `#FFB4AB` · onError `#690005` · errorContainer `#93000A` · onErrorContainer `#FFDAD6` |
| Vndb · Surfaces | surfaceCard `#1F1D16` · surfaceElevated `#2D2B23` · surfaceInset `#171510` · borderSubtle `#322F27` · surfaceGlass `#171510 @ 85%` · hairline `White @ 8%` |
| Vndb · Accent | accent `#FF8A1F` · accentPressed `#C76C18` · onAccent `#0B0A08` · accentDeep `#543312` · chipActiveContent `#FFB36D` · glow `#C76C18 @ 15%` · score `#E9C46A` · placeholder `#807C72` |
| Vndb · Status / labels | playing `#7FB3E8` · finished `#5FD3A5` · stalled `#E9C46A` · dropped `#F0617F` · nsfw `#FF8FB1` · wishlist `#B5A2F2` |
| Vndb · Release pills | releaseNew `#2DD4BF` · releaseTranslation `#A78BFA` · releasePatch `#F472B6` |

Lowest checked contrast: 4.5:1 (dropped).

### Ever17Scheme (dark)

Abyssal LeMU teal, coral accent, aqua secondary. Release-new is mint, since teal is the surface.

| Group | Values |
|---|---|
| M3 · Surfaces | background `#04141A` · surface `#04141A` · surfaceDim `#04141A` · surfaceBright `#2D444B` · surfaceContainerLowest `#030D11` · surfaceContainerLow `#071B22` · surfaceContainer `#0B232B` · surfaceContainerHigh `#162D35` · surfaceContainerHighest `#20383F` · surfaceVariant `#20383F` |
| M3 · Content | onSurface `#DDF3F6` · onBackground `#DDF3F6` · onSurfaceVariant `#A7BBBF` · inverseSurface `#DDF3F6` · inverseOnSurface `#132B33` · outline `#7B8F93` · outlineVariant `#263E45` · scrim `#000000` |
| M3 · Primary | primary `#FFB6AB` · onPrimary `#4D251F` · primaryContainer `#FF7A66` · onPrimaryContainer `#020C10` · inversePrimary `#CC6252` · surfaceTint `#FFA294` |
| M3 · Secondary | secondary `#8DE6EB` · onSecondary `#163C3F` · secondaryContainer `#286C70` · onSecondaryContainer `#DCF7F9` |
| M3 · Tertiary | tertiary `#F5D69A` · onTertiary `#443822` · tertiaryContainer `#665433` · onTertiaryContainer `#FDF7EB` |
| M3 · Error | error `#FFB4AB` · onError `#690005` · errorContainer `#93000A` · onErrorContainer `#FFDAD6` |
| Vndb · Surfaces | surfaceCard `#0B232B` · surfaceElevated `#1A3239` · surfaceInset `#061920` · borderSubtle `#1E363D` · surfaceGlass `#061920 @ 85%` · hairline `White @ 8%` |
| Vndb · Accent | accent `#FF7A66` · accentPressed `#C75F50` · onAccent `#020C10` · accentDeep `#4A312F` · chipActiveContent `#FFA99C` · glow `#C75F50 @ 15%` · score `#F2C879` · placeholder `#708488` |
| Vndb · Status / labels | playing `#4FD8E0` · finished `#5FD3A5` · stalled `#F0B84A` · dropped `#FF8FA3` · nsfw `#F9A8D4` · wishlist `#B5A2F2` |
| Vndb · Release pills | releaseNew `#86EFAC` · releaseTranslation `#A78BFA` · releasePatch `#FDBA74` |

Lowest checked contrast: 4.9:1 (releaseTranslation).

### ClannadScheme (light)

Blush white, sakura-rose accent, dango-amber secondary and score.

| Group | Values |
|---|---|
| M3 · Surfaces | background `#FFF5F7` · surface `#FFF5F7` · surfaceDim `#E8DDE0` · surfaceBright `#FFFFFF` · surfaceContainerLowest `#FFFFFF` · surfaceContainerLow `#FAF0F2` · surfaceContainer `#F5EAED` · surfaceContainerHigh `#EEE4E6` · surfaceContainerHighest `#E8DDE0` · surfaceVariant `#EDE3E5` |
| M3 · Content | onSurface `#2E1F26` · onBackground `#2E1F26` · onSurfaceVariant `#6D5F65` · inverseSurface `#3F3037` · inverseOnSurface `#FFFAFB` · outline `#968A8E` · outlineVariant `#DBD1D3` · scrim `#000000` |
| M3 · Primary | primary `#C2416B` · onPrimary `#FFFFFF` · primaryContainer `#F4DDE4` · onPrimaryContainer `#571D30` · inversePrimary `#E0A0B5` · surfaceTint `#C2416B` |
| M3 · Secondary | secondary `#A35F14` · onSecondary `#FFFFFF` · secondaryContainer `#EDDFD0` · onSecondaryContainer `#492B09` |
| M3 · Tertiary | tertiary `#A35F14` · onTertiary `#FFFFFF` · tertiaryContainer `#EDDFD0` · onTertiaryContainer `#492B09` |
| M3 · Error | error `#BA1A1A` · onError `#FFFFFF` · errorContainer `#FFDAD6` · onErrorContainer `#410002` |
| Vndb · Surfaces | surfaceCard `#FFFFFF` · surfaceElevated `#EAE0E2` · surfaceInset `#F7ECEF` · borderSubtle `#EAE0E2` · surfaceGlass `#FFFFFF @ 85%` · hairline `Black @ 8%` |
| Vndb · Accent | accent `#C2416B` · accentPressed `#9B3456` · onAccent `#FFFFFF` · accentDeep `#F6E4EA` · chipActiveContent `#9B3456` · glow `#C2416B @ 15%` · score `#A35F14` · placeholder `#968A8E` |
| Vndb · Status / labels | playing `#2F5F9E` · finished `#046C4E` · stalled `#8A4F0F` · dropped `#B91C1C` · nsfw `#9D174D` · wishlist `#5B21B6` |
| Vndb · Release pills | releaseNew `#0E6660` · releaseTranslation `#5B21B6` · releasePatch `#9A3412` |

Lowest checked contrast: 4.6:1 (accent/bg).

### HigurashiScheme (dark)

Hinamizawa forest at dusk, blood-red accent, cicada-amber secondary. Playing is a moss green.

| Group | Values |
|---|---|
| M3 · Surfaces | background `#0B130E` · surface `#0B130E` · surfaceDim `#0B130E` · surfaceBright `#36413A` · surfaceContainerLowest `#070C09` · surfaceContainerLow `#101913` · surfaceContainer `#15211A` · surfaceContainerHigh `#1F2B24` · surfaceContainerHighest `#2A352E` · surfaceVariant `#2A352E` |
| M3 · Content | onSurface `#E3ECE0` · onBackground `#E3ECE0` · onSurfaceVariant `#ADB6AC` · inverseSurface `#E3ECE0` · inverseOnSurface `#1D2922` · outline `#828A82` · outlineVariant `#303B34` · scrim `#000000` |
| M3 · Primary | primary `#F6A4A4` · onPrimary `#471B1B` · primaryContainer `#EE5A5A` · onPrimaryContainer `#070B08` · inversePrimary `#BE4848` · surfaceTint `#F38C8C` |
| M3 · Secondary | secondary `#F0C18D` · onSecondary `#412D16` · secondaryContainer `#745028` · onSecondaryContainer `#FAECDC` |
| M3 · Tertiary | tertiary `#EED28F` · onTertiary `#41371E` · tertiaryContainer `#61522D` · onTertiaryContainer `#FCF6E9` |
| M3 · Error | error `#FFB4AB` · onError `#690005` · errorContainer `#93000A` · onErrorContainer `#FFDAD6` |
| Vndb · Surfaces | surfaceCard `#15211A` · surfaceElevated `#232F28` · surfaceInset `#0E1812` · borderSubtle `#28332C` · surfaceGlass `#0E1812 @ 85%` · hairline `White @ 8%` |
| Vndb · Accent | accent `#EE5A5A` · accentPressed `#BA4646` · onAccent `#070B08` · accentDeep `#4B2723` · chipActiveContent `#F49494` · glow `#BA4646 @ 15%` · score `#E8C36A` · placeholder `#778077` |
| Vndb · Status / labels | playing `#8FBF7A` · finished `#6EE7B7` · stalled `#E8A04F` · dropped `#FF8A8A` · nsfw `#FF8FB1` · wishlist `#B5A2F2` |
| Vndb · Release pills | releaseNew `#2DD4BF` · releaseTranslation `#A78BFA` · releasePatch `#FB923C` |

Lowest checked contrast: 4.9:1 (accent/card).

### DokiDokiLiteratureClubScheme (dark)

"Just Monika": plum surfaces, emerald accent, club-pink secondary. Finished is sky blue, since green is the accent.

| Group | Values |
|---|---|
| M3 · Surfaces | background `#160E15` · surface `#160E15` · surfaceDim `#160E15` · surfaceBright `#473944` · surfaceContainerLowest `#0E090E` · surfaceContainerLow `#1D121B` · surfaceContainer `#251723` · surfaceContainerHigh `#30222D` · surfaceContainerHighest `#3A2C38` · surfaceVariant `#3A2C38` |
| M3 · Content | onSurface `#F8EAF3` · onBackground `#F8EAF3` · onSurfaceVariant `#C0B3BC` · inverseSurface `#F8EAF3` · inverseOnSurface `#2D1F2B` · outline `#92878F` · outlineVariant `#40323E` · scrim `#000000` |
| M3 · Primary | primary `#95DFB8` · onPrimary `#133B26` · primaryContainer `#3FC47E` · onPrimaryContainer `#0D080D` · inversePrimary `#329D65` · surfaceTint `#79D6A5` |
| M3 · Secondary | secondary `#FFB3D6` · onSecondary `#472736` · secondaryContainer `#804560` · onSecondaryContainer `#FFE8F2` |
| M3 · Tertiary | tertiary `#F9E197` · onTertiary `#453C20` · tertiaryContainer `#685A31` · onTertiaryContainer `#FEF9EA` |
| M3 · Error | error `#FFB4AB` · onError `#690005` · errorContainer `#93000A` · onErrorContainer `#FFDAD6` |
| Vndb · Surfaces | surfaceCard `#251723` · surfaceElevated `#342632` · surfaceInset `#1B111A` · borderSubtle `#382A36` · surfaceGlass `#1B111A @ 85%` · hairline `White @ 8%` |
| Vndb · Accent | accent `#3FC47E` · accentPressed `#319962` · onAccent `#0D080D` · accentDeep `#214132` · chipActiveContent `#82D9AB` · glow `#319962 @ 15%` · score `#F7D774` · placeholder `#877C84` |
| Vndb · Status / labels | playing `#FF8AC0` · finished `#7DD3FC` · stalled `#F0B84A` · dropped `#F0617F` · nsfw `#FF8FB1` · wishlist `#B5A2F2` |
| Vndb · Release pills | releaseNew `#2DD4BF` · releaseTranslation `#A78BFA` · releasePatch `#FB923C` |

Lowest checked contrast: 4.6:1 (dropped).

### MuvLuvAlternativeScheme (dark)

Gunmetal hangar, hazard-yellow accent, BETA-red secondary. Score shares the accent yellow; stalled moves to light orange.

| Group | Values |
|---|---|
| M3 · Surfaces | background `#0E1013` · surface `#0E1013` · surfaceDim `#0E1013` · surfaceBright `#3A3D42` · surfaceContainerLowest `#090A0C` · surfaceContainerLow `#13161A` · surfaceContainer `#1A1D22` · surfaceContainerHigh `#24272C` · surfaceContainerHighest `#2E3136` · surfaceVariant `#2E3136` |
| M3 · Content | onSurface `#E3E6EA` · onBackground `#E3E6EA` · onSurfaceVariant `#AEB0B4` · inverseSurface `#E3E6EA` · inverseOnSurface `#22252A` · outline `#838689` · outlineVariant `#34373C` · scrim `#000000` |
| M3 · Primary | primary `#FADF80` · onPrimary `#4A3B07` · primaryContainer `#F5C518` · onPrimaryContainer `#080A0B` · inversePrimary `#C49E13` · surfaceTint `#F8D65D` |
| M3 · Secondary | secondary `#EE988B` · onSecondary `#401B15` · secondaryContainer `#723026` · onSecondaryContainer `#FADFDB` |
| M3 · Tertiary | tertiary `#F8D452` · onTertiary `#453707` · tertiaryContainer `#67530A` · onTertiaryContainer `#FEF6DC` |
| M3 · Error | error `#FFB4AB` · onError `#690005` · errorContainer `#93000A` · onErrorContainer `#FFDAD6` |
| Vndb · Surfaces | surfaceCard `#1A1D22` · surfaceElevated `#282B30` · surfaceInset `#121518` · borderSubtle `#2C2F34` · surfaceGlass `#121518 @ 85%` · hairline `White @ 8%` |
| Vndb · Accent | accent `#F5C518` · accentPressed `#BF9A13` · onAccent `#080A0B` · accentDeep `#4F4314` · chipActiveContent `#F8D969` · glow `#BF9A13 @ 15%` · score `#F5C518` · placeholder `#787B7E` |
| Vndb · Status / labels | playing `#7FA7D9` · finished `#5FD3A5` · stalled `#FDBA74` · dropped `#F0617F` · nsfw `#FF8FB1` · wishlist `#B5A2F2` |
| Vndb · Release pills | releaseNew `#2DD4BF` · releaseTranslation `#A78BFA` · releasePatch `#F472B6` |

Lowest checked contrast: 4.6:1 (dropped).

### DanganronpaScheme (dark)

Monokuma's black and white with the series' pink blood. Playing is plain white-grey.

| Group | Values |
|---|---|
| M3 · Surfaces | background `#0A0A0A` · surface `#0A0A0A` · surfaceDim `#0A0A0A` · surfaceBright `#3B3B3B` · surfaceContainerLowest `#060606` · surfaceContainerLow `#101010` · surfaceContainer `#181818` · surfaceContainerHigh `#232323` · surfaceContainerHighest `#2E2E2E` · surfaceVariant `#2E2E2E` |
| M3 · Content | onSurface `#F5F5F5` · onBackground `#F5F5F5` · onSurfaceVariant `#BABABA` · inverseSurface `#F5F5F5` · inverseOnSurface `#212121` · outline `#8B8B8B` · outlineVariant `#353535` · scrim `#000000` |
| M3 · Primary | primary `#FF8CBF` · onPrimary `#4D0E2A` · primaryContainer `#FF2E8B` · onPrimaryContainer `#060606` · inversePrimary `#CC256F` · surfaceTint `#FF6DAE` |
| M3 · Secondary | secondary `#E6E6E6` · onSecondary `#3D3D3D` · secondaryContainer `#6C6C6C` · onSecondaryContainer `#F7F7F7` |
| M3 · Tertiary | tertiary `#FFDE7A` · onTertiary `#473B16` · tertiaryContainer `#6B5920` · onTertiaryContainer `#FFF8E4` |
| M3 · Error | error `#FFB4AB` · onError `#690005` · errorContainer `#93000A` · onErrorContainer `#FFDAD6` |
| Vndb · Surfaces | surfaceCard `#181818` · surfaceElevated `#272727` · surfaceInset `#0F0F0F` · borderSubtle `#2C2C2C` · surfaceGlass `#0F0F0F @ 85%` · hairline `White @ 8%` |
| Vndb · Accent | accent `#FF2E8B` · accentPressed `#C7246C` · onAccent `#060606` · accentDeep `#4F142E` · chipActiveContent `#FF77B4` · glow `#C7246C @ 15%` · score `#FFD34D` · placeholder `#808080` |
| Vndb · Status / labels | playing `#D9D9D9` · finished `#5FD3A5` · stalled `#F0B84A` · dropped `#FF6B6B` · nsfw `#C4B5FD` · wishlist `#B5A2F2` |
| Vndb · Release pills | releaseNew `#2DD4BF` · releaseTranslation `#A78BFA` · releasePatch `#FB923C` |

Lowest checked contrast: 4.9:1 (onSecCont/secCont).

### WhiteAlbum2Scheme (dark)

Snowy night, winter-rose accent (Setsuna's scarf), ice-blue secondary.

| Group | Values |
|---|---|
| M3 · Surfaces | background `#0F1418` · surface `#0F1418` · surfaceDim `#0F1418` · surfaceBright `#3C444A` · surfaceContainerLowest `#0A0D10` · surfaceContainerLow `#141B20` · surfaceContainer `#1A2329` · surfaceContainerHigh `#252D33` · surfaceContainerHighest `#2F383E` · surfaceVariant `#2F383E` |
| M3 · Content | onSurface `#EEF3F7` · onBackground `#EEF3F7` · onSurfaceVariant `#B6BBBF` · inverseSurface `#EEF3F7` · inverseOnSurface `#222B31` · outline `#8A8F93` · outlineVariant `#363E44` · scrim `#000000` |
| M3 · Primary | primary `#F2B9C7` · onPrimary `#46262E` · primaryContainer `#E8809A` · onPrimaryContainer `#090C0E` · inversePrimary `#BA667B` · surfaceTint `#EFA6B8` |
| M3 · Secondary | secondary `#BFDBEF` · onSecondary `#2C3840` · secondaryContainer `#4E6473` · onSecondaryContainer `#EBF4FA` |
| M3 · Tertiary | tertiary `#F5DCA7` · onTertiary `#443A27` · tertiaryContainer `#66573A` · onTertiaryContainer `#FDF8ED` |
| M3 · Error | error `#FFB4AB` · onError `#690005` · errorContainer `#93000A` · onErrorContainer `#FFDAD6` |
| Vndb · Surfaces | surfaceCard `#1A2329` · surfaceElevated `#293237` · surfaceInset `#13191E` · borderSubtle `#2D363C` · surfaceGlass `#13191E @ 85%` · hairline `White @ 8%` |
| Vndb · Accent | accent `#E8809A` · accentPressed `#B56478` · onAccent `#090C0E` · accentDeep `#4C323C` · chipActiveContent `#F0ACBD` · glow `#B56478 @ 15%` · score `#F2D08A` · placeholder `#7E8488` |
| Vndb · Status / labels | playing `#9CC7E6` · finished `#5FD3A5` · stalled `#F0B84A` · dropped `#FF7A7A` · nsfw `#F0ABFC` · wishlist `#B5A2F2` |
| Vndb · Release pills | releaseNew `#2DD4BF` · releaseTranslation `#A78BFA` · releasePatch `#FB923C` |

Lowest checked contrast: 4.7:1 (releaseTranslation).

### EfScheme (dark)

Twilight mauve, apricot accent, lilac secondary. Patch pill is pink, clear of the apricot.

| Group | Values |
|---|---|
| M3 · Surfaces | background `#15131D` · surface `#15131D` · surfaceDim `#15131D` · surfaceBright `#433F4D` · surfaceContainerLowest `#0E0C13` · surfaceContainerLow `#1B1824` · surfaceContainer `#221E2D` · surfaceContainerHigh `#2C2837` · surfaceContainerHighest `#373341` · surfaceVariant `#373341` |
| M3 · Content | onSurface `#F3EEF6` · onBackground `#F3EEF6` · onSurfaceVariant `#BCB7C0` · inverseSurface `#F3EEF6` · inverseOnSurface `#2A2635` · outline `#8F8B94` · outlineVariant `#3D3947` · scrim `#000000` |
| M3 · Primary | primary `#FFD4BE` · onPrimary `#4D3529` · primaryContainer `#FFB088` · onPrimaryContainer `#0D0B11` · inversePrimary `#CC8D6D` · surfaceTint `#FFC8AC` |
| M3 · Secondary | secondary `#D1C4ED` · onSecondary `#342E40` · secondaryContainer `#5C5272` · onSecondaryContainer `#F1EDF9` |
| M3 · Tertiary | tertiary `#F8DC9B` · onTertiary `#453B22` · tertiaryContainer `#675833` · onTertiaryContainer `#FEF8EB` |
| M3 · Error | error `#FFB4AB` · onError `#690005` · errorContainer `#93000A` · onErrorContainer `#FFDAD6` |
| Vndb · Surfaces | surfaceCard `#221E2D` · surfaceElevated `#312D3B` · surfaceInset `#1A1723` · borderSubtle `#35313F` · surfaceGlass `#1A1723 @ 85%` · hairline `White @ 8%` |
| Vndb · Accent | accent `#FFB088` · accentPressed `#C7896A` · onAccent `#0D0B11` · accentDeep `#573F3B` · chipActiveContent `#FFCCB2` · glow `#C7896A @ 15%` · score `#F5D17A` · placeholder `#84808A` |
| Vndb · Status / labels | playing `#9CC7F0` · finished `#5FD3A5` · stalled `#F0B84A` · dropped `#FF7A93` · nsfw `#FF8FB1` · wishlist `#B8A4E3` |
| Vndb · Release pills | releaseNew `#2DD4BF` · releaseTranslation `#A78BFA` · releasePatch `#F9A8D4` |

Lowest checked contrast: 4.8:1 (releaseTranslation).

### KaraNoShoujoScheme (dark)

A 1950s black-and-white photograph: bone-white accent, blood red only for secondary and danger.

| Group | Values |
|---|---|
| M3 · Surfaces | background `#121110` · surface `#121110` · surfaceDim `#121110` · surfaceBright `#3E3C39` · surfaceContainerLowest `#0C0B0A` · surfaceContainerLow `#171614` · surfaceContainer `#1E1C1A` · surfaceContainerHigh `#282624` · surfaceContainerHighest `#32302E` · surfaceVariant `#32302E` |
| M3 · Content | onSurface `#E9E5DE` · onBackground `#E9E5DE` · onSurfaceVariant `#B3B0AA` · inverseSurface `#E9E5DE` · inverseOnSurface `#262422` · outline `#888681` · outlineVariant `#383633` · scrim `#000000` |
| M3 · Primary | primary `#ECE8E1` · onPrimary `#42403C` · primaryContainer `#DCD5C8` · onPrimaryContainer `#0B0A0A` · inversePrimary `#B0AAA0` · surfaceTint `#E6E2D8` |
| M3 · Secondary | secondary `#ED9499` · onSecondary `#40191B` · secondaryContainer `#722E31` · onSecondaryContainer `#F9DEE0` |
| M3 · Tertiary | tertiary `#E2C58F` · onTertiary `#3D321E` · tertiaryContainer `#5B4B2D` · onTertiaryContainer `#F9F3E9` |
| M3 · Error | error `#FFB4AB` · onError `#690005` · errorContainer `#93000A` · onErrorContainer `#FFDAD6` |
| Vndb · Surfaces | surfaceCard `#1E1C1A` · surfaceElevated `#2C2A28` · surfaceInset `#161514` · borderSubtle `#302E2C` · surfaceGlass `#161514 @ 85%` · hairline `White @ 8%` |
| Vndb · Accent | accent `#DCD5C8` · accentPressed `#ACA69C` · onAccent `#0B0A0A` · accentDeep `#4B4844` · chipActiveContent `#E8E4DB` · glow `#ACA69C @ 15%` · score `#D9B26A` · placeholder `#7E7B77` |
| Vndb · Status / labels | playing `#A9B8C9` · finished `#5FD3A5` · stalled `#F0B84A` · dropped `#EC7076` · nsfw `#FF8FB1` · wishlist `#B5A2F2` |
| Vndb · Release pills | releaseNew `#2DD4BF` · releaseTranslation `#A78BFA` · releasePatch `#FB923C` |

Lowest checked contrast: 4.8:1 (dropped).

### UminekoScheme (dark)

Blue-truth accent, red-truth secondary, golden-butterfly score, on ebony.

| Group | Values |
|---|---|
| M3 · Surfaces | background `#110A0C` · surface `#110A0C` · surfaceDim `#110A0C` · surfaceBright `#403436` · surfaceContainerLowest `#0B0608` · surfaceContainerLow `#170E10` · surfaceContainer `#1E1215` · surfaceContainerHigh `#291D1F` · surfaceContainerHighest `#33282A` · surfaceVariant `#33282A` |
| M3 · Content | onSurface `#F2E9E6` · onBackground `#F2E9E6` · onSurfaceVariant `#BAB1B0` · inverseSurface `#F2E9E6` · inverseOnSurface `#261B1D` · outline `#8D8584` · outlineVariant `#3A2E30` · scrim `#000000` |
| M3 · Primary | primary `#A5C0FF` · onPrimary `#1B2A4D` · primaryContainer `#5B8DFF` · onPrimaryContainer `#0A0607` · inversePrimary `#4971CC` · surfaceTint `#8CAFFF` |
| M3 · Secondary | secondary `#F0848D` · onSecondary `#411216` · secondaryContainer `#742028` · onSecondaryContainer `#FAD9DC` |
| M3 · Tertiary | tertiary `#EED088` · onTertiary `#41361B` · tertiaryContainer `#615128` · onTertiaryContainer `#FCF6E7` |
| M3 · Error | error `#FFB4AB` · onError `#690005` · errorContainer `#93000A` · onErrorContainer `#FFDAD6` |
| Vndb · Surfaces | surfaceCard `#1E1215` · surfaceElevated `#2D2124` · surfaceInset `#160D0F` · borderSubtle `#312528` · surfaceGlass `#160D0F @ 85%` · hairline `White @ 8%` |
| Vndb · Accent | accent `#5B8DFF` · accentPressed `#476EC7` · onAccent `#0A0607` · accentDeep `#262F50` · chipActiveContent `#94B5FF` · glow `#476EC7 @ 15%` · score `#E8C060` · placeholder `#827A79` |
| Vndb · Status / labels | playing `#E8C060` · finished `#5FD3A5` · stalled `#F0B84A` · dropped `#FF6B78` · nsfw `#FF8FB1` · wishlist `#B5A2F2` |
| Vndb · Release pills | releaseNew `#2DD4BF` · releaseTranslation `#C4B5FD` · releasePatch `#FB923C` |

Lowest checked contrast: 5.5:1 (dropped).

### FateStayNightScheme (dark)

Saber: armor steel, royal-blue accent, gold secondary and score.

| Group | Values |
|---|---|
| M3 · Surfaces | background `#0E1015` · surface `#0E1015` · surfaceDim `#0E1015` · surfaceBright `#3C3F48` · surfaceContainerLowest `#090A0E` · surfaceContainerLow `#13161D` · surfaceContainer `#1A1E27` · surfaceContainerHigh `#242831` · surfaceContainerHighest `#2F333B` · surfaceVariant `#2F333B` |
| M3 · Content | onSurface `#ECEEF3` · onBackground `#ECEEF3` · onSurfaceVariant `#B4B6BC` · inverseSurface `#ECEEF3` · inverseOnSurface `#22262F` · outline `#888A8F` · outlineVariant `#353942` · scrim `#000000` |
| M3 · Primary | primary `#AEC1F8` · onPrimary `#202B49` · primaryContainer `#6B8EF2` · onPrimaryContainer `#080A0D` · inversePrimary `#5672C2` · surfaceTint `#97B0F6` |
| M3 · Secondary | secondary `#EDCE84` · onSecondary `#403212` · secondaryContainer `#725A20` · onSecondaryContainer `#F9F0D9` |
| M3 · Tertiary | tertiary `#EAC670` · onTertiary `#403212` · tertiaryContainer `#5F4B1B` · onTertiaryContainer `#FBF4E2` |
| M3 · Error | error `#FFB4AB` · onError `#690005` · errorContainer `#93000A` · onErrorContainer `#FFDAD6` |
| Vndb · Surfaces | surfaceCard `#1A1E27` · surfaceElevated `#292D35` · surfaceInset `#12151B` · borderSubtle `#2D3139` · surfaceGlass `#12151B @ 85%` · hairline `White @ 8%` |
| Vndb · Accent | accent `#6B8EF2` · accentPressed `#536FBD` · onAccent `#080A0D` · accentDeep `#283353` · chipActiveContent `#9FB6F7` · glow `#536FBD @ 15%` · score `#E3B341` · placeholder `#7D7F84` |
| Vndb · Status / labels | playing `#9FB4F7` · finished `#5FD3A5` · stalled `#FDBA74` · dropped `#F0617F` · nsfw `#FF8FB1` · wishlist `#B5A2F2` |
| Vndb · Release pills | releaseNew `#2DD4BF` · releaseTranslation `#A78BFA` · releasePatch `#FB923C` |

Lowest checked contrast: 4.5:1 (dropped).


## Appendix B — wallpapers.json

The manifest as committed in `tools/wallpapers/wallpapers.json`, after tuning with `--preview` (§3.3).

```json
{
  "output_size": [1080, 2400],
  "webp_quality": 80,
  "wallpapers": [
    {
      "name": "steins_gate",
      "vndb": "v2002",
      "source": "https://t.vndb.org/cv/65/85365.jpg",
      "region": [0, 0, 1, 0.72],
      "focus": [0.64, 0.4],
      "note": "Digital cover: Okabe, Kurisu, Mayuri. Region stops above the logo."
    },
    {
      "name": "ever17",
      "vndb": "v17",
      "source": "https://t.vndb.org/cv/92/85892.jpg",
      "region": [0, 0.115, 1, 0.78],
      "focus": [0.5, 0.45],
      "note": "PS2 cover: Tsugumi, Coco and You underwater. Region skips the PlayStation 2 bar and the logo."
    },
    {
      "name": "clannad",
      "vndb": "v4",
      "source": "https://t.vndb.org/cv/64/95864.jpg",
      "region": [0, 0.1, 1, 0.6],
      "focus": [0.5, 0.5],
      "note": "PC cover: the heroines, Nagisa in the middle. Region stops above the logo and tagline."
    },
    {
      "name": "higurashi",
      "vndb": "v67",
      "source": "https://t.vndb.org/cv/84/83484.jpg",
      "region": [0, 0.105, 1, 0.52],
      "focus": [0.45, 0.5],
      "note": "PS2 cover: Rena under a red moon. Region skips the PlayStation 2 bar and stops above the logo."
    },
    {
      "name": "doki_doki_literature_club",
      "vndb": "v21905",
      "source": "https://t.vndb.org/cv/77/116477.jpg",
      "region": [0, 0.065, 1, 0.6],
      "focus": [0.72, 0.5],
      "note": "Switch cover (Plus!): Monika. Region skips the Switch bar and stops above the logo."
    },
    {
      "name": "muv_luv_alternative",
      "vndb": "v92",
      "source": "https://t.vndb.org/cv/17/83217.jpg",
      "region": [0.5, 0.145, 0.9, 0.92],
      "focus": [0.7, 0.5],
      "note": "Xbox 360 cover: Meiya and the Takemikazuchi. Region skips the Xbox bar, the logo (left) and the rating marks."
    },
    {
      "name": "danganronpa",
      "vndb": "v7014",
      "source": "https://t.vndb.org/cv/15/75815.jpg",
      "region": [0, 0, 1, 1],
      "focus": [0.75, 0.5],
      "note": "Digital cover: pink-and-black type with Monokuma. PLACEHOLDER: the art is the logo, so the crop keeps part of it."
    },
    {
      "name": "white_album_2",
      "vndb": "v7771",
      "source": "https://t.vndb.org/cv/06/97306.jpg",
      "region": [0, 0, 1, 0.56],
      "focus": [0.24, 0.5],
      "note": "Extended Edition cover: Setsuna (pink scarf) in the snow. Region stops above the logo."
    },
    {
      "name": "ef",
      "vndb": "v88",
      "source": "https://t.vndb.org/cv/33/120033.jpg",
      "region": [0.03, 0.04, 0.97, 0.55],
      "focus": [0.5, 0.5],
      "note": "English package: the four heroines under a blue sky. Region skips the frame and stops above the logo."
    },
    {
      "name": "kara_no_shoujo",
      "vndb": "v810",
      "source": "https://t.vndb.org/cv/88/120088.jpg",
      "region": [0, 0.13, 1, 0.78],
      "focus": [0.55, 0.45],
      "note": "English package: Toko sitting in an eggshell, black and white. Region skips both logos and the 18+ mark."
    },
    {
      "name": "umineko",
      "vndb": "v24",
      "source": "https://t.vndb.org/cv/28/94328.jpg",
      "region": [0, 0.125, 1, 0.5],
      "focus": [0.71, 0.3],
      "note": "PS4 cover (Saku): Beatrice and golden butterflies. Region skips the PS4 bar and stops above the logo."
    },
    {
      "name": "fate_stay_night",
      "vndb": "v11",
      "source": "https://t.vndb.org/cv/74/89274.jpg",
      "region": [0, 0.12, 1, 0.78],
      "focus": [0.5, 0.45],
      "note": "Fate/stay night + hollow ataraxia package: Saber between Rin and Sakura. Region skips both logos and the title text."
    }
  ]
}
```
