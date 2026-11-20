package com.zc.vndb.feature.detail.presentation.component.editionrow

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.theme.cardBackground
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.detail.domain.model.PlatformFamily
import com.zc.vndb.feature.detail.presentation.component.editionrow.mapper.toColors
import com.zc.vndb.feature.detail.presentation.component.editionrow.mapper.toImageVector
import com.zc.vndb.feature.detail.presentation.component.editionrow.mapper.toPlatformIcon
import com.zc.vndb.feature.detail.presentation.component.editionrow.model.EditionRowUIModel
import com.zc.vndb.feature.detail.presentation.component.editionrow.model.PlatformIcon
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private val IconBoxSize = 36.dp
private val IconSize = 20.dp
private val PlatformIconSize = 16.dp

/** One edition: family icon, title, "Windows • English, Japanese • 2016-09-08", platform logos. */
@Composable
fun EditionRowView(model: EditionRowUIModel, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .cardBackground(VndbTheme.colors.surfaceInset)
            .clickable(onClick = onClick)
            .padding(VndbTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(IconBoxSize)
                .background(VndbTheme.colors.surfaceElevated, MaterialTheme.shapes.small),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = model.family.toPlatformIcon().toImageVector(),
                contentDescription = null,
                tint = model.family.toColors().content,
                modifier = Modifier.size(IconSize),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = model.title.asString(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = details(model),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        PlatformIcons(icons = model.platformIcons, moreLabel = model.morePlatformsLabel)
    }
}

/** Platform logos; decorative, since the details line already names the platforms. */
@Composable
private fun PlatformIcons(icons: ImmutableList<PlatformIcon>, moreLabel: UiText?) {
    if (icons.isEmpty()) return
    Row(
        modifier = Modifier.clearAndSetSemantics {},
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icons.forEach { icon ->
            Icon(
                imageVector = icon.toImageVector(),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(PlatformIconSize),
            )
        }
        moreLabel?.let {
            Text(
                text = it.asString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun details(model: EditionRowUIModel): String {
    val separator = " ${UiText.StringResource(StringResource.separator_dot).asString()} "
    return listOfNotNull(
        model.platformLabel.asString(),
        model.languagesLabel?.asString(),
        model.dateLabel.asString(),
    )
        .joinToString(separator)
}

@AdaptivePreviews
@Composable
private fun EditionRowViewPreview() {
    VndbTheme {
        EditionRowView(
            model = EditionRowUIModel(
                key = "r1-win",
                releaseId = "r1",
                title = UiText.StringValue("Steins;Gate - Steam Edition"),
                platformLabel = UiText.StringValue("Windows, Switch"),
                languagesLabel = UiText.StringValue("English, Japanese"),
                dateLabel = UiText.StringValue("2016-09-08"),
                family = PlatformFamily.Pc,
                platformIcons = persistentListOf(PlatformIcon.Windows, PlatformIcon.Nintendo),
                morePlatformsLabel = null,
            ),
            onClick = {},
            modifier = Modifier.fillMaxWidth().padding(VndbTheme.spacing.lg),
        )
    }
}
