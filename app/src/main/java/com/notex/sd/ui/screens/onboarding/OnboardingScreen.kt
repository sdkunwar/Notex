package com.notex.sd.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.notex.sd.R
import com.notex.sd.ui.theme.Primary50
import com.notex.sd.ui.theme.Primary70
import com.notex.sd.ui.theme.Tertiary50
import com.notex.sd.ui.theme.Tertiary70
import kotlinx.coroutines.launch

data class OnboardingPage(
    val icon: ImageVector,
    val iconBackground: List<Color>,
    val titleRes: Int,
    val descriptionRes: Int
)

private val onboardingPages = listOf(
    OnboardingPage(
        icon = Icons.AutoMirrored.Rounded.Notes,
        iconBackground = listOf(Primary50, Primary70),
        titleRes = R.string.onboarding_title_1,
        descriptionRes = R.string.onboarding_desc_1
    ),
    OnboardingPage(
        icon = Icons.Rounded.Edit,
        iconBackground = listOf(Tertiary50, Tertiary70),
        titleRes = R.string.onboarding_title_2,
        descriptionRes = R.string.onboarding_desc_2
    ),
    OnboardingPage(
        icon = Icons.Rounded.FolderOpen,
        iconBackground = listOf(Color(0xFF10B981), Color(0xFF34D399)),
        titleRes = R.string.onboarding_title_3,
        descriptionRes = R.string.onboarding_desc_3
    ),
    OnboardingPage(
        icon = Icons.Rounded.ColorLens,
        iconBackground = listOf(Color(0xFFF59E0B), Color(0xFFFBBF24)),
        titleRes = R.string.onboarding_title_4,
        descriptionRes = R.string.onboarding_desc_4
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            AnimatedVisibility(
                visible = pagerState.currentPage < onboardingPages.lastIndex,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                TextButton(
                    onClick = {
                        viewModel.completeOnboarding()
                        onFinish()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.onboarding_skip),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            OnboardingPageContent(page = onboardingPages[page])
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            onboardingPages.forEachIndexed { index, _ ->
                val isSelected = pagerState.currentPage == index
                val width by animateDpAsState(
                    targetValue = if (isSelected) 24.dp else 8.dp,
                    animationSpec = tween(300),
                    label = "indicator_width"
                )

                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .height(8.dp)
                        .width(width)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                            }
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (pagerState.currentPage < onboardingPages.lastIndex) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    viewModel.completeOnboarding()
                    onFinish()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (pagerState.currentPage < onboardingPages.lastIndex) {
                    stringResource(R.string.onboarding_next)
                } else {
                    stringResource(R.string.onboarding_get_started)
                },
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(
                    brush = Brush.linearGradient(colors = page.iconBackground),
                    shape = RoundedCornerShape(32.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = stringResource(page.titleRes),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(page.descriptionRes),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}
