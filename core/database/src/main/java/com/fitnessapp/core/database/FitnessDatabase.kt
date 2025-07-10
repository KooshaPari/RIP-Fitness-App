package com.fitnessapp.core.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.fitnessapp.core.database.converters.DateTimeConverters
import com.fitnessapp.core.database.entities.*
import com.fitnessapp.core.database.dao.*

/**
 * Main Room database for the comprehensive fitness application.
 * 
 * This database integrates all fitness tracking capabilities including:
 * - User management and preferences
 * - Comprehensive nutrition tracking (MacroFactor-inspired)
 * - Advanced workout tracking (Strong-inspired)
 * - Health metrics and body measurements
 * - Progress tracking with photos, notes, and check-ins
 * 
 * Features:
 * - Adaptive TDEE calculation support
 * - Progressive overload tracking
 * - Health platform integration
 * - Social features and sharing
 * - Professional coaching support
 */
@Database(
    entities = [
        // User Management
        UserEntity::class,
        UserPreferencesEntity::class,
        UserGoalsEntity::class,
        
        // Nutrition (MacroFactor-inspired)
        NutritionEntity::class,
        FoodEntity::class,
        RecipeEntity::class,
        MealEntity::class,
        QuickAddEntity::class,
        
        // Workouts (Strong-inspired)
        WorkoutEntity::class,
        ExerciseEntity::class,
        WorkoutExerciseEntity::class,
        SetEntity::class,
        RoutineEntity::class,
        
        // Health Data
        WeightEntity::class,
        BodyMeasurementEntity::class,
        HealthMetricEntity::class,
        
        // Progress Tracking
        ProgressPhotoEntity::class,
        ProgressNoteEntity::class,
        CheckInEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateTimeConverters::class)
abstract class FitnessDatabase : RoomDatabase() {

    // User Management DAOs
    abstract fun userDao(): UserDao
    abstract fun userPreferencesDao(): UserPreferencesDao
    abstract fun userGoalsDao(): UserGoalsDao
    
    // Nutrition DAOs
    abstract fun nutritionDao(): NutritionDao
    abstract fun foodDao(): FoodDao
    abstract fun recipeDao(): RecipeDao
    abstract fun mealDao(): MealDao
    abstract fun quickAddDao(): QuickAddDao
    
    // Workout DAOs
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutExerciseDao(): WorkoutExerciseDao
    abstract fun setDao(): SetDao
    abstract fun routineDao(): RoutineDao
    
    // Health Data DAOs
    abstract fun weightDao(): WeightDao
    abstract fun bodyMeasurementDao(): BodyMeasurementDao
    abstract fun healthMetricDao(): HealthMetricDao
    
    // Progress Tracking DAOs
    abstract fun progressPhotoDao(): ProgressPhotoDao
    abstract fun progressNoteDao(): ProgressNoteDao
    abstract fun checkInDao(): CheckInDao

    companion object {
        @Volatile
        private var INSTANCE: FitnessDatabase? = null

        fun getDatabase(context: Context): FitnessDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitnessDatabase::class.java,
                    "fitness_database"
                )
                    .enableMultiInstanceInvalidation()
                    .fallbackToDestructiveMigration() // For development - remove in production
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Create in-memory database for testing
         */
        fun createInMemoryDatabase(context: Context): FitnessDatabase {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                FitnessDatabase::class.java
            )
                .allowMainThreadQueries() // Only for testing
                .build()
        }
    }
}