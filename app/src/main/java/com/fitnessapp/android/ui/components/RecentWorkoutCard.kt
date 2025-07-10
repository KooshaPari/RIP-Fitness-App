package com.fitnessapp.android.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Card component for displaying recent workout entries
 * 
 * Shows workout name, duration, exercise count, and date
 * Used in workout history and progress tracking screens
 * 
 * @param workoutName Name of the workout
 * @param duration Duration of the workout
 * @param date When the workout was completed
 * @param exercises Number of exercises in the workout
 * @param onClick Callback when the card is tapped
 * @param modifier Modifier for the card
 */
@Composable
fun RecentWorkoutCard(
    workoutName: String,
    duration: String,
    date: String,
    exercises: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = \"Workout\",\n                    tint = MaterialTheme.colorScheme.primary,\n                    modifier = Modifier.size(24.dp)\n                )\n                \n                Spacer(modifier = Modifier.width(12.dp))\n                \n                Column {\n                    Text(\n                        text = workoutName,\n                        style = MaterialTheme.typography.bodyMedium,\n                        fontWeight = FontWeight.Medium\n                    )\n                    Text(\n                        text = \"$exercises exercises • $duration\",\n                        style = MaterialTheme.typography.bodySmall,\n                        color = MaterialTheme.colorScheme.onSurfaceVariant\n                    )\n                    Text(\n                        text = date,\n                        style = MaterialTheme.typography.bodySmall,\n                        color = MaterialTheme.colorScheme.onSurfaceVariant\n                    )\n                }\n            )\n            \n            Icon(\n                imageVector = Icons.Default.ChevronRight,\n                contentDescription = \"View details\",\n                tint = MaterialTheme.colorScheme.onSurfaceVariant\n            )\n        }\n    }\n}"