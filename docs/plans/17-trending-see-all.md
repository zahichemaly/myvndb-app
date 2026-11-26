# Plan 17 — New & Trending: See all

**Status: Done on `develop`.**

Goal: Discover's "New & Trending" header shows its languages under the title and a "See all" where they were, like Top Rated. See all opens the full list.

Rules: `.claude/skills/*`.

---

## 1. Section headers

`SectionHeaderView` (Discover) takes an optional `subtitle` and `onSeeAllClick` instead of a free `action` slot.

- "See all" is a compact text link, only as tall as its text, so a header with it is as tall as one without, and every section keeps the same 24dp gap above its title. Compose still extends a small clickable's touch target to 48dp when hit-testing.
- Top Rated uses the same link and gets its top padding back (its workaround for the taller Material `TextButton` is gone).
- Trending: the languages ("English, Japanese") are the subtitle.

## 2. The full list

A page over the tabs (`TrendingNavKey`, `trendingEntry`, `Navigator.navigateToTrending()`), in the Discover feature so it reuses the Trending rows (`TrendingReleaseView`).

- Same rows as Discover's: `List<Release>.toTrendingReleases` (one row per visual novel, machine-translation-only releases left out) is shared by `GetTrendingReleasesUseCase` (Discover, first 10) and `GetTrendingReleasesPageUseCase` (the page).
- Paging: `recentReleasesQuery` takes a `page`; `DiscoverRepository.getRecentReleasesPage` returns VNDB's `more` as `ReleasesPage.hasMore`. 30 releases per request, newest first, in Settings → Trending languages.
- A page leaves out visual novels already listed on earlier pages. A page that brings no new row is skipped (up to 3 in a row), because the list's end-reached trigger would have nothing to fire on.
- States: loading (a shimmer skeleton shaped like the page, rows as tall as real ones), error with Retry, empty, pull to refresh (a failed refresh keeps the rows and shows a snackbar), loading more, and Retry when a next page fails.

## 3. Tests

`GetTrendingReleasesPageUseCaseTest`, `TrendingViewModelTest`, a paging case in `DiscoverRepositoryImplTest`, `TrendingNavKey` in `NavKeySerializationTest`, and the See all effect in `DiscoverViewModelTest`.
