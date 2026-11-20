package com.zc.vndb.feature.character.presentation.component.appearancerow

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.component.coverimage.CoverImageView
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.theme.cardBackground
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.character.presentation.component.appearancerow.model.AppearanceRowUIModel

private val ThumbnailWidth = 48.dp
private const val TITLE_MAX_LINES = 2

/** A VN the character is in: thumbnail, role, title, year · score, voice actor. */
@Composable
fun AppearanceRowView(
    model: AppearanceRowUIModel,
    onClick: () -> Unit,
    onVoiceActorClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .cardBackground(VndbTheme.colors.surfaceInset)
            .clickable(onClick = onClick)
            .padding(VndbTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CoverImageView(
            url = model.thumbnailUrl,
            isSensitive = model.isCoverSensitive,
            contentDescription = model.title,
            modifier = Modifier
                .width(ThumbnailWidth)
                .aspectRatio(VndbTheme.dimens.coverAspectRatio),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = model.roleLabel.asString(),
                style = MaterialTheme.typography.labelSmall,
                color = if (model.isMajorRole) VndbTheme.colors.score else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = model.title.asString(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = TITLE_MAX_LINES,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = listOfNotNull(model.yearLabel.asString(), model.ratingLabel?.asString())
                    .joinToString(
                        " ${UiText.StringResource(StringResource.separator_dot).asString()} ",
                    ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
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
                    modifier = if (voiceActorId == null) {
                        Modifier
                    } else {
                        Modifier.clickable(role = Role.Button) { onVoiceActorClick(voiceActorId) }
                    },
                )
            }
        }
    }
}

@AdaptivePreviews
@Composable
private fun AppearanceRowViewPreview() {
    VndbTheme {
        AppearanceRowView(
            model = AppearanceRowUIModel(
                novelId = "v2002",
                title = UiText.StringValue("STEINS;GATE"),
                yearLabel = UiText.StringValue("2009"),
                roleLabel = UiText.StringValue("PRIMARY"),
                isMajorRole = true,
                voiceActorLabel = UiText.StringValue("CV: Imai Asami"),
                voiceActorId = "s1",
                ratingLabel = UiText.StringValue("9.02"),
                thumbnailUrl = null,
                isCoverSensitive = false,
            ),
            onClick = {},
            onVoiceActorClick = {},
            modifier = Modifier.fillMaxWidth().padding(VndbTheme.spacing.lg),
        )
    }
}
