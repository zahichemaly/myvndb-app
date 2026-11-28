package com.zc.vndb.feature.settings.presentation.licenses

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.zc.vndb.feature.settings.presentation.licenses.mapper.groupedByProject

/**
 * The Licenses page, opened from Settings. Its list is static data the AboutLibraries plugin bundles
 * at build time (`res/raw/aboutlibraries.json`), so it is read here rather than through a ViewModel
 * (see the tech-stack skill), and grouped into one row per project once, when it loads.
 */
@Composable
fun LicensesRoute(onNavigateBack: () -> Unit, modifier: Modifier = Modifier) {
    val libraries by produceLibraries()
    val projects = remember(libraries) { libraries?.groupedByProject() }
    LicensesScreen(libraries = projects, onBack = onNavigateBack, modifier = modifier)
}
