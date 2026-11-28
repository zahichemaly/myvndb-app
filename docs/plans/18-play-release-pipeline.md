# Plan 18 — Publish to Google Play from a tag

**Status: Planned.**

Goal: pushing a tag `v<versionName>-<versionCode>-<track>` builds `productionRelease` as an App Bundle with that version, signs it with the upload key, and publishes it to that Google Play track.

Starting point: the app is live on Closed testing – Alpha (uploaded by hand, with Play App Signing). Production hasn't been released yet.

Rules: `.claude/skills/*` (`library-management` for Gradle: no new library is needed).

---

## 1. How it works

```text
git push origin v1.0.0-3-alpha
        │
        ▼
GitHub Actions  .github/workflows/play-release.yml  (on: push tags v<x.y.z>-<code>-*)
  parse    tag → versionName=1.0.0, versionCode=3, track=alpha   (bad tag: fails in seconds)
  publish  (environment "google-play": holds the secrets, only release tags may use it)
           notes from the tag message → unit tests → bundleProductionRelease
           -PversionCode=3 -PversionName=1.0.0, signed via android.injected.signing.*
           → check it is signed → keep AAB + mapping as a run artifact
           → r0adkll/upload-google-play → track "alpha", release "3 (1.0.0)"
        │
        ▼
Play App Signing re-signs with the app signing key → Closed testing – Alpha
```

Tooling: `r0adkll/upload-google-play` (v1.1.5, pinned by commit SHA because it receives the Play key). It keeps publishing out of Gradle, so AGP upgrades never wait on a publishing plugin and `tech-stack` doesn't change. Gradle Play Publisher 4.x does support AGP 9, but it is in maintenance mode and would add a build dependency for something one workflow step does.

## 2. Tag contract

`v<versionName>-<versionCode>-<track>`

| Tag | Track (Play API name) | Console name |
|---|---|---|
| `v1.0.0-3-internal` | `internal` | Internal testing |
| `v1.0.0-3-alpha` | `alpha` | Closed testing – Alpha |
| `v1.0.0-3-beta` | `beta` | Open testing |
| `v1.0.0-3-production` | `production` | Production |

Version and code name one build; the track is where it goes. So `v1.0.0-3-alpha` and then `v1.0.0-3-production` read as the same build moving forward (§5), and each version's tags sort side by side.

