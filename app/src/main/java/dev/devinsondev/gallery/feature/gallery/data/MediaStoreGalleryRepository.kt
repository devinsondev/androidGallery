package dev.devinsondev.gallery.feature.gallery.data

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import dev.devinsondev.gallery.feature.gallery.domain.GalleryMedia
import dev.devinsondev.gallery.feature.gallery.domain.GalleryRepository
import dev.devinsondev.gallery.feature.gallery.domain.MediaKind
import dev.devinsondev.gallery.feature.gallery.domain.MediaReadAccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaStoreGalleryRepository(
    context: Context,
) : GalleryRepository {
    private val resolver = context.contentResolver
    private val appContext = context.applicationContext

    override suspend fun loadMedia(access: MediaReadAccess): List<GalleryMedia> {
        return withContext(Dispatchers.IO) {
            buildList {
                for (volume in MediaStore.getExternalVolumeNames(appContext)) {
                    if (access.canReadImages) addAll(queryImages(volume))
                    if (access.canReadVideos) addAll(queryVideos(volume))
                }
            }.sortedByDescending(GalleryMedia::dateMillis)
        }
    }

    private fun queryImages(volume: String): List<GalleryMedia> {
        val collection = MediaStore.Images.Media.getContentUri(volume)
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATE_ADDED,
        )
        return querySafely(collection, projection) { cursor ->
            cursor.toMedia(
                collection = collection,
                kind = MediaKind.IMAGE,
                durationColumn = null,
            )
        }
    }

    private fun queryVideos(volume: String): List<GalleryMedia> {
        val collection = MediaStore.Video.Media.getContentUri(volume)
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT,
            MediaStore.Video.Media.DATE_TAKEN,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DURATION,
        )
        return querySafely(collection, projection) { cursor ->
            cursor.toMedia(
                collection = collection,
                kind = MediaKind.VIDEO,
                durationColumn = MediaStore.Video.Media.DURATION,
            )
        }
    }

    private fun querySafely(
        collection: Uri,
        projection: Array<String>,
        mapper: (Cursor) -> GalleryMedia,
    ): List<GalleryMedia> {
        return try {
            resolver.query(
                collection,
                projection,
                null,
                null,
                "${MediaStore.MediaColumns.DATE_ADDED} DESC",
            )?.use { cursor ->
                buildList {
                    while (cursor.moveToNext()) add(mapper(cursor))
                }
            }.orEmpty()
        } catch (_: SecurityException) {
            emptyList()
        }
    }

    private fun Cursor.toMedia(
        collection: Uri,
        kind: MediaKind,
        durationColumn: String?,
    ): GalleryMedia {
        val id = long(MediaStore.MediaColumns._ID)
        val dateTaken = long(MediaStore.MediaColumns.DATE_TAKEN)
        val dateAddedMillis = long(MediaStore.MediaColumns.DATE_ADDED) * 1000L

        return GalleryMedia(
            id = id,
            uri = ContentUris.withAppendedId(collection, id).toString(),
            displayName = string(MediaStore.MediaColumns.DISPLAY_NAME)
                .orEmpty()
                .ifBlank { "media-$id" },
            mimeType = string(MediaStore.MediaColumns.MIME_TYPE),
            kind = kind,
            sizeBytes = long(MediaStore.MediaColumns.SIZE),
            width = int(MediaStore.MediaColumns.WIDTH),
            height = int(MediaStore.MediaColumns.HEIGHT),
            dateMillis = dateTaken.takeIf { it > 0 } ?: dateAddedMillis,
            durationMillis = durationColumn?.let { long(it) },
        )
    }

    private fun Cursor.long(column: String): Long {
        val index = getColumnIndexOrThrow(column)
        return if (isNull(index)) 0L else getLong(index)
    }

    private fun Cursor.int(column: String): Int {
        val index = getColumnIndexOrThrow(column)
        return if (isNull(index)) 0 else getInt(index)
    }

    private fun Cursor.string(column: String): String? {
        val index = getColumnIndexOrThrow(column)
        return if (isNull(index)) null else getString(index)
    }
}
