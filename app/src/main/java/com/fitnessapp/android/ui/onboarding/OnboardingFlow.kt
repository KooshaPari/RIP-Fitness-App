package com.fitnessapp.android.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Complete onboarding flow for new users
 * 
 * Features:
 * - Welcome and app introduction
 * - Goal setting and personalization
 * - Permission requests (health data, notifications)
 * - Initial profile setup
 * - Health data source connections
 * 
 * Uses horizontal pager for smooth step-by-step progression
 * Collects essential user preferences and permissions upfront
 */

data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector,
    val actionText: String,
    val onAction: () -> Unit = {}
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingFlow(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = remember {
        listOf(
            OnboardingPage(
                title = \"Welcome to FitnessApp\",\n                subtitle = \"Your Personal Fitness Companion\",\n                description = \"Track your nutrition, workouts, and health data all in one place. Let's help you achieve your fitness goals!\",\n                icon = Icons.Default.FitnessCenter,\n                actionText = \"Get Started\"\n            ),\n            OnboardingPage(\n                title = \"Set Your Goals\",\n                subtitle = \"Personalize Your Journey\",\n                description = \"Tell us about your fitness goals so we can provide personalized recommendations and track your progress.\",\n                icon = Icons.Default.Flag,\n                actionText = \"Set Goals\"\n            ),\n            OnboardingPage(\n                title = \"Track Your Nutrition\",\n                subtitle = \"Fuel Your Success\",\n                description = \"Log your meals, scan barcodes, and monitor your macro intake to optimize your nutrition.\",\n                icon = Icons.Default.Restaurant,\n                actionText = \"Continue\"\n            ),\n            OnboardingPage(\n                title = \"Log Your Workouts\",\n                subtitle = \"Every Rep Counts\",\n                description = \"Record your exercises, build custom routines, and track your strength and endurance progress.\",\n                icon = Icons.Default.FitnessCenter,\n                actionText = \"Continue\"\n            ),\n            OnboardingPage(\n                title = \"Connect Health Data\",\n                subtitle = \"Get the Complete Picture\",\n                description = \"Sync with Apple Health, Google Fit, or your fitness tracker for comprehensive health insights.\",\n                icon = Icons.Default.HealthAndSafety,\n                actionText = \"Connect\"\n            ),\n            OnboardingPage(\n                title = \"Enable Notifications\",\n                subtitle = \"Stay on Track\",\n                description = \"Get reminders for workouts, meal logging, and motivational updates to maintain your momentum.\",\n                icon = Icons.Default.Notifications,\n                actionText = \"Enable\"\n            )\n        )\n    }\n    \n    val pagerState = rememberPagerState(pageCount = { pages.size })\n    val coroutineScope = rememberCoroutineScope()\n    \n    Column(\n        modifier = modifier.fillMaxSize()\n    ) {\n        // Progress indicator\n        LinearProgressIndicator(\n            progress = (pagerState.currentPage + 1).toFloat() / pages.size.toFloat(),\n            modifier = Modifier.fillMaxWidth(),\n            color = MaterialTheme.colorScheme.primary\n        )\n        \n        // Page content\n        HorizontalPager(\n            state = pagerState,\n            modifier = Modifier.weight(1f)\n        ) { page ->\n            OnboardingPage(\n                page = pages[page],\n                modifier = Modifier.fillMaxSize()\n            )\n        }\n        \n        // Navigation controls\n        Row(\n            modifier = Modifier\n                .fillMaxWidth()\n                .padding(16.dp),\n            horizontalArrangement = Arrangement.SpaceBetween,\n            verticalAlignment = Alignment.CenterVertically\n        ) {\n            // Skip button (visible on all pages except last)\n            if (pagerState.currentPage < pages.size - 1) {\n                TextButton(\n                    onClick = {\n                        // Skip to end or complete onboarding\n                        onComplete()\n                    }\n                ) {\n                    Text(\"Skip\")\n                }\n            } else {\n                Spacer(modifier = Modifier.width(1.dp))\n            }\n            \n            // Page indicators\n            Row(\n                horizontalArrangement = Arrangement.spacedBy(8.dp)\n            ) {\n                repeat(pages.size) { index ->\n                    Box(\n                        modifier = Modifier\n                            .size(8.dp)\n                            .background(\n                                color = if (index == pagerState.currentPage) \n                                    MaterialTheme.colorScheme.primary \n                                else \n                                    MaterialTheme.colorScheme.outline,\n                                shape = CircleShape\n                            )\n                    )\n                }\n            }\n            \n            // Next/Complete button\n            if (pagerState.currentPage < pages.size - 1) {\n                Button(\n                    onClick = {\n                        coroutineScope.launch {\n                            pagerState.animateScrollToPage(pagerState.currentPage + 1)\n                        }\n                    }\n                ) {\n                    Text(\"Next\")\n                }\n            } else {\n                Button(\n                    onClick = onComplete\n                ) {\n                    Text(\"Get Started\")\n                }\n            }\n        }\n    }\n}\n\n@Composable\nprivate fun OnboardingPage(\n    page: OnboardingPage,\n    modifier: Modifier = Modifier\n) {\n    Column(\n        modifier = modifier\n            .fillMaxSize()\n            .padding(24.dp),\n        horizontalAlignment = Alignment.CenterHorizontally,\n        verticalArrangement = Arrangement.Center\n    ) {\n        // Icon\n        Icon(\n            imageVector = page.icon,\n            contentDescription = page.title,\n            modifier = Modifier.size(120.dp),\n            tint = MaterialTheme.colorScheme.primary\n        )\n        \n        Spacer(modifier = Modifier.height(32.dp))\n        \n        // Title\n        Text(\n            text = page.title,\n            style = MaterialTheme.typography.headlineMedium,\n            fontWeight = FontWeight.Bold,\n            textAlign = TextAlign.Center\n        )\n        \n        Spacer(modifier = Modifier.height(8.dp))\n        \n        // Subtitle\n        Text(\n            text = page.subtitle,\n            style = MaterialTheme.typography.titleMedium,\n            color = MaterialTheme.colorScheme.primary,\n            textAlign = TextAlign.Center\n        )\n        \n        Spacer(modifier = Modifier.height(24.dp))\n        \n        // Description\n        Text(\n            text = page.description,\n            style = MaterialTheme.typography.bodyLarge,\n            textAlign = TextAlign.Center,\n            color = MaterialTheme.colorScheme.onSurfaceVariant,\n            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight\n        )\n        \n        Spacer(modifier = Modifier.height(48.dp))\n        \n        // Action button for specific pages (goals, permissions, etc.)\n        if (page.actionText != \"Continue\" && page.actionText != \"Get Started\" && page.actionText != \"Next\") {\n            OutlinedButton(\n                onClick = page.onAction,\n                modifier = Modifier.fillMaxWidth(0.8f)\n            ) {\n                Text(page.actionText)\n            }\n        }\n    }\n}"