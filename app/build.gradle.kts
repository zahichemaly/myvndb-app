plugins {
    alias(libs.plugins.vndb.android.application)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.vndb.detekt)
    alias(libs.plugins.koin.compiler)
}

android {
    namespace = "com.zc.vndb"
    compileSdk {
        // 37.1: Compose 1.13 (Compose BOM 2026.11) requires it.
        version = release(37) { minorApiLevel = 1 }
    }

    defaultConfig {
        applicationId = "com.zc.vndb"
        minSdk = 27
        targetSdk = 37
        // CI sets GITHUB_RUN_NUMBER automatically; local builds keep 1.
        versionCode = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 1
        versionName = "1.0.0"

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
        create("development") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
        }
        create("production") {
            dimension = "environment"
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
// signing, and productionRelease is left unsigned until a real release key is set up.
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

    testImplementation(platform(libs.koin.bom))
    testImplementation(libs.bundles.test.unit)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
