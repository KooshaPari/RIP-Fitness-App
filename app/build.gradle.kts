plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
    id("androidx.room")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.fitnessapp.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.fitnessapp.android"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file(project.findProperty("RELEASE_STORE_FILE") ?: "keystore/release.keystore")
            storePassword = project.findProperty("RELEASE_STORE_PASSWORD") as String? ?: System.getenv("RELEASE_STORE_PASSWORD")
            keyAlias = project.findProperty("RELEASE_KEY_ALIAS") as String? ?: System.getenv("RELEASE_KEY_ALIAS")
            keyPassword = project.findProperty("RELEASE_KEY_PASSWORD") as String? ?: System.getenv("RELEASE_KEY_PASSWORD")
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isJniDebuggable = true
            isRenderscriptDebuggable = true
            isPseudoLocalesEnabled = true
            
            // Enable build config for debug variants
            buildConfigField("boolean", "DEBUG_MODE", "true")
            buildConfigField("String", "BUILD_TYPE", "\"debug\"")
        }
        
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            isJniDebuggable = false
            isRenderscriptDebuggable = false
            isPseudoLocalesEnabled = false
            
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            // Use proper release signing
            signingConfig = signingConfigs.getByName("release")
            
            // Production build config
            buildConfigField("boolean", "DEBUG_MODE", "false")
            buildConfigField("String", "BUILD_TYPE", "\"release\"")
            
            // Optimization settings
            postprocessing {
                isRemoveUnusedCode = true
                isRemoveUnusedResources = true
                isObfuscate = true
                isOptimizeCode = true
            }
        }
        
        create("benchmark") {
            initWith(getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
            
            buildConfigField("boolean", "DEBUG_MODE", "false")
            buildConfigField("String", "BUILD_TYPE", "\"benchmark\"")
        }
    }

    flavorDimensions += "environment"
    productFlavors {
        create("development") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            buildConfigField("String", "API_BASE_URL", "\"https://api-dev.fitnessapp.com\"")
            
            // Use environment variables or local.properties for API keys
            buildConfigField("String", "USDA_API_KEY", "\"${project.findProperty("dev.usda.api.key") ?: ""}\"") 
            buildConfigField("String", "FATSECRET_CLIENT_ID", "\"${project.findProperty("dev.fatsecret.client.id") ?: ""}\"") 
            buildConfigField("String", "FATSECRET_CLIENT_SECRET", "\"${project.findProperty("dev.fatsecret.client.secret") ?: ""}\"") 
            buildConfigField("String", "NUTRITIONIX_APP_ID", "\"${project.findProperty("dev.nutritionix.app.id") ?: ""}\"") 
            buildConfigField("String", "NUTRITIONIX_API_KEY", "\"${project.findProperty("dev.nutritionix.api.key") ?: ""}\"") 
            buildConfigField("String", "UPC_DATABASE_API_KEY", "\"${project.findProperty("dev.upc.api.key") ?: ""}\"") 
            buildConfigField("String", "EXERCISE_DB_API_KEY", "\"${project.findProperty("dev.exercise.api.key") ?: ""}\"") 
            
            buildConfigField("boolean", "ENABLE_LOGGING", "true")
            buildConfigField("boolean", "ENABLE_CRASH_REPORTING", "false")
        }
        
        create("staging") {
            dimension = "environment"
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            buildConfigField("String", "API_BASE_URL", "\"https://api-staging.fitnessapp.com\"")
            
            // Use environment variables for staging keys
            buildConfigField("String", "USDA_API_KEY", "\"${System.getenv("STAGING_USDA_API_KEY") ?: ""}\"") 
            buildConfigField("String", "FATSECRET_CLIENT_ID", "\"${System.getenv("STAGING_FATSECRET_CLIENT_ID") ?: ""}\"") 
            buildConfigField("String", "FATSECRET_CLIENT_SECRET", "\"${System.getenv("STAGING_FATSECRET_CLIENT_SECRET") ?: ""}\"") 
            buildConfigField("String", "NUTRITIONIX_APP_ID", "\"${System.getenv("STAGING_NUTRITIONIX_APP_ID") ?: ""}\"") 
            buildConfigField("String", "NUTRITIONIX_API_KEY", "\"${System.getenv("STAGING_NUTRITIONIX_API_KEY") ?: ""}\"") 
            buildConfigField("String", "UPC_DATABASE_API_KEY", "\"${System.getenv("STAGING_UPC_API_KEY") ?: ""}\"") 
            buildConfigField("String", "EXERCISE_DB_API_KEY", "\"${System.getenv("STAGING_EXERCISE_API_KEY") ?: ""}\"") 
            
            buildConfigField("boolean", "ENABLE_LOGGING", "true")
            buildConfigField("boolean", "ENABLE_CRASH_REPORTING", "true")
        }
        
        create("production") {
            dimension = "environment"
            buildConfigField("String", "API_BASE_URL", "\"https://api.fitnessapp.com\"")
            
            // Use environment variables for production keys (secure)
            buildConfigField("String", "USDA_API_KEY", "\"${System.getenv("PROD_USDA_API_KEY") ?: ""}\"") 
            buildConfigField("String", "FATSECRET_CLIENT_ID", "\"${System.getenv("PROD_FATSECRET_CLIENT_ID") ?: ""}\"") 
            buildConfigField("String", "FATSECRET_CLIENT_SECRET", "\"${System.getenv("PROD_FATSECRET_CLIENT_SECRET") ?: ""}\"") 
            buildConfigField("String", "NUTRITIONIX_APP_ID", "\"${System.getenv("PROD_NUTRITIONIX_APP_ID") ?: ""}\"") 
            buildConfigField("String", "NUTRITIONIX_API_KEY", "\"${System.getenv("PROD_NUTRITIONIX_API_KEY") ?: ""}\"") 
            buildConfigField("String", "UPC_DATABASE_API_KEY", "\"${System.getenv("PROD_UPC_API_KEY") ?: ""}\"") 
            buildConfigField("String", "EXERCISE_DB_API_KEY", "\"${System.getenv("PROD_EXERCISE_API_KEY") ?: ""}\"") 
            
            buildConfigField("boolean", "ENABLE_LOGGING", "false")
            buildConfigField("boolean", "ENABLE_CRASH_REPORTING", "true")
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
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    
    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    // Project modules
    implementation(project(":core:common"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:network"))
    implementation(project(":core:security"))
    
    implementation(project(":feature:nutrition"))
    implementation(project(":feature:workout"))
    implementation(project(":feature:health"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:sync"))
    implementation(project(":feature:onboarding"))
    
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    
    // Compose
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    ksp("com.google.dagger:hilt-compiler:2.48")
    
    // Room
    implementation("androidx.room:room-runtime:2.5.2")
    implementation("androidx.room:room-ktx:2.5.2")
    ksp("androidx.room:room-compiler:2.5.2")
    
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    
    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.datastore:datastore-core:1.0.0")
    
    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation("androidx.hilt:hilt-work:1.1.0")
    ksp("androidx.hilt:hilt-compiler:1.1.0")
    
    // Health Connect
    implementation("androidx.health:health-connect-client:1.0.0")
    
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-perf-ktx")
    
    // Biometric
    implementation("androidx.biometric:biometric:1.1.0")
    
    // ML Kit (for barcode scanning)
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    
    // CameraX
    implementation("androidx.camera:camera-core:1.3.0")
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")
    
    // Image loading
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Accompanist
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")
    
    // Testing framework
    testImplementation(project(":testing"))
    androidTestImplementation(project(":testing"))
    
    // Additional testing dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("com.google.truth:truth:1.1.4")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.robolectric:robolectric:4.11.1")
    
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-accessibility:3.5.1")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.48")
    kspAndroidTest("com.google.dagger:hilt-compiler:2.48")
    
    // Performance testing
    androidTestImplementation("androidx.benchmark:benchmark-junit4:1.2.2")
    
    // Accessibility testing
    androidTestImplementation("com.google.android.apps.common.testing.accessibility.framework:accessibility-test-framework:4.0.0")
    
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}