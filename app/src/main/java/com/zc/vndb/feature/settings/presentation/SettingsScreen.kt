package com.zc.vndb.feature.settings.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Explicit
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.domain.model.AppSettings
import com.zc.vndb.common.ui.component.apptopbar.AppTopBarView
import com.zc.vndb.common.ui.component.confirmdialog.ConfirmDialogView
import com.zc.vndb.common.ui.component.languagesdialog.LanguagesDialogView
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.settings.presentation.component.choicedialog.ChoiceDialogView
import com.zc.vndb.feature.settings.presentation.component.settingrow.SettingRowView
import com.zc.vndb.feature.settings.presentation.component.settingrow.SettingSwitchRowView
import com.zc.vndb.feature.settings.presentation.component.settingrow.SettingValueView
import com.zc.vndb.feature.settings.presentation.model.SettingOptionUIModel
import com.zc.vndb.feature.settings.presentation.model.SettingsDialog
import kotlinx.collections.immutable.persistentListOf

private val MaxContentWidth = 640.dp
private val SmallProgressSize = 20.dp

/** Callbacks built once from `onIntent`. */
@Immutable
private class SettingsActions(val onIntent: (SettingsIntent) -> Unit) {
    fun open(dialog: SettingsDialog) = onIntent(SettingsIntent.DialogOpened(dialog))
}

