package com.zc.vndb.feature.settings.presentation.licenses

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Developer
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.libraryColors
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryActionKind
import com.zc.vndb.common.ui.component.apptopbar.AppTopBarView
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews

private val MaxContentWidth = 640.dp

/**
 * The open-source libraries the app ships with, each with its version, authors and licenses; a
 * library expands to show its license text. [libraries] is null while the bundled list loads.
 */
@OptIn(ExperimentalMaterial3Api::class) // LibrariesContainer's dialog-owning overload takes a SheetState.
@Composable
fun LicensesScreen(libraries: Libs?, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val okLabel = UiText.StringResource(StringResource.action_ok).asString()
    var licenseShown by remember { mutableStateOf<Library?>(null) }
    Scaffold(
        modifier = modifier,
        topBar = { AppTopBarView(onBack = onBack) },
        containerColor = Color.Transparent,
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            if (libraries == null) {
                CircularProgressIndicator(
                    color = VndbTheme.colors.accent,
                    modifier = Modifier.align(Alignment.Center),
                )
            } else {
                LibrariesContainer(
                    libraries = libraries,
                    dialogLibrary = licenseShown,
                    sheetLibrary = null,
                    onDialogLibraryChange = { licenseShown = it },
                    onSheetLibraryChange = {},
                    // "View license" shows the bundled text instead of the license's web page;
                    // Source and Website still open the browser.
                    onActionClick = { library, kind ->
                        (kind == LibraryActionKind.License).also { if (it) licenseShown = library }
                    },
                    modifier = Modifier
                        .widthIn(max = MaxContentWidth)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(vertical = VndbTheme.spacing.sm),
                    // Transparent like Settings, so the theme's wallpaper shows through; dialogs
                    // keep a solid card color.
                    colors = LibraryDefaults.libraryColors(
                        libraryBackgroundColor = Color.Transparent,
                        libraryContentColor = MaterialTheme.colorScheme.onSurface,
                        dialogBackgroundColor = VndbTheme.colors.surfaceCard,
                        dialogContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    header = {
                        item(key = "title", contentType = "title") { Title() }
                    },
                    licenseDialogConfirmText = okLabel,
                )
            }
        }
    }
}

/** "Licenses", styled like the Settings page's title and lined up with the library names. */
@Composable
private fun Title() {
    Text(
        text = UiText.StringResource(StringResource.settings_about_licenses).asString(),
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(
            // The library's rows start 20dp in.
            horizontal = VndbTheme.spacing.lg + VndbTheme.spacing.xs,
            vertical = VndbTheme.spacing.sm,
        ),
    )
}

private val previewLicense = License(
    name = "Apache License 2.0",
    url = "https://spdx.org/licenses/Apache-2.0.html",
    spdxId = "Apache-2.0",
    hash = "Apache-2.0",
)

private val previewLibraries = Libs(
    libraries = listOf(
        Library(
            uniqueId = "io.ktor:ktor-client-core",
            artifactVersion = "3.6.2",
            name = "Ktor",
            description = "Ktor is a framework for building asynchronous servers and clients.",
            website = "https://ktor.io",
            developers = listOf(Developer(name = "JetBrains", organisationUrl = null)),
            organization = null,
            scm = null,
            licenses = setOf(previewLicense),
        ),
    ),
    licenses = setOf(previewLicense),
)

@AdaptivePreviews
@Composable
private fun LicensesScreenPreview() {
    VndbTheme {
        LicensesScreen(libraries = previewLibraries, onBack = {})
    }
}
