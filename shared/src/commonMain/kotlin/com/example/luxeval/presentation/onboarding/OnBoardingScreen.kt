package com.example.luxeval.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.luxeval.data.AppSettingsStore
import kotlinx.coroutines.launch
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.ic_arrow_forward
import luxeval.shared.generated.resources.ic_other_jewelry
import luxeval.shared.generated.resources.image1
import luxeval.shared.generated.resources.image2
import luxeval.shared.generated.resources.image3
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

private data class OnboardingSlide(
    val imageUrl: DrawableResource,
    val title: String,
    val subtitle: String,
    val ctaLabel: String
)

// Swap these image URLs for your own hosted/bundled jewelry photography —
// this is the only thing you need to change to reskin the whole flow.
private val slides = listOf(
    OnboardingSlide(
        imageUrl = Res.drawable.image1,
        title = "Know the True\nWorth of Your Jewelry",
        subtitle = "Professional appraisals by certified gemologists for insurance, estate, and resale purposes.",
        ctaLabel = "Continue"
    ),
    OnboardingSlide(
        imageUrl = Res.drawable.image2,
        title = "Certified Reports\nYou Can Trust",
        subtitle = "Receive a detailed appraisal certificate accepted by insurers and financial institutions.",
        ctaLabel = "Continue"
    ),
    OnboardingSlide(
        imageUrl = Res.drawable.image3,
        title = "Submit from\nAnywhere, Anytime",
        subtitle = "Simply photograph your piece and fill in the details — our experts handle the rest.",
        ctaLabel = "Get Started"
    )
)

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val settingsStore: AppSettingsStore = koinInject()
    val pagerState = rememberPagerState(pageCount = { slides.size })
    val scope = rememberCoroutineScope()

    fun finish() {
        settingsStore.setCompletedOnboarding(true)
        onFinished()
    }

    Box(Modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            OnboardingSlideContent(
                slide = slides[page],
                pageIndex = page,
                pageCount = slides.size,
                showSkip = page < slides.lastIndex,
                onSkip = ::finish,
                onCta = {
                    if (page < slides.lastIndex) {
                        scope.launch { pagerState.animateScrollToPage(page + 1) }
                    } else {
                        finish()
                    }
                }
            )
        }
    }
}

@Composable
private fun OnboardingSlideContent(
    slide: OnboardingSlide,
    pageIndex: Int,
    pageCount: Int,
    showSkip: Boolean,
    onSkip: () -> Unit,
    onCta: () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(slide.imageUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Bottom scrim so white text stays legible over any photo.
        Box(
            Modifier
                .fillMaxWidth()
                .height(420.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                    )
                )
        )

        if (showSkip) {
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.White.copy(alpha = 0.18f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 48.dp, end = 20.dp)
            ) {
                Text(
                    "Skip",
                    color = Color.White,
                    modifier = Modifier
                        .clickable(onClick = onSkip)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Box(
                Modifier
                    .width(32.dp)
                    .height(3.dp)
                    .background(Color(0xFFD0B15B))
            )
            Spacer(Modifier.height(16.dp))
            Text(
                slide.title,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                slide.subtitle,
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(24.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                repeat(pageCount) { i ->
                    Box(
                        Modifier
                            .padding(horizontal = 3.dp)
                            .height(4.dp)
                            .width(if (i == pageIndex) 20.dp else 4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (i == pageIndex) Color(0xFFD0B15B) else Color.White.copy(alpha = 0.4f))
                    )
                }
            }
            Spacer(Modifier.height(20.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFB89232), Color(0xFFF0D06A), Color(0xFFB89232))
                        )
                    )
                    .clickable(onClick = onCta),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(24.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(painterResource(Res.drawable.ic_other_jewelry), contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
                Spacer(Modifier.width(10.dp))
                Text(slide.ctaLabel, color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(6.dp))
                Icon(painterResource(Res.drawable.ic_arrow_forward), contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
    }
}