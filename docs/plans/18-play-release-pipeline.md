# Plan 18 — Publish to Google Play from a tag

**Status: Implemented on `feature/play-release-pipeline` with fastlane (§9). Checked locally; the first `-alpha` and `-production` tags are the end-to-end checks.** An earlier script-based promotion was dropped for fastlane before it was pushed.

Goal: pushing a tag `v<versionName>-<versionCode>-<track>` publishes to that Google Play track.
- **Testing tracks:** the workflow builds `productionRelease` as an App Bundle with that version, signs it with the upload key and uploads it.
- **Production:** it promotes the build Alpha already has with that version code. Nothing is built.

Starting point: the app is live on Closed testing – Alpha (uploaded by hand, with Play App Signing). Production hasn't been released yet.

Rules: `.claude/skills/*` (`library-management` for Gradle: no new library is needed). fastlane is CI tooling only: it isn't an app dependency, so it goes in a `Gemfile`, not the version catalog or `tech-stack`.

---

## 1. How it works

```text
git push origin v1.0.1-3-alpha                        git push origin v1.0.1-3-production
        │                                                     │
        ▼                                                     ▼
GitHub Actions  .github/workflows/play-release.yml  (on: push tags v<x.y.z>-<code>-*)
  parse    tag → versionName, versionCode, track   (bad tag: fails in seconds)
  publish  (environment "google-play": holds the secrets, only release tags may use it)
           Ruby + fastlane (versions pinned in Gemfile.lock)
        │                                                     │
   track internal / alpha / beta                         track production
   notes from the tag message                            fastlane supply
   → unit tests → bundleProductionRelease                  --track alpha
     -PversionCode=3 -PversionName=1.0.1,                  --track_promote_to production
     signed via android.injected.signing.*                 --version_code 3
   → check it is signed → keep AAB + mapping             → Alpha's release with code 3,
   → fastlane supply --track alpha --aab …                  notes included, goes to production
        │                                                     │
        ▼                                                     ▼
Play App Signing re-signs → Closed testing – Alpha       Production (same build testers had)
```

Tooling: fastlane's `supply` (`upload_to_play_store`) for both uploading and promoting, called from the workflow with `bundle exec fastlane supply --option value`, with no Fastfile.

- **One tool for both jobs:** the same key file, the same command and the same kind of options for both.
- **Promotes by version code:** `--version_code` picks the release to promote from Alpha. If Alpha doesn't have it, the run fails instead of promoting something else (§5).
- **Maintained:** fastlane 2.242.0 was released on 16 Nov 2026, and its Play docs cover promotion, forced promotion and Workload Identity credentials.
- **Pinned:** `Gemfile.lock` pins fastlane and every gem it pulls in, because this step gets the Play key. `ruby/setup-ruby` installs exactly those and caches them between runs.
- **Rejected:**
  - `r0adkll/upload-google-play` can only upload.
  - The one dedicated promote action (`The-Vincit-Group/promote-google-play-track-action`) was archived in July 2025 and runs on Node 12.
  - Gradle Play Publisher is in maintenance mode, and would add a Gradle plugin to the app.

## 2. Tag contract

`v<versionName>-<versionCode>-<track>`

| Tag | Track (Play API name) | Console name | What happens |
|---|---|---|---|
| `v1.0.1-3-internal` | `internal` | Internal testing | Build and upload |
| `v1.0.1-3-alpha` | `alpha` | Closed testing – Alpha | Build and upload |
| `v1.0.1-3-beta` | `beta` | Open testing | Build and upload |
| `v1.0.1-3-production` | `production` | Production | Promote code 3 from Alpha (§5) |

Version and code name one build; the track is where it goes. So `v1.0.1-3-alpha` and then `v1.0.1-3-production` read as the same build moving forward, and each version's tags sort side by side.

