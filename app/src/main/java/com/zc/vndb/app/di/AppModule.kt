package com.zc.vndb.app.di

import com.zc.vndb.common.di.CommonModule
import com.zc.vndb.feature.browse.di.BrowseModule
import com.zc.vndb.feature.character.di.CharacterModule
import com.zc.vndb.feature.detail.di.DetailModule
import com.zc.vndb.feature.discover.di.DiscoverModule
import com.zc.vndb.feature.login.di.LoginModule
import com.zc.vndb.feature.profile.di.ProfileModule
import com.zc.vndb.feature.release.di.ReleaseModule
import com.zc.vndb.feature.settings.di.SettingsModule
import com.zc.vndb.feature.theme.di.ThemeModule
import com.zc.vndb.feature.userlist.di.UserListModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

/** Aggregates every Koin module in the app (and scans `app` for its root ViewModel). */
@ComponentScan("com.zc.vndb.app")
@Module(
    includes = [
        CommonModule::class,
        DiscoverModule::class,
        BrowseModule::class,
        DetailModule::class,
        CharacterModule::class,
        ReleaseModule::class,
        ProfileModule::class,
        LoginModule::class,
        SettingsModule::class,
        ThemeModule::class,
        UserListModule::class,
    ],
)
class AppModule
