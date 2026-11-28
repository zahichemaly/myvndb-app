package com.zc.vndb.feature.settings.presentation.home.mapper

import com.zc.vndb.common.domain.model.Session
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsUIModelMapperTest {

    private fun accountLabel(read: Boolean, write: Boolean) =
        Session("u2", "Yorhel", canReadPrivateList = read, canWriteList = write).toAccountLabel()

    private fun expected(access: Int) = UiText.StringResource(
        StringResource.settings_log_out_account,
        listOf("Yorhel", UiText.StringResource(access)),
    )

    @Test
    fun `the account label names what the token allows`() {
        assertEquals(
            expected(StringResource.settings_access_read_edit),
            accountLabel(read = true, write = true),
        )
        assertEquals(
            expected(StringResource.settings_access_read),
            accountLabel(read = true, write = false),
        )
        assertEquals(
            expected(StringResource.settings_access_edit),
            accountLabel(read = false, write = true),
        )
        assertEquals(
            expected(StringResource.settings_access_public),
            accountLabel(read = false, write = false),
        )
    }
}
