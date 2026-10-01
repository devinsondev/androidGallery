package dev.devinsondev.gallery.core.media

import android.net.Uri
import android.provider.MediaStore

object MediaUriGuard {
    fun parseTrusted(mediaStoreUri: String): Uri? {
        val uri = runCatching { Uri.parse(mediaStoreUri) }.getOrNull() ?: return null
        val isTrusted = uri.scheme == "content" && uri.authority == MediaStore.AUTHORITY
        return uri.takeIf { isTrusted }
    }
}
