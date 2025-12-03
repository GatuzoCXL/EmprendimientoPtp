package com.example.magnus.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import kotlinx.coroutines.delay

/**
 * Animación de fade in (aparecer gradualmente)
 * Úsala para cuando muestras contenido nuevo
 */
@Composable
fun AnimatedVisibilityFade(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ) + expandVertically(
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = 200,
                easing = FastOutLinearInEasing
            )
        ) + shrinkVertically(
            animationSpec = tween(
                durationMillis = 200,
                easing = FastOutLinearInEasing
            )
        ),
        content = content
    )
}

/**
 * Animación de slide desde abajo
 * Úsala para tarjetas o elementos que aparecen
 */
@Composable
fun AnimatedSlideUp(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(
            animationSpec = tween(durationMillis = 300)
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(durationMillis = 200)
        ) + fadeOut(
            animationSpec = tween(durationMillis = 200)
        ),
        content = content
    )
}

/**
 * Modificador para animar el tamaño al hacer clic
 * Úsalo en botones y tarjetas clickeables
 */
@Composable
fun Modifier.bounceClick(): Modifier {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bounce"
    )
    
    return this.scale(scale)
}

/**
 * Animación de shimmer para skeletons/placeholders
 */
@Composable
fun rememberShimmerAnimation(): Float {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerProgress"
    )
    return progress
}

/**
 * Animación de pulsación (para indicadores o notificaciones)
 */
@Composable
fun rememberPulseAnimation(): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    return scale
}

/**
 * Animación de rotación suave
 * Úsala para iconos de loading o refresh
 */
@Composable
fun Modifier.rotate360(): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationAngle"
    )
    
    return this.graphicsLayer {
        rotationZ = angle
    }
}

/**
 * Delay animado entre elementos de lista
 * Úsalo para animar listas con stagger effect
 */
@Composable
fun AnimatedListItem(
    index: Int,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay((index * 50L).coerceAtMost(500L))
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 400,
                easing = FastOutSlowInEasing
            )
        ) + slideInHorizontally(
            initialOffsetX = { -it / 2 },
            animationSpec = tween(
                durationMillis = 400,
                easing = FastOutSlowInEasing
            )
        ),
        content = { content() }
    )
}

/**
 * Animación de transición de tamaño
 * Úsala cuando cambies el contenido dinámicamente
 */
@Composable
fun AnimatedSizeBox(
    content: @Composable () -> Unit
) {
    AnimatedContent(
        targetState = content,
        transitionSpec = {
            fadeIn(
                animationSpec = tween(300)
            ) + scaleIn(
                initialScale = 0.92f,
                animationSpec = tween(300)
            ) togetherWith fadeOut(
                animationSpec = tween(200)
            ) + scaleOut(
                targetScale = 0.92f,
                animationSpec = tween(200)
            )
        },
        label = "contentTransition"
    ) { currentContent ->
        currentContent()
    }
}

/**
 * Transición suave entre estados
 * Ejemplo: Loading -> Success -> Error
 */
@Composable
fun <T> AnimatedStateTransition(
    targetState: T,
    content: @Composable (T) -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        transitionSpec = {
            fadeIn(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            ) + slideInVertically(
                initialOffsetY = { it / 4 },
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            ) togetherWith fadeOut(
                animationSpec = tween(
                    durationMillis = 200,
                    easing = FastOutLinearInEasing
                )
            ) + slideOutVertically(
                targetOffsetY = { -it / 4 },
                animationSpec = tween(
                    durationMillis = 200,
                    easing = FastOutLinearInEasing
                )
            )
        },
        label = "stateTransition"
    ) { state ->
        content(state)
    }
}

/**
 * Texto que cuenta numéricamente desde 0 hasta el valor objetivo
 */
@Composable
fun CountingText(
    value: String,
    style: androidx.compose.ui.text.TextStyle,
    fontWeight: androidx.compose.ui.text.font.FontWeight? = null,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    // Intentar parsear el valor a número, si no es número, mostrar tal cual
    val targetValue = value.filter { it.isDigit() }.toIntOrNull()
    
    if (targetValue != null) {
        val animatable = remember { Animatable(0f) }
        
        LaunchedEffect(targetValue) {
            animatable.animateTo(
                targetValue = targetValue.toFloat(),
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = FastOutSlowInEasing
                )
            )
        }
        
        Text(
            text = "${animatable.value.toInt()}",
            style = style,
            fontWeight = fontWeight,
            color = color,
            modifier = modifier
        )
    } else {
        Text(
            text = value,
            style = style,
            fontWeight = fontWeight,
            color = color,
            modifier = modifier
        )
    }
}

/**
 * Modificador para efecto de pulso continuo
 */
@Composable
fun Modifier.pulseEffect(
    enabled: Boolean = true,
    scale: Float = 1.05f
): Modifier {
    if (!enabled) return this
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulseEffect")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = scale,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    return this.scale(pulseScale)
}
