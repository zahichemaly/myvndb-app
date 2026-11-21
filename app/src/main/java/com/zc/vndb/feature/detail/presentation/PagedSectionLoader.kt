package com.zc.vndb.feature.detail.presentation

import com.zc.vndb.common.ui.component.error.mapper.toErrorUIModel
import com.zc.vndb.feature.detail.domain.model.ResultPage
import com.zc.vndb.feature.detail.presentation.model.PagedListUIState
import com.zc.vndb.feature.detail.presentation.model.SectionState
import com.zc.vndb.feature.detail.presentation.model.dataOrNull
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * A section loaded a page at a time with "Load more". Every page is appended to [items] without
 * duplicates (by [key]); a failed next page keeps what is shown and offers "Load more" again.
 * [onChanged] runs after every change of [state].
 */
class PagedSectionLoader<T>(
    private val scope: CoroutineScope,
    private val fetch: suspend (page: Int) -> Result<ResultPage<T>>,
    private val key: (T) -> Any,
    private val onChanged: () -> Unit,
) : LazySection {

    var state: SectionState<PagedListUIState<T>> = SectionState.Idle
        private set

    /** Every item loaded so far. */
    val items: List<T> get() = state.dataOrNull?.items.orEmpty()

    private var nextPage = FIRST_PAGE
    private var job: Job? = null

    override fun loadOnce() {
        if (state == SectionState.Idle) load()
    }

    override fun load() {
        if (job?.isActive == true) return
        update(SectionState.Loading)
        job = scope.launch {
            fetch(FIRST_PAGE).fold(
                onSuccess = { page ->
                    nextPage = FIRST_PAGE + 1
                    showPage(page.items, page.hasMore)
                },
                onFailure = { update(SectionState.Failed(it.toErrorUIModel())) },
            )
        }
    }

    /** Appends the next page; does nothing before the first page is in or while a load runs. */
    fun loadMore() {
        val loaded = state.dataOrNull ?: return
        if (job?.isActive == true) return
        showLoadMore(loaded, isLoading = true, failed = false)
        job = scope.launch {
            fetch(nextPage).fold(
                onSuccess = { page ->
                    nextPage++
                    showPage((items + page.items).distinctBy(key), page.hasMore)
                },
                onFailure = { showLoadMore(loaded, isLoading = false, failed = true) },
            )
        }
    }

    private fun showPage(all: List<T>, hasMore: Boolean) {
        update(SectionState.Loaded(PagedListUIState(all.toImmutableList(), hasMore)))
    }

    private fun showLoadMore(loaded: PagedListUIState<T>, isLoading: Boolean, failed: Boolean) {
        update(SectionState.Loaded(loaded.copy(isLoadingMore = isLoading, loadMoreFailed = failed)))
    }

    private fun update(value: SectionState<PagedListUIState<T>>) {
        state = value
        onChanged()
    }

    private companion object {
        const val FIRST_PAGE = 1
    }
}
