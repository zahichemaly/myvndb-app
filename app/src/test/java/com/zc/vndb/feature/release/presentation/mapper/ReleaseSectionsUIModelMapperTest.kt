package com.zc.vndb.feature.release.presentation.mapper

import com.zc.vndb.common.domain.model.ExternalLink
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.release.domain.model.ReleaseImage
import com.zc.vndb.feature.release.domain.model.ReleaseImageType
import com.zc.vndb.feature.release.domain.model.ReleaseLanguage
import com.zc.vndb.feature.release.domain.model.ReleaseSamples.cover
import com.zc.vndb.feature.release.domain.model.ReleaseSamples.release
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReleaseSectionsUIModelMapperTest {

    @Test
    fun `links start with vndb org`() {
        val links = release(
            links = listOf(ExternalLink("Official website", "http://steinsgate.jp/")),
        ).toLinks(showsSensitiveContent = false)

        assertEquals(
            listOf("https://vndb.org/r7668", "http://steinsgate.jp/"),
            links.map { it.url },
        )
        assertTrue(links.first().isVndb)
    }

    @Test
    fun `an adult release keeps only vndb org in builds that never show sensitive content`() {
        val adult = release(links = listOf(ExternalLink("DLsite", "https://www.dlsite.com/")))
            .copy(minAge = 18)

        assertEquals(
            listOf("https://vndb.org/r7668"),
            adult.toLinks(showsSensitiveContent = false).map { it.url },
        )
        assertEquals(
            listOf("https://vndb.org/r7668", "https://www.dlsite.com/"),
            adult.toLinks(showsSensitiveContent = true).map { it.url },
        )
    }

    @Test
    fun `a release is adult only when rated 18+ or flagged erotic`() {
        assertFalse(release().isAdultOnly)
        assertTrue(release().copy(minAge = 18).isAdultOnly)
        assertTrue(release().copy(minAge = null, hasAdultContent = true).isAdultOnly)
    }

    @Test
    fun `languages show their own title only when it adds something`() {
        val languages = release(
            languages = listOf(
                ReleaseLanguage("ja", "シュタインズ・ゲート", isMain = true, isMachineTranslated = false),
                ReleaseLanguage("en", "Steins;Gate", isMain = false, isMachineTranslated = false),
                ReleaseLanguage("pl", null, isMain = false, isMachineTranslated = true),
            ),
        ).copy(altTitle = null).toLanguageUIModels()

        assertEquals(UiText.StringValue("シュタインズ・ゲート"), languages[0].title)
        assertNull(languages[1].title)
        assertTrue(languages[2].isMachineTranslated)
        assertNull(release().copy(altTitle = "シュタインズ・ゲート").toLanguageUIModels().single().title)
    }

    @Test
    fun `producers, images and notes`() {
        val detail = release(
            images = listOf(ReleaseImage(ReleaseImageType.PackageBack, cover("back"))),
        )

        assertEquals(
            listOf<UiText>(
                UiText.StringResource(StringResource.release_developer),
                UiText.StringResource(StringResource.release_publisher),
            ),
            detail.toProducerUIModels().single().roles,
        )
        assertEquals("p1", detail.toProducerUIModels().single().id)
        val image = detail.toImageUIModels().single()
        assertEquals(UiText.StringResource(StringResource.release_image_back), image.typeLabel)
        assertEquals("back-thumb", image.thumbnailUrl)
        assertEquals(UiText.StringValue("Comes with 4 trading cards."), detail.toNotes())
        assertNull(detail.copy(notes = null).toNotes())
    }
}
