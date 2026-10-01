package dev.devinsondev.gallery.feature.viewer.ui

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import dev.devinsondev.gallery.R
import dev.devinsondev.gallery.core.media.SafeImageDecoder
import dev.devinsondev.gallery.feature.gallery.domain.GalleryMedia

@Composable
fun SafeImageViewer(
    media: GalleryMedia,
    imageDecoder: SafeImageDecoder,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val targetWidth = with(density) { maxWidth.roundToPx() }
        val targetHeight = with(density) { maxHeight.roundToPx() }

        val result by produceState<Result<Drawable>?>(
            initialValue = null,
            media.uri,
            targetWidth,
            targetHeight,
        ) {
            value = imageDecoder.decode(media, targetWidth, targetHeight)
        }

        when {
            result == null -> LoadingImage()
            result?.isSuccess == true -> ZoomableDrawable(result!!.getOrThrow())
            else -> ImageError()
        }
    }
}

@Composable
private fun ZoomableDrawable(drawable: Drawable) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformState = rememberTransformableState { zoom, pan, _ ->
        scale = (scale * zoom).coerceIn(1f, 5f)
        offset = if (scale == 1f) Offset.Zero else offset + pan
    }

    DisposableEffect(drawable) {
        (drawable as? Animatable)?.start()
        onDispose { (drawable as? Animatable)?.stop() }
    }

    AndroidView(
        factory = { context ->
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
                setImageDrawable(drawable)
            }
        },
        update = { it.setImageDrawable(drawable) },
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y,
            )
            .transformable(transformState),
    )
}

@Composable
private fun LoadingImage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ImageError() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.viewer_error),
            color = MaterialTheme.colorScheme.error,
        )
    }
}
