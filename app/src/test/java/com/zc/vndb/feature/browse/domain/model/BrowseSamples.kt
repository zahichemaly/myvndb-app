package com.zc.vndb.feature.browse.domain.model

import com.zc.vndb.common.domain.model.Cover
import com.zc.vndb.common.domain.model.NovelLength
import com.zc.vndb.common.domain.model.NovelTag
import com.zc.vndb.common.domain.model.ReleaseDate
import com.zc.vndb.common.domain.model.TagCategory
import kotlinx.datetime.LocalDate

object BrowseSamples {

    fun novel(
        id: String,
        tags: List<NovelTag> = emptyList(),
        languages: List<String> = listOf("ja", "en", "ko"),
    ) = BrowseNovel(
        id = id,
        title = "Novel $id",
        originalTitle = null,
        releaseDate = ReleaseDate.Full(LocalDate(2009, 10, 15)),
        rating = 90.2,
        cover = Cover(url = "full", thumbnailUrl = "thumb", sexual = 0.0, violence = 0.0),
        developerNames = listOf("MAGES."),
        tags = tags,
        length = NovelLength.Long,
        languages = languages,
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
}