- Regex: `^v([0-9]+\.[0-9]+\.[0-9]+)-([1-9][0-9]{0,9})-(internal|alpha|beta|production)$`. Tracks are an allow-list, so a typo (`v1.0.0-3-alhpa`) fails instead of creating a custom track. Adding a custom closed track means adding its name to the list.
- The workflow only triggers on `v[0-9]+.[0-9]+.[0-9]+-[0-9]+-*`, so other `v` tags (a plain `v1.0.0`) never start it.
- `versionCode` ≤ 2100000000 (Play's limit). Play only accepts a code once per app, across all tracks, so each upload needs a code no build has used before.
- Use an annotated tag: its message becomes "What's new" (en-US, at most 500 characters). A lightweight tag gets "Bug fixes and improvements."
- Tools that parse tags strictly as SemVer read these as pre-releases of `x.y.z` (`3-alpha` is the pre-release part). `git tag --sort=v:refname` and `sort -V` order them correctly; this only matters if such a tool is added later.

```bash
git tag -a v1.0.0-3-alpha -m "Trending: See all. Faster theme screen."
git push origin v1.0.0-3-alpha
```

## 3. Gradle properties

The pipeline talks to Gradle only through property names that say nothing about this app, so the same workflow works in another Android project.

| Property | Whose | What it does |
|---|---|---|
| `versionCode`, `versionName` | Ours | Read by `defaultConfig`. The only build-script lines an app needs for this pipeline. |
| `android.injected.signing.store.file`, `…store.password`, `…key.alias`, `…key.password` | AGP's | AGP signs what it builds with this key, with no `signingConfig` in the build script. Android Studio's **Generate Signed App Bundle** passes the same four. |

Why these names:

- `versionCode` / `versionName` match the DSL fields they set, so `-PversionCode=3` needs no explanation. A prefix (`pVersionCode`, `vndbVersionCode`) adds nothing, since `-P` already says it's a property. They don't clash with Gradle's own `version`.
- AGP used to have `android.injected.version.code` / `.name` as well, but 9.5.3 no longer has them (checked in its option classes), so the version needs our own two.
- `android.injected.signing.*` are still in AGP 9.5.3. Checked by building `bundleProductionRelease` with them and a throwaway key: `jarsigner` showed the bundle signed by that key. They're what Android Studio uses, but not documented API. If a later AGP drops them, the workflow's signature check stops the release before anything reaches Play.

`app/build.gradle.kts`:

```kotlin
defaultConfig {
    // CI passes -PversionCode / -PversionName (from the release tag); local builds keep these defaults.
    versionCode = providers.gradleProperty("versionCode").map(String::toInt).getOrElse(2)
    versionName = providers.gradleProperty("versionName").getOrElse("1.0.0")
}
```

- No signing change. `productionRelease` stays unsigned unless the injected properties are passed; only its comment changes, to say who signs it (the release workflow, or Android Studio's Generate Signed App Bundle).
- Injected signing applies to every variant built in that Gradle run, so pass it only when building a release bundle. Never put it in `~/.gradle/gradle.properties`: every Android project on the machine, debug builds included, would be signed with the upload key.
- `build-release-apk.yml` drops the `GITHUB_RUN_NUMBER` read from the build script and passes `-PversionCode=$GITHUB_RUN_NUMBER` instead.

## 4. Workflow (`.github/workflows/play-release.yml`)

Everything specific to this app is in the top `env:` block. To reuse the workflow in another app, copy it and change those four values (plus `UPLOAD_*` and `PLAY_SERVICE_ACCOUNT_JSON` in that repo's `google-play` environment).

```yaml
name: Play release

# Push v<versionName>-<versionCode>-<track> (e.g. v1.0.0-3-alpha) to build the release bundle
# with that version and publish it to that Google Play track.
on:
  push:
    tags:
      - "v[0-9]+.[0-9]+.[0-9]+-[0-9]+-*"

permissions:
  contents: read

# Play edits on one app must not overlap.
concurrency:
  group: play-release
  cancel-in-progress: false

# The only project-specific values. To reuse this workflow in another app, change these.
env:
  APP_ID: com.zc.vndb                     # Play package name
  MODULE: app                             # Gradle module that builds the app
  VARIANT: productionRelease              # builds :<module>:bundle<Variant>
  TEST_TASK: testDevelopmentDebugUnitTest # runs before the build; empty to skip

jobs:
  parse:
    runs-on: ubuntu-latest
    timeout-minutes: 2
    outputs:
      track: ${{ steps.tag.outputs.track }}
      version_name: ${{ steps.tag.outputs.version_name }}
      version_code: ${{ steps.tag.outputs.version_code }}
    steps:
      - id: tag
        env:
          TAG: ${{ github.ref_name }}
        run: |
          re='^v([0-9]+\.[0-9]+\.[0-9]+)-([1-9][0-9]{0,9})-(internal|alpha|beta|production)$'
          if [[ ! "$TAG" =~ $re ]]; then
            echo "::error::'$TAG' is not v<x.y.z>-<versionCode>-<internal|alpha|beta|production>, e.g. v1.0.0-3-alpha"
            exit 1
          fi
          if (( BASH_REMATCH[2] > 2100000000 )); then
            echo "::error::versionCode ${BASH_REMATCH[2]} is above Play's limit (2100000000)"
            exit 1
          fi
          {
            echo "version_name=${BASH_REMATCH[1]}"
            echo "version_code=${BASH_REMATCH[2]}"
            echo "track=${BASH_REMATCH[3]}"
          } >> "$GITHUB_OUTPUT"

  publish:
    needs: parse
    runs-on: ubuntu-latest
    timeout-minutes: 30
    # Holds the Play and upload-key secrets; only release tags (v*.*.*-*-*) may deploy to it.
    environment: google-play
    env:
      TRACK: ${{ needs.parse.outputs.track }}
      VERSION_NAME: ${{ needs.parse.outputs.version_name }}
      VERSION_CODE: ${{ needs.parse.outputs.version_code }}
    steps:
      - name: Checkout
        uses: actions/checkout@3d3c42e5aac5ba805825da76410c181273ba90b1 # v7.0.1

      # The annotated tag's message is Play's "What's new" (en-US, at most 500 characters).
      # Fetch the tag object itself: checkout can leave only a lightweight ref.
      - name: Release notes from the tag
        run: |
          git fetch --force --no-tags origin "refs/tags/$GITHUB_REF_NAME:refs/tags/$GITHUB_REF_NAME"
          notes=""
          if [ "$(git cat-file -t "refs/tags/$GITHUB_REF_NAME")" = "tag" ]; then
            notes=$(git tag -l --format='%(contents:subject)%0a%0a%(contents:body)' "$GITHUB_REF_NAME")
          fi
          [ -n "$notes" ] || notes="Bug fixes and improvements."
          if (( ${#notes} > 500 )); then
            echo "::error::Release notes are ${#notes} characters; Play allows 500."
            exit 1
          fi
          mkdir -p "$RUNNER_TEMP/whatsnew"
          printf '%s\n' "$notes" > "$RUNNER_TEMP/whatsnew/whatsnew-en-US"

      - name: Set up JDK
        uses: actions/setup-java@d1374f8ac74474200d16ef8ecee22c7ae7a51880 # v6.3.0
        with:
          distribution: temurin
          java-version: 25 # the Gradle daemon JVM (gradle/gradle-daemon-jvm.properties), so Gradle doesn't download it

      - name: Set up Gradle
        uses: gradle/actions/setup-gradle@8da46b539cd8fe6ab27ca75128919a18d757604c # v6.4.1

      - name: Decode the upload keystore
        env:
          UPLOAD_KEYSTORE_BASE64: ${{ secrets.UPLOAD_KEYSTORE_BASE64 }}
        run: |
          [ -n "$UPLOAD_KEYSTORE_BASE64" ] || { echo "::error::UPLOAD_KEYSTORE_BASE64 is not set on the google-play environment"; exit 1; }
          printf '%s' "$UPLOAD_KEYSTORE_BASE64" | base64 --decode > "$RUNNER_TEMP/upload.jks"

      - name: Test and build the bundle
        env:
          UPLOAD_KEYSTORE_PASSWORD: ${{ secrets.UPLOAD_KEYSTORE_PASSWORD }}
          UPLOAD_KEY_ALIAS: ${{ secrets.UPLOAD_KEY_ALIAS }}
          UPLOAD_KEY_PASSWORD: ${{ secrets.UPLOAD_KEY_PASSWORD }}
        # productionRelease → :app:bundleProductionRelease. $TEST_TASK is unquoted on purpose:
        # when it's empty, no task is passed.
        run: |
          first=$(printf %s "${VARIANT:0:1}" | tr '[:lower:]' '[:upper:]')
          ./gradlew $TEST_TASK ":$MODULE:bundle${first}${VARIANT:1}" \
            -PversionCode="$VERSION_CODE" \
            -PversionName="$VERSION_NAME" \
            -Pandroid.injected.signing.store.file="$RUNNER_TEMP/upload.jks" \
            -Pandroid.injected.signing.store.password="$UPLOAD_KEYSTORE_PASSWORD" \
            -Pandroid.injected.signing.key.alias="$UPLOAD_KEY_ALIAS" \
            -Pandroid.injected.signing.key.password="$UPLOAD_KEY_PASSWORD" \
            --stacktrace

      # Finds the outputs whatever the module's archivesName, and fails on an unsigned bundle.
      - name: Find the bundle and check it is signed
        run: |
          aab=$(find "$MODULE/build/outputs/bundle/$VARIANT" -name '*.aab' | head -n 1)
          [ -n "$aab" ] || { echo "::error::No .aab in $MODULE/build/outputs/bundle/$VARIANT"; exit 1; }
          jarsigner -verify "$aab" | grep -q "jar verified" \
            || { echo "::error::$aab is not signed: check the android.injected.signing.* properties"; exit 1; }
          mapping="$MODULE/build/outputs/mapping/$VARIANT/mapping.txt"
          [ -f "$mapping" ] || mapping="" # no R8, no mapping
          { echo "AAB=$aab"; echo "MAPPING=$mapping"; } >> "$GITHUB_ENV"

      - name: Keep the bundle and mapping
        uses: actions/upload-artifact@cf430e030ddbb5b0abf93d22962f4752f3646cd9 # v7.0.2
        with:
          name: ${{ env.APP_ID }}-${{ env.VERSION_NAME }}-${{ env.VERSION_CODE }}
          path: |
            ${{ env.AAB }}
            ${{ env.MAPPING }}
          retention-days: 30

      - name: Upload to Google Play
        uses: r0adkll/upload-google-play@e738b9dd8f2476ea806d921b64aacd24f34515a5 # v1.1.5
        with:
          serviceAccountJsonPlainText: ${{ secrets.PLAY_SERVICE_ACCOUNT_JSON }}
          packageName: ${{ env.APP_ID }}
          releaseFiles: ${{ env.AAB }}
          mappingFile: ${{ env.MAPPING }}
          tracks: ${{ env.TRACK }}
          # Same form as Play Console's default name, like the Alpha release uploaded by hand.
          releaseName: ${{ env.VERSION_CODE }} (${{ env.VERSION_NAME }})
          status: completed
          whatsNewDirectory: ${{ runner.temp }}/whatsnew

      - name: Summary
        run: echo "Published $VERSION_NAME ($VERSION_CODE) to $TRACK." >> "$GITHUB_STEP_SUMMARY"
```

## 5. Recommended: promote to production, don't rebuild

The problem with the design as specified: a version code can be uploaded once per app. After `v1.0.0-3-alpha`, the tag `v1.0.0-3-production` would build and upload code 3 again, and Play rejects it. Going to production then needs `v1.0.0-4-production`: a fresh build that no tester ran, and the alpha testers' build can never become the production one.

The fix keeps the tag format. Before building, the workflow asks Play whether the tag's version code is already uploaded:

- **Not uploaded:** build and upload, as in §4 (a hotfix can still go straight to production with a new code).
- **Already uploaded:** promote it. The target track gets a release with that version code, the release name and notes are copied from the track that has it, and the edit is committed. Nothing is built. It fails if that release's name doesn't contain the tag's version name, so a mistyped version name can't promote the wrong build. Releases are named `<versionCode> (<versionName>)`, Play Console's default, so the Alpha build uploaded by hand can be promoted the same way.

```text
v1.0.0-3-alpha        → builds 3 (1.0.0), uploads it to alpha
v1.0.0-3-production   → code 3 is on Play → promotes it from alpha to production (no build)
v1.0.1-4-production   → code 4 is new → builds and uploads straight to production
```

Implementation: `.github/scripts/play-api.sh` (bash, `curl`, `jq`, `openssl`, all on `ubuntu-latest`; no new dependencies), two commands:

- `find <code>` prints `exists=true|false` and `from_track=…` to `$GITHUB_OUTPUT`: it opens an edit, reads `edits.bundles.list` and `edits.tracks.list`, then deletes the edit.
- `promote <code> <track> <versionName>`: opens an edit, `PUT edits.tracks/<track>` with `{name, versionCodes: [code], status, releaseNotes}`, then `edits.commit`.
- It takes the package name from the workflow's `APP_ID`, so it's as reusable as the workflow.
- Auth: signs a JWT with the service account key (`openssl dgst -sha256 -sign`) and exchanges it for an `androidpublisher` access token. Same `PLAY_SERVICE_ACCOUNT_JSON` secret; it needs the "View app information" permission (§6.C).

In the workflow, a `find` step runs right after checkout. The build, sign-check, artifact and upload steps get `if: steps.play.outputs.exists == 'false'`, and a promote step gets `if: steps.play.outputs.exists == 'true'`.

Without it, the Play Console's own **Promote release** button (on the Alpha track) does the same by hand.

## 6. What you need to set up (once)

The app is already live on Closed testing – Alpha, so the first manual upload and Play App Signing are done. A to D don't depend on the repo changes and can be done now. E needs §9 merged.

### A. Play Console: before automating

1. **Note the highest version code used so far, and its version name.** Alpha's release shows it (**Test and release → Testing → Closed testing → Alpha → Releases**), and **App bundle explorer** lists every bundle uploaded to any track. Every tag from now on needs a higher code.
2. **Production access.** If your developer account is a personal account created after 13 Nov 2023, production stays locked until Alpha has had at least 12 opted-in testers for 14 days in a row and you apply for production access from the Dashboard. Until it's granted, `v*-production` tags fail at the upload. The Dashboard shows whether this applies to you and how far along the 14 days are.
3. **Production countries/regions.** Before the first production release, check that **Production → Countries/regions** lists where the app should be available. It's separate from Alpha's list.

### B. The upload key you already have

The Alpha upload registered its signing key as your upload key. Play rejects bundles signed with any other key, so CI must sign with that same keystore.

1. Find that keystore. Android Studio's **Build → Generate Signed App Bundle** dialog remembers its path and alias. Check it's the right one:
   ```bash
   keytool -list -v -keystore <path-to-keystore> -alias <alias>
   ```
   Its SHA-256 must match Play Console → **App integrity → App signing → Upload key certificate**.
2. Back up the keystore, its alias and passwords (password manager plus an offline copy). Never commit it. `debug.keystore` stays for development builds only.
3. **Lost it?** Make a new one and ask for an upload key reset (**App integrity → App signing → Request upload key reset**). It takes a few days, and uploads with the new key fail until Google confirms it.
   ```bash
   mkdir -p ~/keys && keytool -genkeypair -v -keystore ~/keys/myvndb-upload.jks -storetype PKCS12 -alias upload -keyalg RSA -keysize 4096 -validity 10000
   ```
   ```bash
   keytool -export -rfc -keystore ~/keys/myvndb-upload.jks -alias upload -file ~/keys/upload_certificate.pem
   ```
   The reset form asks for that `.pem` file.
4. Building a signed bundle locally works as it does now: Android Studio's **Build → Generate Signed App Bundle** passes the same `android.injected.signing.*` properties CI uses. Don't put them in `~/.gradle/gradle.properties` (§3).

### C. Service account (Google Cloud + Play Console)

1. [Google Cloud Console](https://console.cloud.google.com) → create a project (e.g. `myvndb-publishing`).
2. **APIs & Services → Library** → enable **Google Play Android Developer API**.
3. **IAM & Admin → Service accounts → Create service account**: `github-play-publisher`. Grant it no project roles.
4. Open it → **Keys → Add key → Create new key → JSON**. A `.json` file downloads.
   - If key creation is blocked (`iam.disableServiceAccountKeyCreation`, usual on Workspace-managed accounts), use Workload Identity Federation instead (§7).
5. Play Console → **Users and permissions → Invite new users**: the service account's email (`github-play-publisher@<project>.iam.gserviceaccount.com`).
   - **App permissions → Add app → MyVNDB**, and tick:
     - View app information and download bulk reports (read-only)
     - Release apps to testing tracks
     - Release to production, exclude devices, and use Play App Signing
   - **Invite user**. A service account needs no acceptance. Permissions can take a few hours to apply. Until then uploads fail with "The caller does not have permission".
6. After step D, delete the downloaded `.json` from your Mac.

### D. GitHub

1. Repo → **Settings → Environments → New environment** `google-play`.
   - **Deployment branches and tags → Selected branches and tags → Add rule → Tag** `v*.*.*-*-*`. Only release tags can then read its secrets.
   - Optional: **Required reviewers** (you) if you want an approval click before every publish.
2. In that environment, **Add environment secret**:

   | Secret | Value |
   |---|---|
   | `PLAY_SERVICE_ACCOUNT_JSON` | The whole content of the service account `.json` |
   | `UPLOAD_KEYSTORE_BASE64` | `base64 -i <path-to-keystore> \| pbcopy`, then paste |
   | `UPLOAD_KEYSTORE_PASSWORD` | The keystore password |
   | `UPLOAD_KEY_ALIAS` | The key alias (Android Studio's default is `key0`) |
   | `UPLOAD_KEY_PASSWORD` | The key password (the keystore password, if you didn't set a separate one) |

   Or, with the GitHub CLI (run `gh auth login` first):
   ```bash
   gh secret set PLAY_SERVICE_ACCOUNT_JSON --env google-play < ~/Downloads/<key>.json
   ```
   ```bash
   base64 -i <path-to-keystore> | gh secret set UPLOAD_KEYSTORE_BASE64 --env google-play
   ```
   ```bash
   gh secret set UPLOAD_KEYSTORE_PASSWORD --env google-play
   ```
   ```bash
   gh secret set UPLOAD_KEY_ALIAS --env google-play
   ```
   ```bash
   gh secret set UPLOAD_KEY_PASSWORD --env google-play
   ```

### E. First automated release (after §9 is merged)

Use a code above the highest from A.1. If Alpha has `2 (1.0.0)`:

```bash
git tag -a v1.0.1-3-alpha -m "First release from GitHub Actions."
```
```bash
git push origin v1.0.1-3-alpha
```

Watch it under **Actions → Play release**. The new release replaces the old one on Closed testing – Alpha, and testers get the update.

### F. First production release (once production access is granted)

- **With §5:** tag the build testers already have, with its exact version name and code, e.g. `v1.0.1-3-production`. It's promoted as is, with no rebuild. This also works for the build you uploaded by hand, as long as its release name still contains its version name (Play's default name, e.g. `2 (1.0.0)`, does).
- **Without §5:** use a new code (`v1.0.1-4-production`), or Play Console's **Promote release** on the Alpha track.

## 7. Optional hardening

- **Workload Identity Federation** instead of the JSON key: GitHub gets a short-lived token, and no long-lived key exists. `google-github-actions/auth` with a workload identity pool limited to this repo, then `serviceAccountJson: ${{ steps.auth.outputs.credentials_file_path }}` in the upload step. Worth it if key creation is blocked or you'd rather not hold a key.
- **Production only from `main`:** a step that fails a `v*-production` tag whose commit isn't on `origin/main`.
- **Tag ruleset** (Settings → Rules → Rulesets → Tag, `v*`): only you can create, move or delete release tags. It also makes up for the track sitting at the end of the tag, where a mistaken `-production` is easier to miss.
- **Staged production rollout:** `status: inProgress` with `userFraction: 0.2` for production, then raise it in Play Console.
- **Sharing across apps:** copying the workflow (§4) works for a second app. From the third, or once copies drift, move the workflow and `play-api.sh` into one shared repo as a reusable workflow (`on: workflow_call`, with `app-id`, `module`, `variant` and `test-task` inputs). Each app then keeps a short caller that triggers on the tags and passes its values. Its `google-play` environment and secrets stay in each app's repo.

## 8. When it fails

| Error | Cause | Fix |
|---|---|---|
| The caller does not have permission | Service account not invited, missing app permissions, or still propagating | §6.C.5, wait a few hours |
| Version code N has already been used | That code is already on Play | New tag with a higher code (or §5) |
| Precondition check failed (production) | Production access not granted yet | §6.A.2 |
| The Android App Bundle was signed with the wrong key | `UPLOAD_KEYSTORE_BASE64` isn't the keystore that signed the Alpha upload | §6.B.1: compare the SHA-256, re-encode the right keystore |
| Keystore was tampered with, or password was incorrect / Cannot recover key | Wrong `UPLOAD_KEYSTORE_PASSWORD`, `UPLOAD_KEY_ALIAS` or `UPLOAD_KEY_PASSWORD` | Fix the secret, re-run the job |
| Fails before "Upload to Google Play" | Bad tag, tests, or build | Fix, then delete and re-push the tag (`git push origin :refs/tags/<tag>`) |
| Fails after the upload committed | The code is spent | New tag with a higher code |

## 9. Implementation (repo changes)

1. `app/build.gradle.kts`: `versionCode` / `versionName` properties and the `productionRelease` signing comment (§3). `build-release-apk.yml` passes `-PversionCode`.
2. `.github/workflows/play-release.yml` (§4).
3. If §5 is accepted: `.github/scripts/play-api.sh` and its two steps.
4. `README.md`: a short "Releasing" section (tag format and an example).
5. Checks: `-PversionCode` / `-PversionName` end up in the bundle's manifest; `bundleProductionRelease` unsigned without the injected properties (signed with them is already checked, §3); `assembleDevelopmentRelease` unchanged; the tag regex against valid and invalid tags; `actionlint` on the workflows. The Play upload itself can only be checked by the first real tag (§6.E), since it needs your secrets.
