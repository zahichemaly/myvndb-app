package com.zc.vndb.feature.release.presentation.component.releasehero

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.component.chip.TagChipView
import com.zc.vndb.common.ui.component.coverimage.CoverImageView
import com.zc.vndb.common.ui.component.factline.FactLineView
import com.zc.vndb.common.ui.component.factline.model.FactUIModel
import com.zc.vndb.common.ui.component.sectioncard.SectionCardView
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.release.presentation.component.releasehero.model.ReleaseFlagTone
import com.zc.vndb.feature.release.presentation.component.releasehero.model.ReleaseFlagUIModel
import com.zc.vndb.feature.release.presentation.component.releasehero.model.ReleaseHeroUIModel
import kotlinx.collections.immutable.persistentListOf

private val PackageWidth = 112.dp

/** Package art, titles, release flags, then the release's fact lines. */
@Composable
fun ReleaseHeroView(model: ReleaseHeroUIModel, modifier: Modifier = Modifier) {
    SectionCardView(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md)) {
            CoverImageView(
                url = model.imageUrl,
                isSensitive = model.isImageSensitive,
                contentDescription = model.title,
                modifier = Modifier
                    .width(PackageWidth)
                    .aspectRatio(VndbTheme.dimens.coverAspectRatio),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
            ) {
                Text(
                    text = model.title.asString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                model.altTitle?.let { alt ->
                    Text(
                        text = alt.asString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                FlowRow(
                    modifier = Modifier.padding(top = VndbTheme.spacing.xs),
                    horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
                    verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
                ) {
                    model.flags.forEach { flag ->
                        TagChipView(
                            label = flag.label,
                            colors = flag.tone.colors(),
                        )
                    }
                }
            }
        }
        model.facts.forEach { fact -> FactLineView(fact) }
    }
}

@Composable
private fun ReleaseFlagTone.colors(): TintedColors = when (this) {
    ReleaseFlagTone.Neutral -> VndbTheme.colors.chipNeutral
    ReleaseFlagTone.Accent -> VndbTheme.colors.statusPlaying
    ReleaseFlagTone.Caution -> VndbTheme.colors.spoilerMinor
    ReleaseFlagTone.Safe -> VndbTheme.colors.spoilerNone
    ReleaseFlagTone.Adult -> VndbTheme.colors.tagNsfw
}

@AdaptivePreviews
@Composable
private fun ReleaseHeroViewPreview() {
    VndbTheme {
        ReleaseHeroView(
            model = ReleaseHeroUIModel(
                title = UiText.StringValue("Steins;Gate"),
                altTitle = null,
                imageUrl = null,
                isImageSensitive = false,
                flags = persistentListOf(
                    ReleaseFlagUIModel(UiText.StringValue("Official"), ReleaseFlagTone.Accent),
                    ReleaseFlagUIModel(UiText.StringValue("15+"), ReleaseFlagTone.Neutral),
                ),
                facts = persistentListOf(
                    FactUIModel(
                        UiText.StringResource(StringResource.release_released),
                        UiText.StringValue("2010-08-26"),
                    ),
                    FactUIModel(
                        UiText.StringResource(StringResource.release_platforms),
                        UiText.StringValue("Windows"),
                    ),
                    FactUIModel(
                        UiText.StringResource(StringResource.release_media),
                        UiText.StringValue("DVD"),
                    ),
                ),
            ),
            modifier = Modifier.padding(VndbTheme.spacing.lg),
        )
    }
}
