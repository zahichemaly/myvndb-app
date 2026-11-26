package com.zc.vndb.common.domain.usecase

import com.zc.vndb.common.domain.model.AppConfig
import org.koin.core.annotation.Factory

/** This build's [AppConfig], for screens that adapt to it (version, debug tools, sensitive content). */
@Factory
class GetAppConfigUseCase(private val appConfig: AppConfig) {

    operator fun invoke(): AppConfig = appConfig
}
