# Plan 16 — Floating tab toolbar

**Status: Done on `feature/slide-transitions`.**

Goal: replace the navigation suite (bottom bar / rail / drawer) with a Material 3 floating toolbar over the tabs, now that `HorizontalFloatingToolbar` is stable in Material 3 1.5.0.

Rules: `.claude/skills/*`. Builds on Plan 15 (pages over the tabs).

---

## 1. Dependencies

- Compose BOM `2026.02.01` → `2026.11.01`: Material 3 1.4.0 → **1.5.0**, Compose UI / foundation / animation 1.10.4 → **1.13.0**. Overriding Material 3 alone would pull Compose 1.13 in anyway.
- Compose 1.13 requires `compileSdk` **37.1**: `release(37) { minorApiLevel = 1 }`. `targetSdk` (37) and `minSdk` (27) are unchanged, so runtime behaviour is too.
- `material3-adaptive-navigation-suite` is removed (catalog, `app/build.gradle.kts`, `library-management` artifact map, `tech-stack`).

## 2. Structure

```text
Pages NavDisplay (slides):  [Tabs] → Detail → …
Tabs = Box { tabs NavDisplay (cross-fade)   ← LocalTabToolbarPadding = 64dp + 16dp
             TabToolbar, bottom center, above the system navigation bar, 16dp off it }
```

- `app/navigation/TabToolbar.kt`: `HorizontalFloatingToolbar` (always expanded), solid `surfaceInset` with a `borderSubtle` hairline, like the other navigation surfaces. One tab per `TopLevelDestination`: its icon with the label below, the selected one in an accent pill (`selectable`, `Role.Tab`, inside a `selectableGroup`). Tabs are 56dp tall and the toolbar's vertical padding is 4dp, so it keeps its standard 64dp height.
- Same toolbar on every window size: no rail or drawer on wide screens.
- Pages cover the toolbar (Plan 15), so it never needs hiding.

## 3. Tab content clears the toolbar

The toolbar floats over the tab screens, so their content scrolls behind it. `common/ui/navigation/LocalTabToolbarPadding` (allow-listed in Detekt) is the toolbar's height plus its offset inside the tabs, and `0.dp` elsewhere (pages, including Browse scoped to a tag, producer or staff).

- Discover, Browse and Profile add it to the bottom content padding of their list or grid, so the last item scrolls clear.
- Their snackbars are padded by it, so they show above the toolbar.

## 4. On-device checks

- The toolbar sits bottom center, above the system bar; content scrolls behind it.
- The ends of Discover and Profile scroll clear of it.
- Switching tabs moves the accent pill and its label; the toolbar stays still.
- Opening a page slides the tab, toolbar included, out; the page has no toolbar.
- Accessibility: the selected tab is reported as selected, the others as clickable tabs named by their labels.

## 5. Open points

- Hide on scroll (`FloatingToolbarDefaults.exitAlwaysScrollBehavior`) is not used: the toolbar always stays visible.
- Not checked: wide screens and landscape, gesture navigation, predictive back, a snackbar actually showing.
