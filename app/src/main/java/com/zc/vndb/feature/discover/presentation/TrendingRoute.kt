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
fun TrendingRoute(
    onNavigateBack: () -> Unit,
    onOpenNovel: (String) -> Unit,
    onOpenRelease: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TrendingViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    ObserveAsEvents(viewModel.effect) { effect ->
        when (effect) {
            TrendingEffect.NavigateBack -> onNavigateBack()

            is TrendingEffect.NavigateToNovel -> onOpenNovel(effect.id)

            is TrendingEffect.NavigateToRelease -> onOpenRelease(effect.id)

            is TrendingEffect.ShowError -> snackbarHostState.showSnackbar(
                effect.error.message.asString(context),
            )
        }
    }

    TrendingScreen(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
        snackbarHostState = snackbarHostState,
    )
}
