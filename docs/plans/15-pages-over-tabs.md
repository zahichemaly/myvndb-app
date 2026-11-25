# Plan 15 — Slide transitions, pages over the tabs

**Status: Done on `feature/slide-transitions`.**

Goal: screens slide horizontally (a new one comes in from the end edge as the current one leaves through the start edge; Back reverses it), without the navigation bar fighting the slide. Tabs keep their cross-fade.

Rules: `.claude/skills/*`. Navigation 3 reference: the `navigation-3` skill.

---

## 1. Problems with hiding the bar

The app hid the bar with a flag: when the top key was a `FullScreenNavKey`, a `LaunchedEffect` called `NavigationSuiteScaffoldState.hide()`, and `show()` otherwise.

- While it animates, `NavigationSuiteScaffold` re-measures its content to `height − barHeight × progress` on every frame, so both pages were laid out again on each frame of the slide, their bottom edge moving.
- The bar's spring ran on its own, one frame late, out of step with the page transition.
- Predictive back from a page showed the tab below it without its bar; the bar sprang in only after the gesture finished.
- On the rail, the content width changed mid-slide, so the slide distance did too.
- Every key below a tab was already a `FullScreenNavKey` (except the list editor sheet), so the flag only restated the structure.

Turning off the hide animation (`snapTo`) would have swapped the per-frame relayout for a jump of the bar's height, and kept the predictive back problem.

## 2. Structure

```text
Pages NavDisplay (slides):  [Tabs] → Detail → Character → …   (+ list editor sheet)
Tabs = NavigationSuiteScaffold { tabs NavDisplay (cross-fade): Discover | Browse | Profile }
```

- `NavigationState` holds the selected tab and one `pages` back stack (pages over the selected tab, empty while a tab is shown). Per-tab back stacks are gone: the bar is covered while a page is open, so a tab switch only ever happened with an empty stack.
- Tab screens are decorated in `AppNavigation` (always composed), so they keep their ViewModel and saveable state under pages. A tab switch still resets the scroll position of the tab it lands on.
- `Navigator.navigate` switches tabs (closing any pages) or opens a page; `goBack` closes the top page, else leaves a tab for the start tab. `reselect` is gone: there was never anything to pop while the bar was reachable.
- `FullScreenNavKey` and `OverlayNavKey` are removed; keys are plain `NavKey`s. A bottom sheet is still chosen by entry metadata.
- `isTabSwitch` (added earlier on this branch) is gone: the pages display only opens and closes pages, and the tabs display only switches tabs.

## 3. Tests

`NavigatorTest` covers tab switches, opening and closing pages, switching tabs with pages open, exit through home and closing an overlay.

## 4. On-device checks

- Opening a page slides it over the bar; Back slides it out with the bar already in place.
- Predictive back from a page shows the tab with its bar.
- Tab switches cross-fade with the bar still; the selected indicator animates.
- Scroll position under a page survives Back; a tab switch opens the tab at the top.
