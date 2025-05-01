plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.tp_localisation"
    compileSdk = 34  // Gardé à 34 car c'est le maximum recommandé pour votre plugin

    defaultConfig {
        applicationId = "com.example.tp_localisation"
        minSdk = 24
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")  // Version compatible avec SDK 34
    implementation("com.google.android.material:material:1.11.0")  // Version compatible
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")  // Version stable
    implementation("com.android.volley:volley:1.2.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.6.2")
    implementation("com.google.android.gms:play-services-maps:18.2.0")  // Version compatible
    implementation("androidx.lifecycle:lifecycle-livedata:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime:2.6.2")
    implementation("com.google.android.gms:play-services-ads-identifier:17.0.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")  // Version mise à jour
    // Suppression de la duplication de play-services-maps
    implementation("androidx.activity:activity:1.8.2")  // Remplacé 1.10.1 par 1.8.2 qui est compatible avec SDK 34
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")  // Version stable
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")  // Version stable
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")  // Version mise à jour
    // Suppression de la duplication de material et constraintlayout
}