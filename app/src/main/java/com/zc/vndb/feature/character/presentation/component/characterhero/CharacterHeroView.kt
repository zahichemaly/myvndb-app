package com.zc.vndb.feature.character.presentation.component.characterhero

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.component.chip.TagChipView
import com.zc.vndb.common.ui.component.coverimage.CoverImageView
import com.zc.vndb.common.ui.component.factline.FactLineView
import com.zc.vndb.common.ui.component.factline.model.FactUIModel
import com.zc.vndb.common.ui.component.sectioncard.SectionCardView
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.character.presentation.component.characterhero.model.CharacterHeroUIModel
import com.zc.vndb.feature.character.presentation.component.characterhero.model.HeroVoiceActorUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private val PortraitWidth = 128.dp

/** Portrait, names, sex / gender chips and profile facts. */
@Composable
fun CharacterHeroView(
    model: CharacterHeroUIModel,
    onVoiceActorClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionCardView(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md)) {
            CoverImageView(
                url = model.imageUrl,
                isSensitive = model.isImageSensitive,
                contentDescription = model.name,
                modifier = Modifier
                    .width(PortraitWidth)
                    .aspectRatio(VndbTheme.dimens.coverAspectRatio),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
            ) {
                Text(
                    text = model.name.asString(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                model.originalName?.let { original ->
                    Text(
                        text = original.asString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IdentityChips(model)
                VoiceActorsLine(model.voiceActors, onVoiceActorClick)
                model.facts.forEach { fact -> FactLineView(fact) }
            }
        }
    }
}

/** "CV: Imai Asami, Other Actor", styled and clickable like the CV line on a character card. */
@Composable
private fun VoiceActorsLine(
    voiceActors: ImmutableList<HeroVoiceActorUIModel>,
    onVoiceActorClick: (String) -> Unit,
) {
    if (voiceActors.isEmpty()) return
    val prefix = UiText.StringResource(StringResource.character_cv).asString()
    FlowRow(horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs)) {
        voiceActors.forEachIndexed { index, actor ->
            val name = if (index == 0) "$prefix ${actor.name}" else actor.name
            Text(
                text = if (index < voiceActors.lastIndex) "$name," else name,
                style = MaterialTheme.typography.labelSmall,
                color = VndbTheme.colors.accent,
                modifier = Modifier.clickable(role = Role.Button) { onVoiceActorClick(actor.id) },
            )
        }
    }
}

@Composable
private fun IdentityChips(model: CharacterHeroUIModel) {
    if (model.identity.isEmpty() && model.spoilerIdentity.isEmpty()) return
    FlowRow(
        modifier = Modifier.padding(vertical = VndbTheme.spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
        verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
    ) {
        model.identity.forEach { label -> TagChipView(label = label) }
        model.spoilerIdentity.forEach { label ->
            TagChipView(
                label = label,
                colors = VndbTheme.colors.spoilerMajor,
            )
        }
    }
}

@AdaptivePreviews
@Composable
private fun CharacterHeroViewPreview() {
    VndbTheme {
        CharacterHeroView(
            model = CharacterHeroUIModel(
                name = UiText.StringValue("Makise Kurisu"),
                originalName = UiText.StringValue("牧瀬 紅莉栖"),
                imageUrl = null,
                isImageSensitive = false,
                identity = persistentListOf(UiText.StringValue("Female")),
                voiceActors = persistentListOf(HeroVoiceActorUIModel("s1", "Imai Asami")),
                facts = persistentListOf(
                    FactUIModel(
                        UiText.StringResource(StringResource.character_age),
                        UiText.StringValue("18"),
                    ),
                    FactUIModel(
                        UiText.StringResource(StringResource.character_birthday),
                        UiText.StringValue("Jul 25"),
                    ),
                    FactUIModel(
                        UiText.StringResource(StringResource.character_height),
                        UiText.StringValue("160 cm"),
                    ),
                ),
            ),
            onVoiceActorClick = {},
            modifier = Modifier.padding(VndbTheme.spacing.lg),
        )
    }
}
