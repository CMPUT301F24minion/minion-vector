plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.minion_project"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.minion_project"
        minSdk = 24
        targetSdk = 34
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
    tasks.withType<Test>{
        useJUnitPlatform()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    //firestore do not touch
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.auth)

    // Glide for image loading
    implementation(libs.glide)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.testng)
    testImplementation(libs.androidx.core)

    annotationProcessor(libs.compiler)

    // Android Instrumented Testing

    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.mockito.inline)

    // AndroidX Test (optional, for instrumentation tests)
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Mockito for Android Instrumented Tests

    implementation (libs.qrGenerator)

    //
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    // AndroidX Test
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:rules:1.5.0")

    // Espresso
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")

    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.mockito.inline)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.engine)

    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.androidx.test.espresso.espresso.core.v351)
    androidTestImplementation(libs.mockito.android)

    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)

    // Used for EventControllerTest
    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.8.2")  // JUnit 5 for tests
    testImplementation ("org.junit.jupiter:junit-jupiter-engine:5.8.2")  // JUnit 5 engine
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}