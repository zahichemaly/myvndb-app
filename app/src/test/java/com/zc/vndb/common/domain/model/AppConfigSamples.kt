package com.zc.vndb.common.domain.model

object AppConfigSamples {

    /** The development flavor: sensitive content can be shown, as on vndb.org. */
    val development = AppConfig(
        versionName = "1.0.0-dev",
        versionCode = 7,
        isDebug = true,
        showSensitiveContent = true,
    )

    /** The production flavor (Google Play): sensitive content is never shown. */
    val production = AppConfig(
        versionName = "1.0.0",
        versionCode = 7,
        isDebug = false,
        showSensitiveContent = false,
    )
}
