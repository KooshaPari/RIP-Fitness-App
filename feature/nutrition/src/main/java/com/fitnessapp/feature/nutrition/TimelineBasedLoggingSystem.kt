package com.fitnessapp.feature.nutrition

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Timeline-Based Food Logging System
 * 
 * MacroFactor-inspired non-meal-based food logging system that organizes entries
 * chronologically rather than by traditional meal categories. Features include:
 * 
 * - Continuous timeline view of all food entries
 * - Smart entry suggestions based on time and context
 * - Flexible food entry without meal constraints
 * - Visual timeline with nutrition summaries
 * - Quick add/edit capabilities
 * - Contextual grouping suggestions
 */
class TimelineBasedLoggingSystem {
    
    private val _timelineEntries = MutableStateFlow<List<TimelineEntry>>(emptyList())
    val timelineEntries: StateFlow<List<TimelineEntry>> = _timelineEntries.asStateFlow()
    
    private val _dailySummary = MutableStateFlow(DailySummary())
    val dailySummary: StateFlow<DailySummary> = _dailySummary.asStateFlow()
    
    private val _smartSuggestions = MutableStateFlow<List<SmartSuggestion>>(emptyList())
    val smartSuggestions: StateFlow<List<SmartSuggestion>> = _smartSuggestions.asStateFlow()
    
    data class TimelineEntry(
        val id: String,
        val timestamp: LocalDateTime,
        val foodItems: List<FoodItem>,
        val totalNutrition: NutritionSummary,
        val entryMethod: EntryMethod,
        val context: EntryContext? = null,
        val notes: String = "",
        val isVerified: Boolean = false,
        val groupingSuggestion: GroupingSuggestion? = null
    )
    
    data class FoodItem(
        val id: String,
        val name: String,
        val amount: Double,
        val unit: String,
        val nutrition: NutritionData,
        val confidence: Double = 1.0,
        val brandInfo: BrandInfo? = null
    )
    
    data class NutritionSummary(
        val calories: Double,
        val protein: Double,
        val carbs: Double,
        val fat: Double,
        val fiber: Double,
        val sugar: Double,
        val sodium: Double,
        val micronutrients: Map<String, Double> = emptyMap()
    )
    
