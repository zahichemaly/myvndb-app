package com.zc.vndb.feature.settings.presentation.licenses.mapper

import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library

private val WordSeparators = charArrayOf(' ', '-')

/**
 * One row per project instead of one per artifact: Ktor's 17 modules, Koin's 8 and Lifecycle's 14
 * each become a single entry. Artifacts merge when they share a Maven group, a source repository
 * and their licenses, so projects that share a group (kotlinx.coroutines, kotlinx.serialization)
 * stay apart, and so does an artifact under another license (DataStore's BSD-licensed protobuf).
 */
fun Libs.groupedByProject(): Libs = copy(
    libraries = libraries
        .groupBy { it.projectKey() }
        .values
        .map { it.toProjectLibrary() }
        .sortedBy { it.name.lowercase() },
)

private data class ProjectKey(val group: String, val repository: String?, val licenses: Set<String>)

private fun Library.projectKey() = ProjectKey(
    group = groupId,
    repository = scm?.url ?: website,
    licenses = licenses.map { it.hash }.toSet(),
)

private val Library.groupId: String get() = uniqueId.substringBefore(':')

/** `core` in `androidx.core:core` (not [Library.artifactId], which adds the version). */
private val Library.moduleName: String get() = uniqueId.substringAfter(':')

/**
 * The project's row: its main artifact (the one named after the group, like `androidx.core:core`)
 * with the project's name, and that artifact's version, or the shared version when there is none.
 */
private fun List<Library>.toProjectLibrary(): Library {
    if (size == 1) return single()
    val main = firstOrNull { it.moduleName == it.groupId.substringAfterLast('.') }
    val sharedVersion = map { it.artifactVersion }.distinct().singleOrNull()
    return (main ?: minBy { it.uniqueId }).copy(
        name = projectName(main),
        artifactVersion = main?.artifactVersion ?: sharedVersion,
    )
}

/**
 * "Koin" (all alike), "Compose UI" (the main artifact), "ktor" (the words the names share) or
 * "Lifecycle" (the words the artifact names share).
 */
private fun List<Library>.projectName(main: Library?): String =
    map { it.name }.distinct().singleOrNull()
        ?: main?.name
        ?: commonWordPrefix(map { it.name })
        ?: commonWordPrefix(map { it.moduleName })?.replaceFirstChar { it.titlecase() }
        ?: minBy { it.uniqueId }.name

/** The whole words every text starts with: "ktor-client-core" and "ktor-io" give "ktor". */
private fun commonWordPrefix(texts: List<String>): String? {
    val first = texts.first()
    var end = first.indices
        .firstOrNull { i -> texts.any { it.getOrNull(i) != first[i] } }
        ?: first.length
    // Back off to a word boundary in every text, so "Lifecycle" and "LiveData" don't share "Li".
    while (end > 0 && texts.any { it.length > end && it[end] !in WordSeparators }) end--
    return first.take(end).trimEnd(*WordSeparators).ifEmpty { null }
}
