package com.zc.vndb.common.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.serialization.NavKeySerializer
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer

/**
 * Two levels: the tabs, shown with the navigation bar, and a stack of pages opened over them
 * (covering the bar). Every tab's screen keeps its ViewModel (and loaded data) for the app's
 * lifetime; a page's goes when it is closed. A tab's scroll position is kept under its pages, but
 * reset when a tab switch lands on it (see [NavigationState.resetTabState]). Survives configuration
 * changes and process death.
 */
@Composable
fun rememberNavigationState(startRoute: NavKey, topLevelRoutes: Set<NavKey>): NavigationState {
    val topLevelRoute = rememberSerializable(
        startRoute,
        topLevelRoutes,
        serializer = MutableStateSerializer(NavKeySerializer()),
    ) { mutableStateOf(startRoute) }
    val pages = rememberNavBackStack()
    val tabStateHolder = rememberSaveableStateHolder()
    return remember(startRoute, topLevelRoutes) {
        NavigationState(
            startRoute = startRoute,
            topLevelRoutes = topLevelRoutes,
            topLevelRoute = topLevelRoute,
            pages = pages,
            tabStateHolder = tabStateHolder,
        )
    }
}

/** Navigation state; changed only through [Navigator]. */
class NavigationState(
    val startRoute: NavKey,
    /** The tabs' routes. */
    val topLevelRoutes: Set<NavKey>,
    topLevelRoute: MutableState<NavKey>,
    /** Pages opened over the selected tab, oldest first. Empty while a tab is shown. */
    val pages: NavBackStack<NavKey>,
    /** Saveable UI state of the tabs' screens. */
    private val tabStateHolder: SaveableStateHolder,
) {

    /** The selected tab. */
    var topLevelRoute: NavKey by topLevelRoute

    private val tabRoutes = topLevelRoutes.toList()

    /** Content key of each tab's screen, as last composed. */
    private val tabContentKeys = mutableMapOf<NavKey, Any>()

    /**
     * Forgets the saved UI state (scroll position, …) of [route]'s screen, so it opens at the top
     * next time. Its ViewModel, and so its data, is kept. Only call while [route] is hidden: state
     * of a screen on display is not removed.
     */
    fun resetTabState(route: NavKey) {
        tabContentKeys[route]?.let(tabStateHolder::removeState)
    }

    /**
     * Tab screens to display: the start tab's, then the selected tab's ("exit through home"). All
     * tabs are decorated on each composition, including hidden ones, so their screens keep their
     * ViewModel stores and saveable state while another tab is shown or a page covers them: call
     * this where it stays composed.
     */
    @Composable
    fun decoratedTabEntries(entryProvider: (NavKey) -> NavEntry<NavKey>): List<NavEntry<NavKey>> {
        val decorated = rememberDecoratedNavEntries(
            backStack = tabRoutes,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(tabStateHolder),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider,
        )
        val byRoute = tabRoutes.zip(decorated).toMap()
        SideEffect {
            byRoute.forEach { (route, entry) -> tabContentKeys[route] = entry.contentKey }
        }
        val inUse = if (topLevelRoute == startRoute) {
            listOf(startRoute)
        } else {
            listOf(startRoute, topLevelRoute)
        }
        return inUse.mapNotNull(byRoute::get)
    }

    /** The open pages, oldest first. A page's state and ViewModel go when it is closed. */
    @Composable
    fun decoratedPageEntries(entryProvider: (NavKey) -> NavEntry<NavKey>): List<NavEntry<NavKey>> =
        rememberDecoratedNavEntries(
            backStack = pages,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider,
        )
}
