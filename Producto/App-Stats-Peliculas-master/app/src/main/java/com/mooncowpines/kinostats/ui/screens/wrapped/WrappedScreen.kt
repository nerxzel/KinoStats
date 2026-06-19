package com.mooncowpines.kinostats.ui.screens.wrapped

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.MovieFilter
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import java.time.format.TextStyle
import java.util.Locale
import com.mooncowpines.kinostats.domain.model.UserStats
import com.mooncowpines.kinostats.ui.theme.KinoBlack
import com.mooncowpines.kinostats.ui.theme.KinoGray
import com.mooncowpines.kinostats.ui.theme.KinoYellow
import com.mooncowpines.kinostats.ui.theme.KinoYellowVerticalGradient
import com.mooncowpines.kinostats.ui.components.KinoBentoBox
import com.mooncowpines.kinostats.ui.components.KinoBentoList
import com.mooncowpines.kinostats.ui.components.KinoBentoPoster
import com.mooncowpines.kinostats.utils.formatTotalMinutes
import com.mooncowpines.kinostats.utils.shareWrappedSlide
import kotlinx.coroutines.launch

@Composable
fun WrappedScreen(
    modifier: Modifier = Modifier,
    viewModel: WrappedViewModel = hiltViewModel(),
    onClose: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        Box(modifier = modifier.fillMaxSize().background(KinoBlack), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = KinoYellow)
        }
    } else if (state.errorMsg != null) {
        Box(modifier = modifier.fillMaxSize().background(KinoBlack), contentAlignment = Alignment.Center) {
            Text(state.errorMsg!!, color = Color.Red)
        }
    } else {
        state.stats?.let { stats ->
            val periodText = "${viewModel.targetYear}"

            WrappedContent(
                stats = stats,
                periodText = periodText,
                onClose = onClose,
                modifier = modifier
            )
        }
    }
}

