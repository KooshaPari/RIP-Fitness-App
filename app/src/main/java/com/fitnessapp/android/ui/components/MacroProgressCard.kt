package com.fitnessapp.android.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Reusable card component for displaying macro nutrition progress
 * 
 * Shows current vs goal values with a circular progress indicator
 * Used throughout nutrition screens for calories, protein, carbs, fats
 * 
 * @param title The macro name (e.g., "Calories", "Protein")
 * @param current Current consumed amount
 * @param goal Target goal amount
 * @param unit Unit of measurement (e.g., "kcal", "g")
 * @param color Color for the progress indicator
 * @param modifier Modifier for the card
 */
@Composable
fun MacroProgressCard(
    title: String,
    current: Int,
    goal: Int,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress = (current.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
    val percentage = (progress * 100).toInt()
    
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(60.dp)
            ) {
                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxSize(),
                    color = color,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 6.dp
                )
                Text(
                    text = \"$percentage%\",\n                    style = MaterialTheme.typography.labelSmall,\n                    fontWeight = FontWeight.Bold,\n                    color = color\n                )\n            }\n            \n            Spacer(modifier = Modifier.height(8.dp))\n            \n            Text(\n                text = \"$current\",\n                style = MaterialTheme.typography.titleMedium,\n                fontWeight = FontWeight.Bold\n            )\n            \n            Text(\n                text = \"/ $goal $unit\",\n                style = MaterialTheme.typography.bodySmall,\n                color = MaterialTheme.colorScheme.onSurfaceVariant\n            )\n        }\n    }\n}"