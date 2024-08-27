plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
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
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.1")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui:1.6.8")
    implementation("androidx.compose.ui:ui-graphics:1.6.8")
    implementation("androidx.compose.ui:ui-tooling:1.6.8")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.8")
    implementation("androidx.compose.material3:material3:1.2.0-alpha06")
    implementation("androidx.navigation:navigation-compose:2.7.1")
    implementation("androidx.compose.runtime:runtime-livedata:1.5.1")
    implementation("androidx.room:room-compiler:2.6.1")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.31.3-beta")
    implementation("androidx.compose.material:material:1.6.8")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.8")






    // Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation("com.google.firebase:firebase-firestore-ktx:25.0.0")
    implementation("androidx.compose.material:material-icons-extended:1.6.8")
    implementation("com.google.firebase:firebase-ads:23.2.0")
    implementation("com.google.android.gms:play-services-ads:23.2.0")

    implementation("androidx.compose.material3:material3:1.2.0-alpha06") {
        exclude(group = "com.intellij", module = "annotations")
    }
    implementation(libs.androidx.media3.common)
}

// Исключение конфликтующих аннотаций
configurations.all {
    exclude(group = "com.intellij", module = "annotations")
}
