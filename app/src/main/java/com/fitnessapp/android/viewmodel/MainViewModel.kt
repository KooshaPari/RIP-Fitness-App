package com.fitnessapp.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitnessapp.android.data.repository.FitnessRepository
import com.fitnessapp.android.manager.PermissionManager
import com.fitnessapp.core.database.entities.UserEntity
import com.fitnessapp.core.database.entities.UserGoalsEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
// Remove Timber import - not configured yet
// import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Main ViewModel for the FitnessApp
 * 
 * Manages:
 * - App initialization state
 * - Permission management coordination
 * - User onboarding flow
 * - Navigation state
 * - Error handling
 * - Deep link routing
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val fitnessRepository: FitnessRepository,
    private val permissionManager: PermissionManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        initializeViewModel()
    }
    
    private fun initializeViewModel() {
        viewModelScope.launch {
            try {
                // Combine permission states with loading
                combine(
                    permissionManager.permissionStates,
                    fitnessRepository.getDashboardData()
                ) { permissions, dashboardData ->
                    Pair(permissions, dashboardData)
                }.collect { (permissions, dashboardData) ->
                    updateUiState { currentState ->
                        currentState.copy(
                            permissionStates = permissions,
                            user = dashboardData.user,
                            showOnboarding = dashboardData.user == null,
                            isLoading = false
                        )
                    }
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                // Timber.e(e, "Failed to initialize ViewModel")
                updateUiState { it.copy(hasError = true, errorMessage = e.message, isLoading = false) }
                _isLoading.value = false
            }
        }
    }
    
    fun setHealthConnectAvailable(available: Boolean) {
        updateUiState { it.copy(isHealthConnectAvailable = available) }
    }
    
    fun setMissingPermissions(permissions: List<String>) {
        updateUiState { it.copy(permissionRequests = permissions) }
    }
    
    fun onPermissionsGranted() {
        updateUiState { it.copy(permissionRequests = emptyList()) }
    }
    
    fun onHealthPermissionsChecked() {
        updateUiState { it.copy(healthPermissionRequest = false) }
    }
    
    fun setError(message: String) {
        updateUiState { it.copy(hasError = true, errorMessage = message) }
    }
    
    fun clearError() {
        updateUiState { it.copy(hasError = false, errorMessage = null) }
    }
    
    fun completeOnboarding() {
        viewModelScope.launch {
            try {
                // Create default user if not exists
                val existingUser = fitnessRepository.getUser()
                if (existingUser == null) {
                    val defaultUser = UserEntity(
                        userId = "1",
                        username = "user",
                        email = "user@example.com",
                        firstName = "Default",
                        lastName = "User",
                        dateOfBirth = null,
                        gender = null,
                        heightCm = null,
                        activityLevel = null,
                        fitnessExperience = null,
                        healthKitEnabled = false,
                        googleFitEnabled = false,
                        wearableConnected = false,
                        tdeeCalculationMethod = "adaptive",
                        metabolicAdaptationEnabled = true,
                        isPremium = false,
                        subscriptionType = null,
                        subscriptionExpiresAt = null,
                        profileVisibility = "private",
                        dataSharingConsent = false,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now(),
                        lastActiveAt = LocalDateTime.now(),
                        emailVerifiedAt = LocalDateTime.now(),
                        onboardingCompleted = true,
                        initialGoalsSet = true,
                        firstWorkoutCompleted = false,
                        nutritionTrackingEnabled = true
                    )
                    fitnessRepository.insertUser(defaultUser)
                    
                    // Create default goals
                    val defaultGoals = UserGoalsEntity(
                        userId = "1",
                        targetWeight = null,
                        dailyCalorieGoal = 2000,
                        dailyProteinGoal = 150,
                        dailyCarbGoal = 250,
                        dailyFatGoal = 65,
                        weeklyWorkoutGoal = 3,
                        stepGoal = 10000,
                        waterGoal = 2000,
                        sleepGoal = 8.0,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    )
                    fitnessRepository.insertUserGoals(defaultGoals)
                }
                
                updateUiState { it.copy(showOnboarding = false) }
            } catch (e: Exception) {
                // Timber.e(e, "Failed to complete onboarding")
                setError("Failed to complete setup: ${e.message}")
            }
        }
    }
    
    fun initializeUserData() {
        viewModelScope.launch {
            try {
                val user = fitnessRepository.getUser()
                updateUiState { it.copy(user = user) }
            } catch (e: Exception) {
                // Timber.e(e, "Failed to initialize user data")
            }
        }
    }
    
    // Navigation methods for deep linking
    fun navigateToWorkout(workoutId: String?) {
        updateUiState { 
            it.copy(
                deepLinkDestination = if (workoutId != null) "workout/$workoutId" else "workout",
                startDestination = "workout"
            )
        }
    }
    
    fun navigateToNutrition() {
        updateUiState { it.copy(deepLinkDestination = "nutrition", startDestination = "nutrition") }
    }
    
    fun navigateToHealth() {
        updateUiState { it.copy(deepLinkDestination = "health", startDestination = "health") }
    }
    
    fun navigateToProfile() {
        updateUiState { it.copy(deepLinkDestination = "profile", startDestination = "profile") }
    }
    
    fun navigateToHealthPermissions() {
        updateUiState { it.copy(healthPermissionRequest = true) }
    }
    
    private fun updateUiState(update: (MainUiState) -> MainUiState) {
        _uiState.value = update(_uiState.value)
    }
}

/**
 * UI state for the main app
 */
data class MainUiState(
    val isLoading: Boolean = true,
    val showOnboarding: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null,
    val user: UserEntity? = null,
    val permissionStates: Map<String, Boolean> = emptyMap(),
    val permissionRequests: List<String> = emptyList(),
    val healthPermissionRequest: Boolean = false,
    val isHealthConnectAvailable: Boolean = false,
    val startDestination: String = "dashboard",
    val deepLinkDestination: String? = null
)