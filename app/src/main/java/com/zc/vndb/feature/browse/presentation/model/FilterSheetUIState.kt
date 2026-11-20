package com.zc.vndb.feature.browse.presentation.model

import androidx.compose.runtime.Immutable

/** The open filter sheet and the filters being edited in it. */
@Immutable
data class FilterSheetUIState(val draft: FilterDraft)
