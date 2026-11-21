# MyVNDB

Unofficial Android client for [vndb.org](https://vndb.org) using the public VNDB API (Kana). See `README.md` for the overview and `DESIGN.md` for the design system ("Nocturne VN" theme tokens and guidelines).

## Stack
Kotlin, Jetpack Compose (Material 3, Adaptive), Navigation 3, Koin (Annotations via compiler plugin), Ktor, kotlinx.serialization, Coroutines/Flow, Coil, Detekt.

## Architecture
- Single `:app` module, organised by feature (discover, browse, detail, character, release, profile, userlist, login, settings, theme). Shared code lives in `common`.
- Clean Architecture: each feature has `data`, `domain`, `presentation` and `di` packages (no `data` when it only uses `common` repositories).
- Layer and feature-dependency rules are enforced by `ArchitectureTest` (runs with the unit tests). Features may only use each other's domain models and use cases, along allow-listed, acyclic edges.
- MVI in presentation: a ViewModel exposes one state plus one-off effects and handles intents.

## Commands
```bash
./gradlew installDevelopmentDebug        # install dev flavor (com.zc.vndb.dev)
./gradlew assembleDevelopmentRelease     # release build, signed with debug.keystore
./gradlew testDevelopmentDebugUnitTest   # unit tests
./gradlew detekt                         # static analysis (config in config/detekt)
```
Flavors: `development` and `production`, each with debug and release builds.

## Project skills
Detailed rules live in `.claude/skills/`; load the relevant one before working:
- `architecture`: layers, dependency rules, MVI data flow
- `structure`: package hierarchy and file placement
- `conventions`: naming, immutability, mappers, MVI contracts, Compose
- `best-practices`: Compose performance, Detekt/lint, coroutines, errors, previews, accessibility, testing
- `tech-stack`: approved libraries per layer
- `library-management`: Gradle Version Catalog, build-logic convention plugins

## Notes
- Dependencies go through the Gradle Version Catalog; build logic is in `build-logic/`.
- Plans/specs are kept in `docs/plans`.
