package com.zc.vndb.feature.settings.presentation.home

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zc.vndb.common.ui.util.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsRoute(
    onNavigateBack: () -> Unit,
    onOpenTheme: () -> Unit,
    onOpenLicenses: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.effect) { effect ->
        when (effect) {
            SettingsEffect.NavigateBack -> onNavigateBack()

            SettingsEffect.OpenTheme -> onOpenTheme()

            SettingsEffect.OpenLicenses -> onOpenLicenses()

            is SettingsEffect.OpenUrl -> uriHandler.openUri(effect.url)

            is SettingsEffect.ShowMessage -> snackbarHostState.showSnackbar(
                effect.message.asString(context),
            )
        }
    }

    SettingsScreen(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
        snackbarHostState = snackbarHostState,
    )
}
