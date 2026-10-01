package dev.devinsondev.gallery.feature.gallery.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.devinsondev.gallery.R
import dev.devinsondev.gallery.core.media.MediaSafetyPolicy
import dev.devinsondev.gallery.core.media.ThumbnailLoader
import dev.devinsondev.gallery.feature.gallery.domain.GalleryMedia
import dev.devinsondev.gallery.feature.gallery.domain.MediaKind

@Composable
fun MediaGrid(
    items: List<GalleryMedia>,
    thumbnailLoader: ThumbnailLoader,
    onOpen: (GalleryMedia) -> Unit,
    contentPadding: PaddingValues,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 108.dp),
        contentPadding = contentPadding,
    ) {
        items(
            items = items,
            key = GalleryMedia::uri,
        ) { media ->
            MediaTile(
                media = media,
                thumbnailLoader = thumbnailLoader,
                onClick = { onOpen(media) },
            )
        }
    }
}

@Composable
private fun MediaTile(
    media: GalleryMedia,
    thumbnailLoader: ThumbnailLoader,
    onClick: () -> Unit,
) {
    val tileSizePx = with(LocalDensity.current) { 144.dp.roundToPx() }
    val thumbnail = produceState<Bitmap?>(
        initialValue = null,
        media.uri,
        tileSizePx,
    ) {
        value = thumbnailLoader.load(media, tileSizePx)
    }
    val verdict = MediaSafetyPolicy.evaluate(media)

    Box(
        modifier = Modifier
            .padding(1.dp)
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.extraSmall)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .semantics { contentDescription = media.displayName }
            .clickable(onClick = onClick),
    ) {
        val bitmap = thumbnail.value
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Icon(
                imageVector = if (media.kind == MediaKind.VIDEO) {
                    Icons.Outlined.PlayCircle
                } else {
                    Icons.Outlined.Photo
                },
                contentDescription = null,
                modifier = Modifier.align(Alignment.Center),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (!verdict.isAllowed) {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = stringResource(R.string.blocked_description),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        } else if (media.kind == MediaKind.VIDEO) {
            Text(
                text = formatDuration(media.durationMillis),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.78f))
                    .padding(horizontal = 6.dp, vertical = 3.dp),
            )
        }
    }
}

private fun formatDuration(durationMillis: Long?): String {
    val seconds = ((durationMillis ?: 0L) / 1000L).coerceAtLeast(0L)
    val minutes = seconds / 60L
    val remaining = seconds % 60L
    return "$minutes:${remaining.toString().padStart(2, '0')}"
}
