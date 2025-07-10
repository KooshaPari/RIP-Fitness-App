plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.fitnessapp.core.network"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        targetSdk = 34

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        // API Keys configuration
        buildConfigField("String", "USDA_API_KEY", "\"${project.findProperty("USDA_API_KEY") ?: ""}\"")
        buildConfigField("String", "FATSECRET_CLIENT_ID", "\"${project.findProperty("FATSECRET_CLIENT_ID") ?: ""}\"")
        buildConfigField("String", "FATSECRET_CLIENT_SECRET", "\"${project.findProperty("FATSECRET_CLIENT_SECRET") ?: ""}\"")
        buildConfigField("String", "NUTRITIONIX_APP_ID", "\"${project.findProperty("NUTRITIONIX_APP_ID") ?: ""}\"")
        buildConfigField("String", "NUTRITIONIX_API_KEY", "\"${project.findProperty("NUTRITIONIX_API_KEY") ?: ""}\"")
        buildConfigField("String", "UPC_DATABASE_API_KEY", "\"${project.findProperty("UPC_DATABASE_API_KEY") ?: ""}\"")
        buildConfigField("String", "EXERCISE_DB_API_KEY", "\"${project.findProperty("EXERCISE_DB_API_KEY") ?: ""}\"")
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

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:datastore"))
    implementation(project(":core:security"))

    // Networking
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.okhttp.brotli)
    
    // Serialization
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.gson)
    
    // Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    
    // Authentication & Security
    implementation(libs.androidx.security.crypto)
    implementation(libs.keystore)
    
    // Image Processing for AI
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
    
    // Cache
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    
    // Monitoring & Logging
    implementation(libs.timber)
    implementation(libs.okhttp.eventlistener)
    
    // Testing
    testImplementation(libs.junit4)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.okhttp.mockwebserver)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
}