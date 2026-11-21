# Plan 10 — Themes

The user picks an app theme (colors + wallpaper) from Settings. The choice is saved and the whole app re-themes at once, no restart.
Rules: `.claude/skills/*`. Supersedes the "no theme picker" decision in [plan 09](09-settings.md) (that plan assumed one dark palette and no wallpaper).

## 1. Behaviour
- **Settings:** a new **Theme** row at the top of the Display section. It shows the current theme's name and opens the Theme screen.
- **Theme screen** (full screen, bottom bar hidden, Back in the top bar): one card per theme, each with a **live preview** of that theme (see §3). Tapping a card selects it and applies it at once. There is no Apply button. The selected card is marked with a check and an accent border.
- **Themes now:**

| Theme | Colors | Wallpaper | Default |
|---|---|---|---|
| **Original** | Nocturne VN, as before | none | yes |
| **Angelic Serenade** | Same Nocturne VN palette | `wallpaper_angelic_serenade.webp` at 20% | no |
| **Saya no Uta** | Own wine-and-teal palette, taken from the wallpaper's colors | `wallpaper_saya_no_uta.webp` at 20% | no |
| **House in Fata Morgana** | Own palette: warm near-black `#171311` / `#261F1B`, gold `#C9A66B`, wine `#8C4A4A` | `wallpaper_house_in_fata_morgana.webp` at 20% | no |
| **Tsukihime** | Own palette: midnight blue `#0A0E1A` / `#141C33`, text `#E6ECFF`, crimson `#EC5570` with dark text on it (was `#D92B4B` with white; changed in [plan 14](14-more-themes.md) §9 for contrast) | `wallpaper_tsukihime.webp` at 20% | no |
| **Meikei no Lupercalia** | Own palette: violet-black `#0D0B14` / `#181524`, text `#F3EFFA`, gold `#E2B86B`, rose-crimson `#D94B6A` | `wallpaper_meikei_no_lupercalia.webp` at 20% | no |

- **Saved:** the choice is stored in the `settings` DataStore and read at launch. An unknown or missing value falls back to Original.
- **Dynamic:** changing the theme recomposes the whole app (every screen, the nav bar, the Theme screen itself). Dialogs and the bottom sheet pick it up too, because they read the same theme tokens.

## 2. Design
A theme is a **spec**: everything the UI needs to paint it. Today the two specs share one palette, but the model lets a later theme carry its own colors.

```kotlin
@Immutable data class WallpaperSpec(@DrawableRes val image: Int, val alpha: Float)
@Immutable data class VndbThemeSpec(
    val colorScheme: ColorScheme,   // Material roles (today's NocturneColorScheme)
    val colors: VndbColors,         // extra tokens (surfaceCard, accent, status tints, ...)
    val wallpaper: WallpaperSpec?,  // null = plain background
)
```

- `AppTheme { Original, AngelicSerenade }` is the persisted choice. It is a pure-Kotlin enum in the domain layer, next to `TitleLanguage`.
- `AppTheme -> VndbThemeSpec` is a UI mapper. The specs are defined once in `common/ui/theme`.
- `VndbTheme(spec = OriginalThemeSpec) { }` takes the spec, so `@Preview`s and tests keep working with no argument. It provides the existing locals plus a new `LocalVndbWallpaper` (and swaps `colorScheme`).
- `WallpaperView` stops hard-coding the image and alpha and reads `VndbTheme.wallpaper` instead. A null wallpaper draws just the background.
- **Nested themes:** `VndbTheme` can be nested, so each preview card on the Theme screen wraps itself in `VndbTheme(thatThemesSpec)` and renders real tokens. No duplicated colors, and it cannot drift from the real theme.

## 3. Theme screen and preview
Each card is a small phone-shaped mock rendered with the real components under the nested theme:
- the wallpaper cropped and dimmed as it would be, over the theme's background;
- a mini top bar (wordmark), one card with a title line, tag chips and the accent button, and a strip standing in for the nav bar;
- a swatch row: background, card, accent, secondary (score).

Below it: the theme name and a check when selected. Cards sit in an adaptive grid (`LazyVerticalGrid`, min ~160dp), so more themes, tablets and landscape need no rework.

## 4. Code
**Domain / data (`common`)**
- `common/domain/model/AppSettings.kt`: `val theme: AppTheme = AppTheme.Original`, plus the `AppTheme` enum.
- `common/data/local/SettingsLocalDataSource.kt`: key `theme` (string, enum name), read with `toEnumOr`, written in `write`.
- No repository or use-case changes: `SettingsRepository.update { it.copy(theme = x) }` and `UpdateSettingsUseCase` already cover it.

**UI foundation (`common/ui`)**
- `theme/`: `VndbThemeSpec`, `WallpaperSpec`, `Themes.kt` (the two specs). `Theme.kt` takes a spec and provides `LocalVndbWallpaper`. The raw palette in `Color.kt` is unchanged.
- `mapper/AppThemeMapper.kt`: `AppTheme.toSpec()` and `AppTheme.toLabel(): UiText`.
- `component/wallpaper/WallpaperView.kt`: reads the spec instead of constants.

**App root**
- `AppViewModel`: adds `theme: StateFlow<AppTheme?>`, `null` until the stored settings are read.
- `AppRoute`: owns the theme: `VndbTheme(theme.toSpec()) { WallpaperView { AppNavigation() } }`. `MainActivity` no longer wraps `VndbTheme`.
- **Cold start:** while `theme == null` the root draws only the window color (`nocturne_background`, already the launch background), so a user on Angelic Serenade never sees one frame of Original.
- Optional polish: cross-fade the wallpaper (~300 ms) on change. Colors switch instantly.

