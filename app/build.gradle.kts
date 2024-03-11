@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.pdoyle.nanameue"
    compileSdk = 34
    buildToolsVersion = "34.0.0"

    defaultConfig {
        applicationId = "com.pdoyle.nanameue"
        testApplicationId = "com.pdoyle.nanameue.test"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
        unitTests.all {
            it.useJUnitPlatform()
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.9"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.bundles.coroutines)

    implementation(libs.bundles.androidx)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    debugImplementation(libs.bundles.compose.debug)

    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    implementation(libs.dagger)
    ksp(libs.dagger.ksp)

    implementation(libs.timber)

    implementation(libs.coil)

    testImplementation(libs.bundles.unitTests)
    testImplementation(libs.junit.jupiter.engine)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.bundles.espresso)
}
