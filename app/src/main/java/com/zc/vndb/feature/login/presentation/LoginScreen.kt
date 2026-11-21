package com.zc.vndb.feature.login.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.component.apptopbar.AppTopBarView
import com.zc.vndb.common.ui.component.sectioncard.SectionCardView
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.login.presentation.component.loginstep.LoginStepView
import com.zc.vndb.feature.login.presentation.component.tokenfield.TokenFieldView
import kotlinx.coroutines.launch

private val MaxContentWidth = 560.dp
private val ProgressSize = 20.dp

@Composable
fun LoginScreen(state: LoginState, onIntent: (LoginIntent) -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = { AppTopBarView(onBack = { onIntent(LoginIntent.BackClicked) }) },
        containerColor = Color.Transparent,
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .imePadding(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = MaxContentWidth)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = VndbTheme.spacing.margin,
                        vertical = VndbTheme.spacing.md,
                    ),
                verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md),
            ) {
                Text(
                    text = UiText.StringResource(StringResource.login_title).asString(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                IntroCard()
                StepsHeader()
                StepCards(onIntent)
                TokenInput(state, onIntent)
                ConnectButton(state, onIntent)
                TextButton(
                    onClick = { onIntent(LoginIntent.ContinueAsGuestClicked) },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Text(
                        text = UiText.StringResource(
                            StringResource.login_continue_as_guest,
                        ).asString(),
                        color = VndbTheme.colors.accent,
                    )
                }
            }
        }
    }
}

@Composable
private fun IntroCard() {
    SectionCardView(modifier = Modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md)) {
            Icon(Icons.Outlined.Sync, contentDescription = null, tint = VndbTheme.colors.accent)
            Column(verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs)) {
                Text(
                    text = UiText.StringResource(StringResource.login_intro_title).asString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = UiText.StringResource(
                        StringResource.login_intro_body,
                        listOf(UiText.StringResource(StringResource.app_name)),
                    ).asString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun StepsHeader() {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = UiText.StringResource(StringResource.login_how_to).asString().uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = UiText.StringResource(StringResource.login_steps_count).asString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StepCards(onIntent: (LoginIntent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md)) {
        LoginStepView(
            number = 1,
            title = UiText.StringResource(StringResource.login_step1_title),
            body = UiText.StringResource(StringResource.login_step1_body),
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedButton(
                onClick = { onIntent(LoginIntent.OpenTokensPageClicked) },
                shape = MaterialTheme.shapes.small,
            ) {
                Text(
                    text = UiText.StringResource(StringResource.login_step1_action).asString(),
                    color = VndbTheme.colors.accent,
                )
                Icon(
                    Icons.AutoMirrored.Outlined.OpenInNew,
                    contentDescription = null,
                    tint = VndbTheme.colors.accent,
                    modifier = Modifier
                        .padding(start = VndbTheme.spacing.xs)
                        .size(ProgressSize),
                )
            }
        }
        LoginStepView(
            number = 2,
            title = UiText.StringResource(StringResource.login_step2_title),
            body = UiText.StringResource(
                StringResource.login_step2_body,
                listOf(UiText.StringResource(StringResource.app_name)),
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        LoginStepView(
            number = 3,
            title = UiText.StringResource(StringResource.login_step3_title),
            body = UiText.StringResource(
                StringResource.login_step3_body,
                listOf(UiText.StringResource(StringResource.login_token_example)),
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun TokenInput(state: LoginState, onIntent: (LoginIntent) -> Unit) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    TokenFieldView(
        token = state.token,
        isTokenVisible = state.isTokenVisible,
        error = state.error,
        isEnabled = !state.isSubmitting,
        onTokenChange = { onIntent(LoginIntent.TokenChanged(it)) },
        onVisibilityToggle = { onIntent(LoginIntent.TokenVisibilityToggled) },
        onPasteClick = {
            scope.launch {
                val pasted = clipboard.getClipEntry()?.clipData?.takeIf { it.itemCount > 0 }
                    ?.getItemAt(0)?.text?.toString()
                if (!pasted.isNullOrBlank()) onIntent(LoginIntent.TokenChanged(pasted.trim()))
            }
        },
        onDone = { onIntent(LoginIntent.SubmitClicked) },
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun ConnectButton(state: LoginState, onIntent: (LoginIntent) -> Unit) {
    Button(
        onClick = { onIntent(LoginIntent.SubmitClicked) },
        enabled = state.canSubmit,
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = VndbTheme.colors.accent,
            contentColor = VndbTheme.colors.onAccent,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (state.isSubmitting) {
            CircularProgressIndicator(
                color = VndbTheme.colors.onAccent,
                strokeWidth = 2.dp,
                modifier = Modifier.size(ProgressSize),
            )
        } else {
            Text(
                text = UiText.StringResource(StringResource.login_connect).asString(),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

private class LoginStateProvider : PreviewParameterProvider<LoginState> {
    override val values = sequenceOf(
        LoginState(),
        LoginState(token = "hsoo-ybws4-j8yb9-qxkw-5obay-px8to-bfyk", isSubmitting = true),
        LoginState(
            token = "nope",
            error = UiText.StringResource(StringResource.login_error_format),
        ),
    )
}

@AdaptivePreviews
@Composable
private fun LoginScreenPreview(@PreviewParameter(LoginStateProvider::class) state: LoginState) {
    VndbTheme {
        LoginScreen(state = state, onIntent = {})
    }
}
