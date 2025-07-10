package com.fitnessapp.android.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fitnessapp.android.ui.navigation.FitnessNavigation
import com.fitnessapp.android.ui.theme.FitnessAppTheme
import com.fitnessapp.testing.KMobileTestSetup
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ComposeUITests {
    
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @get:Rule
    val kmobileRule = KMobileTestSetup()
    
    @Before
    fun setup() {
        hiltRule.inject()
    }
    
    @Test
    fun testMainNavigationFlow() {
        composeTestRule.setContent {
            FitnessAppTheme {
                FitnessNavigation()
            }
        }
        
        // Test bottom navigation
        composeTestRule.onNodeWithText("Workout").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nutrition").assertIsDisplayed()
        composeTestRule.onNodeWithText("Health").assertIsDisplayed()
        composeTestRule.onNodeWithText("Profile").assertIsDisplayed()
        
        // Test navigation between screens
        composeTestRule.onNodeWithText("Workout").performClick()
        composeTestRule.onNodeWithText("Start Workout").assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Nutrition").performClick()
        composeTestRule.onNodeWithText("Log Food").assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Health").performClick()
        composeTestRule.onNodeWithText("Sync Health Data").assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Profile").performClick()
        composeTestRule.onNodeWithText("User Settings").assertIsDisplayed()
    }
    
    @Test
    fun testWorkoutLogging() {
        composeTestRule.setContent {
            FitnessAppTheme {
                FitnessNavigation()
            }
        }
        
        // Navigate to workout screen
        composeTestRule.onNodeWithText("Workout").performClick()
        
        // Start a new workout
        composeTestRule.onNodeWithText("Start Workout").performClick()
        
        // Add an exercise
        composeTestRule.onNodeWithText("Add Exercise").performClick()
        composeTestRule.onNodeWithText("Bench Press").performClick()
        
        // Log a set
        composeTestRule.onNodeWithTag("weight_input").performTextInput("80")
        composeTestRule.onNodeWithTag("reps_input").performTextInput("10")
        composeTestRule.onNodeWithText("Add Set").performClick()
        
        // Verify set was added
        composeTestRule.onNodeWithText("80 kg × 10 reps").assertIsDisplayed()
        
        // Finish workout
        composeTestRule.onNodeWithText("Finish Workout").performClick()
        composeTestRule.onNodeWithText("Save Workout").performClick()
        
        // Verify workout saved
        composeTestRule.onNodeWithText("Workout Saved").assertIsDisplayed()
    }
    
    @Test
    fun testFoodLogging() {
        composeTestRule.setContent {
            FitnessAppTheme {
                FitnessNavigation()
            }
        }
        
        // Navigate to nutrition screen
        composeTestRule.onNodeWithText("Nutrition").performClick()
        
        // Log food
        composeTestRule.onNodeWithText("Log Food").performClick()
        
        // Search for food
        composeTestRule.onNodeWithTag("food_search").performTextInput("chicken breast")
        composeTestRule.onNodeWithText("Search").performClick()
        
        // Select food from results
        composeTestRule.onNodeWithText("Chicken Breast, Raw").performClick()
        
        // Set portion size
        composeTestRule.onNodeWithTag("portion_input").performTextInput("150")
        composeTestRule.onNodeWithText("Add to Meal").performClick()
        
        // Verify food added
        composeTestRule.onNodeWithText("Chicken Breast").assertIsDisplayed()
        composeTestRule.onNodeWithText("248 calories").assertIsDisplayed()
    }
    
    @Test
    fun testHealthDataSync() {
        composeTestRule.setContent {
            FitnessAppTheme {
                FitnessNavigation()
            }
        }
        
        // Navigate to health screen
        composeTestRule.onNodeWithText("Health").performClick()
        
        // Test sync button
        composeTestRule.onNodeWithText("Sync Health Data").performClick()
        
        // Verify sync status
        composeTestRule.onNodeWithText("Syncing...").assertIsDisplayed()
        
        // Wait for sync completion (mock)
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Sync Complete").fetchSemanticsNodes().isNotEmpty()
        }
        
        // Verify health metrics displayed
        composeTestRule.onNodeWithText("Steps Today").assertIsDisplayed()
        composeTestRule.onNodeWithText("Heart Rate").assertIsDisplayed()
        composeTestRule.onNodeWithText("Weight").assertIsDisplayed()
    }
    
    @Test
    fun testProgressPhotos() {
        composeTestRule.setContent {
            FitnessAppTheme {
                FitnessNavigation()
            }
        }
        
        // Navigate to profile screen
        composeTestRule.onNodeWithText("Profile").performClick()
        
        // Access progress photos
        composeTestRule.onNodeWithText("Progress Photos").performClick()
        
        // Add new photo
        composeTestRule.onNodeWithText("Add Photo").performClick()
        
        // Select photo type
        composeTestRule.onNodeWithText("Front View").performClick()
        
        // Simulate camera capture (mock)
        composeTestRule.onNodeWithText("Take Photo").performClick()
        
        // Add notes
        composeTestRule.onNodeWithTag("photo_notes").performTextInput("End of week 4")
        
        // Save photo
        composeTestRule.onNodeWithText("Save Photo").performClick()
        
        // Verify photo saved
        composeTestRule.onNodeWithText("Photo Saved").assertIsDisplayed()
    }
    
    @Test
    fun testUserSettings() {
        composeTestRule.setContent {
            FitnessAppTheme {
                FitnessNavigation()
            }
        }
        
        // Navigate to profile screen
        composeTestRule.onNodeWithText("Profile").performClick()
        
        // Access settings
        composeTestRule.onNodeWithText("Settings").performClick()
        
        // Test notification settings
        composeTestRule.onNodeWithText("Notifications").performClick()
        composeTestRule.onNodeWithTag("workout_reminders").performClick() // Toggle
        composeTestRule.onNodeWithTag("meal_reminders").performClick() // Toggle
        
        // Test unit preferences
        composeTestRule.onNodeWithText("Units").performClick()
        composeTestRule.onNodeWithText("Metric").performClick()
        
        // Test privacy settings
        composeTestRule.onNodeWithText("Privacy").performClick()
        composeTestRule.onNodeWithTag("data_sharing").performClick() // Toggle
        
        // Save settings
        composeTestRule.onNodeWithText("Save").performClick()
        
        // Verify settings saved
        composeTestRule.onNodeWithText("Settings Saved").assertIsDisplayed()
    }
    
    @Test
    fun testAccessibilitySupport() {
        composeTestRule.setContent {
            FitnessAppTheme {
                FitnessNavigation()
            }
        }
        
        // Test semantic descriptions
        composeTestRule.onNodeWithContentDescription("Start workout button").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Log food button").assertIsDisplayed()
        
        // Test focus traversal
        composeTestRule.onNodeWithText("Workout").assertHasClickAction()
        composeTestRule.onNodeWithText("Nutrition").assertHasClickAction()
        
        // Test text scaling
        // Note: This would need to be tested with different text scale settings
        composeTestRule.onNodeWithText("Workout").assertIsDisplayed()
    }
    
    @Test
    fun testDarkModeSupport() {
        // Test light theme
        composeTestRule.setContent {
            FitnessAppTheme(darkTheme = false) {
                FitnessNavigation()
            }
        }
        
        composeTestRule.onNodeWithText("Workout").assertIsDisplayed()
        
        // Test dark theme
        composeTestRule.setContent {
            FitnessAppTheme(darkTheme = true) {
                FitnessNavigation()
            }
        }
        
        composeTestRule.onNodeWithText("Workout").assertIsDisplayed()
    }
    
    @Test
    fun testErrorHandling() {
        composeTestRule.setContent {
            FitnessAppTheme {
                FitnessNavigation()
            }
        }
        
        // Navigate to nutrition and simulate network error
        composeTestRule.onNodeWithText("Nutrition").performClick()
        composeTestRule.onNodeWithText("Log Food").performClick()
        
        // Search with invalid input
        composeTestRule.onNodeWithTag("food_search").performTextInput("")
        composeTestRule.onNodeWithText("Search").performClick()
        
        // Verify error message
        composeTestRule.onNodeWithText("Please enter a food name").assertIsDisplayed()
        
        // Test retry functionality
        composeTestRule.onNodeWithText("Retry").performClick()
    }
}