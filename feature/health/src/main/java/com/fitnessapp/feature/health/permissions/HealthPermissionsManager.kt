package com.fitnessapp.feature.health.permissions

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Comprehensive health permissions manager
 * Handles granular permissions for all health data types with user-friendly explanations
 */
@Singleton
class HealthPermissionsManager @Inject constructor(
    private val context: Context
) {
    
    companion object {
        private const val TAG = "HealthPermissionsManager"
        
        // Grouped permissions for better UX
        val WEIGHT_PERMISSIONS = setOf(
            HealthPermission.getReadPermission(WeightRecord::class),
            HealthPermission.getWritePermission(WeightRecord::class)
        )
        
        val FITNESS_PERMISSIONS = setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getWritePermission(StepsRecord::class),
            HealthPermission.getReadPermission(ExerciseSessionRecord::class),
            HealthPermission.getWritePermission(ExerciseSessionRecord::class),
            HealthPermission.getReadPermission(DistanceRecord::class),
            HealthPermission.getWritePermission(DistanceRecord::class),
            HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
            HealthPermission.getWritePermission(ActiveCaloriesBurnedRecord::class),
            HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
            HealthPermission.getWritePermission(TotalCaloriesBurnedRecord::class),
            HealthPermission.getReadPermission(SpeedRecord::class),
            HealthPermission.getWritePermission(SpeedRecord::class)
        )
        
        val HEALTH_VITALS_PERMISSIONS = setOf(
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getWritePermission(HeartRateRecord::class),
            HealthPermission.getReadPermission(BloodPressureRecord::class),
            HealthPermission.getWritePermission(BloodPressureRecord::class),
            HealthPermission.getReadPermission(RestingHeartRateRecord::class),
            HealthPermission.getWritePermission(RestingHeartRateRecord::class),
            HealthPermission.getReadPermission(Vo2MaxRecord::class),
            HealthPermission.getWritePermission(Vo2MaxRecord::class)
        )
        
        val NUTRITION_PERMISSIONS = setOf(
            HealthPermission.getReadPermission(NutritionRecord::class),
            HealthPermission.getWritePermission(NutritionRecord::class),
            HealthPermission.getReadPermission(HydrationRecord::class),
            HealthPermission.getWritePermission(HydrationRecord::class)
        )
        
        val SLEEP_PERMISSIONS = setOf(
            HealthPermission.getReadPermission(SleepSessionRecord::class),
            HealthPermission.getWritePermission(SleepSessionRecord::class)
        )
        
        val ALL_PERMISSIONS = WEIGHT_PERMISSIONS + FITNESS_PERMISSIONS + 
                             HEALTH_VITALS_PERMISSIONS + NUTRITION_PERMISSIONS + SLEEP_PERMISSIONS
    }
    
    private val healthConnectClient by lazy {
        HealthConnectClient.getOrCreate(context)
    }
    
    private val _permissionState = MutableStateFlow(PermissionState())
    val permissionState: StateFlow<PermissionState> = _permissionState.asStateFlow()
    
    data class PermissionState(
        val isHealthConnectAvailable: Boolean = false,
        val grantedPermissions: Set<String> = emptySet(),
        val deniedPermissions: Set<String> = emptySet(),
        val isLoading: Boolean = false,
        val error: String? = null
    )
    
    data class PermissionGroup(
        val name: String,
        val description: String,
        val permissions: Set<String>,
        val isGranted: Boolean,
        val icon: String
    )
    
    suspend fun initialize() {
        _permissionState.value = _permissionState.value.copy(isLoading = true)
        
        try {
            val isAvailable = HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE
            
            if (isAvailable) {
                refreshPermissionStatus()
            }
            
            _permissionState.value = _permissionState.value.copy(
                isHealthConnectAvailable = isAvailable,
                isLoading = false,
                error = null
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing permissions", e)
            _permissionState.value = _permissionState.value.copy(
                isLoading = false,
                error = e.message
            )
        }
    }
    
    suspend fun refreshPermissionStatus() {
        try {
            val granted = healthConnectClient.permissionController.getGrantedPermissions()
            val denied = ALL_PERMISSIONS - granted
            
            _permissionState.value = _permissionState.value.copy(
                grantedPermissions = granted,
                deniedPermissions = denied,
                error = null
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing permissions", e)
            _permissionState.value = _permissionState.value.copy(error = e.message)
        }
    }
    
    fun getPermissionGroups(): List<PermissionGroup> {
        val state = _permissionState.value
        
        return listOf(
            PermissionGroup(
                name = "Weight Management",
                description = "Track and sync your weight data across devices",
                permissions = WEIGHT_PERMISSIONS,
                isGranted = WEIGHT_PERMISSIONS.all { it in state.grantedPermissions },
                icon = "⚖️"
            ),
            PermissionGroup(
                name = "Fitness & Exercise", 
                description = "Steps, workouts, distance, and calories burned",
                permissions = FITNESS_PERMISSIONS,
                isGranted = FITNESS_PERMISSIONS.all { it in state.grantedPermissions },
                icon = "🏃"
            ),
            PermissionGroup(
                name = "Health Vitals",
                description = "Heart rate, blood pressure, and other vital signs",
                permissions = HEALTH_VITALS_PERMISSIONS,
                isGranted = HEALTH_VITALS_PERMISSIONS.all { it in state.grantedPermissions },
                icon = "❤️"
            ),
            PermissionGroup(
                name = "Nutrition & Hydration",
                description = "Food intake, calories, macros, and water consumption",
                permissions = NUTRITION_PERMISSIONS,
                isGranted = NUTRITION_PERMISSIONS.all { it in state.grantedPermissions },
                icon = "🍎"
            ),
            PermissionGroup(
                name = "Sleep Tracking",
                description = "Sleep duration, quality, and patterns",
                permissions = SLEEP_PERMISSIONS,
                isGranted = SLEEP_PERMISSIONS.all { it in state.grantedPermissions },
                icon = "😴"
            )
        )
    }
    
    suspend fun requestPermissions(permissions: Set<String>): Result<Boolean> {
        return try {
            val permissionController = healthConnectClient.permissionController
            
            // Create permission request intent
            val intent = permissionController.createRequestPermissionResultContract()
                .createIntent(context, permissions)
            
            // Note: This would typically be handled by an Activity
            // In a real implementation, you'd start this intent and handle the result
            Log.d(TAG, "Permission request intent created for ${permissions.size} permissions")
            
            // For now, return success - actual implementation would handle the callback
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting permissions", e)
            Result.failure(e)
        }
    }
    
    suspend fun requestPermissionGroup(group: PermissionGroup): Result<Boolean> {
        return requestPermissions(group.permissions)
    }
    
    suspend fun requestAllPermissions(): Result<Boolean> {
        return requestPermissions(ALL_PERMISSIONS)
    }
    
    fun hasPermission(permission: String): Boolean {
        return permission in _permissionState.value.grantedPermissions
    }
    
    fun hasPermissionGroup(group: PermissionGroup): Boolean {
        return group.permissions.all { hasPermission(it) }
    }
    
    fun hasAllPermissions(): Boolean {
        return ALL_PERMISSIONS.all { hasPermission(it) }
    }
    
    fun getMissingPermissions(): Set<String> {
        return ALL_PERMISSIONS - _permissionState.value.grantedPermissions
    }
    
    fun getMissingPermissionGroups(): List<PermissionGroup> {
        return getPermissionGroups().filter { !it.isGranted }
    }
    
    fun getPermissionExplanation(permission: String): String {
        return when {
            permission.contains("Weight") -> 
                "We need access to weight data to track your progress and sync across your health apps."
            permission.contains("Steps") -> 
                "Step tracking helps us monitor your daily activity and calculate your fitness goals."
            permission.contains("Exercise") -> 
                "Exercise session data helps us understand your workout patterns and provide better recommendations."
            permission.contains("HeartRate") -> 
                "Heart rate data allows us to track your fitness intensity and recovery."
            permission.contains("Nutrition") -> 
                "Nutrition data helps us track your calories and macronutrients for better health insights."
            permission.contains("Sleep") -> 
                "Sleep data helps us understand your recovery patterns and overall health."
            permission.contains("Distance") -> 
                "Distance tracking helps us monitor your movement and calculate accurate calorie burn."
            permission.contains("Calories") -> 
                "Calorie data helps us track your energy expenditure and balance."
            else -> 
                "This permission helps us provide you with comprehensive health insights."
        }
    }
    
    fun getPermissionRationale(): String {
        return """
            FitnessApp uses health data to provide you with:
            
            📊 Comprehensive fitness tracking
            🎯 Personalized workout recommendations  
            📈 Progress monitoring and insights
            🔄 Seamless data sync across devices
            🏆 Achievement tracking and motivation
            
            Your data is encrypted and never shared without your explicit consent.
            You can revoke these permissions at any time in your device settings.
        """.trimIndent()
    }
    
    // Permission status monitoring
    fun observePermissionChanges(): Flow<PermissionState> {
        return permissionState
    }
    
    suspend fun checkHealthConnectStatus(): HealthConnectStatus {
        return when (HealthConnectClient.getSdkStatus(context)) {
            HealthConnectClient.SDK_UNAVAILABLE -> HealthConnectStatus.NOT_AVAILABLE
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> HealthConnectStatus.UPDATE_REQUIRED
            HealthConnectClient.SDK_AVAILABLE -> {
                if (hasAllPermissions()) {
                    HealthConnectStatus.READY
                } else {
                    HealthConnectStatus.PERMISSIONS_NEEDED
                }
            }
            else -> HealthConnectStatus.UNKNOWN
        }
    }
    
    enum class HealthConnectStatus {
        NOT_AVAILABLE,
        UPDATE_REQUIRED,
        PERMISSIONS_NEEDED,
        READY,
        UNKNOWN
    }
}