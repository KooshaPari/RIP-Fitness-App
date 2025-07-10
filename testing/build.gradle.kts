plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.fitnessapp.testing"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        targetSdk = 34

        testInstrumentationRunner = "com.fitnessapp.testing.HiltTestRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
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

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
        animationsDisabled = true
    }
}

dependencies {
    // Project modules
    implementation(project(":core:common"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:network"))
    implementation(project(":core:security"))
    
    // KMobile SDK
    implementation("io.kmobile:kmobile-sdk:1.2.0")
    implementation("io.kmobile:device-automation:1.2.0")
    implementation("io.kmobile:performance-testing:1.2.0")
    implementation("io.kmobile:security-testing:1.2.0")
    
    // Core testing libraries
    api("junit:junit:4.13.2")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    api("androidx.arch.core:core-testing:2.2.0")
    api("com.google.truth:truth:1.1.4")
    api("io.mockk:mockk:1.13.8")
    api("org.robolectric:robolectric:4.11.1")
    
    // Android testing
    api("androidx.test:core:1.5.0")
    api("androidx.test.ext:junit:1.1.5")
    api("androidx.test:runner:1.5.2")
    api("androidx.test:rules:1.5.0")
    api("androidx.test.espresso:espresso-core:3.5.1")
    api("androidx.test.espresso:espresso-intents:3.5.1")
    api("androidx.test.espresso:espresso-accessibility:3.5.1")
    api("androidx.test.espresso:espresso-contrib:3.5.1")
    api("androidx.test.espresso:espresso-idling-resource:3.5.1")
    
    // Compose testing
    api(platform("androidx.compose:compose-bom:2023.10.01"))
    api("androidx.compose.ui:ui-test-junit4")
    api("androidx.compose.ui:ui-test-manifest")
    debugApi("androidx.compose.ui:ui-tooling")
    
    // Hilt testing
    api("com.google.dagger:hilt-android-testing:2.48")
    kspTest("com.google.dagger:hilt-compiler:2.48")
    
    // Room testing
    api("androidx.room:room-testing:2.5.2")
    
    // Network testing
    api("com.squareup.okhttp3:mockwebserver:4.12.0")
    api("org.wiremock:wiremock:3.3.1")
    
    // Performance testing
    api("androidx.benchmark:benchmark-junit4:1.2.2")
    api("androidx.benchmark:benchmark-macro-junit4:1.2.2")
    api("androidx.test.uiautomator:uiautomator:2.2.0")
    
    // Security testing
    api("io.github.detekt:detekt-test:1.23.4")
    api("com.github.pinterest:ktlint:0.50.0")
    
    // Accessibility testing
    api("com.google.android.apps.common.testing.accessibility.framework:accessibility-test-framework:4.0.0")
    
    // Health Connect testing
    api("androidx.health:health-connect-client:1.0.0")
    testApi("androidx.health:health-connect-client-testing:1.0.0")
    
    // WorkManager testing
    api("androidx.work:work-testing:2.8.1")
    
    // Firebase testing
    api("com.google.firebase:firebase-auth-ktx")
    testApi("org.mockito:mockito-inline:5.2.0")
    
    // Image testing
    api("io.coil-kt:coil-test:2.5.0")
    
    // Date/time testing
    api("org.threeten:threetenbp:1.6.8")
}