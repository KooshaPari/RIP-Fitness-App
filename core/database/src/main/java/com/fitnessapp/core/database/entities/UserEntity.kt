package com.fitnessapp.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Core user entity for the comprehensive fitness app.
 * 
 * This entity stores essential user information and serves as the foundation
 * for all user-related data in the fitness tracking system.
 * 
 * Features inspired by MacroFactor and Strong:
 * - Adaptive metabolic tracking preparation
 * - Comprehensive user profiling for personalized recommendations
 * - Health integration compatibility
 */
@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], unique = true),
        Index(value = ["username"], unique = true),
        Index(value = ["created_at"]),
        Index(value = ["last_active_at"])
    ]
)
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "username")
    val username: String,
    
    @ColumnInfo(name = "email")
    val email: String,
    
    @ColumnInfo(name = "first_name")
    val firstName: String?,
    
    @ColumnInfo(name = "last_name")
    val lastName: String?,
    
    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: LocalDate?,
    
    @ColumnInfo(name = "gender")
    val gender: String?, // "male", "female", "other", "prefer_not_to_say"
    
    @ColumnInfo(name = "height_cm")
    val heightCm: Float?,
    
    @ColumnInfo(name = "activity_level")
    val activityLevel: String?, // "sedentary", "lightly_active", "moderately_active", "very_active", "extremely_active"
    
    @ColumnInfo(name = "fitness_experience")
    val fitnessExperience: String?, // "beginner", "intermediate", "advanced", "expert"
    
    // Health Integration Points
    @ColumnInfo(name = "health_kit_enabled")
    val healthKitEnabled: Boolean = false,
    
    @ColumnInfo(name = "google_fit_enabled")
    val googleFitEnabled: Boolean = false,
    
    @ColumnInfo(name = "wearable_connected")
    val wearableConnected: Boolean = false,
    
    // MacroFactor-inspired adaptive tracking
    @ColumnInfo(name = "tdee_calculation_method")
    val tdeeCalculationMethod: String? = "adaptive", // "adaptive", "formula_based", "manual"
    
    @ColumnInfo(name = "metabolic_adaptation_enabled")
    val metabolicAdaptationEnabled: Boolean = true,
    
    // Account management
    @ColumnInfo(name = "is_premium")
    val isPremium: Boolean = false,
    
    @ColumnInfo(name = "subscription_type")
    val subscriptionType: String?, // "monthly", "yearly", "lifetime"
    
    @ColumnInfo(name = "subscription_expires_at")
    val subscriptionExpiresAt: LocalDateTime?,
    
    // Privacy and preferences
    @ColumnInfo(name = "profile_visibility")
    val profileVisibility: String = "private", // "public", "friends", "private"
    
    @ColumnInfo(name = "data_sharing_consent")
    val dataSharingConsent: Boolean = false,
    
    // Timestamps
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime,
    
    @ColumnInfo(name = "last_active_at")
    val lastActiveAt: LocalDateTime?,
    
    @ColumnInfo(name = "email_verified_at")
    val emailVerifiedAt: LocalDateTime?,
    
    // Onboarding and setup
    @ColumnInfo(name = "onboarding_completed")
    val onboardingCompleted: Boolean = false,
    
    @ColumnInfo(name = "initial_goals_set")
    val initialGoalsSet: Boolean = false,
    
    @ColumnInfo(name = "first_workout_completed")
    val firstWorkoutCompleted: Boolean = false,
    
    @ColumnInfo(name = "nutrition_tracking_enabled")
    val nutritionTrackingEnabled: Boolean = false
)