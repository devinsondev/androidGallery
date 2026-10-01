package dev.devinsondev.gallery.core.media

import dev.devinsondev.gallery.feature.gallery.domain.GalleryMedia
import dev.devinsondev.gallery.feature.gallery.domain.MediaKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MediaSafetyPolicyTest {
    @Test
    fun normalPhonePhotoIsAllowed() {
        val result = MediaSafetyPolicy.evaluate(
            media(
                mime = "image/jpeg",
                width = 8160,
                height = 6120,
                size = 18L * 1024L * 1024L,
            ),
        )

        assertTrue(result.isAllowed)
    }

    @Test
    fun oversizedGifIsBlockedBeforeDecode() {
        val result = MediaSafetyPolicy.evaluate(
            media(
                mime = "image/gif",
                size = 129L * 1024L * 1024L,
            ),
        )

        assertFalse(result.isAllowed)
        assertEquals(SafetyIssue.TOO_LARGE, result.issue)
    }

    @Test
    fun unsupportedMimeIsBlocked() {
        val result = MediaSafetyPolicy.evaluate(
            media(mime = "image/svg+xml"),
        )

        assertFalse(result.isAllowed)
        assertEquals(SafetyIssue.UNSUPPORTED_TYPE, result.issue)
    }

    @Test
    fun extremeDimensionsAreBlocked() {
        val result = MediaSafetyPolicy.evaluate(
            media(
                mime = "image/png",
                width = 20_000,
                height = 20_000,
            ),
        )

        assertFalse(result.isAllowed)
        assertEquals(SafetyIssue.SUSPICIOUS_DIMENSIONS, result.issue)
    }

    private fun media(
        mime: String,
        width: Int = 1920,
        height: Int = 1080,
        size: Long = 5L * 1024L * 1024L,
    ) = GalleryMedia(
        id = 1L,
        uri = "content://media/external/images/media/1",
        displayName = "sample",
        mimeType = mime,
        kind = MediaKind.IMAGE,
        sizeBytes = size,
        width = width,
        height = height,
        dateMillis = 0L,
    )
}
