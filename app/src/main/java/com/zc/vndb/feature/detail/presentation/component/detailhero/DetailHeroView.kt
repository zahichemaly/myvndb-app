package com.zc.vndb.feature.detail.presentation.component.detailhero

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.component.chip.TagChipView
import com.zc.vndb.common.ui.component.coverimage.CoverImageView
import com.zc.vndb.common.ui.component.sectioncard.SectionCardView
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.common.ui.util.LocalShowSensitiveContent
import com.zc.vndb.feature.detail.presentation.component.detailhero.model.DetailHeroUIModel
import com.zc.vndb.feature.detail.presentation.component.detailhero.model.HeroDeveloperUIModel
import com.zc.vndb.feature.detail.presentation.model.TagChipUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private val CoverWidth = 128.dp
private val MetaLabelWidth = 84.dp
private val ScoreStarSize = 16.dp

/** Centers the "Length:" label on the first line, whose height is set by the chip. */
private val LengthLabelTopPadding = 2.dp

/** Cover, titles, top tags, age rating, and developer / release / length / score lines. */
@Composable
fun DetailHeroView(
    model: DetailHeroUIModel,
    onDeveloperClick: (String) -> Unit,
    onTagClick: (String) -> Unit,
    onRevealCoverClick: () -> Unit,
    modifier: Modifier = Modifier,
    isCoverRevealed: Boolean = false,
) {
    // Tapping a blurred cover asks "I'm 18 or older"; Settings' "Show sensitive content" skips it.
    val isCoverBlurred = model.isCoverSensitive && !isCoverRevealed &&
        !LocalShowSensitiveContent.current
    SectionCardView(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md)) {
            Box(modifier = Modifier.width(CoverWidth)) {
                CoverImageView(
                    url = model.coverUrl,
                    isSensitive = model.isCoverSensitive && !isCoverRevealed,
                    contentDescription = model.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(VndbTheme.dimens.coverAspectRatio)
                        .then(
                            if (isCoverBlurred) {
                                Modifier
                                    .clip(VndbTheme.shapes.cover)
                                    .clickable(
                                        onClickLabel = UiText.StringResource(
                                            StringResource.cd_reveal_cover,
                                        ).asString(),
                                        role = Role.Button,
                                        onClick = onRevealCoverClick,
                                    )
                            } else {
                                Modifier
                            },
                        ),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
            ) {
                Text(
                    text = model.title.asString(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                model.originalTitle?.let { original ->
                    Text(
                        text = original.asString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                HeroChips(model, onTagClick)
            }
        }
        DeveloperLine(model.developers, onDeveloperClick)
        MetaLine(UiText.StringResource(StringResource.detail_released), model.releasedLabel)
        LengthLine(model)
        ScoreLine(model)
    }
}

/** "Developer:  5pb. / Nitroplus"; a name with a known producer id opens that producer. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DeveloperLine(
    developers: ImmutableList<HeroDeveloperUIModel>,
    onDeveloperClick: (String) -> Unit,
) {
    if (developers.isEmpty()) return
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = UiText.StringResource(StringResource.detail_dev).asString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(MetaLabelWidth),
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
            verticalArrangement = Arrangement.Center,
        ) {
            developers.forEachIndexed { index, developer ->
                val id = developer.id
                Text(
                    text = developer.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (id == null) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        VndbTheme.colors.accent
                    },
                    modifier = if (id != null) {
                        Modifier.clickable(role = Role.Button) { onDeveloperClick(id) }
                    } else {
                        Modifier
                    },
                )
                if (index < developers.lastIndex) {
                    Text(
                        text = UiText.StringResource(StringResource.separator_slash).asString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

/** "Score:  8.69 / 10 · 12,409 votes", the score in the score color. */
@Composable
private fun ScoreLine(model: DetailHeroUIModel) {
    val score = model.scoreLabel ?: return
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = UiText.StringResource(StringResource.detail_score).asString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(MetaLabelWidth),
        )
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = VndbTheme.colors.score,
            modifier = Modifier.size(ScoreStarSize),
        )
        Spacer(Modifier.width(VndbTheme.spacing.xs))
        Text(
            text = score.asString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = VndbTheme.colors.score,
        )
        model.scoreVotesLabel?.let { votes ->
            Text(
                text = " ${UiText.StringResource(
                    StringResource.separator_dot,
                ).asString()} ${votes.asString()}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HeroChips(model: DetailHeroUIModel, onTagClick: (String) -> Unit) {
    FlowRow(
        modifier = Modifier.padding(top = VndbTheme.spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
        verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
    ) {
        model.tags.forEach { tag ->
            TagChipView(label = tag.label, onClick = { onTagClick(tag.id) })
        }
        model.ageLabel?.let { age ->
            TagChipView(
                label = age,
                colors = VndbTheme.colors.spoilerNone,
            )
        }
    }
}

@Composable
private fun MetaLine(label: UiText, value: UiText?) {
    if (value == null) return
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label.asString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(MetaLabelWidth),
        )
        Text(
            text = value.asString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

/**
 * "Length:  73h [Very Long] · 90 votes": the hours first, then the length class, then the votes.
 * The values wrap together under the label when the line is too narrow, so the votes never split.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LengthLine(model: DetailHeroUIModel) {
    if (model.lengthName == null && model.lengthValueLabel == null) return
    Row(verticalAlignment = Alignment.Top) {
        Text(
            text = UiText.StringResource(StringResource.detail_length).asString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .width(MetaLabelWidth)
                .padding(top = LengthLabelTopPadding),
        )
        FlowRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
            verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            model.lengthValueLabel?.let { value ->
                Text(
                    text = value.asString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            model.lengthName?.let { TagChipView(label = it) }
            model.lengthVotesLabel?.let { votes ->
                Text(
                    text = "${UiText.StringResource(
                        StringResource.separator_dot,
                    ).asString()} ${votes.asString()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    softWrap = false,
                )
            }
        }
    }
}

@AdaptivePreviews
@Composable
private fun DetailHeroViewPreview() {
    VndbTheme {
        DetailHeroView(
            model = DetailHeroUIModel(
                title = UiText.StringValue("Steins;Gate"),
                originalTitle = UiText.StringValue("シュタインズ・ゲート"),
                coverUrl = null,
                isCoverSensitive = false,
                developers = persistentListOf(
                    HeroDeveloperUIModel(id = "p1", name = "5pb."),
                    HeroDeveloperUIModel(id = null, name = "Nitroplus"),
                ),
                releasedLabel = UiText.StringValue("2009-10-15"),
                lengthName = UiText.StringValue("Long"),
                lengthValueLabel = UiText.StringValue("44h"),
                lengthVotesLabel = UiText.StringValue("1,021 votes"),
                scoreLabel = UiText.StringValue("9.02 / 10"),
                scoreVotesLabel = UiText.StringValue("17,205 votes"),
                ageLabel = UiText.StringValue("All Ages / 15+"),
                tags = persistentListOf(
                    TagChipUIModel("g105", UiText.StringValue("Sci-Fi")),
                    TagChipUIModel("g230", UiText.StringValue("Time Travel")),
                ),
            ),
            onDeveloperClick = {},
            onRevealCoverClick = {},
            onTagClick = {},
            modifier = Modifier.padding(VndbTheme.spacing.lg),
        )
    }
}
