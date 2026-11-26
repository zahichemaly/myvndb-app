package com.zc.vndb.feature.discover.presentation.home.component.quoteoftheday

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.theme.cardBackground
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.discover.presentation.home.component.quoteoftheday.model.QuoteOfTheDayUIModel
import com.zc.vndb.feature.discover.presentation.home.component.sectionheader.SectionHeaderView

/** "Quote of the Day": the quote and where it is from; tapping it opens that visual novel. */
@Composable
fun QuoteOfTheDayView(
    model: QuoteOfTheDayUIModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
    ) {
        SectionHeaderView(
            title = UiText.StringResource(StringResource.discover_quote_title),
            icon = Icons.Outlined.FormatQuote,
            modifier = Modifier.padding(top = VndbTheme.spacing.md),
        )
        val shape = MaterialTheme.shapes.medium
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .cardBackground()
                .border(VndbTheme.dimens.borderWidth, VndbTheme.colors.borderSubtle, shape)
                .clickable(role = Role.Button, onClick = onClick)
                .padding(VndbTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
        ) {
            Text(
                text = model.text,
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = model.attribution.asString(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@AdaptivePreviews
@Composable
private fun QuoteOfTheDayViewPreview() {
    VndbTheme {
        QuoteOfTheDayView(
            model = QuoteOfTheDayUIModel(
                text = "People die when they are killed.",
                attribution = UiText.StringValue("— Emiya Shirou, Fate/stay night"),
                novelId = "v11",
            ),
            onClick = {},
            modifier = Modifier.padding(VndbTheme.spacing.lg),
        )
    }
}