**Theme feature (`feature/theme`)** — the Theme screen is its own feature (decided with the plan). Settings only links to it, so neither feature imports the other:
- `domain/usecase/SelectThemeUseCase`: saves the choice through `SettingsRepository.update`. There is no `data/` package: the choice is stored by the shared settings DataStore in `common`.
- `presentation/`: `ThemeRoute|Screen|ViewModel|State|Intent|Effect`. State holds `ImmutableList<ThemeUIModel>` (theme, name, spec, isSelected). Intents are `BackClicked` and `ThemeSelected(theme)`. `mapper/ThemeUIModelMapper` builds the state. `component/themepreview/ThemePreviewView` is the card from §3, with its `model/ThemeUIModel`.
- `di/ThemeModule`, added to `AppModule`.
- Strings: `theme_title`, `theme_original`, `theme_angelic_serenade`, `theme_selected`, `cd_theme_preview`.

**Settings feature (`feature/settings`)** — only the entry point:
- A **Theme** row in the Display section (`SettingsState.themeLabel`, `SettingsIntent.ThemeClicked`, `SettingsEffect.OpenTheme`). `SettingsRoute` gets `onOpenTheme`. Strings: `settings_theme`, `settings_theme_body`.

**Navigation (`AppNavigation`)**
- `@Serializable data object ThemeNavKey : FullScreenNavKey`, pushed from Settings, with an entry for `ThemeRoute`.

**Docs**
- Update plan 09 (theme picker now exists, see this plan), the `structure` skill's `common/ui/theme` and `mapper` lines, and add the Angelic Serenade palette notes to `DESIGN.md` only once a theme has its own colors.

## Adding a theme
A theme is a name, a color scheme and an optional wallpaper. Color schemes live one per file in `common/ui/theme/schemes/`; a theme may reuse an existing scheme (Original and Angelic Serenade both use Nocturne).
1. **Scheme** (only if the theme has new colors): copy `schemes/TsukihimeScheme.kt` (dark) or `schemes/LightScheme.kt` (light), rename it, and set every Material role and every `VndbColors` token. `VndbColors` has no defaults, so a missing token is a compile error, not a silent inherit. [Plan 14](14-more-themes.md) §2 has the rules that derive a full scheme from six anchor colors. Contrast to check: the accent against the card and background colors (>= 4.5:1), `onAccent` on the accent, cream/body text on the wallpaper wash. `ThemeContrastTest` checks body text and the accent on cards, and `onAccent` on the accent, for every theme.
2. **Wallpaper** (optional): add an entry to `tools/wallpapers/wallpapers.json` (source URL, clean region, focus point), run `python3 -I tools/wallpapers/fetch_wallpapers.py --preview --only <name>`, check `build/wallpapers/preview.png`, then run it again without `--preview`. It writes a 1080 x 2400 WebP (quality 80) to `res/drawable-nodpi/wallpaper_<name>.webp`. See plan 14 §3.
3. **Strings:** `theme_<name>` in `strings.xml`.
4. **Spec:** one entry in `common/ui/theme/Themes.kt`: `val <Name>ThemeSpec = <Scheme>.toSpec(name, wallpaper)`.
5. **Enum and mapper:** add the entry to `AppTheme` (domain) and one branch to `AppTheme.toSpec()` (`common/ui/mapper/AppThemeUIModelMapper.kt`). The `when` is exhaustive, so the build fails until this is done.

Nothing else changes: the Theme screen lists `AppTheme.entries`, the Settings row, persistence (an unknown stored value falls back to Original) and the root all read from the spec. The mapper test checks every theme has a unique name and look, and a wallpaper unless it is one of the plain themes (Original, Light, Dark, Neon, Sepia, AMOLED). A light theme (background luminance > 0.5) gets dark system bar icons automatically (`AppRoute`).

## 5. Tests
- `SettingsLocalDataSourceTest`: theme round-trip, default is Original, an unknown stored value falls back to Original.
- `FakeSettingsRepository` and `SettingsViewModelTest`: the Theme row shows the current theme's name.
- `SelectThemeUseCaseTest`: saves the theme and leaves other settings alone.
- `ThemeViewModelTest`: the list holds both themes, exactly one is selected, `ThemeSelected` persists and moves the selection.
- `AppViewModelTest`: `theme` is null before settings load, then the stored theme.
- Mapper test: every `AppTheme` maps to a spec and a label (guards a future theme added without a spec).
- `KoinModulesTest` still passes.
- On-device check of each screen, the nav bar, a dialog and the bottom sheet under both themes, plus a cold start with Angelic Serenade saved.

## 6. Order of work
1. Domain + DataStore (`AppTheme`, key, tests).
2. UI foundation: specs, `VndbTheme(spec)`, `WallpaperView` from spec, mapper (no visible change yet).
3. App root: `AppViewModel.theme`, `AppRoute` owns the theme, cold-start gate.
4. Theme screen (ViewModel, preview card, screen, route, nav key, strings) and the Settings row.
5. Tests, on-device pass, docs.

## 7. Out of scope
- User-supplied wallpapers or per-theme opacity sliders.
- Dynamic (Material You) color.
- Distinct colors for Angelic Serenade (it reuses Nocturne VN for now; the spec model already allows it).
- A third theme.
