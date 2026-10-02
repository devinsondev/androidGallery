package dev.devinsondev.gallery.feature.gallery.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.devinsondev.gallery.core.media.SafeImageDecoder
import dev.devinsondev.gallery.core.media.ThumbnailLoader
import dev.devinsondev.gallery.feature.viewer.ui.MediaViewerScreen

@Composable
fun GalleryRoute(
    viewModel: GalleryViewModel,
    thumbnailLoader: ThumbnailLoader,
    imageDecoder: SafeImageDecoder,
    onRequestMediaAccess: () -> Unit,
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val selected = state.selectedMedia
    val gridState = rememberLazyGridState()

    BackHandler(enabled = selected != null) {
        viewModel.closeViewer()
    }

    if (selected != null) {
        MediaViewerScreen(
            media = selected,
            imageDecoder = imageDecoder,
            onBack = viewModel::closeViewer,
        )
    } else {
        GalleryScreen(
            state = state,
            gridState = gridState,
            thumbnailLoader = thumbnailLoader,
            onRequestMediaAccess = onRequestMediaAccess,
            onRefresh = viewModel::refresh,
            onOpen = viewModel::open,
        )
    }
}