    data class DailySummary(
        val date: LocalDateTime = LocalDateTime.now(),
        val totalNutrition: NutritionSummary = NutritionSummary(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
        val entryCount: Int = 0,
        val targetProgress: TargetProgress = TargetProgress(),
        val timeDistribution: Map<TimeOfDay, NutritionSummary> = emptyMap(),
        val nutritionBalance: NutritionBalance = NutritionBalance()
    )
    
    data class TargetProgress(
        val calorieProgress: Double = 0.0,
        val proteinProgress: Double = 0.0,
        val carbProgress: Double = 0.0,
        val fatProgress: Double = 0.0,
        val isOnTrack: Boolean = true
    )
    
    data class NutritionBalance(
        val macroBalance: MacroBalance = MacroBalance(),
        val mealTiming: MealTimingAnalysis = MealTimingAnalysis(),
        val qualityScore: Double = 0.0
    )
    
    data class MacroBalance(
        val proteinPercentage: Double = 0.0,
        val carbPercentage: Double = 0.0,
        val fatPercentage: Double = 0.0,
        val isBalanced: Boolean = true
    )
    
    data class MealTimingAnalysis(
        val averageTimeBetweenEntries: Double = 0.0,
        val lastMealTime: LocalDateTime? = null,
        val suggestedNextMeal: LocalDateTime? = null
    )
    
    data class SmartSuggestion(
        val type: SuggestionType,
        val foodItem: FoodItem,
        val reasoning: String,
        val confidence: Double,
        val suggestedTime: LocalDateTime? = null
    )
    
    data class EntryContext(
        val location: String? = null,
        val activity: String? = null,
        val mood: String? = null,
        val companions: List<String> = emptyList(),
        val occasion: String? = null
    )
    
    data class GroupingSuggestion(
        val suggestedGroup: String,
        val confidence: Double,
        val reasoning: String,
        val nearbyEntries: List<String> = emptyList()
    )
    
    data class BrandInfo(
        val brand: String,
        val productLine: String? = null,
        val verified: Boolean = false
    )
    
    enum class EntryMethod {
        PHOTO_SCAN,
        BARCODE_SCAN,
        VOICE_INPUT,
        MANUAL_SEARCH,
        QUICK_ADD,
        RECIPE_IMPORT,
        SMART_SUGGESTION
    }
    
    enum class SuggestionType {
        FREQUENT_FOOD,
        TIME_BASED,
        NUTRITION_GAP,
        COMPLETION_SUGGESTION,
        SIMILAR_DAY,
        SEASONAL_FAVORITE
    }
    
    enum class TimeOfDay {
        EARLY_MORNING,  // 5-8 AM
        MORNING,        // 8-11 AM  
        MIDDAY,         // 11 AM-2 PM
        AFTERNOON,      // 2-5 PM
        EVENING,        // 5-8 PM
        NIGHT,          // 8-11 PM
        LATE_NIGHT      // 11 PM-5 AM
    }
    
    /**
     * Add a new food entry to the timeline
     */
    suspend fun addTimelineEntry(
        foodItems: List<FoodItem>,
        timestamp: LocalDateTime = LocalDateTime.now(),
        entryMethod: EntryMethod,
        context: EntryContext? = null,
        notes: String = ""
    ): String {
        
        val entryId = generateEntryId()
        val totalNutrition = calculateTotalNutrition(foodItems)
        val groupingSuggestion = generateGroupingSuggestion(timestamp, foodItems)
        
        val newEntry = TimelineEntry(
            id = entryId,
            timestamp = timestamp,
            foodItems = foodItems,
            totalNutrition = totalNutrition,
            entryMethod = entryMethod,
            context = context,
            notes = notes,
            groupingSuggestion = groupingSuggestion
        )
        
        val currentEntries = _timelineEntries.value.toMutableList()
        currentEntries.add(newEntry)
        currentEntries.sortBy { it.timestamp }
        
        _timelineEntries.value = currentEntries
        
        // Update daily summary and suggestions
        updateDailySummary()
        updateSmartSuggestions()
        
        return entryId
    }
    
    /**
     * Generate smart suggestions based on current timeline and user patterns
     */
    suspend fun generateSmartSuggestions(userId: String): List<SmartSuggestion> {
        
        val currentTime = LocalDateTime.now()
        val todayEntries = getTodayEntries()
        val userPatterns = analyzeUserPatterns(userId)
        val nutritionGaps = analyzeNutritionGaps(todayEntries)
        
        val suggestions = mutableListOf<SmartSuggestion>()
        
        // Time-based suggestions
        suggestions.addAll(generateTimeBasedSuggestions(currentTime, userPatterns))
        
        // Nutrition gap suggestions
        suggestions.addAll(generateNutritionGapSuggestions(nutritionGaps))
        
        // Frequent food suggestions
        suggestions.addAll(generateFrequentFoodSuggestions(currentTime, userPatterns))
        
        // Completion suggestions (meals that are typically eaten together)
        suggestions.addAll(generateCompletionSuggestions(todayEntries, userPatterns))
        
        // Similar day suggestions
        suggestions.addAll(generateSimilarDaySuggestions(todayEntries, userPatterns))
        
        val finalSuggestions = suggestions
            .distinctBy { it.foodItem.id }
            .sortedByDescending { it.confidence }
            .take(10)
        
        _smartSuggestions.value = finalSuggestions
        return finalSuggestions
    }
    
    /**
     * Update an existing timeline entry
     */
    suspend fun updateTimelineEntry(
        entryId: String,
        foodItems: List<FoodItem>? = null,
        timestamp: LocalDateTime? = null,
        notes: String? = null,
        context: EntryContext? = null
    ): Boolean {
        
        val currentEntries = _timelineEntries.value.toMutableList()
        val entryIndex = currentEntries.indexOfFirst { it.id == entryId }
        
        if (entryIndex == -1) return false
        
        val existingEntry = currentEntries[entryIndex]
        val updatedEntry = existingEntry.copy(
            foodItems = foodItems ?: existingEntry.foodItems,
            timestamp = timestamp ?: existingEntry.timestamp,
            notes = notes ?: existingEntry.notes,
            context = context ?: existingEntry.context,
            totalNutrition = calculateTotalNutrition(foodItems ?: existingEntry.foodItems)
        )
        
        currentEntries[entryIndex] = updatedEntry
        currentEntries.sortBy { it.timestamp }
        
        _timelineEntries.value = currentEntries
        updateDailySummary()
        
        return true
    }
    
    /**
     * Delete a timeline entry
     */
    suspend fun deleteTimelineEntry(entryId: String): Boolean {
        val currentEntries = _timelineEntries.value.toMutableList()
        val removed = currentEntries.removeAll { it.id == entryId }
        
        if (removed) {
            _timelineEntries.value = currentEntries
            updateDailySummary()
        }
        
        return removed
    }
    
    /**
     * Get entries for a specific time range
     */
    fun getEntriesInTimeRange(
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): List<TimelineEntry> {
        return _timelineEntries.value.filter { entry ->
            entry.timestamp.isAfter(startTime) && entry.timestamp.isBefore(endTime)
        }
    }
    
    /**
     * Group entries into suggested meal categories
     */
    fun getGroupedEntries(): Map<String, List<TimelineEntry>> {
        val entries = getTodayEntries()
        val groups = mutableMapOf<String, MutableList<TimelineEntry>>()
        
        entries.forEach { entry ->
            val groupName = entry.groupingSuggestion?.suggestedGroup 
                ?: determineTimeBasedGroup(entry.timestamp)
            
            groups.getOrPut(groupName) { mutableListOf() }.add(entry)
        }
        
        return groups.mapValues { it.value.toList() }
    }
    
    /**
     * Calculate comprehensive daily summary
     */
    private suspend fun updateDailySummary() {
        val todayEntries = getTodayEntries()
        val totalNutrition = calculateDayTotalNutrition(todayEntries)
        val timeDistribution = calculateTimeDistribution(todayEntries)
        val nutritionBalance = calculateNutritionBalance(totalNutrition, timeDistribution)
        val targetProgress = calculateTargetProgress(totalNutrition)
        
        _dailySummary.value = DailySummary(
            date = LocalDateTime.now(),
            totalNutrition = totalNutrition,
            entryCount = todayEntries.size,
            targetProgress = targetProgress,
            timeDistribution = timeDistribution,
            nutritionBalance = nutritionBalance
        )
    }
    
    /**
     * Generate grouping suggestions for entries
     */
    private fun generateGroupingSuggestion(
        timestamp: LocalDateTime,
        foodItems: List<FoodItem>
    ): GroupingSuggestion? {
        
        val nearbyEntries = findNearbyEntries(timestamp, 2.0) // Within 2 hours
        
        if (nearbyEntries.isEmpty()) {
            return null
        }
        
        // Analyze food types to suggest grouping
        val foodTypes = foodItems.map { categorizeFood(it) }
        val context = analyzeGroupingContext(foodTypes, timestamp)
        
        val suggestedGroup = when {
            context.contains("breakfast") -> "Morning Meal"
            context.contains("lunch") -> "Midday Meal"
            context.contains("dinner") -> "Evening Meal"
            context.contains("snack") -> "Snack"
            else -> "Food Session"
        }
        
        return GroupingSuggestion(
            suggestedGroup = suggestedGroup,
            confidence = calculateGroupingConfidence(nearbyEntries, foodTypes),
            reasoning = "Based on timing and food types",
            nearbyEntries = nearbyEntries.map { it.id }
        )
    }
    
    /**
     * Generate time-based food suggestions
     */
    private fun generateTimeBasedSuggestions(
        currentTime: LocalDateTime,
        userPatterns: UserPatterns
    ): List<SmartSuggestion> {
        
        val timeOfDay = getTimeOfDay(currentTime)
        val typicalFoods = userPatterns.foodsByTimeOfDay[timeOfDay] ?: emptyList()
        
        return typicalFoods.take(3).map { food ->
            SmartSuggestion(
                type = SuggestionType.TIME_BASED,
                foodItem = food,
                reasoning = "You typically eat this around ${currentTime.format(DateTimeFormatter.ofPattern("h:mm a"))}",
                confidence = calculateTimeBasedConfidence(food, currentTime, userPatterns),
                suggestedTime = currentTime
            )
        }
    }
    
    /**
     * Generate nutrition gap suggestions
     */
    private fun generateNutritionGapSuggestions(
        nutritionGaps: NutritionGaps
    ): List<SmartSuggestion> {
        
        val suggestions = mutableListOf<SmartSuggestion>()
        
        if (nutritionGaps.proteinDeficit > 10) {
            val proteinFoods = getHighProteinFoods()
            suggestions.addAll(proteinFoods.map { food ->
                SmartSuggestion(
                    type = SuggestionType.NUTRITION_GAP,
                    foodItem = food,
                    reasoning = "You need ${nutritionGaps.proteinDeficit.toInt()}g more protein today",
                    confidence = 0.8
                )
            })
        }
        
        if (nutritionGaps.fiberDeficit > 5) {
            val fiberFoods = getHighFiberFoods()
            suggestions.addAll(fiberFoods.map { food ->
                SmartSuggestion(
                    type = SuggestionType.NUTRITION_GAP,
                    foodItem = food,
                    reasoning = "Boost your fiber intake with this nutritious option",
                    confidence = 0.7
                )
            })
        }
        
        return suggestions
    }
    
    // Helper functions and data classes
    
    private fun getTodayEntries(): List<TimelineEntry> {
        val today = LocalDateTime.now().toLocalDate()
        return _timelineEntries.value.filter { it.timestamp.toLocalDate() == today }
    }
    
    private fun calculateTotalNutrition(foodItems: List<FoodItem>): NutritionSummary {
        return NutritionSummary(
            calories = foodItems.sumOf { it.nutrition.calories },
            protein = foodItems.sumOf { it.nutrition.protein },
            carbs = foodItems.sumOf { it.nutrition.carbs },
            fat = foodItems.sumOf { it.nutrition.fat },
            fiber = foodItems.sumOf { it.nutrition.fiber },
            sugar = foodItems.sumOf { it.nutrition.sugar },
            sodium = foodItems.sumOf { it.nutrition.sodium },
            micronutrients = aggregateMicronutrients(foodItems.map { it.nutrition.micronutrients })
        )
    }
    
    private fun getTimeOfDay(time: LocalDateTime): TimeOfDay {
        return when (time.hour) {
            in 5..7 -> TimeOfDay.EARLY_MORNING
            in 8..10 -> TimeOfDay.MORNING
            in 11..13 -> TimeOfDay.MIDDAY
            in 14..16 -> TimeOfDay.AFTERNOON
            in 17..19 -> TimeOfDay.EVENING
            in 20..22 -> TimeOfDay.NIGHT
            else -> TimeOfDay.LATE_NIGHT
        }
    }
    
    private fun generateEntryId(): String = System.currentTimeMillis().toString()
    
    private fun determineTimeBasedGroup(timestamp: LocalDateTime): String {
        return when (getTimeOfDay(timestamp)) {
            TimeOfDay.EARLY_MORNING, TimeOfDay.MORNING -> "Morning"
            TimeOfDay.MIDDAY -> "Midday"
            TimeOfDay.AFTERNOON -> "Afternoon"
            TimeOfDay.EVENING -> "Evening"
            TimeOfDay.NIGHT, TimeOfDay.LATE_NIGHT -> "Night"
        }
    }
    
    // Placeholder data classes and functions
    data class UserPatterns(
        val foodsByTimeOfDay: Map<TimeOfDay, List<FoodItem>> = emptyMap(),
        val frequentFoods: List<FoodItem> = emptyList(),
        val mealPatterns: List<MealPattern> = emptyList()
    )
    
    data class MealPattern(
        val foods: List<FoodItem>,
        val frequency: Int,
        val lastUsed: LocalDateTime
    )
    
    data class NutritionGaps(
        val proteinDeficit: Double = 0.0,
        val fiberDeficit: Double = 0.0,
        val calorieDeficit: Double = 0.0
    )
    
    // Placeholder implementations
    private suspend fun analyzeUserPatterns(userId: String): UserPatterns = UserPatterns()
    private fun analyzeNutritionGaps(entries: List<TimelineEntry>): NutritionGaps = NutritionGaps()
    private fun generateFrequentFoodSuggestions(time: LocalDateTime, patterns: UserPatterns): List<SmartSuggestion> = emptyList()
    private fun generateCompletionSuggestions(entries: List<TimelineEntry>, patterns: UserPatterns): List<SmartSuggestion> = emptyList()
    private fun generateSimilarDaySuggestions(entries: List<TimelineEntry>, patterns: UserPatterns): List<SmartSuggestion> = emptyList()
    private suspend fun updateSmartSuggestions() {}
    private fun findNearbyEntries(timestamp: LocalDateTime, hoursRange: Double): List<TimelineEntry> = emptyList()
    private fun categorizeFood(foodItem: FoodItem): String = "general"
    private fun analyzeGroupingContext(foodTypes: List<String>, timestamp: LocalDateTime): String = ""
    private fun calculateGroupingConfidence(entries: List<TimelineEntry>, foodTypes: List<String>): Double = 0.7
    private fun calculateTimeBasedConfidence(food: FoodItem, time: LocalDateTime, patterns: UserPatterns): Double = 0.8
    private fun getHighProteinFoods(): List<FoodItem> = emptyList()
    private fun getHighFiberFoods(): List<FoodItem> = emptyList()
    private fun calculateDayTotalNutrition(entries: List<TimelineEntry>): NutritionSummary = NutritionSummary()
    private fun calculateTimeDistribution(entries: List<TimelineEntry>): Map<TimeOfDay, NutritionSummary> = emptyMap()
    private fun calculateNutritionBalance(total: NutritionSummary, distribution: Map<TimeOfDay, NutritionSummary>): NutritionBalance = NutritionBalance()
    private fun calculateTargetProgress(nutrition: NutritionSummary): TargetProgress = TargetProgress()
    private fun aggregateMicronutrients(micronutrientsList: List<Map<String, Double>>): Map<String, Double> = emptyMap()
}

/**
 * Compose UI for Timeline-Based Food Logging
 */
@Composable
fun TimelineLoggingUI(
    timelineSystem: TimelineBasedLoggingSystem,
    modifier: Modifier = Modifier
) {
    val timelineEntries by timelineSystem.timelineEntries.collectAsState()
    val dailySummary by timelineSystem.dailySummary.collectAsState()
    val smartSuggestions by timelineSystem.smartSuggestions.collectAsState()
    
    Column(modifier = modifier.fillMaxSize()) {
        
        // Daily Summary Card
        DailySummaryCard(
            summary = dailySummary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        
        // Smart Suggestions
        if (smartSuggestions.isNotEmpty()) {
            SmartSuggestionsRow(
                suggestions = smartSuggestions,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        
        // Timeline Entries
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(timelineEntries) { entry ->
                TimelineEntryCard(
                    entry = entry,
                    onEdit = { /* Handle edit */ },
                    onDelete = { /* Handle delete */ }
                )
            }
        }
    }
}

@Composable
private fun DailySummaryCard(
    summary: TimelineBasedLoggingSystem.DailySummary,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Daily Progress",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NutritionProgressItem(
                    label = "Calories",
                    current = summary.totalNutrition.calories,
                    target = 2000.0, // This would come from user goals
                    unit = "cal"
                )
                
                NutritionProgressItem(
                    label = "Protein",
                    current = summary.totalNutrition.protein,
                    target = 150.0,
                    unit = "g"
                )
                
                NutritionProgressItem(
                    label = "Carbs",
                    current = summary.totalNutrition.carbs,
                    target = 250.0,
                    unit = "g"
                )
                
                NutritionProgressItem(
                    label = "Fat",
                    current = summary.totalNutrition.fat,
                    target = 78.0,
                    unit = "g"
                )
            }
        }
    }
}

