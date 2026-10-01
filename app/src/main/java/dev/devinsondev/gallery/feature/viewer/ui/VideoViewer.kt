package dev.devinsondev.gallery.feature.viewer.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import dev.devinsondev.gallery.R
import dev.devinsondev.gallery.core.media.MediaUriGuard
import dev.devinsondev.gallery.feature.gallery.domain.GalleryMedia

@Composable
fun VideoViewer(media: GalleryMedia) {
    val context = LocalContext.current
    val uri = remember(media.uri) { MediaUriGuard.parseTrusted(media.uri) }

    if (uri == null) {
        InvalidVideo()
        return
    }

    val resumePosition = rememberSaveable(media.uri) { mutableLongStateOf(0L) }
    val player = remember(uri) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            seekTo(resumePosition.longValue)
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(player) {
        onDispose {
            resumePosition.longValue = player.currentPosition.coerceAtLeast(0L)
            player.release()
        }
    }

    AndroidView(
        factory = { viewContext ->
            PlayerView(viewContext).apply {
                useController = true
                setShowNextButton(false)
                setShowPreviousButton(false)
                this.player = player
            }
        },
        update = { it.player = player },
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
private fun InvalidVideo() {
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
