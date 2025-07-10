package com.fitnessapp.android.ui.nutrition

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fitnessapp.android.ui.components.MacroProgressCard
import com.fitnessapp.android.ui.components.QuickActionCard
import com.fitnessapp.android.ui.components.RecentMealCard

/**
 * Main nutrition screen displaying nutrition dashboard
 * 
 * Features:
 * - Daily macro progress (calories, protein, carbs, fats)
 * - Quick action buttons for common tasks
 * - Recent meals timeline
 * - Water intake tracking
 * - Meal planning suggestions
 * 
 * Integrates with nutrition data layer for real-time updates
 * Supports both manual food logging and barcode scanning
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    onNavigateToFoodSearch: () -> Unit,
    onNavigateToFoodLogging: () -> Unit,
    onNavigateToMealPlanning: () -> Unit,
    onNavigateToBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Mock data - will be replaced with actual data from ViewModel
    val dailyCalorieGoal = 2200
    val caloriesConsumed = 1650
    val proteinGoal = 120
    val proteinConsumed = 85
    val carbGoal = 250
    val carbsConsumed = 180
    val fatGoal = 80
    val fatConsumed = 65
    val waterGoal = 8
    val waterConsumed = 6

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with date and streak
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Nutrition",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "7 day streak",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Daily macro overview cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MacroProgressCard(
                    modifier = Modifier.weight(1f),
                    title = "Calories",
                    current = caloriesConsumed,
                    goal = dailyCalorieGoal,
                    unit = "kcal",
                    color = MaterialTheme.colorScheme.primary
                )
                MacroProgressCard(
                    modifier = Modifier.weight(1f),
                    title = "Protein",
                    current = proteinConsumed,
                    goal = proteinGoal,
                    unit = "g",
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MacroProgressCard(
                    modifier = Modifier.weight(1f),
                    title = "Carbs",
                    current = carbsConsumed,
                    goal = carbGoal,
                    unit = "g",
                    color = MaterialTheme.colorScheme.tertiary
                )
                MacroProgressCard(
                    modifier = Modifier.weight(1f),
                    title = "Fat",
                    current = fatConsumed,
                    goal = fatGoal,
                    unit = "g",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        // Water intake tracker
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Water Intake",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "$waterConsumed / $waterGoal glasses",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = waterConsumed.toFloat() / waterGoal.toFloat(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(waterGoal) { index ->
                            Icon(\n                                imageVector = if (index < waterConsumed) Icons.Filled.WaterDrop \n                                else Icons.Outlined.WaterDrop,\n                                contentDescription = \"Glass ${index + 1}\",\n                                tint = if (index < waterConsumed) MaterialTheme.colorScheme.primary \n                                else MaterialTheme.colorScheme.outline,\n                                modifier = Modifier.size(20.dp)\n                            )\n                        }\n                    }\n                }\n            }\n        }\n\n        // Quick actions\n        item {\n            Text(\n                text = \"Quick Actions\",\n                style = MaterialTheme.typography.titleMedium,\n                fontWeight = FontWeight.SemiBold\n            )\n        }\n\n        item {\n            LazyRow(\n                horizontalArrangement = Arrangement.spacedBy(12.dp)\n            ) {\n                items(\n                    listOf(\n                        \"Log Food\" to Icons.Default.Add,\n                        \"Search Food\" to Icons.Default.Search,\n                        \"Scan Barcode\" to Icons.Default.QrCodeScanner,\n                        \"Meal Plan\" to Icons.Default.CalendarMonth\n                    )\n                ) { (title, icon) ->\n                    QuickActionCard(\n                        title = title,\n                        icon = icon,\n                        onClick = {\n                            when (title) {\n                                \"Log Food\" -> onNavigateToFoodLogging()\n                                \"Search Food\" -> onNavigateToFoodSearch()\n                                \"Scan Barcode\" -> onNavigateToBarcodeScanner()\n                                \"Meal Plan\" -> onNavigateToMealPlanning()\n                            }\n                        }\n                    )\n                }\n            }\n        }\n\n        // Recent meals\n        item {\n            Text(\n                text = \"Recent Meals\",\n                style = MaterialTheme.typography.titleMedium,\n                fontWeight = FontWeight.SemiBold\n            )\n        }\n\n        // Mock recent meals data\n        items(\n            listOf(\n                \"Breakfast\" to \"Oatmeal with berries\" to \"320 kcal\",\n                \"Lunch\" to \"Grilled chicken salad\" to \"450 kcal\",\n                \"Snack\" to \"Greek yogurt\" to \"150 kcal\",\n                \"Dinner\" to \"Salmon with vegetables\" to \"520 kcal\"\n            )\n        ) { (mealType, foodName, calories) ->\n            RecentMealCard(\n                mealType = mealType,\n                foodName = foodName,\n                calories = calories,\n                time = \"2 hours ago\", // Mock time\n                onClick = { /* Navigate to meal details */ }\n            )\n        }\n\n        // Add some bottom padding for fab\n        item {\n            Spacer(modifier = Modifier.height(80.dp))\n        }\n    }\n\n    // Floating action button for quick food logging\n    Box(\n        modifier = Modifier.fillMaxSize(),\n        contentAlignment = Alignment.BottomEnd\n    ) {\n        FloatingActionButton(\n            onClick = onNavigateToFoodLogging,\n            modifier = Modifier.padding(16.dp)\n        ) {\n            Icon(\n                imageVector = Icons.Default.Add,\n                contentDescription = \"Log food\"\n            )\n        }\n    }\n}"