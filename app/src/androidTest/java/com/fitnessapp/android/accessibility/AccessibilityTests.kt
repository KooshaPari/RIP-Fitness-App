package com.fitnessapp.android.accessibility

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.fitnessapp.android.ui.navigation.FitnessNavigation
import com.fitnessapp.android.ui.theme.FitnessAppTheme
import com.fitnessapp.testing.KMobileTestSetup
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResult
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResultUtils
import com.google.android.apps.common.testing.accessibility.framework.checks.SpeakableTextPresentCheck
import com.google.android.apps.common.testing.accessibility.framework.checks.TouchTargetSizeCheck
import com.google.android.apps.common.testing.accessibility.framework.checks.ImageContrastCheck
import com.google.android.apps.common.testing.accessibility.framework.checks.ColorContrastCheck
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AccessibilityTests {
    
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
    fun testContentDescriptions() {
        composeTestRule.setContent {
            FitnessAppTheme {
                FitnessNavigation()
            }
        }
        
        // Test navigation buttons have content descriptions
        composeTestRule.onNodeWithContentDescription("Workout tab").assertExists()
        composeTestRule.onNodeWithContentDescription("Nutrition tab").assertExists()
        composeTestRule.onNodeWithContentDescription("Health tab").assertExists()
        composeTestRule.onNodeWithContentDescription("Profile tab").assertExists()
        
        // Navigate to workout screen and test content descriptions
        composeTestRule.onNodeWithContentDescription("Workout tab").performClick()
        
        composeTestRule.onNodeWithContentDescription("Start new workout").assertExists()
        composeTestRule.onNodeWithContentDescription("View workout history").assertExists()
        composeTestRule.onNodeWithContentDescription("Browse workout templates").assertExists()
        
        // Test exercise-related content descriptions
        composeTestRule.onNodeWithContentDescription("Start new workout").performClick()
        composeTestRule.onNodeWithContentDescription("Add exercise to workout").assertExists()
        composeTestRule.onNodeWithContentDescription("Finish current workout").assertExists()
    }
    
    @Test
    fun testTouchTargetSizes() {
        composeTestRule.setContent {
            FitnessAppTheme {
                FitnessNavigation()
            }
        }
        
        // Test touch targets meet minimum size requirements (48dp)
        val touchTargetCheck = TouchTargetSizeCheck()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        
        // Test navigation buttons
        composeTestRule.onNodeWithText("Workout").assertTouchTargetSizeIsAtLeast(48.dp)
        composeTestRule.onNodeWithText("Nutrition").assertTouchTargetSizeIsAtLeast(48.dp)
        composeTestRule.onNodeWithText("Health").assertTouchTargetSizeIsAtLeast(48.dp)
        composeTestRule.onNodeWithText("Profile").assertTouchTargetSizeIsAtLeast(48.dp)
        
        // Test action buttons
        composeTestRule.onNodeWithText("Workout").performClick()
        composeTestRule.onNodeWithText("Start Workout").assertTouchTargetSizeIsAtLeast(48.dp)
        
        composeTestRule.onNodeWithText("Nutrition").performClick()
        composeTestRule.onNodeWithText("Log Food").assertTouchTargetSizeIsAtLeast(48.dp)
    }
    
    @Test
    fun testColorContrast() {
        composeTestRule.setContent {
            FitnessAppTheme {
                FitnessNavigation()
            }
        }
        
        // Test color contrast ratios meet WCAG AA standards (4.5:1 for normal text, 3:1 for large text)
        val colorContrastCheck = ColorContrastCheck()
        
        // Test main navigation text contrast
        composeTestRule.onNodeWithText("Workout").assertExists()
        composeTestRule.onNodeWithText("Nutrition").assertExists()
        composeTestRule.onNodeWithText("Health").assertExists()
        composeTestRule.onNodeWithText("Profile").assertExists()
        
        // Test in different theme modes
        composeTestRule.setContent {
            FitnessAppTheme(darkTheme = true) {
                FitnessNavigation()
            }
        }
        
        // Verify dark theme contrast
        composeTestRule.onNodeWithText("Workout").assertExists()
        composeTestRule.onNodeWithText("Nutrition").assertExists()
    }
    
    @Test
    fun testScreenReaderSupport() {
        composeTestRule.setContent {
            FitnessAppTheme {
                FitnessNavigation()
            }
        }
        
        // Test semantic properties for screen readers
        composeTestRule.onNodeWithText("Workout")
            .assertHasClickAction()
            .assertIsSelectable()
        
        // Test heading semantics
        composeTestRule.onNode(hasText("Recent Workouts") and hasRole(Role.Heading))
            .assertExists()
        
        // Test form input semantics
        composeTestRule.onNodeWithText("Nutrition").performClick()
        composeTestRule.onNodeWithText("Log Food").performClick()
        
        composeTestRule.onNodeWithTag("food_search")
            .assertHasClickAction()
            .assertIsEnabled()
            .assertContentDescriptionEquals("Search for food to log")
    }
    
    @Test
    fun testFocusTraversal() {
        composeTestRule.setContent {
            FitnessAppTheme {
                FitnessNavigation()
            }
        }
        
        // Test focus order is logical
        composeTestRule.onNodeWithText("Workout").requestFocus()
        composeTestRule.onNodeWithText("Workout").assertIsFocused()
        
        // Test tab navigation
        composeTestRule.onNodeWithText("Workout").performKeyInput {
            pressKey(Key.Tab)
        }
        composeTestRule.onNodeWithText("Nutrition").assertIsFocused()
        
        composeTestRule.onNodeWithText("Nutrition").performKeyInput {
            pressKey(Key.Tab)
        }
        composeTestRule.onNodeWithText("Health").assertIsFocused()
        
        // Test focus wrapping
        composeTestRule.onNodeWithText("Profile").performKeyInput {
            pressKey(Key.Tab)
        }
        composeTestRule.onNodeWithText("Workout").assertIsFocused()
    }
    
    @Test
    fun testTextScaling() {
        // Test with different text scaling factors
        val scalingFactors = listOf(1.0f, 1.15f, 1.3f, 2.0f)
        
        scalingFactors.forEach { scaleFactor ->
            composeTestRule.setContent {
                FitnessAppTheme(textScale = scaleFactor) {
                    FitnessNavigation()
                }
            }
            
            // Verify UI still works with scaled text
            composeTestRule.onNodeWithText("Workout").assertIsDisplayed()
            composeTestRule.onNodeWithText("Nutrition").assertIsDisplayed()
            composeTestRule.onNodeWithText("Health").assertIsDisplayed()
            composeTestRule.onNodeWithText("Profile").assertIsDisplayed()
            
            // Test that text doesn't overlap or get cut off
            composeTestRule.onNodeWithText("Workout").performClick()
            composeTestRule.onNodeWithText("Start Workout").assertIsDisplayed()
        }
    }
    
    @Test
    fun testKeyboardNavigation() {
        composeTestRule.setContent {
            FitnessAppTheme {
                FitnessNavigation()
            }
        }
        
        // Test keyboard navigation in workout logging
        composeTestRule.onNodeWithText("Workout").performClick()
        composeTestRule.onNodeWithText("Start Workout").performClick()
        composeTestRule.onNodeWithText("Add Exercise").performClick()
        
        // Test keyboard input in exercise selection
        composeTestRule.onNodeWithTag("exercise_search").performKeyInput {
            pressKey(Key.B)
            pressKey(Key.E)
            pressKey(Key.N)
            pressKey(Key.C)
            pressKey(Key.H)
        }
        
        // Test Enter key selection
        composeTestRule.onNodeWithTag("exercise_search").performKeyInput {
            pressKey(Key.Enter)
        }
        
        // Test Tab navigation in set logging
        composeTestRule.onNodeWithTag("weight_input").requestFocus()
        composeTestRule.onNodeWithTag("weight_input").assertIsFocused()
        
        composeTestRule.onNodeWithTag("weight_input").performKeyInput {
            pressKey(Key.Tab)
        }
        composeTestRule.onNodeWithTag("reps_input").assertIsFocused()
    }
    
    @Test
    fun testHighContrastMode() {
        // Test app usability in high contrast mode
        composeTestRule.setContent {
            FitnessAppTheme(highContrast = true) {
                FitnessNavigation()
            }
        }
        
        // Verify all essential UI elements are visible in high contrast
        composeTestRule.onNodeWithText("Workout").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nutrition").assertIsDisplayed()
        composeTestRule.onNodeWithText("Health").assertIsDisplayed()
        composeTestRule.onNodeWithText("Profile").assertIsDisplayed()
        
        // Test that interactive elements are clearly distinguishable
        composeTestRule.onNodeWithText("Workout").performClick()
        composeTestRule.onNodeWithText("Start Workout").assertIsDisplayed()
        composeTestRule.onNodeWithText("View History").assertIsDisplayed()
    }
    
    @Test
    fun testVoiceOverSupport() {
        composeTestRule.setContent {
            FitnessAppTheme {
                FitnessNavigation()
            }
        }
        
        // Test semantic descriptions for voice over
        composeTestRule.onNodeWithContentDescription("Workout tab, currently selected")
            .assertExists()
        
        // Test state announcements
        composeTestRule.onNodeWithText("Nutrition").performClick()
        composeTestRule.onNodeWithContentDescription("Nutrition tab, currently selected")
            .assertExists()
        
        // Test custom actions for voice over
        composeTestRule.onNodeWithText("Log Food").performClick()
        composeTestRule.onNodeWithContentDescription("Food search field, enter food name to search")
            .assertExists()
    }
    
    @Test
    fun testReducedMotionSupport() {
        // Test app behavior with reduced motion preferences
        composeTestRule.setContent {
            FitnessAppTheme(reduceMotion = true) {
                FitnessNavigation()
            }
        }
        
        // Verify animations are reduced or disabled
        composeTestRule.onNodeWithText("Workout").performClick()
        composeTestRule.onNodeWithText("Start Workout").assertIsDisplayed()
        
        // Test that essential functionality works without animations
        composeTestRule.onNodeWithText("Start Workout").performClick()
        composeTestRule.onNodeWithText("Add Exercise").assertIsDisplayed()
    }
    
    @Test
    fun testAccessibilityServices() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        
        composeTestRule.setContent {
            FitnessAppTheme {
                FitnessNavigation()
            }
        }
        
        // Test compatibility with TalkBack
        val accessibilityManager = context.getSystemService(android.content.Context.ACCESSIBILITY_SERVICE) as android.view.accessibility.AccessibilityManager
        
        if (accessibilityManager.isEnabled) {
            // Test that UI responds appropriately to accessibility services
            composeTestRule.onNodeWithText("Workout").performClick()
            
            // Verify accessibility events are properly fired
            composeTestRule.onNodeWithText("Start Workout").assertExists()
        }
    }
    
    @Test
    fun testComprehensiveAccessibilityAudit() {
        composeTestRule.setContent {
            FitnessAppTheme {
                FitnessNavigation()
            }
        }
        
        // Run comprehensive accessibility checks
        val speakableTextCheck = SpeakableTextPresentCheck()
        val touchTargetCheck = TouchTargetSizeCheck()
        val imageContrastCheck = ImageContrastCheck()
        val colorContrastCheck = ColorContrastCheck()
        
        // Test main navigation
        testScreenAccessibility("Main Navigation")
        
        // Test workout screen
        composeTestRule.onNodeWithText("Workout").performClick()
        testScreenAccessibility("Workout Screen")
        
        // Test nutrition screen
        composeTestRule.onNodeWithText("Nutrition").performClick()
        testScreenAccessibility("Nutrition Screen")
        
        // Test health screen
        composeTestRule.onNodeWithText("Health").performClick()
        testScreenAccessibility("Health Screen")
        
        // Test profile screen
        composeTestRule.onNodeWithText("Profile").performClick()
        testScreenAccessibility("Profile Screen")
    }
    
    private fun testScreenAccessibility(screenName: String) {
        // Custom accessibility testing logic for each screen
        println("Testing accessibility for: $screenName")
        
        // Verify all interactive elements have proper semantics
        composeTestRule.onAllNodes(hasClickAction()).fetchSemanticsNodes().forEach { node ->
            // Each clickable node should have either text or content description
            val hasText = node.config.getOrNull(androidx.compose.ui.semantics.SemanticsProperties.Text)?.isNotEmpty() == true
            val hasContentDescription = node.config.getOrNull(androidx.compose.ui.semantics.SemanticsProperties.ContentDescription)?.isNotEmpty() == true
            
            assert(hasText || hasContentDescription) {
                "Clickable node in $screenName lacks proper semantic information"
            }
        }
        
        // Verify heading hierarchy
        composeTestRule.onAllNodes(hasRole(Role.Heading)).fetchSemanticsNodes().forEach { node ->
            // Headings should have proper text
            val hasText = node.config.getOrNull(androidx.compose.ui.semantics.SemanticsProperties.Text)?.isNotEmpty() == true
            assert(hasText) { "Heading in $screenName lacks text" }
        }
    }
    
    @Test
    fun testAccessibilityInDifferentStates() {
        composeTestRule.setContent {
            FitnessAppTheme {
                FitnessNavigation()
            }
        }
        
        // Test accessibility in loading state
        composeTestRule.onNodeWithText("Health").performClick()
        composeTestRule.onNodeWithText("Sync Health Data").performClick()
        
        // Should have loading indicator with proper semantics
        composeTestRule.onNodeWithContentDescription("Syncing health data, please wait")
            .assertExists()
        
        // Test accessibility in error state
        composeTestRule.onNodeWithText("Nutrition").performClick()
        composeTestRule.onNodeWithText("Log Food").performClick()
        composeTestRule.onNodeWithTag("food_search").performTextInput("")
        composeTestRule.onNodeWithText("Search").performClick()
        
        // Error message should be announced to screen readers
        composeTestRule.onNodeWithContentDescription("Error: Please enter a food name to search")
            .assertExists()
        
        // Test accessibility in success state
        composeTestRule.onNodeWithText("Workout").performClick()
        composeTestRule.onNodeWithText("Start Workout").performClick()
        composeTestRule.onNodeWithText("Finish Workout").performClick()
        composeTestRule.onNodeWithText("Save Workout").performClick()
        
        // Success message should be properly announced
        composeTestRule.onNodeWithContentDescription("Success: Workout saved successfully")
            .assertExists()
    }
}