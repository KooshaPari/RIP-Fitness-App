package com.fitnessapp.core.network.di

import android.content.Context
import com.fitnessapp.core.network.BuildConfig
import com.fitnessapp.core.network.api.*
import com.fitnessapp.core.network.auth.ApiKeyManager
import com.fitnessapp.core.network.auth.AuthInterceptor
import com.fitnessapp.core.network.auth.RateLimitInterceptor
import com.fitnessapp.core.network.cache.NetworkCacheManager
import com.fitnessapp.core.network.interceptor.ErrorHandlingInterceptor
import com.fitnessapp.core.network.interceptor.LoggingInterceptor
import com.fitnessapp.core.network.interceptor.SecurityInterceptor
import com.fitnessapp.core.network.monitoring.NetworkMonitor
import com.fitnessapp.core.network.monitoring.NetworkMonitorImpl
import com.fitnessapp.core.network.security.CertificatePinner
import com.fitnessapp.core.network.sync.OfflineSyncManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideApiKeyManager(@ApplicationContext context: Context): ApiKeyManager {
        return ApiKeyManager(context)
    }

    @Provides
    @Singleton
    fun provideCertificatePinner(): CertificatePinner {
        return CertificatePinner()
    }

    @Provides
    @Singleton
    fun provideNetworkCache(@ApplicationContext context: Context): Cache {
        val cacheSize = 50L * 1024 * 1024 // 50 MB
        val cacheDir = File(context.cacheDir, "network_cache")
        return Cache(cacheDir, cacheSize)
    }

    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return NetworkMonitorImpl(context)
    }

    @Provides
    @Singleton
    @Named("base")
    fun provideBaseOkHttpClient(
        cache: Cache,
        certificatePinner: CertificatePinner,
        networkMonitor: NetworkMonitor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache)
            .certificatePinner(certificatePinner.getPinner())
            .addInterceptor(SecurityInterceptor())
            .addInterceptor(ErrorHandlingInterceptor())
            .addNetworkInterceptor(LoggingInterceptor())
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("usda")
    fun provideUsdaOkHttpClient(
        @Named("base") baseClient: OkHttpClient,
        apiKeyManager: ApiKeyManager
    ): OkHttpClient {
        return baseClient.newBuilder()
            .addInterceptor(AuthInterceptor(apiKeyManager, "USDA"))
            .addInterceptor(RateLimitInterceptor(1000, 1, TimeUnit.HOURS)) // 1000 requests per hour
            .build()
    }

    @Provides
    @Singleton
    @Named("fatsecret")
    fun provideFatSecretOkHttpClient(
        @Named("base") baseClient: OkHttpClient,
        apiKeyManager: ApiKeyManager
    ): OkHttpClient {
        return baseClient.newBuilder()
            .addInterceptor(AuthInterceptor(apiKeyManager, "FATSECRET"))
            .addInterceptor(RateLimitInterceptor(5000, 1, TimeUnit.DAYS)) // 5000 requests per day
            .build()
    }

    @Provides
    @Singleton
    @Named("nutritionix")
    fun provideNutritionixOkHttpClient(
        @Named("base") baseClient: OkHttpClient,
        apiKeyManager: ApiKeyManager
    ): OkHttpClient {
        return baseClient.newBuilder()
            .addInterceptor(AuthInterceptor(apiKeyManager, "NUTRITIONIX"))
            .addInterceptor(RateLimitInterceptor(500, 1, TimeUnit.DAYS)) // 500 requests per day
            .build()
    }

    @Provides
    @Singleton
    @Named("upc")
    fun provideUpcOkHttpClient(
        @Named("base") baseClient: OkHttpClient,
        apiKeyManager: ApiKeyManager
    ): OkHttpClient {
        return baseClient.newBuilder()
            .addInterceptor(AuthInterceptor(apiKeyManager, "UPC"))
            .addInterceptor(RateLimitInterceptor(100, 1, TimeUnit.DAYS)) // 100 requests per day
            .build()
    }

    @Provides
    @Singleton
    @Named("exercise")
    fun provideExerciseOkHttpClient(
        @Named("base") baseClient: OkHttpClient,
        apiKeyManager: ApiKeyManager
    ): OkHttpClient {
        return baseClient.newBuilder()
            .addInterceptor(AuthInterceptor(apiKeyManager, "EXERCISE"))
            .addInterceptor(RateLimitInterceptor(1000, 1, TimeUnit.DAYS)) // 1000 requests per day
            .build()
    }

    // USDA FoodData Central API
    @Provides
    @Singleton
    fun provideUsdaApi(@Named("usda") client: OkHttpClient): UsdaFoodDataApi {
        return Retrofit.Builder()
            .baseUrl("https://api.nal.usda.gov/fdc/v1/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UsdaFoodDataApi::class.java)
    }

    // FatSecret Platform API
    @Provides
    @Singleton
    fun provideFatSecretApi(@Named("fatsecret") client: OkHttpClient): FatSecretApi {
        return Retrofit.Builder()
            .baseUrl("https://platform.fatsecret.com/rest/server.api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FatSecretApi::class.java)
    }

    // Nutritionix API
    @Provides
    @Singleton
    fun provideNutritionixApi(@Named("nutritionix") client: OkHttpClient): NutritionixApi {
        return Retrofit.Builder()
            .baseUrl("https://trackapi.nutritionix.com/v2/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NutritionixApi::class.java)
    }

    // UPC Database API
    @Provides
    @Singleton
    fun provideUpcDatabaseApi(@Named("upc") client: OkHttpClient): UpcDatabaseApi {
        return Retrofit.Builder()
            .baseUrl("https://api.upcitemdb.com/prod/trial/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UpcDatabaseApi::class.java)
    }

    // Exercise Database API
    @Provides
    @Singleton
    fun provideExerciseApi(@Named("exercise") client: OkHttpClient): ExerciseApi {
        return Retrofit.Builder()
            .baseUrl("https://exercisedb.p.rapidapi.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExerciseApi::class.java)
    }

    // AI Food Recognition API (Custom or third-party)
    @Provides
    @Singleton
    fun provideFoodRecognitionApi(@Named("base") client: OkHttpClient): FoodRecognitionApi {
        return Retrofit.Builder()
            .baseUrl("https://api.foodrecognition.com/v1/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FoodRecognitionApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNetworkCacheManager(
        @ApplicationContext context: Context,
        networkMonitor: NetworkMonitor
    ): NetworkCacheManager {
        return NetworkCacheManager(context, networkMonitor)
    }

    @Provides
    @Singleton
    fun provideOfflineSyncManager(
        @ApplicationContext context: Context,
        networkMonitor: NetworkMonitor,
        networkCacheManager: NetworkCacheManager
    ): OfflineSyncManager {
        return OfflineSyncManager(context, networkMonitor, networkCacheManager)
    }
}