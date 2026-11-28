package com.zc.vndb.feature.settings.presentation.licenses.mapper

import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.entity.Scm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LicensesUIModelMapperTest {

    private val apache = license("Apache-2.0")
    private val bsd = license("BSD-3-Clause")

    private fun license(spdxId: String) =
        License(name = spdxId, url = null, spdxId = spdxId, hash = spdxId)

    private fun library(
        uniqueId: String,
        name: String,
        version: String = "1.0.0",
        repository: String = uniqueId.substringBefore(':'),
        license: License = apache,
    ) = Library(
        uniqueId = uniqueId,
        artifactVersion = version,
        name = name,
        description = null,
        website = null,
        developers = emptyList(),
        organization = null,
        scm = Scm(connection = null, developerConnection = null, url = repository),
        licenses = setOf(license),
    )

    private fun grouped(vararg libraries: Library): List<Library> =
        Libs(libraries.toList(), setOf(apache, bsd)).groupedByProject().libraries

    @Test
    fun `a project's artifacts become one row with the name they share`() {
        val rows = grouped(
            library("io.insert-koin:koin-core", "Koin"),
            library("io.insert-koin:koin-android", "Koin"),
            library("io.insert-koin:koin-compose", "Koin"),
        )

        assertEquals(listOf("Koin"), rows.map { it.name })
        assertEquals("io.insert-koin:koin-android", rows.single().uniqueId)
    }

    @Test
    fun `the artifact named after the group names the row and gives its version`() {
        val row = grouped(
            library("androidx.core:core-viewtree", "androidx.core:core-viewtree", "1.0.0"),
            library("androidx.core:core", "Core", "1.19.1"),
            library("androidx.core:core-ktx", "Core Kotlin Extensions", "1.19.1"),
        ).single()

        assertEquals("Core", row.name)
        assertEquals("1.19.1", row.artifactVersion)
        assertEquals("androidx.core:core", row.uniqueId)
    }

    @Test
    fun `without a main artifact the row takes the words the names start with`() {
        val rows = grouped(
            library("io.ktor:ktor-client-core", "ktor-client-core"),
            library("io.ktor:ktor-io", "ktor-io"),
            library("org.jetbrains.kotlin:kotlin-stdlib", "Kotlin Stdlib"),
            library("org.jetbrains.kotlin:kotlin-stdlib-common", "Kotlin Stdlib Common"),
            library("androidx.arch.core:core-common", "Android Arch-Common"),
            library("androidx.arch.core:core-runtime", "Android Arch-Runtime"),
        )

        assertEquals(listOf("Android Arch", "Kotlin Stdlib", "ktor"), rows.map { it.name })
    }

    @Test
    fun `names with no whole word in common fall back to the artifact names`() {
        val row = grouped(
            library("androidx.lifecycle:lifecycle-runtime", "Lifecycle Runtime"),
            library("androidx.lifecycle:lifecycle-livedata-core-ktx", "LiveData Core Extensions"),
        ).single()

        assertEquals("Lifecycle", row.name)
    }

    @Test
    fun `a row shows no version when its artifacts differ and none is the main one`() {
        val row = grouped(
            library("androidx.compose.material:material-ripple", "Compose Material Ripple", "2.0"),
            library("androidx.compose.material:material-icons", "Compose Material Icons", "1.7"),
        ).single()

        assertEquals("Compose Material", row.name)
        assertNull(row.artifactVersion)
    }

    @Test
    fun `projects that share a group but not a repository stay apart`() {
        val rows = grouped(
            library(
                "org.jetbrains.kotlinx:kotlinx-coroutines-core",
                "kotlinx-coroutines-core",
                repository = "coroutines",
            ),
            library(
                "org.jetbrains.kotlinx:kotlinx-coroutines-android",
                "kotlinx-coroutines-android",
                repository = "coroutines",
            ),
            library(
                "org.jetbrains.kotlinx:kotlinx-datetime",
                "kotlinx-datetime",
                repository = "datetime",
            ),
        )

        assertEquals(listOf("kotlinx-coroutines", "kotlinx-datetime"), rows.map { it.name })
    }

    @Test
    fun `an artifact under another license keeps its own row`() {
        val rows = grouped(
            library("androidx.datastore:datastore", "DataStore"),
            library("androidx.datastore:datastore-core", "DataStore Core"),
            library(
                "androidx.datastore:datastore-preferences-external-protobuf",
                "Preferences External Protobuf",
                license = bsd,
            ),
        )

        assertEquals(listOf("DataStore", "Preferences External Protobuf"), rows.map { it.name })
        assertEquals(setOf(bsd), rows.last().licenses)
    }

    @Test
    fun `a library alone in its project is left as it is`() {
        val okio = library("com.squareup.okio:okio", "okio", "3.18.2")

        assertEquals(listOf(okio), grouped(okio))
    }
}
