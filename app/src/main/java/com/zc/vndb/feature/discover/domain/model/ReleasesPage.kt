package com.zc.vndb.feature.discover.domain.model

/** One page of releases, and whether VNDB has more after it. */
data class ReleasesPage(val releases: List<Release>, val hasMore: Boolean)
