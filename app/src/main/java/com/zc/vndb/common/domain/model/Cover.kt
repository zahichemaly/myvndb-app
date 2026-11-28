package com.zc.vndb.common.domain.model

/** A cover image with VNDB's averaged content flags (0 = safe, 1 = suggestive/violent, 2 = explicit/brutal). */
data class Cover(
    val url: String,
    val thumbnailUrl: String?,
    val sexual: Double,
    val violence: Double,
    /** The build's rule for [isSensitive]. */
    val policy: SensitiveContentPolicy = SensitiveContentPolicy.Standard,
) {
    /** Sensitive covers are blurred until the user opts in, which builds that never show them don't allow. */
    val isSensitive: Boolean
        get() = policy.isSensitive(sexual, violence)
}
