package com.zc.vndb.feature.detail.presentation.component.charactercard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.component.coverimage.CoverImageView
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.detail.presentation.component.charactercard.model.CharacterCardUIModel

private val CardWidth = 132.dp

/** Character portrait with role, name, original name and voice actor; opens the character. */
@Composable
fun CharacterCardView(
    model: CharacterCardUIModel,
    onClick: () -> Unit,
    onVoiceActorClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(CardWidth)
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs / 2),
    ) {
        CoverImageView(
            url = model.imageUrl,
            isSensitive = model.isImageSensitive,
            contentDescription = model.name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(VndbTheme.dimens.coverAspectRatio),
        )
        Text(
            text = model.roleLabel.asString(),
            style = MaterialTheme.typography.labelSmall,
            color = if (model.isMain) VndbTheme.colors.score else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = VndbTheme.spacing.xs),
        )
        Text(
            text = model.name.asString(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        model.originalName?.let { original ->
            Text(
                text = original.asString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        model.voiceActorLabel?.let { cv ->
            val voiceActorId = model.voiceActorId
            Text(
                text = cv.asString(),
                style = MaterialTheme.typography.labelSmall,
                color = if (voiceActorId == null) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    VndbTheme.colors.accent
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = if (voiceActorId == null) {
                    Modifier
                } else {
                    Modifier.clickable(role = Role.Button) { onVoiceActorClick(voiceActorId) }
                },
            )
        }
    }
}

@AdaptivePreviews
@Composable
private fun CharacterCardViewPreview() {
    VndbTheme {
        CharacterCardView(
            model = CharacterCardUIModel(
                id = "c6487",
                name = UiText.StringValue("Makise Kurisu"),
                originalName = UiText.StringValue("牧瀬 紅莉栖"),
                roleLabel = UiText.StringValue("ROLE: PRIMARY"),
                isMain = false,
                voiceActorLabel = UiText.StringValue("CV: Imai Asami"),
                voiceActorId = "s1",
                imageUrl = null,
                isImageSensitive = false,
            ),
            onClick = {},
            onVoiceActorClick = {},
            modifier = Modifier.padding(VndbTheme.spacing.lg),
        )
    }
}
