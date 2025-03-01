plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.projectpam"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.projectpam"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // Gunakan versi terbaru
    implementation("io.coil-kt:coil:2.2.2")

    implementation ("com.google.android.material:material:1.12.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-compose:2.8.7-alpha01") // Gunakan versi terbaru
    implementation("io.coil-kt:coil-compose:2.2.2")
    implementation ("androidx.compose.material:material-icons-core:1.7.5")
    implementation ("androidx.compose.material:material-icons-extended:1.7.5")

        // Firebase Authentication
        implementation ("com.google.firebase:firebase-auth-ktx:22.3.1")

        // Google Sign-In
        implementation ("com.google.android.gms:play-services-auth:20.7.0")

        // Credentials Manager
        implementation ("androidx.credentials:credentials:1.5.0-alpha05")
        implementation ("androidx.credentials:credentials-play-services-auth:1.5.0-alpha05")

    implementation(platform(libs.firebase.bom))


    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.firebase.storage.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}