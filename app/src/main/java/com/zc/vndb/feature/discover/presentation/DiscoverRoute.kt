package com.zc.vndb.feature.discover.presentation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zc.vndb.common.ui.util.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DiscoverRoute(
    onOpenNovel: (String) -> Unit,
    onOpenRelease: (String) -> Unit,
    onOpenBrowse: () -> Unit,
    onOpenTopRatedPage: () -> Unit,
    onOpenTrendingPage: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DiscoverViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    ObserveAsEvents(viewModel.effect) { effect ->
        when (effect) {
            is DiscoverEffect.NavigateToNovel -> onOpenNovel(effect.id)

            is DiscoverEffect.NavigateToRelease -> onOpenRelease(effect.id)

            DiscoverEffect.NavigateToBrowse -> onOpenBrowse()

            DiscoverEffect.NavigateToTopRated -> onOpenTopRatedPage()

            DiscoverEffect.NavigateToTrending -> onOpenTrendingPage()

            is DiscoverEffect.ShowError -> snackbarHostState.showSnackbar(
                effect.error.message.asString(context),
            )
        }
    }

    DiscoverScreen(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
        snackbarHostState = snackbarHostState,
    )
}
