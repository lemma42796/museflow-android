package com.ixuea.courses.mymusic.component.music.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.ui.compose.MuseFlowScaffold
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ScanLocalMusicScreen(
    progressText: String,
    isScanning: Boolean,
    isScanComplete: Boolean,
    onBack: () -> Unit,
    onScanClick: () -> Unit,
) {
    MuseFlowScaffold(
        title = stringResource(R.string.scan_local_music),
        onBack = onBack,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = progressText,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(24.dp))

            ScanAnimation(isScanning = isScanning)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onScanClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(
                        when {
                            isScanComplete -> R.string.to_my_music
                            isScanning -> R.string.stop_scan
                            else -> R.string.start_scan
                        },
                    ),
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ScanAnimation(isScanning: Boolean) {
    val transition = rememberInfiniteTransition(label = "scan-local-music")
    val lineOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 96f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "scan-line-offset",
    )
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 30000),
            repeatMode = RepeatMode.Restart,
        ),
        label = "scan-zoom-angle",
    )
    val radius = with(LocalDensity.current) { 30.dp.toPx() }

    Box(
        modifier = Modifier.size(width = 220.dp, height = 250.dp),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.scan_local_music_phone),
            contentDescription = null,
        )

        if (isScanning) {
            Image(
                painter = painterResource(R.drawable.scan_local_music_line),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = lineOffset.dp),
            )
        }

        Image(
            painter = painterResource(R.drawable.scan_local_music_zoom),
            contentDescription = null,
            modifier = Modifier.graphicsLayer {
                if (isScanning) {
                    translationX = (radius * cos(Math.toRadians(angle.toDouble()))).toFloat()
                    translationY = (radius * sin(Math.toRadians(angle.toDouble()))).toFloat()
                }
            },
        )
    }
}