@Composable
private fun NutritionProgressItem(
    label: String,
    current: Double,
    target: Double,
    unit: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
        
        Text(
            text = "${current.toInt()}",
            style = MaterialTheme.typography.titleSmall
        )
        
        Text(
            text = "of ${target.toInt()} $unit",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        LinearProgressIndicator(
            progress = (current / target).toFloat().coerceIn(0f, 1f),
            modifier = Modifier.width(60.dp)
        )
    }
}

@Composable
private fun SmartSuggestionsRow(
    suggestions: List<TimelineBasedLoggingSystem.SmartSuggestion>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Smart Suggestions",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Horizontal scrollable row of suggestion chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            suggestions.take(3).forEach { suggestion ->
                SuggestionChip(
                    onClick = { /* Handle suggestion selection */ },
                    label = { Text(suggestion.foodItem.name) },
                    leadingIcon = {
                        Icon(
                            imageVector = when (suggestion.type) {
                                TimelineBasedLoggingSystem.SuggestionType.TIME_BASED -> Icons.Default.Schedule
                                TimelineBasedLoggingSystem.SuggestionType.NUTRITION_GAP -> Icons.Default.TrendingUp
                                TimelineBasedLoggingSystem.SuggestionType.FREQUENT_FOOD -> Icons.Default.Star
                                else -> Icons.Default.Lightbulb
                            },
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun TimelineEntryCard(
    entry: TimelineBasedLoggingSystem.TimelineEntry,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.timestamp.format(DateTimeFormatter.ofPattern("h:mm a")),
                    style = MaterialTheme.typography.titleSmall
                )
                
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
            
            entry.foodItems.forEach { foodItem ->
                Text(
                    text = "${foodItem.name} (${foodItem.amount} ${foodItem.unit})",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "${entry.totalNutrition.calories.toInt()} cal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "P: ${entry.totalNutrition.protein.toInt()}g",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "C: ${entry.totalNutrition.carbs.toInt()}g",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "F: ${entry.totalNutrition.fat.toInt()}g",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}