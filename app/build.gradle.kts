plugins {
    alias(libs.plugins.vndb.android.application)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.vndb.detekt)
    alias(libs.plugins.koin.compiler)
    // Generates res/raw/aboutlibraries.json (the Licenses page) from the dependencies at build time.
    alias(libs.plugins.aboutlibraries.android)
}

// Prefix for the APK and AAB file names (myvndb-development-release.apk) instead of the module name.
base {
    archivesName = "myvndb"
}

android {
    namespace = "com.zc.vndb"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.zc.vndb"
        minSdk = 27
        targetSdk = 37
        // CI passes -PversionCode and -PversionName (the Play release workflow takes them from the
        // release tag); local builds keep these defaults.
        versionCode = providers.gradleProperty("versionCode").map(String::toInt).getOrElse(2)
        versionName = providers.gradleProperty("versionName").getOrElse("1.0.0")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        // The public Android debug keystore committed at the repo root, so every machine
        // (and CI) signs development release builds with the same key.
        create("development") {
            storeFile = rootProject.file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    flavorDimensions += "environment"
    productFlavors {
        // Installs next to production: its own application id, version suffix and app name.
        // Shows sensitive content like vndb.org: blurred images can be revealed after the 18+ question.
        create("development") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            buildConfigField("boolean", "SHOW_SENSITIVE_CONTENT", "true")
        }
        // The Google Play build: sensitive images stay blurred and can never be revealed.
        create("production") {
            dimension = "environment"
            buildConfigField("boolean", "SHOW_SENSITIVE_CONTENT", "false")
        }
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlinx.**")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

// Only developmentRelease uses the repo keystore; debug builds keep Android's default debug
// signing. productionRelease has no signing config here: it is signed with the Play upload key by
// whoever builds it, through AGP's android.injected.signing.* properties (the Play release workflow,
// or Android Studio's Generate Signed App Bundle). Without them it stays unsigned.
androidComponents {
    onVariants(selector().withFlavor("environment" to "development").withBuildType("release")) { variant ->
        variant.signingConfig.setConfig(android.signingConfigs.getByName("development"))
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.bundles.ktor.client)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.annotations)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.ktor3)

    implementation(libs.aboutlibraries.compose.m3)

    testImplementation(platform(libs.koin.bom))
    testImplementation(libs.bundles.test.unit)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
