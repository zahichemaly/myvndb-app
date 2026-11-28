package com.zc.vndb.feature.detail.presentation

import androidx.compose.runtime.Immutable
import com.zc.vndb.common.ui.component.error.model.ErrorUIModel
import com.zc.vndb.common.ui.component.linksrow.model.LinkUIModel
import com.zc.vndb.common.ui.component.spoilerfilter.model.SpoilerLevel
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.detail.presentation.component.detailhero.model.DetailHeroUIModel
import com.zc.vndb.feature.detail.presentation.component.editionrow.model.EditionRowUIModel
import com.zc.vndb.feature.detail.presentation.component.mylist.model.MyListCardUIModel
import com.zc.vndb.feature.detail.presentation.component.relationcard.model.RelationCardUIModel
import com.zc.vndb.feature.detail.presentation.component.taggroups.model.ContentNoteUIModel
import com.zc.vndb.feature.detail.presentation.component.taggroups.model.TagGroupUIModel
import com.zc.vndb.feature.detail.presentation.model.AlsoKnownAsUIModel
import com.zc.vndb.feature.detail.presentation.model.CastUIModel
import com.zc.vndb.feature.detail.presentation.model.CoverRevealRequest
import com.zc.vndb.feature.detail.presentation.model.DetailTab
import com.zc.vndb.feature.detail.presentation.model.DetailTabUIModel
import com.zc.vndb.feature.detail.presentation.model.ImageViewerUIState
import com.zc.vndb.feature.detail.presentation.model.MediaImageUIModel
import com.zc.vndb.feature.detail.presentation.model.MediaUIModel
import com.zc.vndb.feature.detail.presentation.model.PagedListUIState
import com.zc.vndb.feature.detail.presentation.model.QuoteUIModel
import com.zc.vndb.feature.detail.presentation.model.ReleaseGroupUIModel
import com.zc.vndb.feature.detail.presentation.model.SectionState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * The VN page. [hero] and the Overview fields come from the one request made on open; every
 * [SectionState] stays Idle until its tab is opened or its Overview preview scrolls into view.
 */
@Immutable
data class DetailState(
    val novelId: String = "",
    val isLoading: Boolean = false,
    val error: ErrorUIModel? = null,
    val selectedTab: DetailTab = DetailTab.Overview,
    val tabs: ImmutableList<DetailTabUIModel> = persistentListOf(),
    // Overview (loaded on open)
    val hero: DetailHeroUIModel? = null,
    /** The signed-in user's entry; null for guests or until it loads. */
    val myList: MyListCardUIModel? = null,
    val links: ImmutableList<LinkUIModel> = persistentListOf(),
    val spoilerLevel: SpoilerLevel = SpoilerLevel.None,
    val synopsis: UiText? = null,
    val isSynopsisExpanded: Boolean = false,
    val alsoKnownAs: AlsoKnownAsUIModel? = null,
    val tagGroups: ImmutableList<TagGroupUIModel> = persistentListOf(),
    val hiddenTagsLabel: UiText? = null,
    /** Every tag is listed, not only each group's most relevant; starts from Settings → Show all tags. */
    val areAllTagsShown: Boolean = false,
    /** "See all 34" / "Show less" on the tags card; null when every tag already shows. */
    val tagsToggleLabel: UiText? = null,
    val contentNotes: ImmutableList<ContentNoteUIModel> = persistentListOf(),
    val relations: ImmutableList<RelationCardUIModel> = persistentListOf(),
    // Lazy sections
    val editions: SectionState<ImmutableList<EditionRowUIModel>> = SectionState.Idle,
    val cast: SectionState<CastUIModel> = SectionState.Idle,
    val screenshots: SectionState<MediaUIModel> = SectionState.Idle,
    val covers: SectionState<ImmutableList<MediaImageUIModel>> = SectionState.Idle,
    val releases: SectionState<PagedListUIState<ReleaseGroupUIModel>> = SectionState.Idle,
    val quotes: SectionState<PagedListUIState<QuoteUIModel>> = SectionState.Idle,
    val viewer: ImageViewerUIState? = null,
    /** The user confirmed they are 18 or older: this page's blurred covers are shown. */
    val areCoversRevealed: Boolean = false,
    /** Whether blurred images can be revealed at all; builds that never show sensitive content can't. */
    val canRevealSensitiveContent: Boolean = false,
    /** Set while the "I'm 18 or older" question is open. */
    val coverReveal: CoverRevealRequest? = null,
)
