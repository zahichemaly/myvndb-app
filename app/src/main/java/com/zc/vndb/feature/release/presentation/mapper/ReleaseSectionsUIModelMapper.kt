package com.zc.vndb.feature.release.presentation.mapper

import com.zc.vndb.common.ui.component.linksrow.model.LinkUIModel
import com.zc.vndb.common.ui.mapper.bbCodeToPlainText
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.release.domain.model.ReleaseDetail
import com.zc.vndb.feature.release.domain.model.ReleaseImageType
import com.zc.vndb.feature.release.presentation.model.ReleaseImageUIModel
import com.zc.vndb.feature.release.presentation.model.ReleaseLanguageUIModel
import com.zc.vndb.feature.release.presentation.model.ReleaseProducerUIModel
import java.util.Locale
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/** The release's page on vndb.org. */
fun releaseUrl(id: String): String = "https://vndb.org/$id"

/**
 * vndb.org first, then the release's own links (stores, official site). Builds that never show
 * sensitive content leave out an adult release's own links: they mostly lead to adult stores.
 */
fun ReleaseDetail.toLinks(showsSensitiveContent: Boolean): ImmutableList<LinkUIModel> = buildList {
    add(
        LinkUIModel(
            UiText.StringResource(StringResource.detail_vndb_link),
            releaseUrl(id),
            isVndb = true,
        ),
    )
    if (showsSensitiveContent || !isAdultOnly) {
        links.forEach { add(LinkUIModel(UiText.StringValue(it.label), it.url, isVndb = false)) }
    }
}.toImmutableList()

/** Each language, with its own title only when it differs from the titles the hero already shows. */
fun ReleaseDetail.toLanguageUIModels(): ImmutableList<ReleaseLanguageUIModel> =
    languages.map { language ->
        ReleaseLanguageUIModel(
            code = language.code,
            name = UiText.StringValue(
                Locale.forLanguageTag(language.code).displayLanguage.ifBlank { language.code },
            ),
            title = language.title?.takeIf { it != title && it != altTitle }?.let(
                UiText::StringValue,
            ),
            isMachineTranslated = language.isMachineTranslated,
        )
    }.toImmutableList()

fun ReleaseDetail.toProducerUIModels(): ImmutableList<ReleaseProducerUIModel> =
    producers.map { producer ->
        ReleaseProducerUIModel(
            id = producer.id,
            name = UiText.StringValue(producer.name),
            roles = listOfNotNull(
                UiText.StringResource(
                    StringResource.release_developer,
                ).takeIf { producer.isDeveloper },
                UiText.StringResource(
                    StringResource.release_publisher,
                ).takeIf { producer.isPublisher },
            ).toImmutableList(),
        )
    }.toImmutableList()

fun ReleaseDetail.toImageUIModels(): ImmutableList<ReleaseImageUIModel> = images.map { image ->
    ReleaseImageUIModel(
        url = image.image.url,
        thumbnailUrl = image.image.thumbnailUrl,
        typeLabel = image.type.toLabel(),
        isSensitive = image.image.isSensitive,
    )
}.toImmutableList()

fun ReleaseDetail.toNotes(): UiText? = notes
    ?.bbCodeToPlainText(keepParagraphs = true, showSpoilers = true)
    ?.takeIf { it.isNotEmpty() }
    ?.let(UiText::StringValue)

private fun ReleaseImageType.toLabel(): UiText = UiText.StringResource(
    when (this) {
        ReleaseImageType.PackageFront -> StringResource.release_image_front
        ReleaseImageType.PackageBack -> StringResource.release_image_back
        ReleaseImageType.PackageContents -> StringResource.release_image_contents
        ReleaseImageType.PackageSide -> StringResource.release_image_side
        ReleaseImageType.PackageMedia -> StringResource.release_image_media
        ReleaseImageType.Digital -> StringResource.release_image_digital
        ReleaseImageType.Other -> StringResource.release_image_other
    },
)
