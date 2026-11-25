package com.zc.vndb.feature.theme.presentation

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.domain.model.AppTheme
import com.zc.vndb.common.ui.component.apptopbar.AppTopBarView
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.theme.presentation.component.themepreview.ThemePreviewView
import com.zc.vndb.feature.theme.presentation.mapper.themeState

private val MaxContentWidth = 640.dp
private val MinPreviewWidth = 100.dp

/** The themes follow the title in the grid. */
private const val FIRST_THEME_INDEX = 1

@Composable
fun ThemeScreen(state: ThemeState, onIntent: (ThemeIntent) -> Unit, modifier: Modifier = Modifier) {
    val gridState = rememberLazyGridState()
    val selectedIndex = state.themes.indexOfFirst { it.isSelected }
    var hasShownSelection by rememberSaveable { mutableStateOf(false) }
    // Opens on the selected theme once the themes have loaded; a theme picked later is in view.
    LaunchedEffect(selectedIndex) {
        if (selectedIndex < 0 || hasShownSelection) return@LaunchedEffect
        gridState.centerItem(FIRST_THEME_INDEX + selectedIndex)
        hasShownSelection = true
    }
    Scaffold(
        modifier = modifier,
        topBar = { AppTopBarView(onBack = { onIntent(ThemeIntent.BackClicked) }) },
        containerColor = Color.Transparent,
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(MinPreviewWidth),
                state = gridState,
                modifier = Modifier
                    .widthIn(max = MaxContentWidth)
                    .fillMaxSize(),
                contentPadding = PaddingValues(
                    horizontal = VndbTheme.spacing.margin,
                    vertical = VndbTheme.spacing.sm,
                ),
                horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.lg),
            ) {
                item(key = "title", span = { GridItemSpan(maxLineSpan) }, contentType = "title") {
                    Text(
                        text = UiText.StringResource(StringResource.theme_title).asString(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                items(state.themes, key = { it.theme.name }, contentType = { "theme" }) { model ->
                    ThemePreviewView(
                        model = model,
                        onSelect = { onIntent(ThemeIntent.ThemeSelected(model.theme)) },
                    )
                }
            }
        }
    }
}

/** Moves item [index] to the middle of the grid, or as close as the grid can scroll. */
private suspend fun LazyGridState.centerItem(index: Int) {
    scrollToItem(index)
    val item = layoutInfo.visibleItemsInfo.firstOrNull { it.index == index } ?: return
    scrollBy(-(layoutInfo.viewportSize.height - item.size.height) / 2f)
}

@AdaptivePreviews
@Composable
private fun ThemeScreenPreview() {
    VndbTheme {
        ThemeScreen(state = themeState(AppTheme.AngelicSerenade), onIntent = {})
    }
}
