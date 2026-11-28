package com.zc.vndb.common.domain.model

/**
 * When an image counts as sensitive (blurred), from VNDB's averaged votes: 0 = safe, 1 = suggestive /
 * violent, 2 = explicit / brutal. Each build uses one policy, see [AppConfig.sensitiveContentPolicy].
 */
enum class SensitiveContentPolicy {
    /** Builds that may show sensitive content: suggestive or violent on average and above. */
    Standard,

    /**
     * Builds that never show it (Google Play): also anything vndb.org does not rate sexually "Safe"
     * (above 0.4), since split votes can average an explicit image down to 1.0 or below.
     */
    Censored,
    ;

    fun isSensitive(sexual: Double, violence: Double): Boolean = when (this) {
        Standard -> sexual >= SENSITIVE_LEVEL
        Censored -> sexual > SAFE_SEXUAL_MAX
    } || violence >= SENSITIVE_LEVEL

    private companion object {
        const val SENSITIVE_LEVEL = 1.0

        /** vndb.org's own cut-off between "Safe" and "Suggestive". */
        const val SAFE_SEXUAL_MAX = 0.4
    }
}
