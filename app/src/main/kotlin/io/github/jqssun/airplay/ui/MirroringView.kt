package io.github.jqssun.airplay.ui

import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun MirroringView(
    onSurfaceAvailable: (Surface) -> Unit,
    onSurfaceDestroyed: (Surface) -> Unit,
    aspectRatio: Float = 16f / 9f,
    fillScreen: Boolean = false,
    modifier: Modifier = Modifier
) {
    val callbacks = remember {
        object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                onSurfaceAvailable(holder.surface)
            }
            override fun surfaceChanged(holder: SurfaceHolder, fmt: Int, w: Int, h: Int) {
                onSurfaceAvailable(holder.surface)
            }
            override fun surfaceDestroyed(holder: SurfaceHolder) {
                onSurfaceDestroyed(holder.surface)
            }
        }
    }

    val view: @Composable (Modifier) -> Unit = { m ->
        AndroidView(
            factory = { ctx ->
                SurfaceView(ctx).also {
                    it.holder.addCallback(callbacks)
                }
            },
            modifier = m
        )
    }

    if (fillScreen) {
        // scale the video up to cover the whole container, cropping the overflow
        BoxWithConstraints(
            modifier = modifier.fillMaxSize().clipToBounds(),
            contentAlignment = Alignment.Center
        ) {
            val w = maxOf(maxWidth, maxHeight * aspectRatio)
            view(Modifier.requiredSize(width = w, height = w / aspectRatio))
        }
    } else {
        view(
            modifier
                .aspectRatio(aspectRatio, matchHeightConstraintsFirst = aspectRatio < 1f)
                .fillMaxSize()
        )
    }
}
