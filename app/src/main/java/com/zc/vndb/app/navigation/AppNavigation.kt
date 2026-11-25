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
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.defaultTransitionSpec
import com.zc.vndb.common.ui.navigation.BottomSheetSceneStrategy
import com.zc.vndb.common.ui.navigation.FullScreenNavKey
import com.zc.vndb.common.ui.navigation.NavigationState
import com.zc.vndb.common.ui.navigation.Navigator
import com.zc.vndb.common.ui.navigation.OverlayNavKey
import com.zc.vndb.common.ui.navigation.rememberNavigationState
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.feature.discover.presentation.navigation.DiscoverNavKey

/** App root: adaptive navigation suite (bar / rail / drawer) around the Navigation 3 back stack. */
@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navigationState = rememberNavigationState(
        startRoute = DiscoverNavKey,
        topLevelRoutes = remember { TopLevelDestination.entries.map { it.key }.toSet() },
    )
    val navigator = remember(navigationState) { Navigator(navigationState) }
    val itemColors = navigationItemColors()

    // Full-screen pages slide the navigation bar (or rail) away while they are on top.
    val currentKey = navigationState.backStacks[navigationState.topLevelRoute]
        ?.lastOrNull { it !is OverlayNavKey }
    val isNavigationHidden = currentKey is FullScreenNavKey
    val scaffoldState = rememberNavigationSuiteScaffoldState()
    LaunchedEffect(isNavigationHidden) {
        if (isNavigationHidden) scaffoldState.hide() else scaffoldState.show()
    }

    NavigationSuiteScaffold(
        modifier = modifier,
        state = scaffoldState,
        containerColor = Color.Transparent,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = VndbTheme.colors.surfaceInset,
            navigationRailContainerColor = VndbTheme.colors.surfaceInset,
            navigationDrawerContainerColor = VndbTheme.colors.surfaceInset,
        ),
        navigationSuiteItems = {
            TopLevelDestination.entries.forEach { destination ->
                val selected = navigationState.topLevelRoute == destination.key
                item(
                    selected = selected,
                    onClick = {
                        if (selected) {
                            navigator.reselect(
                                destination.key,
                            )
                        } else {
                            navigator.navigate(destination.key)
                        }
                    },
                    icon = { Icon(destination.icon, contentDescription = null) },
                    label = { Text(destination.label.asString()) },
                    colors = itemColors,
                )
            }
        },
    ) {
        AppNavDisplay(navigationState, navigator)
    }
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

/**
 * Pages slide: a new one comes in from the end edge as the current one leaves through the start
 * edge, and Back plays it in reverse (following the finger on predictive back). Tab switches keep
 * Navigation 3's default cross-fade.
 */
@Composable
private fun AppNavDisplay(navigationState: NavigationState, navigator: Navigator) {
    val bottomSheetStrategy = remember { BottomSheetSceneStrategy<NavKey>() }
    val entryProvider = remember(navigator) { appEntryProvider(navigator) }
    NavDisplay(
        entries = navigationState.toDecoratedEntries(entryProvider),
        onBack = navigator::goBack,
        // Sliding pages would otherwise be drawn over the navigation rail.
        modifier = Modifier.clipToBounds(),
        sceneStrategies = listOf(bottomSheetStrategy),
        transitionSpec = {
            if (navigationState.isTabSwitch) tabSwitchTransition() else slide(SlideDirection.Start)
        },
        popTransitionSpec = {
            if (navigationState.isTabSwitch) tabSwitchTransition() else slide(SlideDirection.End)
        },
        predictivePopTransitionSpec = { _ -> slide(SlideDirection.End, easing = LinearEasing) },
    )
}

private const val SLIDE_DURATION_MILLIS = 350

private val tabSwitchTransition = defaultTransitionSpec<NavKey>()

/** Both pages move [towards] the same edge: the new one slides in as the old one slides out. */
private fun AnimatedContentTransitionScope<Scene<NavKey>>.slide(
    towards: SlideDirection,
    easing: Easing = FastOutSlowInEasing,
): ContentTransform {
    val spec = tween<IntOffset>(SLIDE_DURATION_MILLIS, easing = easing)
    return slideIntoContainer(towards, spec) togetherWith slideOutOfContainer(towards, spec)
}
