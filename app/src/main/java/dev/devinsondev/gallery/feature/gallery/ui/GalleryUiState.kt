package dev.devinsondev.gallery.feature.gallery.ui

import dev.devinsondev.gallery.feature.gallery.domain.GalleryMedia
import dev.devinsondev.gallery.feature.gallery.domain.MediaReadAccess

data class GalleryUiState(
    val access: MediaReadAccess = MediaReadAccess.None,
    val content: GalleryContent = GalleryContent.Loading,
    val selectedMedia: GalleryMedia? = null,
)

sealed interface GalleryContent {
    data object Loading : GalleryContent
    data class Ready(val items: List<GalleryMedia>) : GalleryContent
    data object Error : GalleryContent
}
