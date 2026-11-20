package com.zc.vndb.feature.login.presentation.component.loginstep

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zc.vndb.common.ui.component.sectioncard.SectionCardView
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews

/** One numbered step of the log-in instructions, with optional [action] content below. */
@Composable
fun LoginStepView(
    number: Int,
    title: UiText,
    body: UiText,
    modifier: Modifier = Modifier,
    action: (@Composable ColumnScope.() -> Unit)? = null,
) {
    SectionCardView(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md)) {
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Column(verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs)) {
                Text(
                    text = title.asString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = body.asString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                action?.invoke(this)
            }
        }
    }
}

@AdaptivePreviews
@Composable
private fun LoginStepViewPreview() {
    VndbTheme {
        LoginStepView(
            number = 1,
            title = UiText.StringValue("Create a token on vndb.org"),
            body = UiText.StringValue("Log in on vndb.org, then open My Profile → Applications."),
        )
    }
}
