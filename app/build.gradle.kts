plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // id("com.google.gms.google-services") version "4.4.0" // Comentado temporalmente
}

android {
    namespace = "com.example.magnus"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.magnus"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    
    // Jetpack Compose BOM and UI components
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    
    // Navigation for Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")
    
    // ViewModel for MVVM architecture
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    
    // Firebase BOM and services (comentado temporalmente)
    // implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    // implementation("com.google.firebase:firebase-auth-ktx")
    // implementation("com.google.firebase:firebase-firestore-ktx")
    // implementation("com.google.firebase:firebase-storage-ktx")
    
    // Coroutines for async operations
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    // implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3") // Comentado temporalmente
    
    // Google Maps SDK (comentado temporalmente)
    // implementation("com.google.android.gms:play-services-maps:18.2.0")
    // implementation("com.google.maps.android:maps-compose:4.3.3")
    
    // Image loading with Coil
    implementation("io.coil-kt:coil-compose:2.6.0")
    
    // Lottie animations
    implementation("com.airbnb.android:lottie-compose:6.3.0")
    
    // Accompanist - UI enhancements
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.34.0")
    implementation("com.google.accompanist:accompanist-placeholder-material3:0.34.0")
    
    // DataStore for local persistence
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // Gson for JSON serialization
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Retrofit for API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}