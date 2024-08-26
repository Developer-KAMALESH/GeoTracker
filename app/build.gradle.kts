plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.d4"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.example.d4"
        minSdk = 23
        targetSdk = 34
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
}

dependencies {
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")
    implementation("com.google.firebase:firebase-firestore-ktx:24.5.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity-ktx:1.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    implementation("com.google.android.material:material:1.9.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.google.firebase:firebase-firestore-ktx:24.5.0")
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation("com.google.firebase:firebase-firestore:25.1.0")
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Authentication (if needed)
    //implementation("com.google.firebase:firebase-auth-ktx:22.1.0")
}
