package dev.devinsondev.gallery.feature.gallery.ui

import dev.devinsondev.gallery.feature.gallery.domain.GalleryMedia
import dev.devinsondev.gallery.feature.gallery.domain.MediaKind
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneOffset

class MediaDateSectionsTest {
    @Test
    fun separatesMediaByDateInDescendingOrder() {
        val sections = splitMediaByDate(
            items = listOf(
                media(id = 1L, epochMillis = 1_759_284_000_000L),
                media(id = 3L, epochMillis = 1_759_190_400_000L),
                media(id = 2L, epochMillis = 1_759_280_400_000L),
            ),
            zoneId = ZoneOffset.UTC,
        )

        assertEquals(2, sections.size)
        assertEquals(LocalDate.of(2025, 10, 1), sections[0].date)
        assertEquals(listOf(1L, 2L), sections[0].items.map(GalleryMedia::id))
        assertEquals(LocalDate.of(2025, 9, 30), sections[1].date)
        assertEquals(listOf(3L), sections[1].items.map(GalleryMedia::id))
    }

    private fun media(
        id: Long,
        epochMillis: Long,
    ) = GalleryMedia(
        id = id,
        uri = "content://media/external/images/media/$id",
        displayName = "media-$id",
        mimeType = "image/jpeg",
        kind = MediaKind.IMAGE,
        sizeBytes = 1024L,
        width = 1920,
        height = 1080,
        dateMillis = epochMillis,
    )
}
