package dev.devinsondev.gallery.core.media

import dev.devinsondev.gallery.feature.gallery.domain.GalleryMedia
import dev.devinsondev.gallery.feature.gallery.domain.MediaKind

object MediaSafetyPolicy {
    private const val MAX_IMAGE_BYTES = 256L * 1024L * 1024L
    private const val MAX_GIF_BYTES = 128L * 1024L * 1024L
    private const val MAX_VIDEO_BYTES = 64L * 1024L * 1024L * 1024L
    private const val MAX_DIMENSION = 16_384
    private const val MAX_PIXELS = 150_000_000L
    private const val MAX_VIDEO_DURATION_MS = 24L * 60L * 60L * 1000L

    private val imageMimeTypes = setOf(
        "image/jpeg",
        "image/png",
        "image/webp",
        "image/gif",
        "image/heif",
        "image/heic",
        "image/avif",
    )

    private val videoMimeTypes = setOf(
        "video/mp4",
        "video/webm",
        "video/3gpp",
        "video/quicktime",
        "video/x-matroska",
    )

    fun evaluate(media: GalleryMedia): SafetyVerdict {
        val mime = media.mimeType?.lowercase()
            ?: return SafetyVerdict.blocked(SafetyIssue.UNSUPPORTED_TYPE)

        if (!isMimeAllowed(media.kind, mime)) {
            return SafetyVerdict.blocked(SafetyIssue.UNSUPPORTED_TYPE)
        }
        if (isTooLarge(media, mime)) {
            return SafetyVerdict.blocked(SafetyIssue.TOO_LARGE)
        }
        if (hasSuspiciousDimensions(media)) {
            return SafetyVerdict.blocked(SafetyIssue.SUSPICIOUS_DIMENSIONS)
        }
        if (
            media.kind == MediaKind.VIDEO &&
            (media.durationMillis ?: 0L) > MAX_VIDEO_DURATION_MS
        ) {
            return SafetyVerdict.blocked(SafetyIssue.EXTREME_DURATION)
        }

        return SafetyVerdict.Allowed
    }

    fun dimensionsAreSafe(width: Int, height: Int): Boolean {
        if (width <= 0 || height <= 0) return false
        if (width > MAX_DIMENSION || height > MAX_DIMENSION) return false
        return width.toLong() * height.toLong() <= MAX_PIXELS
    }

    private fun isMimeAllowed(kind: MediaKind, mime: String): Boolean = when (kind) {
        MediaKind.IMAGE -> mime in imageMimeTypes
        MediaKind.VIDEO -> mime in videoMimeTypes
    }

    private fun isTooLarge(media: GalleryMedia, mime: String): Boolean {
        val limit = when {
            media.kind == MediaKind.VIDEO -> MAX_VIDEO_BYTES
            mime == "image/gif" -> MAX_GIF_BYTES
            else -> MAX_IMAGE_BYTES
        }
        return media.sizeBytes > 0 && media.sizeBytes > limit
    }

    private fun hasSuspiciousDimensions(media: GalleryMedia): Boolean {
        if (media.width <= 0 || media.height <= 0) return false
        return !dimensionsAreSafe(media.width, media.height)
    }
}

data class SafetyVerdict(
    val isAllowed: Boolean,
    val issue: SafetyIssue? = null,
) {
    companion object {
        val Allowed = SafetyVerdict(isAllowed = true)

        fun blocked(issue: SafetyIssue) = SafetyVerdict(
            isAllowed = false,
            issue = issue,
        )
    }
}

enum class SafetyIssue {
    UNSUPPORTED_TYPE,
    TOO_LARGE,
    SUSPICIOUS_DIMENSIONS,
    EXTREME_DURATION,
    INVALID_URI,
}
