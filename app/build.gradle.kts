plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
}

android {
    namespace = "aodintsov.to_do_list"
    compileSdk = 34

    defaultConfig {
        applicationId = "aodintsov.to_do_list"
        minSdk = 28
        targetSdk = 34
        versionCode = 4
        versionName = "1.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += setOf("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    kapt {
        correctErrorTypes = true
        arguments {
            arg("dagger.hilt.disableModulesHaveInstallInCheck", "true")
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.activity:activity-compose:1.9.1")
    implementation(platform("androidx.compose:compose-bom:2024.08.00"))
    implementation("androidx.compose.ui:ui:1.6.8")
    implementation("androidx.compose.ui:ui-graphics:1.6.8")
    implementation("androidx.compose.ui:ui-tooling:1.6.8")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.8")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.8")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.31.3-beta")
    implementation("androidx.compose.material:material:1.6.8")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.8")
    implementation (libs.retrofit)
    implementation (libs.converter.gson)


    // Hilt dependencies
    implementation("com.google.dagger:hilt-android:2.52")
    kapt("com.google.dagger:hilt-compiler:2.52")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("jakarta.inject:jakarta.inject-api:2.0.1")

    // Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.0")
    implementation("androidx.compose.material:material-icons-extended:1.6.8")
    implementation("com.google.firebase:firebase-ads:23.3.0")
    implementation("com.google.android.gms:play-services-ads:23.3.0") {
        exclude(group = "com.google.android.gms", module = "play-services-measurement-api")
        exclude(group = "com.google.android.gms", module = "play-services-measurement-sdk-api")
    }

    implementation("androidx.compose.material3:material3:1.2.1") {
        exclude(group = "com.intellij", module = "annotations")
    }
    implementation("androidx.media3:media3-common:1.4.1")
}

kapt {
    correctErrorTypes = true
}

// Exclude conflicting annotations and modules
//configurations.all {
//    exclude(group = "com.intellij", module = "annotations")
//    exclude(group = "com.google.android.gms", module = "play-services-measurement-api")
//    exclude(group = "com.google.android.gms", module = "play-services-measurement-sdk-api")
//}
