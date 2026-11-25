package com.zc.vndb.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import com.zc.vndb.common.ui.navigation.BottomSheetSceneStrategy
import com.zc.vndb.common.ui.navigation.Navigator
import com.zc.vndb.common.ui.navigation.rememberNavigationState
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.feature.discover.presentation.navigation.DiscoverNavKey

/**
 * App root, in two levels. The tabs sit inside the adaptive navigation suite (bar / rail / drawer)
 * and cross-fade (Navigation 3's default). Pages open over the tabs, navigation suite included:
 * a new one slides in from the end edge as what is below leaves through the start edge, and Back
 * plays it in reverse (following the finger on predictive back).
 */
@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navigationState = rememberNavigationState(
        startRoute = DiscoverNavKey,
        topLevelRoutes = remember { TopLevelDestination.entries.map { it.key }.toSet() },
    )
    val navigator = remember(navigationState) { Navigator(navigationState) }
    val entryProvider = remember(navigator) { appEntryProvider(navigator) }
    // Decorated here, which stays composed, so the tabs keep their state while a page covers them.
    val tabEntries by rememberUpdatedState(navigationState.decoratedTabEntries(entryProvider))
    val tabsEntry = remember(navigationState, navigator) {
        NavEntry<NavKey>(TabsNavKey) {
            TabsScaffold(
                selectedTab = navigationState.topLevelRoute,
                onSelectTab = navigator::navigate,
            ) {
                NavDisplay(entries = tabEntries, onBack = navigator::goBack)
            }
        }
    }
    val bottomSheetStrategy = remember { BottomSheetSceneStrategy<NavKey>() }
    NavDisplay(
        entries = listOf(tabsEntry) + navigationState.decoratedPageEntries(entryProvider),
        modifier = modifier,
        sceneStrategies = listOf(bottomSheetStrategy),
        transitionSpec = { slide(SlideDirection.Start) },
        popTransitionSpec = { slide(SlideDirection.End) },
        predictivePopTransitionSpec = { _ -> slide(SlideDirection.End, easing = LinearEasing) },
        onBack = navigator::goBack,
    )
}

/** The bottom entry of the page stack: the tabs, under every page. */
private data object TabsNavKey : NavKey

@Composable
private fun TabsScaffold(
    selectedTab: NavKey,
    onSelectTab: (NavKey) -> Unit,
    content: @Composable () -> Unit,
) {
    val itemColors = navigationItemColors()
    NavigationSuiteScaffold(
        containerColor = Color.Transparent,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = VndbTheme.colors.surfaceInset,
            navigationRailContainerColor = VndbTheme.colors.surfaceInset,
            navigationDrawerContainerColor = VndbTheme.colors.surfaceInset,
        ),
        navigationSuiteItems = {
            TopLevelDestination.entries.forEach { destination ->
                item(
                    selected = selectedTab == destination.key,
                    onClick = { onSelectTab(destination.key) },
                    icon = { Icon(destination.icon, contentDescription = null) },
                    label = { Text(destination.label.asString()) },
                    colors = itemColors,
                )
            }
        },
        content = content,
    )
}

/** Selected tab in the accent color on an elevated-surface indicator pill, as in the design. */
@Composable
private fun navigationItemColors(): NavigationSuiteItemColors {
    val accent = VndbTheme.colors.accent
    val idle = MaterialTheme.colorScheme.onSurfaceVariant
    val indicator = VndbTheme.colors.surfaceElevated
    return NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = accent,
            selectedTextColor = accent,
            indicatorColor = indicator,
            unselectedIconColor = idle,
            unselectedTextColor = idle,
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            selectedIconColor = accent,
            selectedTextColor = accent,
            indicatorColor = indicator,
            unselectedIconColor = idle,
            unselectedTextColor = idle,
        ),
    )
}

private const val SLIDE_DURATION_MILLIS = 350

/** Both screens move [towards] the same edge: the new one slides in as the old one slides out. */
private fun AnimatedContentTransitionScope<Scene<NavKey>>.slide(
    towards: SlideDirection,
    easing: Easing = FastOutSlowInEasing,
): ContentTransform {
    val spec = tween<IntOffset>(SLIDE_DURATION_MILLIS, easing = easing)
    return slideIntoContainer(towards, spec) togetherWith slideOutOfContainer(towards, spec)
}
