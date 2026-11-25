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
 * One back stack per top-level tab, so switching tabs never pops a tab's entries: their
 * ViewModels (and loaded data) survive until the entry itself is popped. UI state such as scroll
 * position is kept for Back within a tab, but reset for the screen a tab switch lands on
 * (see [NavigationState.resetTopScreenState]). Survives configuration changes and process death.
 */
@Composable
fun rememberNavigationState(startRoute: NavKey, topLevelRoutes: Set<NavKey>): NavigationState {
    val topLevelRoute = rememberSerializable(
        startRoute,
        topLevelRoutes,
        serializer = MutableStateSerializer(NavKeySerializer()),
    ) { mutableStateOf(startRoute) }
    val backStacks = topLevelRoutes.associateWith { key -> rememberNavBackStack(key) }
    val stateHolders = topLevelRoutes.associateWith { rememberSaveableStateHolder() }
    return remember(startRoute, topLevelRoutes) {
        NavigationState(
            startRoute = startRoute,
            topLevelRoute = topLevelRoute,
            backStacks = backStacks,
            stateHolders = stateHolders,
        )
    }
}

/** Navigation state; changed only through [Navigator]. */
class NavigationState(
    val startRoute: NavKey,
    topLevelRoute: MutableState<NavKey>,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>,
    /** Saveable UI state of each tab's entries. */
    private val stateHolders: Map<NavKey, SaveableStateHolder>,
) {

    /** The top-level (tab) routes: the keys of [backStacks]. */
    val topLevelRoutes: Set<NavKey> = backStacks.keys.toSet()

    /** The selected tab. */
    var topLevelRoute: NavKey by topLevelRoute

    /**
     * Whether the latest move was a switch from the navigation bar (or rail) rather than a push or
     * a pop within a stack: pages slide in and out, but tabs cross-fade.
     */
    var isTabSwitch: Boolean = false

    /** Content key of each tab's top entry, as last composed. */
    private val topContentKeys = mutableMapOf<NavKey, Any>()

    /**
     * Forgets the saved UI state (scroll position, …) of [route]'s top screen, so it opens at the
     * top next time. Its ViewModel, and so its data, is kept. Only call while [route] is hidden:
     * state of a screen on display is not removed.
     */
    fun resetTopScreenState(route: NavKey) {
        val contentKey = topContentKeys[route] ?: return
        stateHolders[route]?.removeState(contentKey)
    }

    /**
     * Entries to display: the start tab's stack, then the selected tab's ("exit through home").
     * Every stack is decorated on each composition, including hidden ones, so their entries
     * keep their ViewModel stores and saveable state while another tab is shown.
     */
    @Composable
    fun toDecoratedEntries(entryProvider: (NavKey) -> NavEntry<NavKey>): List<NavEntry<NavKey>> {
        val decorated = backStacks.mapValues { (route, stack) ->
            rememberDecoratedNavEntries(
                backStack = stack,
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(stateHolders.getValue(route)),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
                entryProvider = entryProvider,
            )
        }
        SideEffect {
            decorated.forEach { (route, entries) ->
                entries.lastOrNull()?.let { topContentKeys[route] = it.contentKey }
            }
        }
        val inUse = if (topLevelRoute == startRoute) {
            listOf(
                startRoute,
            )
        } else {
            listOf(startRoute, topLevelRoute)
        }
        return inUse.flatMap { decorated[it].orEmpty() }
    }
}
