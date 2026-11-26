package com.zc.vndb.feature.detail.presentation.mapper

import com.zc.vndb.common.domain.model.SensitiveContentPolicy
import com.zc.vndb.common.ui.resource.PluralResource
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.detail.domain.model.DetailSamples.coverArt
import com.zc.vndb.feature.detail.domain.model.DetailSamples.screenshot
import com.zc.vndb.feature.detail.presentation.model.ContentFilter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class DetailMediaUIModelMapperTest {

    private val screenshots = listOf(
        screenshot("safe"),
        screenshot("suggestive", sexual = 0.8),
        screenshot("explicit", sexual = 1.9),
        screenshot("violent", release = "r2", violence = 1.2),
    )

    @Test
    fun `defaults show safe and tame only, with counts on every toggle`() {
        val media = screenshots.toMediaUIModel(ContentFilter.Default, blursSensitive = false)

        assertEquals(4, media.totalCount)
        assertEquals(1, media.visibleCount)
        assertEquals(listOf("safe"), media.preview.map { it.url })
        assertEquals(
            UiText.StringPlural(PluralResource.detail_hidden_by_filters, 3, listOf(3)),
            media.hiddenLabel,
        )
        assertEquals(
            listOf(
                UiText.StringResource(StringResource.detail_filter_safe, listOf(2)),
                UiText.StringResource(StringResource.detail_filter_suggestive, listOf(1)),
                UiText.StringResource(StringResource.detail_filter_explicit, listOf(1)),
            ),
            media.sexualToggles.map { it.label },
        )
        assertEquals(listOf(true, false, false), media.sexualToggles.map { it.isSelected })
    }

    @Test
    fun `every filter shows everything, grouped by release with running indexes`() {
        val media = screenshots.toMediaUIModel(
            ContentFilter.entries.toSet(),
            blursSensitive = false,
        )

        assertNull(media.hiddenLabel)
        assertEquals(
            listOf(UiText.StringValue("Release r1"), UiText.StringValue("Release r2")),
            media.groups.map { it.title },
        )
        assertEquals(listOf(0, 1, 2, 3), media.groups.flatMap { it.screenshots }.map { it.index })
        assertEquals(4, media.preview.size)
    }

    @Test
    fun `the viewer only pages through allowed screenshots`() {
        val viewer = screenshots.toViewer(
            setOf(ContentFilter.Safe, ContentFilter.Tame, ContentFilter.Violent),
            index = 1,
            includesSensitive = true,
        )

        assertEquals(listOf("safe", "violent"), viewer.images.map { it.url })
        assertEquals(1, viewer.startIndex)
    }

    @Test
    fun `a build that may show sensitive content never blurs a listed screenshot`() {
        val media = screenshots.toMediaUIModel(
            ContentFilter.entries.toSet(),
            blursSensitive = false,
        )

        assertFalse(media.groups.flatMap { it.screenshots }.any { it.isSensitive })
    }

    @Test
    fun `a production build blurs sensitive screenshots, and its viewer skips them`() {
        val censored = listOf(
            screenshot("safe", policy = SensitiveContentPolicy.Censored),
            screenshot("split-votes", sexual = 0.8, policy = SensitiveContentPolicy.Censored),
            screenshot("explicit", sexual = 1.9, policy = SensitiveContentPolicy.Censored),
            screenshot(
                "violent",
                release = "r2",
                violence = 0.4,
                policy = SensitiveContentPolicy.Censored,
            ),
        )
        val every = ContentFilter.entries.toSet()

        val media = censored.toMediaUIModel(every, blursSensitive = true)
        val viewer = censored.toViewer(every, index = 3, includesSensitive = false)

        assertEquals(
            listOf(false, true, true, false),
            media.groups.flatMap { it.screenshots }.map { it.isSensitive },
        )
        assertEquals(listOf("safe", "violent"), viewer.images.map { it.url })
        assertEquals(1, viewer.startIndex)
    }

    @Test
    fun `the covers viewer skips sensitive covers only when they can't be revealed`() {
        val covers = listOf(
            coverArt("front"),
            coverArt("explicit", sexual = 2.0),
            coverArt("back"),
        )

        val development = covers.toViewer(index = 2, includesSensitive = true)
        val production = covers.toViewer(index = 2, includesSensitive = false)

        assertEquals(listOf("front", "explicit", "back"), development.images.map { it.url })
        assertEquals(2, development.startIndex)
        assertEquals(listOf("front", "back"), production.images.map { it.url })
        assertEquals(1, production.startIndex)
    }
}
