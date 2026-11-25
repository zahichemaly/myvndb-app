package com.zc.vndb.common.ui.navigation

import androidx.navigation3.runtime.NavKey

/**
 * Moves through [NavigationState]: tab switches keep each tab's stack and data but open its screen
 * at the top; other routes push onto the current tab.
 */
class Navigator(private val state: NavigationState) {

    /** Switches to a tab, or pushes [route] onto the current tab's stack. */
    fun navigate(route: NavKey) {
        if (route in state.topLevelRoutes) {
            if (route == state.topLevelRoute) return
            // Each tab opens at the top: its data is kept, its scroll position is not.
            state.resetTopScreenState(route)
            state.isTabSwitch = true
            state.topLevelRoute = route
        } else {
            state.isTabSwitch = false
            currentStack().add(route)
        }
    }

    /** Tapping the already selected tab returns it to its root, keeping the root's state. */
    fun reselect(route: NavKey) {
        val stack = state.backStacks[route] ?: return
        state.isTabSwitch = false
        while (stack.size > 1) stack.removeLastOrNull()
    }

    /** Back is always a pop, even when it leaves a tab's root for the start tab. */
    fun goBack() {
        state.isTabSwitch = false
        val stack = currentStack()
        if (stack.size == 1) {
            state.resetTopScreenState(state.startRoute)
            state.topLevelRoute = state.startRoute
        } else {
            stack.removeLastOrNull()
        }
    }

    private fun currentStack() = state.backStacks[state.topLevelRoute]
        ?: error("No back stack for ${state.topLevelRoute}")
}
