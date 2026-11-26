package com.zc.vndb.common.domain.model

/**
 * Facts about this build, set by Gradle. Code reads them here rather than from `BuildConfig`
 * (mapped once in `CommonModule`), so `domain` stays plain Kotlin and tests can pick any build.
 */
data class AppConfig(
    val versionName: String,
    val versionCode: Int,
    val isDebug: Boolean,
    /**
     * Whether this build may show sensitive content: true for the development flavor, false for
     * production (Google Play), where sensitive images stay blurred and can never be revealed.
     */
    val showSensitiveContent: Boolean,
) {
    val sensitiveContentPolicy: SensitiveContentPolicy
        get() = if (showSensitiveContent) SensitiveContentPolicy.Standard else SensitiveContentPolicy.Censored
}
