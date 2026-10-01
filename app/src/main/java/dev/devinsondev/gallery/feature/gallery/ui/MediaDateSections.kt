package dev.devinsondev.gallery.feature.gallery.ui

import dev.devinsondev.gallery.feature.gallery.domain.GalleryMedia
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

internal data class MediaDateSection(
    val date: LocalDate,
    val items: List<GalleryMedia>,
)

internal fun splitMediaByDate(
    items: List<GalleryMedia>,
    zoneId: ZoneId = ZoneId.systemDefault(),
): List<MediaDateSection> {
    if (items.isEmpty()) return emptyList()

    return items
        .sortedByDescending(GalleryMedia::dateMillis)
        .groupBy { media ->
            Instant.ofEpochMilli(media.dateMillis)
                .atZone(zoneId)
                .toLocalDate()
        }
        .map { (date, media) ->
            MediaDateSection(
                date = date,
                items = media,
            )
        }
}
