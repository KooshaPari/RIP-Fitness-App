package com.fitnessapp.android.ui.theme

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.window.core.layout.WindowWidthSizeClass

/**
 * Advanced Material Design 3 Adaptive Theme
 * 
 * Features:
 * - Dynamic color theming with user preferences
 * - Adaptive layouts for different screen sizes
 * - Enhanced accessibility support
 * - Motion design tokens
 * - Haptic feedback integration
 * - Right-to-left (RTL) support
 */

@Composable
fun FitnessAppAdaptiveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    highContrast: Boolean = false,
    largeText: Boolean = false,
    reduceMotion: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    
    // Determine color scheme based on preferences and device capabilities
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }.let { scheme ->
                if (highContrast) scheme.toHighContrast() else scheme
            }
        }
        darkTheme -> if (highContrast) DarkColorScheme.toHighContrast() else DarkColorScheme
        else -> if (highContrast) LightColorScheme.toHighContrast() else LightColorScheme
    }
    
    // Adaptive typography based on screen size and accessibility preferences
    val adaptiveTypography = when (windowSizeClass.windowWidthSizeClass) {
        WindowWidthSizeClass.COMPACT -> if (largeText) LargeTextTypography else CompactTypography
        WindowWidthSizeClass.MEDIUM -> if (largeText) LargeTextTypography else MediumTypography
        WindowWidthSizeClass.EXPANDED -> if (largeText) LargeTextTypography else ExpandedTypography
        else -> Typography
    }
    
    // Motion preferences for animations
    val motionScheme = if (reduceMotion) ReducedMotionScheme else StandardMotionScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // Provide motion scheme and accessibility preferences to composition locals
    CompositionLocalProvider(
        LocalMotionScheme provides motionScheme,
        LocalAccessibilityPreferences provides AccessibilityPreferences(
            highContrast = highContrast,
            largeText = largeText,
            reduceMotion = reduceMotion
        ),
        LocalWindowSizeClass provides windowSizeClass
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = adaptiveTypography,
            content = content
        )
    }
}

/**
 * Extension function to create high contrast color schemes
 */
private fun ColorScheme.toHighContrast(): ColorScheme = copy(
    surface = if (this == LightColorScheme) Color.White else Color.Black,
    onSurface = if (this == LightColorScheme) Color.Black else Color.White,
    outline = if (this == LightColorScheme) Color.Black else Color.White,
    outlineVariant = if (this == LightColorScheme) Color.Black else Color.White
)

/**
 * Motion design tokens for animations
 */
data class MotionScheme(
    val durationShort: Int,
    val durationMedium: Int,
    val durationLong: Int,
    val easingStandard: String,
    val easingEmphasized: String
)

val StandardMotionScheme = MotionScheme(
    durationShort = 150,
    durationMedium = 300,
    durationLong = 500,
    easingStandard = "cubic-bezier(0.2, 0.0, 0, 1.0)",
    easingEmphasized = "cubic-bezier(0.05, 0.7, 0.1, 1.0)"
)

val ReducedMotionScheme = MotionScheme(
    durationShort = 0,
    durationMedium = 0,
    durationLong = 0,
    easingStandard = "linear",
    easingEmphasized = "linear"
)

/**
 * Accessibility preferences data class
 */
data class AccessibilityPreferences(
    val highContrast: Boolean = false,
    val largeText: Boolean = false,
    val reduceMotion: Boolean = false
)

/**
 * Composition locals for theme customization
 */
val LocalMotionScheme = compositionLocalOf { StandardMotionScheme }
val LocalAccessibilityPreferences = compositionLocalOf { AccessibilityPreferences() }
val LocalWindowSizeClass = compositionLocalOf<androidx.window.core.layout.WindowSizeClass> {
    error("WindowSizeClass not provided")
}

/**
 * Utility function to check if device supports dynamic color
 */
fun supportsDynamicColor(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

/**
 * Utility function to get system language direction
 */
fun Context.isRtl(): Boolean {
    return resources.configuration.layoutDirection == android.view.View.LAYOUT_DIRECTION_RTL
}