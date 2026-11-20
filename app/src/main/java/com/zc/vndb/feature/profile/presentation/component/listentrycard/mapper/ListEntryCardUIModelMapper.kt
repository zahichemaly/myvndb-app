package com.zc.vndb.feature.profile.presentation.component.listentrycard.mapper

import com.zc.vndb.common.domain.model.LanguageOption
import com.zc.vndb.common.domain.model.TitleLanguage
import com.zc.vndb.common.ui.component.entrylabel.EntryLabelUIModel
import com.zc.vndb.common.ui.mapper.primaryTitle
import com.zc.vndb.common.ui.mapper.toLanguagePills
import com.zc.vndb.common.ui.resource.PluralResource
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.profile.domain.model.ListEntry
import com.zc.vndb.feature.profile.domain.model.ListLabel
import com.zc.vndb.feature.profile.domain.model.MyListEntry
import com.zc.vndb.feature.profile.presentation.component.listentrycard.model.ListEntryCardUIModel
import kotlin.time.Duration
import kotlin.time.Instant
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private const val HOURS_PER_DAY = 24
private const val DAYS_SHOWN_AS_AGO = 30
private const val VOTE_SCALE = 10.0

/**
 * A list entry as a card with all its labels, whatever tab it is shown in: the status first,
 * then Wishlist, Blacklist and the user's own labels. "Voted" is left out (the vote is shown).
 */
fun ListEntry.toCardUIModel(
    now: Instant,
    titleLanguage: TitleLanguage = TitleLanguage.Romanized,
    cardLanguages: List<LanguageOption> = emptyList(),
): ListEntryCardUIModel =
    toCardUIModel(now, primaryTitle(title, originalTitle, titleLanguage), cardLanguages)

private fun ListEntry.toCardUIModel(
    now: Instant,
    primaryTitle: UiText,
    cardLanguages: List<LanguageOption>,
) = ListEntryCardUIModel(
    novelId = novelId,
    title = primaryTitle,
    coverUrl = cover?.thumbnailUrl ?: cover?.url,
    isCoverSensitive = cover?.isSensitive ?: false,
    voteLabel = vote?.let { UiText.StringValue("%.1f".format(it / VOTE_SCALE)) },
    dates = listOfNotNull(
        started?.let {
            UiText.StringResource(
                StringResource.profile_started,
                listOf(it.toString()),
            )
        },
        finished?.let {
            UiText.StringResource(
                StringResource.profile_finished,
                listOf(it.toString()),
            )
        },
    ).toImmutableList(),
    labels = labels
        .filter { it.id != ListLabel.VOTED }
        .sortedWith(compareBy({ it.id !in MyListEntry.STATUS_LABELS }, { it.id }))
        .map { EntryLabelUIModel(it.id, UiText.StringValue(it.name)) }
        .toImmutableList(),
    notes = notes?.let(UiText::StringValue),
    updatedLabel = lastModified.toUpdatedLabel(now),
    languagePills = toLanguagePills(languages, cardLanguages),
)

/** "Updated just now", "… 5 minutes / 3 hours / 12 days ago", then the date. */
fun Instant.toUpdatedLabel(now: Instant): UiText {
    val elapsed: Duration = now - this
    val minutes = elapsed.inWholeMinutes.toInt()
    val hours = elapsed.inWholeHours.toInt()
    val days = elapsed.inWholeDays.toInt()
    return when {
        minutes < 1 -> UiText.StringResource(StringResource.profile_updated_just_now)

        hours < 1 -> UiText.StringPlural(
            PluralResource.profile_updated_minutes,
            minutes,
            listOf(minutes),
        )

        hours < HOURS_PER_DAY -> UiText.StringPlural(
            PluralResource.profile_updated_hours,
            hours,
            listOf(hours),
        )

        days < DAYS_SHOWN_AS_AGO -> UiText.StringPlural(
            PluralResource.profile_updated_days,
            days,
            listOf(days),
        )

        else -> UiText.StringResource(
            StringResource.profile_updated_on,
            listOf(toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()),
        )
    }
}
