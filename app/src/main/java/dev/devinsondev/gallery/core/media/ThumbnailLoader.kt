package dev.devinsondev.gallery.core.media

import android.content.ContentResolver
import android.graphics.Bitmap
import android.util.LruCache
import android.util.Size
import dev.devinsondev.gallery.feature.gallery.domain.GalleryMedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ThumbnailLoader(
    private val contentResolver: ContentResolver,
) {
    private val cache = object : LruCache<String, Bitmap>(CACHE_SIZE_MIB) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            return (value.byteCount / BYTES_PER_MIB).coerceAtLeast(1)
        }
    }

    suspend fun load(media: GalleryMedia, sizePx: Int): Bitmap? {
        if (!MediaSafetyPolicy.evaluate(media).isAllowed) return null
        val uri = MediaUriGuard.parseTrusted(media.uri) ?: return null
        val cacheKey = "${media.uri}:$sizePx"
        cache.get(cacheKey)?.let { return it }

        return withContext(Dispatchers.IO) {
            runCatching {
                contentResolver.loadThumbnail(
                    uri,
                    Size(sizePx, sizePx),
                    null,
                )
            }.getOrNull()?.also { cache.put(cacheKey, it) }
        }
    }

    private companion object {
        const val CACHE_SIZE_MIB = 32
        const val BYTES_PER_MIB = 1024 * 1024
    }
}
