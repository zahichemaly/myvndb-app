package com.zc.vndb.common.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class NavigatorTest {

    @Serializable
    private data object Home : NavKey

    @Serializable
    private data object Search : NavKey

    @Serializable
    private data class Page(val id: String) : NavKey

    @Serializable
    private data class Sheet(val id: String) : NavKey

    /** Tab state is only recorded while composed, which these tests never are. */
    private object NoTabState : SaveableStateHolder {
        @Composable
        override fun SaveableStateProvider(key: Any, content: @Composable () -> Unit) = content()

        override fun removeState(key: Any) = Unit
    }

    private lateinit var state: NavigationState
    private lateinit var navigator: Navigator

    @Before
    fun setUp() {
        state = NavigationState(
            startRoute = Home,
            topLevelRoutes = setOf(Home, Search),
            topLevelRoute = mutableStateOf<NavKey>(Home),
            pages = NavBackStack(),
            tabStateHolder = NoTabState,
        )
        navigator = Navigator(state)
    }

    private fun pages() = state.pages.toList()

    @Test
    fun `navigating to the current tab does nothing`() {
        navigator.navigate(Home)

        assertEquals(Home, state.topLevelRoute)
        assertEquals(emptyList<NavKey>(), pages())
    }

    @Test
    fun `navigating to another tab selects it`() {
        navigator.navigate(Search)

        assertEquals(Search, state.topLevelRoute)
        assertEquals(emptyList<NavKey>(), pages())
    }

    @Test
    fun `navigating to a page opens it over the selected tab`() {
        navigator.navigate(Search)

        navigator.navigate(Page("a"))
        navigator.navigate(Page("b"))

        assertEquals(Search, state.topLevelRoute)
        assertEquals(listOf<NavKey>(Page("a"), Page("b")), pages())
    }

    @Test
    fun `switching tabs closes the open pages`() {
        navigator.navigate(Page("a"))

        navigator.navigate(Search)

        assertEquals(Search, state.topLevelRoute)
        assertEquals(emptyList<NavKey>(), pages())
    }

    @Test
    fun `going back closes the top page`() {
        navigator.navigate(Search)
        navigator.navigate(Page("a"))
        navigator.navigate(Page("b"))

        navigator.goBack()

        assertEquals(Search, state.topLevelRoute)
        assertEquals(listOf<NavKey>(Page("a")), pages())
    }

    @Test
    fun `going back from the last page shows the tab below`() {
        navigator.navigate(Search)
        navigator.navigate(Page("a"))

        navigator.goBack()

        assertEquals(Search, state.topLevelRoute)
        assertEquals(emptyList<NavKey>(), pages())
    }

    @Test
    fun `going back from another tab returns to the start tab`() {
        navigator.navigate(Search)

        navigator.goBack()

        assertEquals(Home, state.topLevelRoute)
    }

    @Test
    fun `going back at the start tab keeps the start tab`() {
        navigator.goBack()

        assertEquals(Home, state.topLevelRoute)
        assertEquals(emptyList<NavKey>(), pages())
    }

    @Test
    fun `an overlay is closed without changing the page or tab below`() {
        navigator.navigate(Search)
        navigator.navigate(Page("a"))
        navigator.navigate(Sheet("a"))

        navigator.goBack()

        assertEquals(Search, state.topLevelRoute)
        assertEquals(listOf<NavKey>(Page("a")), pages())
    }
}