@Composable
fun WrappedContent(
    stats: UserStats,
    periodText: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentSlide by remember { mutableIntStateOf(0) }
    val totalSlides = 5
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(KinoBlack)
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(KinoYellowVerticalGradient)
                .drawWithContent {
                    graphicsLayer.record {
                        this@drawWithContent.drawContent()
                    }
                    drawContent()
                },
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = currentSlide,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInVertically(animationSpec = tween(400)) { width -> width } + fadeIn()) togetherWith
                                (slideOutHorizontally(animationSpec = tween(400)) { width -> -width } + fadeOut())
                    }
                    else {
                        (slideInHorizontally(animationSpec = tween(400)) { width -> -width } + fadeIn()) togetherWith
                                (slideOutHorizontally(animationSpec = tween(400)) { width -> width } + fadeOut())
                    }
                },
                label = "WrappedTransition"
            ) { targetSlide ->

                when (targetSlide) {
                    0 -> WelcomeSlide(totalMovies = stats.totalMovies, period = periodText)
                    1 -> TimeSlide(totalMinutes = stats.totalMinutes)
                    2 -> CategorySlide(
                        title = "Your Top Genre",
                        value = stats.genres.firstOrNull()?.label ?: "Unknown",
                        subtitle = "This was your comfort zone all year.",
                        icon = Icons.Rounded.MovieFilter
                    )
                    3 -> CategorySlide(
                        title = "Your Top Director",
                        value = stats.topDirectors.firstOrNull()?.label ?: "Unknown",
                        subtitle = "The mastermind behind your screen.",
                        icon = Icons.Rounded.Videocam
                    )

                    4 -> EndSlide(
                        stats = stats,
                        period = periodText
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (i in 0 until totalSlides) {
                Box(modifier = Modifier.weight(1f).height(4.dp).background(if (i <= currentSlide) Color.White else Color.DarkGray))
            }
        }

        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxHeight().weight(0.3f).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                    if (currentSlide > 0) currentSlide--
                }
            )
            Box(
                modifier = Modifier.fillMaxHeight().weight(0.7f).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                    if (currentSlide < totalSlides - 1) currentSlide++ else onClose()
                }
            )
        }

        IconButton(
            onClick = {
                coroutineScope.launch {
                    val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                    shareWrappedSlide(context, bitmap)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share Slide",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun WelcomeSlide(totalMovies: Int, period: String) {
    var targetValue by remember { mutableIntStateOf(0) }

    val animationDuration = (totalMovies * 100).coerceIn(300, 1500)
    val animatedCount by animateIntAsState(
        targetValue = targetValue,
        animationSpec = tween(durationMillis = animationDuration, easing = FastOutSlowInEasing),
        label = "MovieCounter"
    )

    LaunchedEffect(Unit) {
        targetValue = totalMovies
    }

    val funFact = if (totalMovies > 0) {
        val moviesPerMonth = totalMovies / 12
        if (moviesPerMonth > 0) "That's about $moviesPerMonth movies every month!"
        else "Quality over quantity!"
    } else {
        "Every frame counts."
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {

        Text(
            text = "During $period, you watched",
            color = KinoGray,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "$animatedCount",
            color = KinoYellow,
            fontSize = 110.sp,
            fontWeight = FontWeight.Black
        )

        Text(
            text = "MOVIES",
            color = KinoYellow,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = funFact,
            color = KinoGray,
            fontSize = 16.sp,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TimeSlide(totalMinutes: Int) {
    var currentTotalMinutes by remember { mutableIntStateOf(0) }

    val dynamicDuration = (totalMinutes * 50).coerceIn(400, 5000)

    val animatedTotal by animateIntAsState(
        targetValue = currentTotalMinutes,
        animationSpec = tween(durationMillis = dynamicDuration, easing = FastOutSlowInEasing),
        label = "TimeCounter"
    )

    LaunchedEffect(Unit) {
        currentTotalMinutes = totalMinutes
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        Text("You spent", color = Color.Gray, fontSize = 20.sp)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = formatTotalMinutes(animatedTotal),
            color = KinoYellow,
            fontSize = 65.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("in front of the screen", color = Color.Gray, fontSize = 20.sp)
    }
}

@Composable
fun CategorySlide(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = KinoYellow,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(title, color = Color.LightGray, fontSize = 20.sp, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
            visible = isVisible,
            enter = scaleIn(animationSpec = tween(600)) + fadeIn(animationSpec = tween(600))
        ) {
            Text(
                text = value.uppercase(),
                color = KinoYellow,
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                lineHeight = 48.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(800, delayMillis = 400))
        ) {
            Text(
                text = subtitle,
                color = Color.Gray,
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EndSlide(stats: UserStats, period: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 64.dp, bottom = 88.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Your Kino Wrapped", color = Color.Gray, fontSize = 16.sp)
            Text(period.uppercase(), color = KinoYellow, fontSize = 24.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                KinoBentoBox(
                    modifier = Modifier.weight(1f),
                    title = "MOVIES",
                    value = "${stats.totalMovies}")
                KinoBentoBox(
                    modifier = Modifier.weight(1f),
                    title = "TIME WATCHED",
                    value = formatTotalMinutes(stats.totalMinutes)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 180.dp)
            ) {
                KinoBentoPoster(
                    modifier = Modifier.weight(1f),
                    title = "First Watched",
                    movieCard = stats.firstMovie
                )
                KinoBentoPoster(
                    modifier = Modifier.weight(1f),
                    title = "Last Watched",
                    movieCard = stats.lastMovie
                )
            }

            KinoBentoList(
                modifier = Modifier.fillMaxWidth(),
                title = "TOP GENRES",
                items = stats.genres,
                backgroundColor = KinoYellow.copy(alpha = 0.15f),
                valueColor = KinoYellow
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                KinoBentoList(
                    modifier = Modifier.weight(1f),
                    title = "TOP ACTORS",
                    items = stats.topActors
                )
                KinoBentoList(
                    modifier = Modifier.weight(1f),
                    title = "DIRECTORS",
                    items = stats.topDirectors
                )
            }
        }
    }
}

