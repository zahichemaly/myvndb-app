package com.zc.vndb.app.navigation

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.zc.vndb.common.ui.navigation.Navigator
import com.zc.vndb.feature.browse.presentation.navigation.browseEntry
import com.zc.vndb.feature.browse.presentation.navigation.browseScopedEntries
import com.zc.vndb.feature.browse.presentation.navigation.navigateToBrowse
import com.zc.vndb.feature.browse.presentation.navigation.navigateToProducer
import com.zc.vndb.feature.browse.presentation.navigation.navigateToStaff
import com.zc.vndb.feature.browse.presentation.navigation.navigateToTag
import com.zc.vndb.feature.browse.presentation.navigation.navigateToTopRated
import com.zc.vndb.feature.character.presentation.navigation.characterEntry
import com.zc.vndb.feature.character.presentation.navigation.navigateToCharacter
import com.zc.vndb.feature.detail.presentation.navigation.DetailNavActions
import com.zc.vndb.feature.detail.presentation.navigation.detailEntry
import com.zc.vndb.feature.detail.presentation.navigation.navigateToDetail
import com.zc.vndb.feature.discover.presentation.navigation.discoverEntry
import com.zc.vndb.feature.login.presentation.navigation.loginEntry
import com.zc.vndb.feature.login.presentation.navigation.navigateToLogin
import com.zc.vndb.feature.profile.presentation.navigation.profileEntry
import com.zc.vndb.feature.release.presentation.navigation.navigateToRelease
import com.zc.vndb.feature.release.presentation.navigation.releaseEntry
import com.zc.vndb.feature.settings.presentation.navigation.navigateToSettings
import com.zc.vndb.feature.settings.presentation.navigation.settingsEntry
import com.zc.vndb.feature.theme.presentation.navigation.navigateToTheme
import com.zc.vndb.feature.theme.presentation.navigation.themeEntry
import com.zc.vndb.feature.userlist.presentation.navigation.listEditorEntry
import com.zc.vndb.feature.userlist.presentation.navigation.navigateToListEditor

/**
 * Registers every feature's entries. The only place that knows all the features: each one gets
 * the lambdas that open the others' screens, so features never depend on each other.
 */
internal fun appEntryProvider(navigator: Navigator): (NavKey) -> NavEntry<NavKey> = entryProvider {
    discoverEntry(
        onOpenNovel = navigator::navigateToDetail,
        onOpenRelease = navigator::navigateToRelease,
        onOpenBrowse = navigator::navigateToBrowse,
        onOpenTopRatedPage = navigator::navigateToTopRated,
    )
    browseEntry(onOpenNovel = navigator::navigateToDetail)
    browseScopedEntries(
        onNavigateBack = navigator::navigateBack,
        onOpenNovel = navigator::navigateToDetail,
    )
    profileEntry(
        onOpenLogin = navigator::navigateToLogin,
        onOpenSettings = navigator::navigateToSettings,
        onOpenNovel = navigator::navigateToDetail,
        onOpenListEditor = navigator::navigateToListEditor,
    )
    settingsEntry(
        onNavigateBack = navigator::navigateBack,
        onOpenTheme = navigator::navigateToTheme,
    )
    themeEntry(onNavigateBack = navigator::navigateBack)
    loginEntry(onNavigateBack = navigator::navigateBack)
    detailEntry(
        DetailNavActions(
            onNavigateBack = navigator::navigateBack,
            onOpenNovel = navigator::navigateToDetail,
            onOpenCharacter = navigator::navigateToCharacter,
            onOpenTag = navigator::navigateToTag,
            onOpenProducer = navigator::navigateToProducer,
            onOpenStaff = navigator::navigateToStaff,
            onOpenRelease = navigator::navigateToRelease,
            onOpenListEditor = navigator::navigateToListEditor,
        ),
    )
    releaseEntry(
        onNavigateBack = navigator::navigateBack,
        onOpenNovel = navigator::navigateToDetail,
        onOpenProducer = navigator::navigateToProducer,
    )
    characterEntry(
        onNavigateBack = navigator::navigateBack,
        onOpenNovel = navigator::navigateToDetail,
        onOpenStaff = navigator::navigateToStaff,
    )
    listEditorEntry(onClose = navigator::navigateBack)
}
