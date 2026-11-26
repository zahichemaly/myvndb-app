package com.zc.vndb.feature.discover.domain.model

/**
 * One page of "New & Trending" rows. It can be empty while [hasMore] is true: every release on
 * the page was machine-translated only or for a visual novel already shown.
 */
data class TrendingPage(val rows: List<TrendingRelease>, val hasMore: Boolean)
