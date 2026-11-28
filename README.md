# MyVNDB

An unofficial Android client for [vndb.org](https://vndb.org), the Visual Novel Database. It uses the public [VNDB API (Kana)](https://api.vndb.org/kana).

Browse and search visual novels, then dig into the details: tags, releases, screenshots, staff, characters and voice actors.

<table>
  <tr>
    <td><img src="docs/store-listing/screenshots/phone/01-discover.jpg" alt="Discover" width="180"></td>
    <td><img src="docs/store-listing/screenshots/phone/02-browse.jpg" alt="Browse" width="180"></td>
    <td><img src="docs/store-listing/screenshots/phone/03-filters.jpg" alt="Browse filters" width="180"></td>
    <td><img src="docs/store-listing/screenshots/phone/04-detail.jpg" alt="Visual novel overview" width="180"></td>
  </tr>
  <tr>
    <td><img src="docs/store-listing/screenshots/phone/05-media.jpg" alt="Screenshots with content filters" width="180"></td>
    <td><img src="docs/store-listing/screenshots/phone/06-cast.jpg" alt="Cast and staff" width="180"></td>
    <td><img src="docs/store-listing/screenshots/phone/07-releases.jpg" alt="Releases" width="180"></td>
    <td></td>
  </tr>
</table>

## Technologies
- Kotlin
- Jetpack Compose (Material 3, Adaptive)
- Navigation 3
- Koin (Annotations, via the Koin compiler plugin)
- Ktor
- kotlinx.serialization
- Coroutines and Flow
- Coil
- Detekt

## Architecture
- Single `:app` module, organised by feature (discover, browse, detail, character, release, profile, login)
- Clean Architecture: each feature has `data`, `domain`, `presentation` and `di` packages
- MVI in the presentation layer: a ViewModel exposes one state and one-off effects, and handles intents
- Shared code lives in `common`

## Requirements
- Android Studio (latest stable)
- JDK 11+
- Android 8.1 (API 27) or newer

## Build

Two flavors, each with debug and release builds:

| Flavor | Application id | App name | Release signing |
|---|---|---|---|
| `development` | `com.zc.vndb.dev` (version `…-dev`) | MyVNDB Dev | `debug.keystore` in the repo |
| `production` | `com.zc.vndb` | MyVNDB | Play upload key, passed in when building (see [Releasing](#releasing)) |

Both flavors can be installed side by side.

```bash
./gradlew installDevelopmentDebug
```

```bash
./gradlew assembleDevelopmentRelease
```

```bash
./gradlew testDevelopmentDebugUnitTest
```

## Releasing

Pushing a tag `v<versionName>-<versionCode>-<track>` builds the `productionRelease` bundle with that version, signs it with the Play upload key and publishes it to that Google Play track (`.github/workflows/play-release.yml`).

| Track | Google Play |
|---|---|
| `internal` | Internal testing |
| `alpha` | Closed testing – Alpha |
| `beta` | Open testing |
| `production` | Production |

```bash
git tag -a v1.0.1-3-alpha -m "What's new in this release (at most 500 characters)."
```

```bash
git push origin v1.0.1-3-alpha
```

- The tag's message becomes the release notes.
- Each version code can be uploaded to Play only once, so every new build needs a higher code.

Setup, secrets and troubleshooting: [docs/plans/18-play-release-pipeline.md](docs/plans/18-play-release-pipeline.md).

## Disclaimer

Not affiliated with vndb.org. All data is provided by VNDB and subject to its terms.

## License

Licensed under the [Apache License, Version 2.0](LICENSE).
