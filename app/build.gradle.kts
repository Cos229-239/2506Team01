import org.gradle.kotlin.dsl.apply

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.teamjg.dreamsanddoses"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.teamjg.dreamsanddoses"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.2")
    implementation("androidx.activity:activity-compose:1.10.1")

    //ViewModel support for Compose (Need for Calendar)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")

    //Core library desugaring to get local time for Java 8+
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

    // Single up-to-date BOM
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))

    // Compose dependencies
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")

    // Navigation & animation
    implementation("androidx.navigation:navigation-compose:2.9.2")
    implementation("androidx.compose.animation:animation")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.05.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.navigation:navigation-compose:2.9.2")
    implementation("androidx.compose.animation:animation:1.8.3")

    // Dante added concerning FireBase Authentication setup

    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-common-ktx")
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    //ML Kit text recognition for scanning text from images.
    implementation("com.google.mlkit:text-recognition:16.0.1")

    //CameraX dependencies for camera functionality
    implementation("androidx.camera:camera-core:1.4.2")
    implementation("androidx.camera:camera-camera2:1.4.2")
    implementation("androidx.camera:camera-lifecycle:1.4.2")
    implementation("androidx.camera:camera-view:1.4.2")

    //Image analyzing integration with ML Kit
    implementation("androidx.camera:camera-mlkit-vision:1.4.2")

}
apply(plugin = "com.google.gms.google-services")