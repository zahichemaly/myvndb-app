package com.zc.vndb.feature.browse.presentation.model

import androidx.compose.runtime.Immutable
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.browse.domain.model.AdvancedFilterKind
import com.zc.vndb.feature.browse.domain.model.Genre

/** Which applied filter a pill stands for, so tapping it can switch that one off. */
sealed interface ActiveFilterKey {
    val id: String

    /** The age rating, shown only when it is not the starting one. */
    data object Age : ActiveFilterKey {
        override val id = "age"
    }

    data class GenreFilter(val genre: Genre) : ActiveFilterKey {
        override val id = "genre:${genre.name}"
    }

    data class Advanced(val kind: AdvancedFilterKind) : ActiveFilterKey {
        override val id = "advanced:${kind.name}"
    }
}

/** One applied filter, shown as a removable pill: "All Ages", "Mystery", "Short". */
@Immutable
data class ActiveFilterUIModel(val key: ActiveFilterKey, val label: UiText)
