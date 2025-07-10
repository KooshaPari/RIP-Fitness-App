package com.fitnessapp.android.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.fitnessapp.android.ui.nutrition.NutritionScreen
import com.fitnessapp.android.ui.workout.WorkoutScreen
import com.fitnessapp.android.ui.health.HealthScreen
import com.fitnessapp.android.ui.profile.ProfileScreen

/**
 * Main navigation component for the FitnessApp
 * 
 * Implements bottom navigation with nested navigation graphs for each main section:
 * - Nutrition: Food logging, nutrition dashboard, meal planning
 * - Workout: Exercise tracking, routine management, progress charts  
 * - Health: Health data integration, sync settings
 * - Profile: User settings, goals, preferences
 * 
 * Uses Material 3 NavigationBar with proper state management and deep linking support
 */

// Navigation destinations for bottom navigation
sealed class TopLevelDestination(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Nutrition : TopLevelDestination("nutrition_graph", Icons.Default.Restaurant, "Nutrition")
    object Workout : TopLevelDestination("workout_graph", Icons.Default.FitnessCenter, "Workout")
    object Health : TopLevelDestination("health_graph", Icons.Default.Favorite, "Health")
    object Profile : TopLevelDestination("profile_graph", Icons.Default.Person, "Profile")
}

// All top level destinations
val topLevelDestinations = listOf(
    TopLevelDestination.Nutrition,
    TopLevelDestination.Workout,
    TopLevelDestination.Health,
    TopLevelDestination.Profile
)

/**
 * Main app scaffold with bottom navigation
 * 
 * @param navController The navigation controller for the app
 * @param modifier Modifier for the scaffold
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessAppScaffold(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier,
    startDestination: String = TopLevelDestination.Nutrition.route,
    deepLinkDestination: String? = null
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                topLevelDestinations.forEach { destination ->
                    NavigationBarItem(
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true,
                        onClick = {
                            navController.navigate(destination.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        FitnessNavHost(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

/**
 * Navigation host with nested navigation graphs
 * 
 * Each main section has its own navigation graph to support:
 * - Deep linking within sections
 * - State preservation
 * - Independent navigation stacks
 * 
 * @param navController The navigation controller
 * @param modifier Modifier for the NavHost
 */
@Composable
fun FitnessNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = TopLevelDestination.Nutrition.route,
        modifier = modifier
    ) {
        // Nutrition navigation graph
        navigation(
            startDestination = "nutrition_home",
            route = TopLevelDestination.Nutrition.route
        ) {
            composable("nutrition_home") {
                NutritionScreen(
                    onNavigateToFoodSearch = { navController.navigate("food_search") },
                    onNavigateToFoodLogging = { navController.navigate("food_logging") },
                    onNavigateToMealPlanning = { navController.navigate("meal_planning") },
                    onNavigateToBarcodeScanner = { navController.navigate("barcode_scanner") }
                )
            }
            composable("food_search") {
                // FoodSearchScreen(onBackClick = { navController.popBackStack() })
            }
            composable("food_logging") {
                // FoodLoggingScreen(onBackClick = { navController.popBackStack() })
            }
            composable("meal_planning") {
                // MealPlanningScreen(onBackClick = { navController.popBackStack() })
            }
            composable("barcode_scanner") {
                // BarcodeScannerScreen(onBackClick = { navController.popBackStack() })
            }
        }

        // Workout navigation graph
        navigation(
            startDestination = "workout_home",
            route = TopLevelDestination.Workout.route
        ) {
            composable("workout_home") {
                WorkoutScreen(
                    onNavigateToExerciseBrowser = { navController.navigate("exercise_browser") },
                    onNavigateToWorkoutLogging = { navController.navigate("workout_logging") },
                    onNavigateToRoutineBuilder = { navController.navigate("routine_builder") },
                    onNavigateToProgressCharts = { navController.navigate("progress_charts") }
                )
            }
            composable("exercise_browser") {
                // ExerciseBrowserScreen(onBackClick = { navController.popBackStack() })
            }
            composable("workout_logging") {
                // WorkoutLoggingScreen(onBackClick = { navController.popBackStack() })
            }
            composable("routine_builder") {
                // RoutineBuilderScreen(onBackClick = { navController.popBackStack() })
            }
            composable("progress_charts") {
                // ProgressChartsScreen(onBackClick = { navController.popBackStack() })
            }
        }

        // Health navigation graph
        navigation(
            startDestination = "health_home",
            route = TopLevelDestination.Health.route
        ) {
            composable("health_home") {
                HealthScreen(
                    onNavigateToHealthData = { navController.navigate("health_data") },
                    onNavigateToSyncSettings = { navController.navigate("sync_settings") },
                    onNavigateToPermissions = { navController.navigate("permissions") }
                )
            }
            composable("health_data") {
                // HealthDataScreen(onBackClick = { navController.popBackStack() })
            }
            composable("sync_settings") {
                // SyncSettingsScreen(onBackClick = { navController.popBackStack() })
            }
            composable("permissions") {
                // PermissionsScreen(onBackClick = { navController.popBackStack() })
            }
        }

        // Profile navigation graph
        navigation(
            startDestination = "profile_home",
            route = TopLevelDestination.Profile.route
        ) {
            composable("profile_home") {
                ProfileScreen(
                    onNavigateToGoals = { navController.navigate("goals") },
                    onNavigateToPreferences = { navController.navigate("preferences") },
                    onNavigateToCoaching = { navController.navigate("coaching") }
                )
            }
            composable("goals") {
                // GoalsScreen(onBackClick = { navController.popBackStack() })
            }
            composable("preferences") {
                // PreferencesScreen(onBackClick = { navController.popBackStack() })
            }
            composable("coaching") {
                // CoachingScreen(onBackClick = { navController.popBackStack() })
            }
        }
    }
}