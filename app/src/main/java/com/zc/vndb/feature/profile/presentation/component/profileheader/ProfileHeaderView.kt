package com.zc.vndb.feature.profile.presentation.component.profileheader

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.theme.cardBackground
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.profile.presentation.component.profileheader.model.ProfileHeaderUIModel

/** ProfileSkeletonView draws the same size, so the page does not jump when the profile arrives. */
private val AvatarSize = 36.dp

/** The signed-in user's card, kept slim: a small avatar initial beside the name and play-time votes. */
@Composable
fun ProfileHeaderView(model: ProfileHeaderUIModel, modifier: Modifier = Modifier) {
    val shape = MaterialTheme.shapes.large
    Column(
        modifier = modifier
            .clip(shape)
            .cardBackground()
            .border(VndbTheme.dimens.borderWidth, VndbTheme.colors.borderSubtle, shape)
            .padding(horizontal = VndbTheme.spacing.md, vertical = VndbTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(AvatarSize)
                    .clip(CircleShape)
                    .background(VndbTheme.colors.chipActiveContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = model.initial.asString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = VndbTheme.colors.accent,
                )
            }
            Column {
                Text(
                    text = model.username.asString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                model.statsLabel?.let { stats ->
                    Text(
                        text = stats.asString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        model.privateNote?.let { note ->
            Text(
                text = note.asString(),
                style = MaterialTheme.typography.bodySmall,
                color = VndbTheme.colors.spoilerMinor.content,
            )
        }
    }
}

@AdaptivePreviews
@Composable
private fun ProfileHeaderViewPreview() {
    VndbTheme {
        ProfileHeaderView(
            model = ProfileHeaderUIModel(
                username = UiText.StringValue("Yorhel"),
                initial = UiText.StringValue("Y"),
                statsLabel = UiText.StringValue("10 play-time votes · 171h logged"),
                privateNote = UiText.StringValue("Private lists are hidden."),
            ),
            modifier = Modifier.padding(VndbTheme.spacing.lg),
        )
    }
}
