package com.anant.sivonotes.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconColor: Color,
    val iconBgColor: Color
)

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = listOf(
        OnboardingPage(
            title = "Distraction-Free Notes\n& Folders",
            subtitle = "Organize thoughts, concepts, and memory points with instant debounced auto-save and color-coded folders.",
            icon = Icons.Outlined.Description,
            iconColor = Color(0xFF6C5CE7),
            iconBgColor = Color(0xFF6C5CE7).copy(alpha = 0.12f)
        ),
        OnboardingPage(
            title = "Daily Tasks &\nStreak Tracking",
            subtitle = "Schedule daily and recurring tasks with local offline alarms. Build consistent habits with live streak tracking.",
            icon = Icons.Outlined.LocalFireDepartment,
            iconColor = Color(0xFFE17055),
            iconBgColor = Color(0xFFFFECEB)
        ),
        OnboardingPage(
            title = "Hardware-Encrypted\nSecure Vault",
            subtitle = "Store passwords and private documents with AES-256-GCM Keystore encryption and instant Biometric authentication.",
            icon = Icons.Outlined.Shield,
            iconColor = Color(0xFF00B894),
            iconBgColor = Color(0xFF00B894).copy(alpha = 0.12f)
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        // Skip Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.End
        ) {
            if (pagerState.currentPage < pages.size - 1) {
                TextButton(onClick = onComplete) {
                    Text(
                        text = "Skip",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }

        // Pager Content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { pageIndex ->
            val page = pages[pageIndex]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(page.iconBgColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = page.icon,
                        contentDescription = null,
                        tint = page.iconColor,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))

                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 28.sp, lineHeight = 34.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = page.subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Dot Page Indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pages.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .height(8.dp)
                        .width(if (isSelected) 24.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Button
        Button(
            onClick = {
                if (pagerState.currentPage < pages.size - 1) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    onComplete()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = if (pagerState.currentPage == pages.size - 1) "Get Started" else "Continue",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(36.dp))
    }
}
