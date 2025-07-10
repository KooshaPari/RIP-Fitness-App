package com.fitnessapp.android.di

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.room.Room
import androidx.work.WorkManager
import com.fitnessapp.android.data.repository.FitnessRepository
import com.fitnessapp.android.data.repository.FitnessRepositoryImpl
import com.fitnessapp.android.manager.PermissionManager
import com.fitnessapp.android.manager.PermissionManagerImpl
import com.fitnessapp.core.database.FitnessDatabase
import com.fitnessapp.feature.health.integration.HealthConnectManager
import com.fitnessapp.feature.health.permissions.HealthPermissionsManager
import com.fitnessapp.feature.health.sync.HealthDataSyncManager
import com.fitnessapp.feature.nutrition.AiFoodLoggingEngine
import com.fitnessapp.feature.nutrition.TimelineBasedLoggingSystem
import com.fitnessapp.feature.workout.RoutineManager
import com.fitnessapp.feature.workout.WorkoutLogger
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Main Hilt module for app-level dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
    
    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
    
    @Provides
    @Singleton
    fun provideHealthConnectClient(@ApplicationContext context: Context): HealthConnectClient? {
        return if (HealthConnectClient.isProviderAvailable(context)) {
            HealthConnectClient.getOrCreate(context)
        } else null
    }
    
    @Provides
    @Singleton
    fun provideFitnessDatabase(@ApplicationContext context: Context): FitnessDatabase {
        return Room.databaseBuilder(
            context,
            FitnessDatabase::class.java,
            "fitness_database"
        )
        .addMigrations(
            // Add migrations here as needed
        )
        .fallbackToDestructiveMigration() // Remove in production
        .build()
    }
}

/**
 * Module for binding interfaces to implementations
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindsModule {
    
    @Binds
    @Singleton
    abstract fun bindFitnessRepository(
        fitnessRepositoryImpl: FitnessRepositoryImpl
    ): FitnessRepository
    
    @Binds
    @Singleton
    abstract fun bindPermissionManager(
        permissionManagerImpl: PermissionManagerImpl
    ): PermissionManager
}

/**
 * Module for feature-specific dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object FeatureModule {
    
    @Provides
    @Singleton
    fun provideHealthConnectManager(
        @ApplicationContext context: Context,
        healthConnectClient: HealthConnectClient?
    ): HealthConnectManager {
        return HealthConnectManager(context, healthConnectClient)
    }
    
    @Provides
    @Singleton
    fun provideHealthPermissionsManager(
        @ApplicationContext context: Context,
        healthConnectClient: HealthConnectClient?
    ): HealthPermissionsManager {
        return HealthPermissionsManager(context, healthConnectClient)
    }
    
    @Provides
    @Singleton
    fun provideHealthDataSyncManager(
        healthConnectManager: HealthConnectManager,
        database: FitnessDatabase,
        applicationScope: CoroutineScope
    ): HealthDataSyncManager {
        return HealthDataSyncManager(healthConnectManager, database, applicationScope)
    }
    
    @Provides
    @Singleton
    fun provideAiFoodLoggingEngine(
        @ApplicationContext context: Context,
        database: FitnessDatabase
    ): AiFoodLoggingEngine {
        return AiFoodLoggingEngine(context, database)
    }
    
    @Provides
    @Singleton
    fun provideTimelineBasedLoggingSystem(
        database: FitnessDatabase,
        aiFoodLoggingEngine: AiFoodLoggingEngine
    ): TimelineBasedLoggingSystem {
        return TimelineBasedLoggingSystem(database, aiFoodLoggingEngine)
    }
    
    @Provides
    @Singleton
    fun provideRoutineManager(
        database: FitnessDatabase,
        applicationScope: CoroutineScope
    ): RoutineManager {
        return RoutineManager(database, applicationScope)
    }
    
    @Provides
    @Singleton
    fun provideWorkoutLogger(
        database: FitnessDatabase,
        routineManager: RoutineManager
    ): WorkoutLogger {
        return WorkoutLogger(database, routineManager)
    }
}

/**
 * Qualifiers for different types of coroutine scopes
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoScope

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainScope