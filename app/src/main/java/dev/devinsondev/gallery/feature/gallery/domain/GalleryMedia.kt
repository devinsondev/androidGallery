package dev.devinsondev.gallery.feature.gallery.domain

data class GalleryMedia(
    val id: Long,
    val uri: String,
    val displayName: String,
    val mimeType: String?,
    val kind: MediaKind,
    val sizeBytes: Long,
    val width: Int,
    val height: Int,
    val dateMillis: Long,
    val durationMillis: Long? = null,
)

enum class MediaKind {
    IMAGE,
    VIDEO,
}
