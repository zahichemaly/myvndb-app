package com.zc.vndb.app

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import com.zc.vndb.app.di.AppKoin
import com.zc.vndb.common.di.buildAppConfig
import io.ktor.client.HttpClient
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level
import org.koin.plugin.module.dsl.startKoin

class AppApplication :
    Application(),
    SingletonImageLoader.Factory {

    override fun onCreate() {
        super.onCreate()
        startKoin<AppKoin> {
            // Koin is not started yet, so the config comes straight from the build.
            androidLogger(if (buildAppConfig().isDebug) Level.INFO else Level.NONE)
            androidContext(this@AppApplication)
        }
    }

    // Coil shares the app's Ktor HttpClient, so images and API calls use one HTTP stack.
    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory(httpClient = { get<HttpClient>() })) }
            .diskCache { get<DiskCache>() }
            .crossfade(true)
            .build()
}
