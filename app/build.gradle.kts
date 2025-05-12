plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.yoursmartgymbuddy"
    compileSdk = 35 // You might want to check if this is the latest stable API

    defaultConfig {
        applicationId = "com.example.yoursmartgymbuddy"
        minSdk = 27 // Ensure this is the minimum version that suits your app's functionality
        targetSdk = 35 // Consider upgrading to the latest SDK
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
    buildFeatures {
        viewBinding = true
    }

    // If you're using Kotlin, you can enable Kotlin extensions here as well
    // kotlinOptions {
    //     jvmTarget = '1.8'
    // }
}



dependencies {
    // Basic Libraries
    implementation(libs.appcompat)
    implementation(libs.material)  // Add this line
    implementation("com.google.android.material:material:1.4.0")  // or the latest version
    implementation("com.google.gms:google-services:4.4.1")

    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")

    // Firebase
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)

    implementation ("org.osmdroid:osmdroid-android:6.1.16")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("com.google.code.gson:gson:2.8.9")
    implementation("com.google.firebase:firebase-firestore")


    // Play Services (for authentication, if needed)
    implementation(libs.credentials.play.services.auth)
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    // Testing Libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
