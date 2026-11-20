package com.zc.vndb.feature.detail.presentation.component.quotecard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import com.zc.vndb.common.ui.component.chip.TagChipView
import com.zc.vndb.common.ui.component.sectioncard.SectionCardView
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.detail.presentation.model.QuoteUIModel

/** A quote, its character (tap opens them) and its vote score. */
@Composable
fun QuoteCardView(
    model: QuoteUIModel,
    onCharacterClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionCardView(modifier = modifier) {
        Text(
            text = "“${model.text.asString()}”",
            style = MaterialTheme.typography.bodyLarge,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            model.characterName?.let { name ->
                Text(
                    text = "— ${name.asString()}",
                    style = MaterialTheme.typography.labelLarge,
                    // Accent only when the name opens the character.
                    color = if (model.characterId != null) {
                        VndbTheme.colors.accent
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .then(
                            model.characterId?.let { id ->
                                Modifier.clickable {
                                    onCharacterClick(
                                        id,
                                    )
                                }
                            } ?: Modifier,
                        )
                        .padding(vertical = VndbTheme.spacing.xs),
                )
            }
            Spacer(Modifier.weight(1f))
            TagChipView(label = model.scoreLabel, colors = VndbTheme.colors.spoilerMinor)
        }
    }
}

@AdaptivePreviews
@Composable
private fun QuoteCardViewPreview() {
    VndbTheme {
        QuoteCardView(
            model = QuoteUIModel(
                id = "q467",
                text = UiText.StringValue("I didn't remember men's names on principle."),
                characterId = "c17297",
                characterName = UiText.StringValue("Narita Shinri"),
                scoreLabel = UiText.StringValue("+28"),
            ),
            onCharacterClick = {},
            modifier = Modifier.fillMaxWidth().padding(VndbTheme.spacing.lg),
        )
    }
}
