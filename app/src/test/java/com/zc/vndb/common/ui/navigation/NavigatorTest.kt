package com.zc.vndb.common.ui.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NavigatorTest {

    @Serializable
    private data object Home : NavKey

    @Serializable
    private data object Search : NavKey

    @Serializable
    private data class Page(val id: String) : FullScreenNavKey

    @Serializable
    private data class Sheet(val id: String) : OverlayNavKey

    private lateinit var state: NavigationState
    private lateinit var navigator: Navigator

    @Before
    fun setUp() {
        state = NavigationState(
            startRoute = Home,
            topLevelRoute = mutableStateOf<NavKey>(Home),
            backStacks = mapOf(
                Home to NavBackStack<NavKey>(Home),
                Search to NavBackStack<NavKey>(Search),
            ),
            stateHolders = emptyMap(),
        )
        navigator = Navigator(state)
    }

    private fun stack(route: NavKey) = state.backStacks.getValue(route).toList()

    @Test
    fun `navigating to the current tab does nothing`() {
        navigator.navigate(Home)

        assertEquals(Home, state.topLevelRoute)
        assertEquals(listOf<NavKey>(Home), stack(Home))
    }

    @Test
    fun `navigating to another tab switches tab and keeps both stacks`() {
        navigator.navigate(Page("a"))

        navigator.navigate(Search)

        assertEquals(Search, state.topLevelRoute)
        assertEquals(listOf<NavKey>(Home, Page("a")), stack(Home))
        assertEquals(listOf<NavKey>(Search), stack(Search))
    }

    @Test
    fun `navigating to a page pushes it on the current tab only`() {
        navigator.navigate(Search)

        navigator.navigate(Page("a"))

        assertEquals(listOf<NavKey>(Search, Page("a")), stack(Search))
        assertEquals(listOf<NavKey>(Home), stack(Home))
    }

    @Test
    fun `reselecting a tab pops it to its root`() {
        navigator.navigate(Page("a"))
        navigator.navigate(Page("b"))

        navigator.reselect(Home)

        assertEquals(listOf<NavKey>(Home), stack(Home))
        assertEquals(Home, state.topLevelRoute)
    }

    @Test
    fun `going back pops within a stack`() {
        navigator.navigate(Page("a"))
        navigator.navigate(Page("b"))

        navigator.goBack()

        assertEquals(listOf<NavKey>(Home, Page("a")), stack(Home))
    }

    @Test
    fun `going back from another tab's root returns to the start tab`() {
        navigator.navigate(Search)

        navigator.goBack()

        assertEquals(Home, state.topLevelRoute)
        assertEquals(listOf<NavKey>(Search), stack(Search))
    }

    @Test
    fun `going back at the start tab's root keeps the start tab`() {
        navigator.goBack()

        assertEquals(Home, state.topLevelRoute)
        assertEquals(listOf<NavKey>(Home), stack(Home))
    }

    @Test
    fun `switching tabs is a tab switch`() {
        navigator.navigate(Search)

        assertTrue(state.isTabSwitch)
    }

    @Test
    fun `pushing a page after a tab switch is not a tab switch`() {
        navigator.navigate(Search)

        navigator.navigate(Page("a"))

        assertFalse(state.isTabSwitch)
    }

    @Test
    fun `going back to the start tab is not a tab switch`() {
        navigator.navigate(Search)

        navigator.goBack()

        assertFalse(state.isTabSwitch)
    }

    @Test
    fun `reselecting a tab is not a tab switch`() {
        navigator.navigate(Search)
        navigator.navigate(Page("a"))
        navigator.navigate(Home)

        navigator.reselect(Home)

        assertFalse(state.isTabSwitch)
    }

    @Test
    fun `an overlay is popped without changing the page or tab below`() {
        navigator.navigate(Search)
        navigator.navigate(Page("a"))
        navigator.navigate(Sheet("a"))

        navigator.goBack()

        assertEquals(Search, state.topLevelRoute)
        assertEquals(listOf<NavKey>(Search, Page("a")), stack(Search))
    }
}
