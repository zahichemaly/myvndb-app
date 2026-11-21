package com.zc.vndb.feature.detail.domain.model

import com.zc.vndb.common.domain.model.CharacterRole
import com.zc.vndb.common.domain.model.Cover
import com.zc.vndb.common.domain.model.ExternalLink
import com.zc.vndb.common.domain.model.NovelLength
import com.zc.vndb.common.domain.model.NovelTag
import com.zc.vndb.common.domain.model.PlatformFamily
import com.zc.vndb.common.domain.model.ReleaseDate
import com.zc.vndb.common.domain.model.TagCategory
import com.zc.vndb.common.domain.model.VoiceActor
import kotlinx.datetime.LocalDate

object DetailSamples {

    fun date(year: Int, month: Int = 1, day: Int = 1): ReleaseDate = ReleaseDate.Full(
        LocalDate(year, month, day),
    )

    fun tag(
        name: String,
        rating: Double = 2.0,
        spoiler: Int = 0,
        category: TagCategory = TagCategory.Content,
    ) = NovelTag(
        id = "g-$name",
        name = name,
        rating = rating,
        spoilerLevel = spoiler,
        category = category,
    )

    fun novel(
        id: String = "v2002",
        tags: List<NovelTag> = emptyList(),
        relations: List<RelatedNovel> = emptyList(),
        links: List<ExternalLink> = emptyList(),
        description: String? = "Hello",
        cover: Cover? = Cover(url = "full", thumbnailUrl = "thumb", sexual = 0.0, violence = 0.0),
        titles: List<NovelTitle> = emptyList(),
        aliases: List<String> = emptyList(),
        originalLanguage: String? = "ja",
        languages: List<String> = listOf("ja", "en", "ko", "zh-Hans", "es"),
    ) = NovelDetail(
        id = id,
        title = "STEINS;GATE",
        originalTitle = null,
        releaseDate = date(2009, 10, 15),
        rating = 90.2,
        average = 90.25,
        voteCount = 17205,
        cover = cover,
        developers = listOf(
            NovelDeveloper(id = "p1", name = "NITRO PLUS"),
            NovelDeveloper(id = null, name = "MAGES."),
            NovelDeveloper(id = "p3", name = "Third"),
        ),
        description = description,
        length = NovelLength.Long,
        averagePlayMinutes = 2625,
        tags = tags,
        links = links,
        relations = relations,
        titles = titles,
        aliases = aliases,
        lengthVotes = 1021,
        originalLanguage = originalLanguage,
        languages = languages,
    )

    fun relation(
        id: String,
        official: Boolean = true,
        released: ReleaseDate = date(
            2015,
        ),
    ) = RelatedNovel(
        id = id,
        title = "Novel $id",
        relation = RelationType.SideStory,
        isOfficial = official,
        releaseDate = released,
        rating = 80.8,
    )

    fun character(id: String, role: CharacterRole = CharacterRole.Primary, spoiler: Int = 0) =
        NovelCharacter(
            id = id,
            name = "Character $id",
            originalName = null,
            image = null,
            role = role,
            spoilerLevel = spoiler,
            voiceActor = null,
        )

    fun edition(
        id: String,
        platform: String = "win",
        released: ReleaseDate = date(2010),
        official: Boolean = true,
        patch: Boolean = false,
        minAge: Int? = 15,
    ) = Edition(
        id = id,
        title = "Edition $id",
        releaseDate = released,
        platform = platform,
        family = PlatformFamily.Pc,
        languageCodes = listOf("ja"),
        isOfficial = official,
        isPatch = patch,
        minAge = minAge,
    )

    fun credits(
        staff: List<StaffCredit> = emptyList(),
        editions: List<StaffEdition> = emptyList(),
        voiceActors: Map<String, VoiceActor> = emptyMap(),
    ) = NovelCredits(staff = staff, editions = editions, voiceActors = voiceActors)

    fun staff(id: String, role: StaffCreditRole, editionId: Int? = null, note: String? = null) =
        StaffCredit(
            staffId = id,
            name = "Staff $id",
            role = role,
            note = note,
            editionId = editionId,
        )

    fun screenshot(
        url: String,
        release: String? = "r1",
        sexual: Double = 0.0,
        violence: Double = 0.0,
    ) = Screenshot(
        image = Cover(url = url, thumbnailUrl = "$url-t", sexual = sexual, violence = violence),
        releaseId = release,
        releaseTitle = release?.let { "Release $it" },
    )

    fun coverArt(url: String, release: String = "r1", packageFront: Boolean = true) = CoverArt(
        image = Cover(url = url, thumbnailUrl = "$url-t", sexual = 0.0, violence = 0.0),
        releaseId = release,
        releaseTitle = "Release $release",
        isPackageFront = packageFront,
    )

    fun releaseSummary(id: String, languages: List<String> = listOf("ja")) = ReleaseSummary(
        id = id,
        title = "Release $id",
        releaseDate = date(2010),
        platforms = listOf("win"),
        family = PlatformFamily.Pc,
        languageCodes = languages,
        isOfficial = true,
        isPatch = false,
        isFreeware = false,
        minAge = 18,
    )

    fun quote(id: String, score: Int = 10) = Quote(
        id = id,
        text = "Quote $id",
        score = score,
        characterId = "c1",
        characterName = "Character c1",
    )
}
