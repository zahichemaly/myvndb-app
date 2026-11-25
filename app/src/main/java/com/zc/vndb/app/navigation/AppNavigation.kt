package com.zc.vndb.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import com.zc.vndb.common.ui.navigation.BottomSheetSceneStrategy
import com.zc.vndb.common.ui.navigation.LocalTabToolbarPadding
import com.zc.vndb.common.ui.navigation.Navigator
import com.zc.vndb.common.ui.navigation.rememberNavigationState
import com.zc.vndb.feature.discover.presentation.navigation.DiscoverNavKey

/**
 * App root, in two levels. The tabs sit under a floating tab toolbar and cross-fade (Navigation
 * 3's default). Pages open over the tabs, toolbar included: a new one slides in from the end edge
 * as what is below leaves through the start edge, and Back plays it in reverse (following the
 * finger on predictive back).
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
            TabsShell(
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

/**
 * The tabs, with the floating tab toolbar over them at the bottom center, above the system
 * navigation bar. Tab screens read [LocalTabToolbarPadding] to keep their content clear of it.
 */
@Composable
private fun TabsShell(
    selectedTab: NavKey,
    onSelectTab: (NavKey) -> Unit,
    content: @Composable () -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        CompositionLocalProvider(
            LocalTabToolbarPadding provides
                    FloatingToolbarDefaults.ContainerSize + FloatingToolbarDefaults.ScreenOffset,
            content = content,
        )
        TabToolbar(
            selectedTab = selectedTab,
            onSelectTab = onSelectTab,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = FloatingToolbarDefaults.ScreenOffset),
        )
    }
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
