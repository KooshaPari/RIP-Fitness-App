package com.fitnessapp.android.e2e

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.fitnessapp.android.MainActivity
import com.fitnessapp.testing.KMobileTestSetup
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.kmobile.device.automation.DeviceAutomation
import io.kmobile.performance.PerformanceMonitor
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class EndToEndTests {
    
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @get:Rule
    val kmobileRule = KMobileTestSetup()
    
    @Inject
    lateinit var deviceAutomation: DeviceAutomation
    
    @Inject
    lateinit var performanceMonitor: PerformanceMonitor
    
    private lateinit var uiDevice: UiDevice
    
    @Before
    fun setup() {
        hiltRule.inject()
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        
        // Enable performance monitoring for E2E tests
        performanceMonitor.startSession("e2e_tests")
    }
    
    @Test
    fun completeUserWorkflow_NewUserOnboardingToFirstWorkout() {
        performanceMonitor.startTrace("complete_user_workflow")
        
        // 1. App Launch and Onboarding
        composeTestRule.setContent {
            MainActivity()
        }
        
        // Handle first launch
        composeTestRule.onNodeWithText("Welcome to Fitness App").assertIsDisplayed()
        composeTestRule.onNodeWithText("Get Started").performClick()
        
        // Personal Information
        composeTestRule.onNodeWithTag("name_input").performTextInput("John Doe")
        composeTestRule.onNodeWithTag("age_input").performTextInput("30")
        composeTestRule.onNodeWithTag("height_input").performTextInput("175")
        composeTestRule.onNodeWithTag("weight_input").performTextInput("80")
        composeTestRule.onNodeWithText("Male").performClick()
        composeTestRule.onNodeWithText("Next").performClick()
        
        // Goals Setup
        composeTestRule.onNodeWithText("Lose Weight").performClick()
        composeTestRule.onNodeWithTag("goal_weight_input").performTextInput("75")
        composeTestRule.onNodeWithText("Moderate Activity").performClick()
        composeTestRule.onNodeWithText("Next").performClick()
        
        // Permissions Setup
        composeTestRule.onNodeWithText("Enable Health Connect").performClick()
        // Handle system permission dialog
        deviceAutomation.handleSystemPermissionDialog("Allow")
        
        composeTestRule.onNodeWithText("Enable Notifications").performClick()
        deviceAutomation.handleSystemPermissionDialog("Allow")
        
        composeTestRule.onNodeWithText("Complete Setup").performClick()
        
        // 2. First Workout
        composeTestRule.onNodeWithText("Start Your First Workout").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create Workout").performClick()
        
        // Select workout template
        composeTestRule.onNodeWithText("Push Day").performClick()
        composeTestRule.onNodeWithText("Use Template").performClick()
        
        // Start workout
        composeTestRule.onNodeWithText("Start Workout").performClick()
        
        // Log exercises
        composeTestRule.onNodeWithText("Bench Press").performClick()
        
        // Set 1
        composeTestRule.onNodeWithTag("weight_input_1").performTextInput("60")
        composeTestRule.onNodeWithTag("reps_input_1").performTextInput("10")
        composeTestRule.onNodeWithText("Complete Set").performClick()
        
        // Set 2  
        composeTestRule.onNodeWithTag("weight_input_2").performTextInput("65")
        composeTestRule.onNodeWithTag("reps_input_2").performTextInput("8")
        composeTestRule.onNodeWithText("Complete Set").performClick()
        
        // Set 3
        composeTestRule.onNodeWithTag("weight_input_3").performTextInput("70")
        composeTestRule.onNodeWithTag("reps_input_3").performTextInput("6")
        composeTestRule.onNodeWithText("Complete Set").performClick()
        
        // Move to next exercise
        composeTestRule.onNodeWithText("Next Exercise").performClick()
        composeTestRule.onNodeWithText("Shoulder Press").performClick()
        
        // Quick complete remaining exercises for time
        composeTestRule.onNodeWithText("Auto Complete").performClick()
        
        // Finish workout
        composeTestRule.onNodeWithText("Finish Workout").performClick()
        composeTestRule.onNodeWithTag("workout_notes").performTextInput("Great first workout!")
        composeTestRule.onNodeWithText("Save Workout").performClick()
        
        // 3. Log Post-Workout Meal
        composeTestRule.onNodeWithText("Log Post-Workout Meal").performClick()
        
        // Search and add protein shake
        composeTestRule.onNodeWithTag("food_search").performTextInput("protein shake")
        composeTestRule.onNodeWithText("Search").performClick()
        
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Whey Protein Shake").fetchSemanticsNodes().isNotEmpty()
        }
        
        composeTestRule.onNodeWithText("Whey Protein Shake").performClick()
        composeTestRule.onNodeWithTag("portion_input").performTextInput("1")
        composeTestRule.onNodeWithText("Add to Meal").performClick()
        
        // Add banana
        composeTestRule.onNodeWithText("Add Food").performClick()
        composeTestRule.onNodeWithTag("food_search").performTextReplacement("banana")
        composeTestRule.onNodeWithText("Search").performClick()
        
        composeTestRule.onNodeWithText("Banana, Medium").performClick()
        composeTestRule.onNodeWithText("Add to Meal").performClick()
        
        // Save meal
        composeTestRule.onNodeWithText("Save Meal").performClick()
        
        // 4. Check Progress Dashboard
        composeTestRule.onNodeWithText("View Progress").performClick()
        
        // Verify workout is logged
        composeTestRule.onNodeWithText("Workouts This Week: 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Calories Consumed Today").assertIsDisplayed()
        composeTestRule.onNodeWithText("Weight Progress").assertIsDisplayed()
        
        performanceMonitor.endTrace("complete_user_workflow")
    }
    
    @Test
    fun testCompleteNutritionLoggingWorkflow() {
        performanceMonitor.startTrace("nutrition_workflow")
        
        // Navigate to nutrition screen
        composeTestRule.onNodeWithText("Nutrition").performClick()
        
        // Log breakfast
        composeTestRule.onNodeWithText("Breakfast").performClick()
        composeTestRule.onNodeWithText("Add Food").performClick()
        
        // Test barcode scanning
        composeTestRule.onNodeWithText("Scan Barcode").performClick()
        deviceAutomation.simulateCameraInput("1234567890123") // Mock barcode
        
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithText("Oatmeal").fetchSemanticsNodes().isNotEmpty()
        }
        
        composeTestRule.onNodeWithText("Add to Breakfast").performClick()
        
        // Add more foods via search
        composeTestRule.onNodeWithText("Add Food").performClick()
        composeTestRule.onNodeWithTag("food_search").performTextInput("greek yogurt")
        composeTestRule.onNodeWithText("Search").performClick()
        
        composeTestRule.onNodeWithText("Greek Yogurt, Plain").performClick()
        composeTestRule.onNodeWithTag("portion_input").performTextInput("150")
        composeTestRule.onNodeWithText("Add to Breakfast").performClick()
        
        // Log lunch with recipe
        composeTestRule.onNodeWithText("Lunch").performClick()
        composeTestRule.onNodeWithText("Use Recipe").performClick()
        composeTestRule.onNodeWithText("Chicken Salad").performClick()
        composeTestRule.onNodeWithText("Add Recipe").performClick()
        
        // Log dinner with photo recognition
        composeTestRule.onNodeWithText("Dinner").performClick()
        composeTestRule.onNodeWithText("Take Photo").performClick()
        deviceAutomation.simulateCameraCapture()
        
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule.onAllNodesWithText("Grilled Salmon").fetchSemanticsNodes().isNotEmpty()
        }
        
        composeTestRule.onNodeWithText("Confirm").performClick()
        
        // Check daily nutrition summary
        composeTestRule.onNodeWithText("Daily Summary").assertIsDisplayed()
        composeTestRule.onNodeWithText("Calories").assertIsDisplayed()
        composeTestRule.onNodeWithText("Protein").assertIsDisplayed()
        composeTestRule.onNodeWithText("Carbs").assertIsDisplayed()
        composeTestRule.onNodeWithText("Fat").assertIsDisplayed()
        
        performanceMonitor.endTrace("nutrition_workflow")
    }
    
    @Test
    fun testHealthDataSyncAndConflictResolution() {
        performanceMonitor.startTrace("health_sync_workflow")
        
        // Navigate to health screen
        composeTestRule.onNodeWithText("Health").performClick()
        
        // Test manual sync
        composeTestRule.onNodeWithText("Sync Now").performClick()
        
        // Wait for sync to complete
        composeTestRule.waitUntil(timeoutMillis = 30000) {
            composeTestRule.onAllNodesWithText("Sync Complete").fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodesWithText("Conflicts Detected").fetchSemanticsNodes().isNotEmpty()
        }
        
        // Handle conflicts if they exist
        if (composeTestRule.onAllNodesWithText("Conflicts Detected").fetchSemanticsNodes().isNotEmpty()) {
            composeTestRule.onNodeWithText("Resolve Conflicts").performClick()
            
            // Choose resolution strategy for weight data
            composeTestRule.onNodeWithText("Weight Conflict").performClick()
            composeTestRule.onNodeWithText("Use Health Connect Value").performClick()
            
            // Choose resolution for steps data
            composeTestRule.onNodeWithText("Steps Conflict").performClick()
            composeTestRule.onNodeWithText("Merge Values").performClick()
            
            composeTestRule.onNodeWithText("Apply Resolutions").performClick()
        }
        
        // Verify health data is displayed
        composeTestRule.onNodeWithText("Today's Stats").assertIsDisplayed()
        composeTestRule.onNodeWithText("Steps").assertIsDisplayed()
        composeTestRule.onNodeWithText("Heart Rate").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sleep").assertIsDisplayed()
        
        // Test data export
        composeTestRule.onNodeWithText("Export Data").performClick()
        composeTestRule.onNodeWithText("Last 30 Days").performClick()
        composeTestRule.onNodeWithText("Export").performClick()
        
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithText("Export Complete").fetchSemanticsNodes().isNotEmpty()
        }
        
        performanceMonitor.endTrace("health_sync_workflow")
    }
    
    @Test
    fun testProgressTrackingAndAnalytics() {
        performanceMonitor.startTrace("progress_tracking_workflow")
        
        // Navigate to profile/progress
        composeTestRule.onNodeWithText("Profile").performClick()
        composeTestRule.onNodeWithText("Progress").performClick()
        
        // View weight progress
        composeTestRule.onNodeWithText("Weight Progress").performClick()
        composeTestRule.onNodeWithText("Add Weight").performClick()
        composeTestRule.onNodeWithTag("weight_input").performTextInput("79.5")
        composeTestRule.onNodeWithText("Save").performClick()
        
        // Take progress photo
        composeTestRule.onNodeWithText("Progress Photos").performClick()
        composeTestRule.onNodeWithText("Add Photo").performClick()
        composeTestRule.onNodeWithText("Front View").performClick()
        composeTestRule.onNodeWithText("Take Photo").performClick()
        
        deviceAutomation.simulateCameraCapture()
        composeTestRule.onNodeWithTag("photo_notes").performTextInput("End of week 2")
        composeTestRule.onNodeWithText("Save Photo").performClick()
        
        // View workout analytics
        composeTestRule.onNodeWithText("Workout Analytics").performClick()
        composeTestRule.onNodeWithText("Strength Progress").assertIsDisplayed()
        composeTestRule.onNodeWithText("Volume Progress").assertIsDisplayed()
        composeTestRule.onNodeWithText("Frequency").assertIsDisplayed()
        
        // View nutrition analytics
        composeTestRule.onNodeWithText("Nutrition Analytics").performClick()
        composeTestRule.onNodeWithText("Calorie Trends").assertIsDisplayed()
        composeTestRule.onNodeWithText("Macro Distribution").assertIsDisplayed()
        composeTestRule.onNodeWithText("Meal Timing").assertIsDisplayed()
        
        // Generate progress report
        composeTestRule.onNodeWithText("Generate Report").performClick()
        composeTestRule.onNodeWithText("Weekly Report").performClick()
        
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithText("Report Generated").fetchSemanticsNodes().isNotEmpty()
        }
        
        composeTestRule.onNodeWithText("Share Report").performClick()
        
        performanceMonitor.endTrace("progress_tracking_workflow")
    }
    
    @Test
    fun testOfflineModeAndSyncWhenOnline() {
        performanceMonitor.startTrace("offline_sync_workflow")
        
        // Disable network
        deviceAutomation.setNetworkState(enabled = false)
        
        // Log workout while offline
        composeTestRule.onNodeWithText("Workout").performClick()
        composeTestRule.onNodeWithText("Start Workout").performClick()
        composeTestRule.onNodeWithText("Quick Workout").performClick()
        
        // Add exercise
        composeTestRule.onNodeWithText("Push Ups").performClick()
        composeTestRule.onNodeWithTag("reps_input").performTextInput("20")
        composeTestRule.onNodeWithText("Complete Set").performClick()
        
        composeTestRule.onNodeWithText("Finish Workout").performClick()
        composeTestRule.onNodeWithText("Save Workout").performClick()
        
        // Verify offline indicator
        composeTestRule.onNodeWithText("Offline - Will sync when connected").assertIsDisplayed()
        
        // Log food while offline
        composeTestRule.onNodeWithText("Nutrition").performClick()
        composeTestRule.onNodeWithText("Quick Add").performClick()
        composeTestRule.onNodeWithTag("food_name").performTextInput("Protein Bar")
        composeTestRule.onNodeWithTag("calories_input").performTextInput("250")
        composeTestRule.onNodeWithText("Add Food").performClick()
        
        // Re-enable network
        deviceAutomation.setNetworkState(enabled = true)
        
        // Wait for auto-sync
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule.onAllNodesWithText("Sync Complete").fetchSemanticsNodes().isNotEmpty()
        }
        
        // Verify data was synced
        composeTestRule.onNodeWithText("All data synced").assertIsDisplayed()
        
        performanceMonitor.endTrace("offline_sync_workflow")
    }
    
    @Test
    fun testCompleteAppRestoreAfterReinstall() {
        performanceMonitor.startTrace("app_restore_workflow")
        
        // Simulate app data backup
        composeTestRule.onNodeWithText("Profile").performClick()
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithText("Backup & Restore").performClick()
        composeTestRule.onNodeWithText("Create Backup").performClick()
        
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithText("Backup Complete").fetchSemanticsNodes().isNotEmpty()
        }
        
        // Simulate app reinstall (clear all data)
        deviceAutomation.clearAppData()
        
        // Launch app again
        composeTestRule.setContent {
            MainActivity()
        }
        
        // Restore from backup
        composeTestRule.onNodeWithText("Restore from Backup").performClick()
        composeTestRule.onNodeWithText("Select Backup").performClick()
        composeTestRule.onNodeWithText("Latest Backup").performClick()
        composeTestRule.onNodeWithText("Restore").performClick()
        
        composeTestRule.waitUntil(timeoutMillis = 30000) {
            composeTestRule.onAllNodesWithText("Restore Complete").fetchSemanticsNodes().isNotEmpty()
        }
        
        // Verify all data was restored
        composeTestRule.onNodeWithText("Welcome back, John!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Workout").performClick()
        composeTestRule.onNodeWithText("Recent Workouts").assertIsDisplayed()
        
        performanceMonitor.endTrace("app_restore_workflow")
    }
}