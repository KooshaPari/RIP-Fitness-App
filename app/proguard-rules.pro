# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# Keep Retrofit interfaces
-keep interface com.fitnessapp.** { *; }

# Keep Room entities
-keep @androidx.room.Entity class * {
    <fields>;
}

# Keep Hilt modules
-keep @dagger.hilt.** class *

# Keep Firebase classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Keep Health Connect classes
-keep class androidx.health.connect.client.** { *; }

# Keep Gson/Serialization classes
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }

# Keep Kotlin serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep R8 compatibility
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Keep data classes
-keep class com.fitnessapp.**.model.** { *; }
-keep class com.fitnessapp.**.data.** { *; }

# Keep WorkManager
-keep class androidx.work.** { *; }
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.InputMerger
-keep class * extends androidx.work.ListenableWorker

# OkHttp and Retrofit
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-keep class okhttp3.** { *; }
-keep class retrofit2.** { *; }

# Remove logging in release builds for security and performance
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Remove Timber logging in release builds
-assumenosideeffects class timber.log.Timber {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
    public static *** tag(...);
}

# Security: Remove debug information
-keepattributes !SourceFile,!LineNumberTable
-renamesourcefileattribute ""

# Security: Obfuscate package names  
-repackageclasses ''
-allowaccessmodification
-mergeinterfacesaggressively
-overloadaggressively

# Security: Remove unused code aggressively
-dontshrink
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5

# Remove BuildConfig fields except essential ones
-assumenosideeffects class **.BuildConfig {
    public static boolean DEBUG return false;
    public static java.lang.String BUILD_TYPE return "release";
}

# Network Security
# Keep security-critical classes for certificate pinning
-keep class com.fitnessapp.core.network.security.** { *; }
-keep class com.fitnessapp.core.network.auth.ApiKeyManager { *; }

# Prevent API key obfuscation (breaks reflection)
-keepnames class **.BuildConfig
-keepclassmembers class **.BuildConfig {
    public static java.lang.String USDA_API_KEY;
    public static java.lang.String FATSECRET_CLIENT_ID;
    public static java.lang.String FATSECRET_CLIENT_SECRET;
    public static java.lang.String NUTRITIONIX_APP_ID;
    public static java.lang.String NUTRITIONIX_API_KEY;
    public static java.lang.String UPC_DATABASE_API_KEY;
    public static java.lang.String EXERCISE_DB_API_KEY;
}

# AndroidKeyStore security
-keep class android.security.keystore.** { *; }
-keep class javax.crypto.** { *; }
-keep class java.security.** { *; }

# Biometric authentication
-keep class androidx.biometric.** { *; }

# Performance: Aggressive optimization for data classes
-keep,allowobfuscation,allowshrinking class com.fitnessapp.**.model.**
-keep,allowobfuscation,allowshrinking class com.fitnessapp.**.data.**

# Performance: Optimize ViewModels but keep factory methods
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Performance: Optimize but keep Compose functions
-keep,allowobfuscation @androidx.compose.runtime.Composable class **
-keep,allowobfuscation @androidx.compose.runtime.Composable *** *(...)

# Database migrations security
-keep class androidx.room.migration.Migration { *; }
-keep class com.fitnessapp.core.database.migration.** { *; }

# Health Connect - keep for proper functioning
-keep class androidx.health.connect.client.** { *; }
-dontwarn androidx.health.connect.client.**

# ML Kit for barcode scanning
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# Camera X
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

# Performance: Remove debug annotations
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
    static void checkExpressionValueIsNotNull(java.lang.Object, java.lang.String);
    static void checkNotNullExpressionValue(java.lang.Object, java.lang.String);
    static void checkReturnedValueIsNotNull(java.lang.Object, java.lang.String, java.lang.String);
    static void checkFieldIsNotNull(java.lang.Object, java.lang.String, java.lang.String);
    static void checkNotNullParameter(java.lang.Object, java.lang.String);
}

# Remove stack trace information for security
-keepattributes !SourceFile,!LineNumberTable,Signature,*Annotation*,InnerClasses,EnclosingMethod

# Crashlytics - keep mapping file info
-keep class com.google.firebase.crashlytics.** { *; }
-dontwarn com.google.firebase.crashlytics.**

# Keep custom exception classes for crash reporting
-keep public class * extends java.lang.Exception