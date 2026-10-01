package dev.devinsondev.gallery.feature.gallery.domain

data class MediaReadAccess(
    val canReadImages: Boolean,
    val canReadVideos: Boolean,
    val isLimited: Boolean,
) {
    val hasAnyAccess: Boolean
        get() = canReadImages || canReadVideos

    companion object {
        val None = MediaReadAccess(
            canReadImages = false,
            canReadVideos = false,
            isLimited = false,
        )
    }
}
