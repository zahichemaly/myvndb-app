package com.zc.vndb.feature.userlist.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zc.vndb.common.ui.util.ObserveAsEvents
import com.zc.vndb.feature.userlist.presentation.model.ListEditorArgs
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/** The list editor, shown in a bottom sheet over Detail or Profile. */
@Composable
fun ListEditorRoute(
    novelId: String,
    novelTitle: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ListEditorViewModel = koinViewModel {
        parametersOf(ListEditorArgs(novelId, novelTitle))
    },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.effect) { effect ->
        when (effect) {
            ListEditorEffect.Close -> onClose()
        }
    }

    ListEditorScreen(state = state, onIntent = viewModel::onIntent, modifier = modifier)
}
