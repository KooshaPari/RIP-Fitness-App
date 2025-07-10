package com.fitnessapp.android.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Card component for displaying health data sync status
 * 
 * Shows connection status, last sync time, and connected sources
 * Used in health settings and dashboard screens
 * 
 * @param isConnected Whether health data sync is active
 * @param lastSyncTime When data was last synchronized
 * @param connectedSources List of connected health data sources
 * @param onSyncClick Callback when sync button is tapped
 * @param modifier Modifier for the card
 */
@Composable
fun SyncStatusCard(
    isConnected: Boolean,
    lastSyncTime: String,
    connectedSources: List<String>,
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isConnected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isConnected) Icons.Default.CloudDone else Icons.Default.CloudOff,
                        contentDescription = if (isConnected) \"Connected\" else \"Disconnected\",\n                        tint = if (isConnected) \n                            MaterialTheme.colorScheme.onPrimaryContainer \n                        else \n                            MaterialTheme.colorScheme.onErrorContainer\n                    )\n                    Spacer(modifier = Modifier.width(8.dp))\n                    Column {\n                        Text(\n                            text = if (isConnected) \"Health Data Synced\" else \"Sync Disconnected\",\n                            style = MaterialTheme.typography.titleSmall,\n                            fontWeight = FontWeight.SemiBold,\n                            color = if (isConnected) \n                                MaterialTheme.colorScheme.onPrimaryContainer \n                            else \n                                MaterialTheme.colorScheme.onErrorContainer\n                        )\n                        Text(\n                            text = if (isConnected) \"Last sync: $lastSyncTime\" else \"Tap to reconnect\",\n                            style = MaterialTheme.typography.bodySmall,\n                            color = if (isConnected) \n                                MaterialTheme.colorScheme.onPrimaryContainer \n                            else \n                                MaterialTheme.colorScheme.onErrorContainer\n                        )\n                    }\n                }\n                \n                IconButton(\n                    onClick = onSyncClick\n                ) {\n                    Icon(\n                        imageVector = Icons.Default.Sync,\n                        contentDescription = \"Sync settings\",\n                        tint = if (isConnected) \n                            MaterialTheme.colorScheme.onPrimaryContainer \n                        else \n                            MaterialTheme.colorScheme.onErrorContainer\n                    )\n                }\n            }\n            \n            if (isConnected && connectedSources.isNotEmpty()) {\n                Spacer(modifier = Modifier.height(12.dp))\n                \n                Text(\n                    text = \"Connected Sources:\",\n                    style = MaterialTheme.typography.labelMedium,\n                    color = MaterialTheme.colorScheme.onPrimaryContainer\n                )\n                \n                Spacer(modifier = Modifier.height(8.dp))\n                \n                LazyRow(\n                    horizontalArrangement = Arrangement.spacedBy(8.dp)\n                ) {\n                    items(connectedSources) { source ->\n                        AssistChip(\n                            onClick = { },\n                            label = { \n                                Text(\n                                    text = source,\n                                    style = MaterialTheme.typography.labelSmall\n                                ) \n                            },\n                            leadingIcon = {\n                                Icon(\n                                    imageVector = Icons.Default.CheckCircle,\n                                    contentDescription = null,\n                                    modifier = Modifier.size(16.dp)\n                                )\n                            },\n                            colors = AssistChipDefaults.assistChipColors(\n                                containerColor = MaterialTheme.colorScheme.primary,\n                                labelColor = MaterialTheme.colorScheme.onPrimary,\n                                leadingIconContentColor = MaterialTheme.colorScheme.onPrimary\n                            )\n                        )\n                    }\n                }\n            }\n        }\n    }\n}"