package com.zc.vndb.feature.detail.presentation

import com.zc.vndb.common.ui.component.error.mapper.toErrorUIModel
import com.zc.vndb.feature.detail.presentation.model.SectionState
import com.zc.vndb.feature.detail.presentation.model.dataOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * A section loaded by one request. It keeps the domain data it loaded in [state], so the
 * ViewModel can map it again when a filter changes; [onChanged] runs after every change so the
 * ViewModel can map it into its State.
 */
class SectionLoader<T>(
    private val scope: CoroutineScope,
    private val fetch: suspend () -> Result<T>,
    private val onChanged: () -> Unit,
) : LazySection {

    var state: SectionState<T> = SectionState.Idle
        private set

    val data: T? get() = state.dataOrNull

    private var job: Job? = null

    override fun loadOnce() {
        if (state == SectionState.Idle) load()
    }

    override fun load() {
        if (job?.isActive == true) return
        update(SectionState.Loading)
        job = scope.launch {
            fetch().fold(
                onSuccess = { update(SectionState.Loaded(it)) },
                onFailure = { update(SectionState.Failed(it.toErrorUIModel())) },
            )
        }
    }

    private fun update(value: SectionState<T>) {
        state = value
        onChanged()
    }
}