- Regex: `^v([0-9]+\.[0-9]+\.[0-9]+)-([1-9][0-9]{0,9})-(internal|alpha|beta|production)$`. Tracks are an allow-list, so a typo (`v1.0.1-3-alhpa`) fails instead of creating a custom track. Adding a custom closed track means adding its name to the list.
- The workflow only triggers on `v[0-9]+.[0-9]+.[0-9]+-[0-9]+-*`, so other `v` tags (a plain `v1.0.0`) never start it.
- `versionCode` ≤ 2100000000 (Play's limit). Play only accepts a code once per app, across all tracks, so each upload needs a code no build has used before.
- Release notes come from the tag message, on testing tracks only:
  - Use an annotated tag: its message becomes "What's new" (en-US, at most 500 characters). A lightweight tag gets "Bug fixes and improvements."
  - A production tag's message isn't used; the promoted release keeps its Alpha notes.
- Tools that parse tags strictly as SemVer read these as pre-releases of `x.y.z` (`3-alpha` is the pre-release part). `git tag --sort=v:refname` and `sort -V` order them correctly; this only matters if such a tool is added later.

```bash
git tag -a v1.0.1-3-alpha -m "Trending: See all. Faster theme screen."
git push origin v1.0.1-3-alpha
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

## 4. Workflow (`.github/workflows/play-release.yml`) and `Gemfile`

Everything specific to this app is in the top `env:` block. To reuse the workflow in another app, copy it with the `Gemfile` and `Gemfile.lock`, change those five values, and set `UPLOAD_*` and `PLAY_SERVICE_ACCOUNT_JSON` in that repo's `google-play` environment.

`Gemfile` (repo root):

```ruby
source "https://rubygems.org"

# Google Play uploads and promotions in .github/workflows/play-release.yml.
gem "fastlane", "~> 2.242"
```

`Gemfile.lock` is generated with `bundle lock --add-platform x86_64-linux`, so the Linux runner installs the same versions as the lock.

```yaml
name: Play release

# Push v<versionName>-<versionCode>-<track> (e.g. v1.0.1-3-alpha). For a testing track, the release
# bundle is built with that version and uploaded there. For production, the build Alpha has with
# that version code is promoted.
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
  PROMOTE_FROM: alpha                     # production tags promote the build this track has

jobs:
  parse:
    # Unchanged from the current branch: checks the tag, outputs version_name, version_code, track.

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

      # fastlane at the versions in Gemfile.lock, cached between runs.
      - name: Set up Ruby and fastlane
        uses: ruby/setup-ruby@145243922b4465b78e04dd66294782c7de45901f # v1.345.0
        with:
          ruby-version: "3.4"
          bundler-cache: true

      - name: Write the Play service account key
        env:
          PLAY_SERVICE_ACCOUNT_JSON: ${{ secrets.PLAY_SERVICE_ACCOUNT_JSON }}
        run: |
          [ -n "$PLAY_SERVICE_ACCOUNT_JSON" ] || { echo "::error::PLAY_SERVICE_ACCOUNT_JSON is not set on the google-play environment"; exit 1; }
          printf '%s' "$PLAY_SERVICE_ACCOUNT_JSON" > "$RUNNER_TEMP/play-service-account.json"
          # A job-level env can't use the runner context, so the path is exported from here.
          echo "PLAY_KEY=$RUNNER_TEMP/play-service-account.json" >> "$GITHUB_ENV"

      # ── Production: promote the build Alpha has with this version code (§5). Nothing is built.
      - name: Promote to production
        if: env.TRACK == 'production'
        run: >-
          bundle exec fastlane supply
          --package_name "$APP_ID"
          --json_key "$PLAY_KEY"
          --track "$PROMOTE_FROM"
          --track_promote_to production
          --version_code "$VERSION_CODE"
          --skip_upload_metadata true --skip_upload_changelogs true
          --skip_upload_images true --skip_upload_screenshots true

      # ── Testing tracks: build, sign and upload.

      # The annotated tag's message is Play's "What's new" (en-US, at most 500 characters), written
      # where fastlane looks for this version's notes: <metadata_path>/en-US/changelogs/<versionCode>.txt.
      # Fetch the tag object itself: checkout can leave only a lightweight ref.
      - name: Release notes from the tag
        if: env.TRACK != 'production'
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
          mkdir -p "$RUNNER_TEMP/metadata/en-US/changelogs"
          printf '%s\n' "$notes" > "$RUNNER_TEMP/metadata/en-US/changelogs/$VERSION_CODE.txt"

      # Set up JDK, Set up Gradle, Decode the upload keystore, Test and build the bundle,
      # Find the bundle and check it is signed, Keep the bundle and mapping:
      # unchanged from the current branch, each with `if: env.TRACK != 'production'`.

      # Only the bundle, its mapping and this version's notes go up: the skip flags keep fastlane
      # away from the store listing, which it would otherwise sync from the metadata folder.
      - name: Upload to Google Play
        if: env.TRACK != 'production'
        run: >-
          bundle exec fastlane supply
          --package_name "$APP_ID"
          --json_key "$PLAY_KEY"
          --track "$TRACK"
          --aab "$AAB"
          ${MAPPING:+--mapping "$MAPPING"}
          --release_status completed
          --metadata_path "$RUNNER_TEMP/metadata"
          --skip_upload_metadata true --skip_upload_images true --skip_upload_screenshots true

      - name: Summary
        run: |
          if [ "$TRACK" = "production" ]; then what="Promoted"; else what="Built and published"; fi
          echo "$what $VERSION_NAME ($VERSION_CODE) to $TRACK." >> "$GITHUB_STEP_SUMMARY"
```

Notes on the fastlane calls (from fastlane's docs and `supply/lib/supply/uploader.rb`):

- The skip flags matter: with a metadata folder and no skip flags, fastlane would also update the listing text, images and screenshots, and a promotion would try to update changelogs on production. `--skip_upload_metadata` doesn't cover changelogs, so uploads still send this version's notes.
- The upload's release name is fastlane's default (from the bundle's version name). Nothing checks names anymore.
- Every flag above is in `fastlane supply --help` for 2.242.0. Boolean flags take an optional value (`--skip_upload_metadata [VALUE]`), so `--skip_upload_metadata true` is valid. fastlane rejects unknown flags (`invalid option`), and both commands parse.

## 5. Production: promote, don't rebuild

Play takes each version code once, so the build that passed testing on Alpha can't be uploaded to production again. A production tag promotes it instead:

```text
v1.0.1-3-alpha        → builds 3 (1.0.1), uploads it to Alpha
v1.0.1-3-production   → promotes Alpha's release with code 3 to production (no build)
```

What `fastlane supply --track alpha --track_promote_to production --version_code 3` does (per `uploader.rb`):

1. Reads Alpha and keeps the release whose version codes include 3. Play only returns the release a track currently serves, so that's the build testers have now.
2. Copies that release as Play returns it (name, version code, release notes) to production as a completed release (`track_promote_release_status`, default `completed`), replacing what production had.
3. Commits the edit. Alpha isn't edited.

Consequences:

- **Fails if Alpha doesn't serve code 3**, with "Track 'alpha' doesn't have any releases". That happens if the code was mistyped, or a newer Alpha build has replaced it. Nothing changes on Play.
- **Only the code is checked.** The version name in a production tag isn't compared with the build, so it only documents what you're promoting.
- **No production-only hotfix by tag.** A fix goes to Alpha first (`-alpha` tag), then production (`-production` tag with the same name and code). Play Console's **Promote release** still works by hand for anything else.
- **Pushing the same production tag again** runs the same promotion, which leaves production as it is.
- **Staged rollout:** add `--rollout 0.2` to the promote step (status `inProgress`, 20%), then raise it in Play Console. Not part of this change.

## 6. What you need to set up (once)

The app is already live on Closed testing – Alpha, so the first manual upload and Play App Signing are done. A to D don't depend on the repo changes. E needs §9 merged. Switching to fastlane changes nothing in A to D: same secrets, same service account and permissions.

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

Tag the version name and code that Alpha currently has, e.g. `v1.0.1-3-production`. That build is promoted as is, with no rebuild. This also works for the build you uploaded by hand, as long as it's still the one on Alpha. Use a lightweight tag or any message; production keeps Alpha's notes.

## 7. Optional hardening

- **Workload Identity Federation** instead of the JSON key: GitHub gets a short-lived token, and no long-lived key exists. fastlane's `json_key` accepts a Workload Identity credentials file, so `google-github-actions/auth` (with a workload identity pool limited to this repo) replaces the "Write the Play service account key" step, and `PLAY_KEY` is set to `${{ steps.auth.outputs.credentials_file_path }}`. Worth it if key creation is blocked or you'd rather not hold a key.
- **Production only from `main`:** a step that fails a `v*-production` tag whose commit isn't on `origin/main`.
- **Tag ruleset** (Settings → Rules → Rulesets → Tag, `v*`): only you can create, move or delete release tags. It also makes up for the track sitting at the end of the tag, where a mistaken `-production` is easier to miss.
- **Staged production rollout:** `--rollout 0.2` on the promote step (§5).
- **Sharing across apps:** copying the workflow and `Gemfile`/`Gemfile.lock` (§4) works for a second app. From the third, or once copies drift, move the workflow into one shared repo as a reusable workflow (`on: workflow_call`, with `app-id`, `module`, `variant`, `test-task` and `promote-from` inputs). Each app then keeps a short caller that triggers on the tags and passes its values. Its `google-play` environment and secrets stay in each app's repo.
- **Store listing from the repo:** fastlane can also publish the listing text and screenshots. `docs/store-listing/` would move into fastlane's `metadata/android/<language>/` layout and the skip flags would come off. Not part of this change.

## 8. When it fails

| Error | Cause | Fix |
|---|---|---|
| The caller does not have permission | Service account not invited, missing app permissions, or still propagating | §6.C.5, wait a few hours |
| Version code N has already been used | That code is already on Play (testing tags) | New tag with a higher code |
| Track 'alpha' doesn't have any releases (production tag) | Alpha doesn't serve that version code: mistyped, or a newer Alpha build replaced it | Check Alpha → Releases. Tag the code Alpha has, or promote from Play Console |
| Precondition check failed (production) | Production access not granted yet | §6.A.2 |
| The Android App Bundle was signed with the wrong key | `UPLOAD_KEYSTORE_BASE64` isn't the keystore that signed the Alpha upload | §6.B.1: compare the SHA-256, re-encode the right keystore |
| Keystore was tampered with, or password was incorrect / Cannot recover key | Wrong `UPLOAD_KEYSTORE_PASSWORD`, `UPLOAD_KEY_ALIAS` or `UPLOAD_KEY_PASSWORD` | Fix the secret, re-run the job |
| Fails before "Upload to Google Play" or "Promote to production" | Bad tag, tests, build, or missing secret | Fix, then delete and re-push the tag (`git push origin :refs/tags/<tag>`) |
| Fails after the upload committed | The code is spent | New tag with a higher code |

## 9. Implementation (repo changes)

All on `feature/play-release-pipeline`.

**First commit** (`3f30115`):

1. `app/build.gradle.kts`: `versionCode` / `versionName` properties and the `productionRelease` signing comment (§3). `build-release-apk.yml` passes `-PversionCode`.
2. `.github/workflows/play-release.yml`: tag parsing, release notes, build, signing check and artifact. Its upload step used `r0adkll/upload-google-play`, which the second commit replaces.
3. `README.md`: a "Releasing" section.

These were checked locally by running the workflow's own `run:` blocks (taken from the YAML with `yq`):
- **Tag parsing:** valid tags give the right name, code and track. A misspelled track, a zero-padded code, a missing track and the old `release-…` format are refused.
- **Release notes:** an annotated tag's message is used, a lightweight tag gets the default, and notes above 500 characters are refused.
- **Build:** with a throwaway JKS key (key password different from the store password), the unit tests pass and `bundleProductionRelease` is signed by that key. The bundle's manifest has `versionCode` 12345 and `versionName` 9.8.7 from `-PversionCode` / `-PversionName`. The find-and-check step finds the bundle and mapping.
- **Without the injected properties,** the production bundle is unsigned. `assembleDevelopmentRelease -PversionCode=7` gives `versionCode` 7, `1.0.0-dev`, signed with `debug.keystore`.

**Second commit (fastlane):**

1. The script-based promotion (`play-api.sh`, its tests, the lookup and promote steps) was dropped before it was pushed.
2. `Gemfile` and `Gemfile.lock`: fastlane `~> 2.242`, locked at 2.242.0 for `arm64-darwin`, `ruby` and `x86_64-linux`. `.gitignore` ignores `/.bundle` and `/vendor/bundle`.
3. Workflow:
   - Ruby 3.4 and fastlane through `ruby/setup-ruby` (with `bundler-cache`);
   - the key-file step, which exports `PLAY_KEY` through `$GITHUB_ENV` because a job-level `env:` can't use `runner.temp`;
   - the production promote step;
   - notes written to `metadata/en-US/changelogs/<versionCode>.txt`;
   - `fastlane supply` in place of `r0adkll/upload-google-play`;
   - `if: env.TRACK != 'production'` on the build steps.
4. `README.md`: what each kind of tag does, with the alpha-then-production example.

These were checked locally, with fastlane installed from `Gemfile.lock` into a scratch folder (Ruby 3.2 here, 3.4 in CI):
- **Flags:** every flag in §4 is in `fastlane supply --help`. fastlane rejects unknown flags, and the promote and upload commands as written in the YAML (taken with `yq`) both parse, with and without a mapping file. They stop at the missing key file, before any network call.
- **YAML:** it parses. The step conditions are right: the promote step runs only for production, the build and upload steps only for testing tracks, and checkout, Ruby, the key and the summary run always.
- **Unchanged parts:** the `parse` job and the keystore, build and find-and-check `run:` blocks are byte-identical to the first commit, so the build checks above still hold.
- **Key step:** it writes the key file and `PLAY_KEY`, and fails with a clear message when the secret is missing.
- **Notes step:** an annotated tag's message lands in `metadata/en-US/changelogs/3.txt`.

Not checkable here: nothing can talk to Google Play without your key. The first `-alpha` tag (§6.E) checks the upload, and the first `-production` tag (§6.F) checks promotion, including that Alpha's notes carry over.
