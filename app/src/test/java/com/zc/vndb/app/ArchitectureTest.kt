package com.zc.vndb.app

import java.io.File
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The layer and feature rules of the `architecture` and `structure` skills. The app is one Gradle
 * module, so the compiler can't stop a layer or feature reaching into another; this test does, on
 * every unit-test run.
 */
class ArchitectureTest {

    @Test
    fun `only app, common and feature sit at the top level`() {
        val extra = root.listFiles().orEmpty().map { it.name }.filterNot { it in TOP_LEVEL }
        assertTrue(extra.isEmpty(), "Unexpected top-level packages: $extra")
    }

    @Test
    fun `domain is plain Kotlin`() = check("domain imports a framework") { file, import ->
        file.layer == "domain" && FRAMEWORK_IN_DOMAIN.containsMatchIn(import)
    }

    @Test
    fun `domain never reaches into data, presentation or ui`() =
        check("domain reaches out") { file, import ->
            file.layer == "domain" && import.target?.layer in NOT_FOR_DOMAIN
        }

    @Test
    fun `presentation never touches data`() = check("presentation imports data") { file, import ->
        file.layer == "presentation" &&
            (import.target?.layer == "data" || DATA_LIBRARIES.containsMatchIn(import))
    }

    @Test
    fun `data never touches presentation or Compose`() = check("data imports UI") { file, import ->
        file.layer == "data" &&
            (import.target?.layer in UI_LAYERS || import.startsWith("androidx.compose."))
    }

    @Test
    fun `ViewModels take use cases, never repositories`() =
        check("ViewModel imports a repository") { file, import ->
            val target = import.target
            file.path.endsWith("ViewModel.kt") &&
                target?.layer == "domain" &&
                target.sublayer == "repository"
        }

    @Test
    fun `common depends on no feature and not on app`() =
        check("common imports app or a feature") { file, import ->
            file.area == "common" && import.target?.area in setOf("app", "feature")
        }

    @Test
    fun `features never depend on app`() = check("feature imports app") { file, import ->
        file.area == "feature" && import.target?.area == "app"
    }

    @Test
    fun `features only use another feature's domain models and use cases`() =
        check("feature reaches past another feature's domain") { file, import ->
            val target = import.target
            target?.feature != null &&
                file.feature != null &&
                target.feature != file.feature &&
                !(target.layer == "domain" && target.sublayer in SHARED_DOMAIN)
        }

    @Test
    fun `dependencies between features are allowed and acyclic`() {
        val edges = sources.flatMap { file ->
            val from = file.feature ?: return@flatMap emptyList()
            file.imports.mapNotNull { it.target?.feature }.filter { it != from }.map { from to it }
        }.toSet()
        val unexpected = edges.filterNot { (from, to) ->
            to in ALLOWED_FEATURE_DEPENDENCIES[from].orEmpty()
        }
        assertTrue(
            unexpected.isEmpty(),
            "Feature dependencies not on the allow-list: " +
                unexpected.joinToString { (from, to) -> "$from -> $to" } +
                ". Move the shared code to common, or add the edge to " +
                "ALLOWED_FEATURE_DEPENDENCIES if it is deliberate (domain models and use cases " +
                "only, no cycles).",
        )
        assertNull(
            findCycle(ALLOWED_FEATURE_DEPENDENCIES),
            "Feature dependencies must not form a cycle",
        )
    }

    /** Fails with every import for which [isViolation] holds, as `path: import`. */
    private fun check(rule: String, isViolation: (SourceFile, String) -> Boolean) {
        val violations = sources.flatMap { file ->
            file.imports.filter { isViolation(file, it) }.map { "${file.path}: $it" }
        }
        assertTrue(violations.isEmpty(), "$rule:\n${violations.joinToString("\n")}")
    }

    /** A path that comes back to where it started, like `a -> b -> a`, or null. */
    private fun findCycle(graph: Map<String, Set<String>>): List<String>? {
        fun visit(node: String, path: List<String>): List<String>? = if (node in path) {
            path.dropWhile { it != node } + node
        } else {
            graph[node].orEmpty().firstNotNullOfOrNull { visit(it, path + node) }
        }
        return graph.keys.firstNotNullOfOrNull { visit(it, emptyList()) }
    }

    /** Where a file or import sits: `app`, `common/<layer>/…` or `feature/<name>/<layer>/…`. */
    private data class Location(
        val area: String,
        val feature: String? = null,
        val layer: String? = null,
        val sublayer: String? = null,
    ) {
        companion object {
            fun of(segments: List<String>): Location? = when (segments.firstOrNull()) {
                "app" -> Location("app")

                "common" -> Location(
                    area = "common",
                    layer = segments.getOrNull(1),
                    sublayer = segments.getOrNull(2),
                )

                "feature" -> Location(
                    area = "feature",
                    feature = segments.getOrNull(1),
                    layer = segments.getOrNull(2),
                    sublayer = segments.getOrNull(3),
                )

                else -> null
            }
        }
    }

    private class SourceFile(val path: String, val imports: List<String>) {
        private val location = Location.of(path.split('/'))
        val area = location?.area
        val feature = location?.feature
        val layer = location?.layer
    }

    /** The app package an import points into, or null for a library import. */
    private val String.target: Location?
        get() = takeIf { it.startsWith(APP_PACKAGE) }
            ?.let { Location.of(it.removePrefix(APP_PACKAGE).split('.')) }

    private companion object {
        const val APP_PACKAGE = "com.zc.vndb."
        val TOP_LEVEL = setOf("app", "common", "feature")
        val NOT_FOR_DOMAIN = setOf("data", "presentation", "ui", "di")
        val UI_LAYERS = setOf("presentation", "ui")
        val SHARED_DOMAIN = setOf("model", "usecase")

        /**
         * Every allowed `feature -> other feature` edge: a feature using another's domain models
         * and use cases. Keep the graph acyclic so any feature can become its own module.
         */
        val ALLOWED_FEATURE_DEPENDENCIES = mapOf(
            "detail" to setOf("userlist"),
            "profile" to setOf("userlist"),
        )

        val FRAMEWORK_IN_DOMAIN = Regex(
            "^(android\\.|androidx\\.|io\\.ktor\\.|kotlinx\\.serialization\\.|javax\\.inject\\.|" +
                "org\\.koin\\.(android|compose|core\\.(context|module|parameter|scope))|" +
                "com\\.zc\\.vndb\\.(R|BuildConfig)$)",
        )
        val DATA_LIBRARIES = Regex("^(io\\.ktor\\.|androidx\\.datastore\\.)")
        val IMPORT = Regex("^import\\s+([\\w.*]+)")

        val root = File("src/main/java/com/zc/vndb").also {
            require(it.isDirectory) { "Run from the app module: ${it.absolutePath} not found" }
        }

        val sources: List<SourceFile> = root.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .map { file ->
                val imports = file.useLines { lines ->
                    lines.mapNotNull { IMPORT.find(it)?.groupValues?.get(1) }.toList()
                }
                SourceFile(file.relativeTo(root).invariantSeparatorsPath, imports)
            }
            .toList()
    }
}
