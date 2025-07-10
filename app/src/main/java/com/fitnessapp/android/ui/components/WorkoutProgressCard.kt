package com.fitnessapp.android.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Card component for displaying workout progress
 * 
 * Shows weekly workout completion progress and streak information
 * Used in workout dashboard and progress tracking screens
 * 
 * @param title The card title
 * @param current Current workouts completed
 * @param goal Weekly workout goal
 * @param streakDays Current streak in days
 * @param modifier Modifier for the card
 */
@Composable
fun WorkoutProgressCard(
    title: String,
    current: Int,
    goal: Int,
    streakDays: Int,
    modifier: Modifier = Modifier
) {
    val progress = (current.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
    val percentage = (progress * 100).toInt()
    
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = \"Streak\",\n                        tint = MaterialTheme.colorScheme.primary,\n                        modifier = Modifier.size(20.dp)\n                    )\n                    Spacer(modifier = Modifier.width(4.dp))\n                    Text(\n                        text = \"$streakDays days\",\n                        style = MaterialTheme.typography.bodyMedium,\n                        color = MaterialTheme.colorScheme.primary,\n                        fontWeight = FontWeight.Medium\n                    )\n                }\n            )\n            \n            Spacer(modifier = Modifier.height(16.dp))\n            \n            Row(\n                modifier = Modifier.fillMaxWidth(),\n                horizontalArrangement = Arrangement.SpaceBetween,\n                verticalAlignment = Alignment.Bottom\n            ) {\n                Column {\n                    Text(\n                        text = \"$current / $goal\",\n                        style = MaterialTheme.typography.headlineSmall,\n                        fontWeight = FontWeight.Bold,\n                        color = MaterialTheme.colorScheme.primary\n                    )\n                    Text(\n                        text = \"workouts this week\",\n                        style = MaterialTheme.typography.bodyMedium,\n                        color = MaterialTheme.colorScheme.onSurfaceVariant\n                    )\n                }\n                \n                Text(\n                    text = \"$percentage%\",\n                    style = MaterialTheme.typography.titleLarge,\n                    fontWeight = FontWeight.Bold,\n                    color = MaterialTheme.colorScheme.secondary\n                )\n            }\n            \n            Spacer(modifier = Modifier.height(12.dp))\n            \n            LinearProgressIndicator(\n                progress = progress,\n                modifier = Modifier.fillMaxWidth(),\n                color = MaterialTheme.colorScheme.primary,\n                trackColor = MaterialTheme.colorScheme.surfaceVariant\n            )\n        }\n    }\n}"