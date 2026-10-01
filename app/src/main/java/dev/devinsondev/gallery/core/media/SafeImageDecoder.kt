package dev.devinsondev.gallery.core.media

import android.content.ContentResolver
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import dev.devinsondev.gallery.feature.gallery.domain.GalleryMedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.min
import kotlin.math.roundToInt

class SafeImageDecoder(
    private val contentResolver: ContentResolver,
) {
    suspend fun decode(
        media: GalleryMedia,
        maxWidthPx: Int,
        maxHeightPx: Int,
    ): Result<Drawable> = withContext(Dispatchers.IO) {
        runCatching {
            check(MediaSafetyPolicy.evaluate(media).isAllowed)
            val uri = requireNotNull(MediaUriGuard.parseTrusted(media.uri))
            val source = ImageDecoder.createSource(contentResolver, uri)

            ImageDecoder.decodeDrawable(source) { decoder, info, _ ->
                val width = info.size.width
                val height = info.size.height
                check(MediaSafetyPolicy.dimensionsAreSafe(width, height))

                val target = fitInside(
                    sourceWidth = width,
                    sourceHeight = height,
                    maxWidth = maxWidthPx,
                    maxHeight = maxHeightPx,
                )
                decoder.setTargetSize(target.first, target.second)
                decoder.memorySizePolicy = ImageDecoder.MEMORY_POLICY_LOW_RAM
            }
        }
    }

    private fun fitInside(
        sourceWidth: Int,
        sourceHeight: Int,
        maxWidth: Int,
        maxHeight: Int,
    ): Pair<Int, Int> {
        val safeMaxWidth = maxWidth.coerceAtLeast(1)
        val safeMaxHeight = maxHeight.coerceAtLeast(1)
        val scale = min(
            safeMaxWidth.toFloat() / sourceWidth,
            safeMaxHeight.toFloat() / sourceHeight,
        ).coerceAtMost(1f)

        return Pair(
            (sourceWidth * scale).roundToInt().coerceAtLeast(1),
            (sourceHeight * scale).roundToInt().coerceAtLeast(1),
        )
    }
}
