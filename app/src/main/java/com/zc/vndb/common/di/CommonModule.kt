package com.zc.vndb.common.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import coil3.disk.DiskCache
import com.zc.vndb.BuildConfig
import com.zc.vndb.common.data.local.LanguageLocalDataSource
import com.zc.vndb.common.data.local.SessionLocalDataSource
import com.zc.vndb.common.data.local.SettingsLocalDataSource
import com.zc.vndb.common.data.network.LogcatHttpLogger
import com.zc.vndb.common.data.network.appJson
import com.zc.vndb.common.data.network.createHttpClient
import com.zc.vndb.common.domain.model.AppConfig
import io.ktor.client.HttpClient
import kotlin.time.Clock
import kotlinx.serialization.json.Json
import okio.Path.Companion.toOkioPath
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

/** This build's [AppConfig], from Gradle's `BuildConfig`: the only place that reads it. */
fun buildAppConfig(): AppConfig = AppConfig(
    versionName = BuildConfig.VERSION_NAME,
    versionCode = BuildConfig.VERSION_CODE,
    isDebug = BuildConfig.DEBUG,
    showSensitiveContent = BuildConfig.SHOW_SENSITIVE_CONTENT,
)

@Module
@ComponentScan("com.zc.vndb.common")
class CommonModule {
    @Single
    fun appConfig(): AppConfig = buildAppConfig()

    @Single
    fun json(): Json = appJson

    @Single
    fun httpClient(json: Json, appConfig: AppConfig): HttpClient =
        createHttpClient(json = json, logger = if (appConfig.isDebug) LogcatHttpLogger() else null)

    @Single
    fun clock(): Clock = Clock.System

    // Signed-in VNDB account, shared by the login and profile features.
    @Single
    @Named(SessionLocalDataSource.SESSION_DATASTORE)
    fun sessionDataStore(context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile(
                SessionLocalDataSource.SESSION_DATASTORE,
            )
        }

    // User preferences from the Settings screen, read by most features.
    @Single
    @Named(SettingsLocalDataSource.SETTINGS_DATASTORE)
    fun settingsDataStore(context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile(
                SettingsLocalDataSource.SETTINGS_DATASTORE,
            )
        }

    // VNDB's language list, fetched when a picker needs it and kept for a month.
    @Single
    @Named(LanguageLocalDataSource.LANGUAGE_DATASTORE)
    fun languageDataStore(context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile(
                LanguageLocalDataSource.LANGUAGE_DATASTORE,
            )
        }

    // Coil's disk cache, shared with the image loader so Settings can show its size and clear it.
    @Single
    fun imageDiskCache(context: Context): DiskCache = DiskCache.Builder()
        .directory(context.cacheDir.resolve(IMAGE_CACHE_DIRECTORY).toOkioPath())
        .build()

    private companion object {
        const val IMAGE_CACHE_DIRECTORY = "image_cache"
    }
}
