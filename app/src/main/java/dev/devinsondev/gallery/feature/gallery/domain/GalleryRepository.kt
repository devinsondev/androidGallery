package dev.devinsondev.gallery.feature.gallery.domain

interface GalleryRepository {
    suspend fun loadMedia(access: MediaReadAccess): List<GalleryMedia>
}
