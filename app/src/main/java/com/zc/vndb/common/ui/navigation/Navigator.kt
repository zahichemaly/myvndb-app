package com.zc.vndb.common.ui.navigation

import androidx.navigation3.runtime.NavKey

/**
 * Moves through [NavigationState]: tab switches keep each tab's data but open its screen at the
 * top; any other route opens as a page over the selected tab.
 */
class Navigator(private val state: NavigationState) {

    /** Switches to a tab (closing any open pages), or opens [route] as a page. */
    fun navigate(route: NavKey) {
        if (route !in state.topLevelRoutes) {
            state.pages.add(route)
            return
        }
        state.pages.clear()
        if (route == state.topLevelRoute) return
        // Each tab opens at the top: its data is kept, its scroll position is not.
        state.resetTabState(route)
        state.topLevelRoute = route
    }

    /** Closes the top page; with none open, leaves another tab for the start tab. */
    fun goBack() {
        when {
            state.pages.isNotEmpty() -> state.pages.removeLastOrNull()

            state.topLevelRoute != state.startRoute -> {
                state.resetTabState(state.startRoute)
                state.topLevelRoute = state.startRoute
            }
        }
    }
}