@Composable
fun SettingsScreen(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val actions = remember(onIntent) { SettingsActions(onIntent) }
    Scaffold(
        modifier = modifier,
        topBar = { AppTopBarView(onBack = { onIntent(SettingsIntent.BackClicked) }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent,
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            if (!state.isLoaded) {
                CircularProgressIndicator(
                    color = VndbTheme.colors.accent,
                    modifier = Modifier.align(Alignment.Center),
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .widthIn(max = MaxContentWidth)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = VndbTheme.spacing.margin,
                        vertical = VndbTheme.spacing.sm,
                    ),
                    verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
                ) {
                    item(key = "title", contentType = "title") {
                        Text(
                            text = UiText.StringResource(StringResource.settings_title).asString(),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    displaySection(state, actions)
                    languagesSection(state, actions)
                    spoilerSection(state, actions)
                    adultSection(state, actions)
                    storageSection(state, actions)
                    aboutSection(state, actions)
                }
            }
        }
    }
    SettingsDialogs(state, onIntent)
}

private fun LazyListScope.sectionHeader(key: String, title: UiText) {
    item(key = "header-$key", contentType = "header") {
        Text(
            text = title.asString().uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = VndbTheme.spacing.md),
        )
    }
}

private fun LazyListScope.displaySection(state: SettingsState, actions: SettingsActions) {
    sectionHeader("display", UiText.StringResource(StringResource.settings_section_display))
    item(key = "theme", contentType = "row") {
        SettingRowView(
            icon = Icons.Outlined.Palette,
            title = UiText.StringResource(StringResource.settings_theme),
            body = UiText.StringResource(StringResource.settings_theme_body),
            onClick = { actions.onIntent(SettingsIntent.ThemeClicked) },
            modifier = Modifier.fillMaxWidth(),
        ) { state.themeLabel?.let { SettingValueView(it) } }
    }
    item(key = "title-language", contentType = "row") {
        SettingRowView(
            icon = Icons.Outlined.Translate,
            title = UiText.StringResource(StringResource.settings_title_language),
            body = UiText.StringResource(StringResource.settings_title_language_body),
            onClick = { actions.open(SettingsDialog.TitleLanguage) },
            modifier = Modifier.fillMaxWidth(),
        ) { state.titleLanguageLabel?.let { SettingValueView(it) } }
    }
    item(key = "show-all-tags", contentType = "switch") {
        SettingSwitchRowView(
            icon = Icons.Outlined.Sell,
            title = UiText.StringResource(StringResource.settings_show_all_tags),
            body = UiText.StringResource(StringResource.settings_show_all_tags_body),
            isOn = state.showAllTags,
            onChange = { actions.onIntent(SettingsIntent.ShowAllTagsChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private fun LazyListScope.spoilerSection(state: SettingsState, actions: SettingsActions) {
    sectionHeader("spoilers", UiText.StringResource(StringResource.settings_section_spoilers))
    item(key = "spoiler-level", contentType = "row") {
        SettingRowView(
            icon = Icons.Outlined.VisibilityOff,
            title = UiText.StringResource(StringResource.settings_spoiler_level),
            body = UiText.StringResource(StringResource.settings_spoiler_level_body),
            onClick = { actions.open(SettingsDialog.SpoilerLevel) },
            modifier = Modifier.fillMaxWidth(),
        ) { state.spoilerLabel?.let { SettingValueView(it) } }
    }
    item(key = "spoil-if-finished", contentType = "switch") {
        SettingSwitchRowView(
            icon = Icons.Outlined.TaskAlt,
            title = UiText.StringResource(StringResource.settings_spoil_if_finished),
            body = UiText.StringResource(
                if (state.isSignedIn) {
                    StringResource.settings_spoil_if_finished_body
                } else {
                    StringResource.settings_spoil_if_finished_guest
                },
            ),
            isOn = state.spoilIfFinished && state.isSignedIn,
            isEnabled = state.isSignedIn,
            onChange = { actions.onIntent(SettingsIntent.SpoilIfFinishedChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private fun LazyListScope.adultSection(state: SettingsState, actions: SettingsActions) {
    sectionHeader("adult", UiText.StringResource(StringResource.settings_section_adult))
    if (state.canShowSensitiveContent) {
        item(key = "show-sensitive", contentType = "switch") {
            SettingSwitchRowView(
                icon = Icons.Outlined.Visibility,
                title = UiText.StringResource(StringResource.settings_show_sensitive),
                body = UiText.StringResource(StringResource.settings_show_sensitive_body),
                isOn = state.showSensitiveContent,
                onChange = { actions.onIntent(SettingsIntent.ShowSensitiveChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
    item(key = "include-adult", contentType = "switch") {
        SettingSwitchRowView(
            icon = Icons.Outlined.Explicit,
            title = UiText.StringResource(StringResource.settings_include_adult),
            body = UiText.StringResource(StringResource.settings_include_adult_body),
            isOn = state.includeAdultInBrowse,
            onChange = { actions.onIntent(SettingsIntent.IncludeAdultChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private fun LazyListScope.languagesSection(state: SettingsState, actions: SettingsActions) {
    sectionHeader("languages", UiText.StringResource(StringResource.settings_section_languages))
    item(key = "card-languages", contentType = "row") {
        SettingRowView(
            icon = Icons.Outlined.Flag,
            title = UiText.StringResource(StringResource.settings_card_languages),
            body = UiText.StringResource(StringResource.settings_card_languages_body),
            onClick = { actions.open(SettingsDialog.CardLanguages) },
            modifier = Modifier.fillMaxWidth(),
        ) { state.cardLanguagesLabel?.let { SettingValueView(it) } }
    }
    item(key = "trending-languages", contentType = "row") {
        SettingRowView(
            icon = Icons.Outlined.Language,
            title = UiText.StringResource(StringResource.settings_trending_languages),
            body = UiText.StringResource(StringResource.settings_trending_languages_body),
            onClick = { actions.open(SettingsDialog.TrendingLanguages) },
            modifier = Modifier.fillMaxWidth(),
        ) { state.languagesLabel?.let { SettingValueView(it) } }
    }
}

private fun LazyListScope.storageSection(state: SettingsState, actions: SettingsActions) {
    sectionHeader("storage", UiText.StringResource(StringResource.settings_section_storage))
    item(key = "image-cache", contentType = "row") {
        SettingRowView(
            icon = Icons.Outlined.Image,
            title = UiText.StringResource(StringResource.settings_image_cache),
            body = state.imageCacheLabel ?: UiText.StringResource(
                StringResource.settings_image_cache_body,
            ),
            onClick = { actions.open(SettingsDialog.ClearImageCache) },
            isEnabled = !state.isClearingCache,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (state.isClearingCache) {
                CircularProgressIndicator(
                    color = VndbTheme.colors.accent,
                    modifier = Modifier.size(SmallProgressSize),
                )
            } else {
                SettingValueView(UiText.StringResource(StringResource.settings_clear))
            }
        }
    }
}

private fun LazyListScope.aboutSection(state: SettingsState, actions: SettingsActions) {
    sectionHeader("about", UiText.StringResource(StringResource.settings_section_about))
    if (state.showSourceCodeSection) {
        item(key = "source", contentType = "link") {
            LinkRow(
                icon = Icons.Outlined.Code,
                title = UiText.StringResource(StringResource.settings_about_source),
                body = null,
                onClick = {
                    actions.onIntent(
                        SettingsIntent.LinkClicked(SettingsViewModel.SOURCE_URL),
                    )
                },
            )
        }
    }
    item(key = "privacy", contentType = "link") {
        LinkRow(
            icon = Icons.Outlined.Lock,
            title = UiText.StringResource(StringResource.settings_about_privacy),
            body = null,
            onClick = {
                actions.onIntent(
                    SettingsIntent.LinkClicked(SettingsViewModel.PRIVACY_URL),
                )
            },
        )
    }
    logOutRow(state, actions)
    state.versionLabel?.let { version ->
        item(key = "version", contentType = "footer") {
            Text(
                text = version.asString(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = VndbTheme.spacing.lg),
            )
        }
    }
}

/** Signed-in users only, last before the version: Log out (asks first), with the account under it. */
private fun LazyListScope.logOutRow(state: SettingsState, actions: SettingsActions) {
    val account = state.accountLabel ?: return
    item(key = "log-out", contentType = "row") {
        SettingRowView(
            icon = Icons.AutoMirrored.Outlined.Logout,
            title = UiText.StringResource(StringResource.settings_log_out),
            body = account,
            isDestructive = true,
            onClick = { actions.open(SettingsDialog.ConfirmLogOut) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = VndbTheme.spacing.lg),
        )
    }
}

@Composable
private fun LinkRow(icon: ImageVector, title: UiText, body: UiText?, onClick: () -> Unit) {
    SettingRowView(
        icon = icon,
        title = title,
        body = body,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Icon(
            Icons.AutoMirrored.Outlined.OpenInNew,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** New & Trending: any number of languages, but at least one. */
@Composable
private fun TrendingLanguagesDialog(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    dismiss: () -> Unit,
) {
    LanguagesDialogView(
        title = UiText.StringResource(StringResource.settings_trending_languages),
        hint = UiText.StringResource(StringResource.settings_languages_hint),
        options = state.languages,
        selected = state.trendingLanguages.toSet(),
        minSelected = 1,
        onConfirm = { onIntent(SettingsIntent.LanguagesConfirmed(it)) },
        onDismiss = dismiss,
    )
}

/** Languages on cards: none to [AppSettings.MAX_CARD_LANGUAGES]; none turns the pills off. */
@Composable
private fun CardLanguagesDialog(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    dismiss: () -> Unit,
) {
    LanguagesDialogView(
        title = UiText.StringResource(StringResource.settings_card_languages),
        hint = UiText.StringResource(StringResource.settings_card_languages_hint),
        options = state.languages,
        selected = state.cardLanguages.toSet(),
        maxSelected = AppSettings.MAX_CARD_LANGUAGES,
        onConfirm = { onIntent(SettingsIntent.CardLanguagesConfirmed(it)) },
        onDismiss = dismiss,
    )
}

@Composable
private fun SettingsDialogs(state: SettingsState, onIntent: (SettingsIntent) -> Unit) {
    val dismiss = { onIntent(SettingsIntent.DialogDismissed) }
    when (state.dialog) {
        SettingsDialog.TitleLanguage -> ChoiceDialogView(
            title = UiText.StringResource(StringResource.settings_title_language),
            options = state.titleLanguageOptions,
            onSelect = {
                onIntent(
                    SettingsIntent.OptionSelected(SettingsDialog.TitleLanguage, it),
                )
            },
            onDismiss = dismiss,
        )

        SettingsDialog.SpoilerLevel -> ChoiceDialogView(
            title = UiText.StringResource(StringResource.settings_spoiler_level),
            options = state.spoilerOptions,
            onSelect = { onIntent(SettingsIntent.OptionSelected(SettingsDialog.SpoilerLevel, it)) },
            onDismiss = dismiss,
        )

        SettingsDialog.TrendingLanguages -> TrendingLanguagesDialog(state, onIntent, dismiss)

        SettingsDialog.CardLanguages -> CardLanguagesDialog(state, onIntent, dismiss)

        SettingsDialog.ConfirmSensitive -> ConfirmDialogView(
            title = UiText.StringResource(StringResource.settings_confirm_adult_title),
            body = UiText.StringResource(StringResource.settings_confirm_adult_body),
            confirm = UiText.StringResource(StringResource.settings_confirm_adult_action),
            onConfirm = { onIntent(SettingsIntent.SensitiveConfirmed) },
            onDismiss = dismiss,
        )

        SettingsDialog.ClearImageCache -> ConfirmDialogView(
            title = UiText.StringResource(StringResource.settings_clear_cache_title),
            body = UiText.StringResource(StringResource.settings_clear_cache_body),
            confirm = UiText.StringResource(StringResource.settings_clear),
            onConfirm = { onIntent(SettingsIntent.ClearCacheConfirmed) },
            onDismiss = dismiss,
        )

        SettingsDialog.ConfirmLogOut -> ConfirmDialogView(
            title = UiText.StringResource(StringResource.settings_log_out_title),
            body = UiText.StringResource(StringResource.settings_log_out_body),
            confirm = UiText.StringResource(StringResource.settings_log_out),
            onConfirm = { onIntent(SettingsIntent.LogOutConfirmed) },
            onDismiss = dismiss,
        )

        null -> Unit
    }
}

private val previewState = SettingsState(
    isLoaded = true,
    titleLanguageLabel = UiText.StringValue("Romanized"),
    spoilerLabel = UiText.StringValue("None"),
    isSignedIn = true,
    accountLabel = UiText.StringValue("Yorhel · read & edit access"),
    languagesLabel = UiText.StringValue("English, Japanese"),
    imageCacheLabel = UiText.StringValue("142 MB"),
    canShowSensitiveContent = true,
    versionLabel = UiText.StringResource(
        StringResource.settings_version,
        listOf(UiText.StringResource(StringResource.app_name), "1.0.0", 1),
    ),
    titleLanguageOptions = persistentListOf(
        SettingOptionUIModel("Romanized", UiText.StringValue("Romanized"), isSelected = true),
        SettingOptionUIModel("Original", UiText.StringValue("Japanese"), isSelected = false),
    ),
)

private class SettingsStateProvider : PreviewParameterProvider<SettingsState> {
    override val values = sequenceOf(
        previewState,
        previewState.copy(isSignedIn = false, showSensitiveContent = true, isClearingCache = true),
        previewState.copy(dialog = SettingsDialog.TitleLanguage),
    )
}

@AdaptivePreviews
@Composable
private fun SettingsScreenPreview(
    @PreviewParameter(
        SettingsStateProvider::class,
    ) state: SettingsState,
) {
    VndbTheme {
        SettingsScreen(state = state, onIntent = {})
    }
}
