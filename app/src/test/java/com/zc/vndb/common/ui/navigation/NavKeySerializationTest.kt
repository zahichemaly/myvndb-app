package com.zc.vndb.common.ui.navigation

import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.serialization.NavKeySerializer
import com.zc.vndb.feature.browse.presentation.navigation.BrowseNavKey
import com.zc.vndb.feature.browse.presentation.navigation.ProducerNavKey
import com.zc.vndb.feature.browse.presentation.navigation.StaffNavKey
import com.zc.vndb.feature.browse.presentation.navigation.TagNavKey
import com.zc.vndb.feature.browse.presentation.navigation.TopRatedNavKey
import com.zc.vndb.feature.character.presentation.navigation.CharacterNavKey
import com.zc.vndb.feature.detail.presentation.navigation.DetailNavKey
import com.zc.vndb.feature.discover.presentation.navigation.DiscoverNavKey
import com.zc.vndb.feature.discover.presentation.navigation.TrendingNavKey
import com.zc.vndb.feature.login.presentation.navigation.LoginNavKey
import com.zc.vndb.feature.profile.presentation.navigation.ProfileNavKey
import com.zc.vndb.feature.release.presentation.navigation.ReleaseNavKey
import com.zc.vndb.feature.settings.presentation.navigation.LicensesNavKey
import com.zc.vndb.feature.settings.presentation.navigation.SettingsNavKey
import com.zc.vndb.feature.theme.presentation.navigation.ThemeNavKey
import com.zc.vndb.feature.userlist.presentation.navigation.ListEditorNavKey
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

/** Back stacks survive process death through these keys: every key must round-trip. */
class NavKeySerializationTest {

    // New NavKey? Add a sample of it here.
    private val keys: List<NavKey> = listOf(
        DiscoverNavKey,
        BrowseNavKey,
        ProfileNavKey,
        SettingsNavKey,
        LicensesNavKey,
        ThemeNavKey,
        LoginNavKey,
        DetailNavKey("v17"),
        ReleaseNavKey("r1"),
        CharacterNavKey("c1"),
        TagNavKey("g1"),
        ProducerNavKey("p1"),
        StaffNavKey("s1"),
        TopRatedNavKey,
        TrendingNavKey,
        ListEditorNavKey("v17", "Fate/stay night"),
    )

    @Test
    fun `every nav key round-trips through its serializer`() {
        val serializer = NavKeySerializer<NavKey>()
        keys.forEach { key ->
            val json = Json.encodeToString(serializer, key)

            assertEquals(key, Json.decodeFromString(serializer, json))
        }
    }
}
