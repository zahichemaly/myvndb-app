package com.zc.vndb.feature.settings.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zc.vndb.common.ui.navigation.Navigator
import com.zc.vndb.feature.settings.presentation.home.SettingsRoute
import com.zc.vndb.feature.settings.presentation.licenses.LicensesRoute

fun EntryProviderScope<NavKey>.settingsEntry(
    onNavigateBack: () -> Unit,
    onOpenTheme: () -> Unit,
    onOpenLicenses: () -> Unit,
) {
    entry<SettingsNavKey> {
        SettingsRoute(
            onNavigateBack = onNavigateBack,
            onOpenTheme = onOpenTheme,
            onOpenLicenses = onOpenLicenses,
        )
    }
}

/** The open-source licenses, opened from Settings' About section. */
fun EntryProviderScope<NavKey>.licensesEntry(onNavigateBack: () -> Unit) {
    entry<LicensesNavKey> {
        LicensesRoute(onNavigateBack = onNavigateBack)
    }
}

/** Opens Settings. */
fun Navigator.navigateToSettings() = navigate(SettingsNavKey)

/** Opens the open-source licenses. */
fun Navigator.navigateToLicenses() = navigate(LicensesNavKey)
