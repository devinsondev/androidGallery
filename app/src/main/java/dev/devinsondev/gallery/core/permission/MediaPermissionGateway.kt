package dev.devinsondev.gallery.core.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dev.devinsondev.gallery.feature.gallery.domain.MediaReadAccess

class MediaPermissionGateway(
    private val context: Context,
) {
    fun currentAccess(): MediaReadAccess {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE ->
                accessFromSelectedMediaAwarePermissions()
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                accessFromGranularMediaPermissions()
            else -> accessFromLegacyStoragePermission()
        }
    }

    fun permissionsToRequest(): Array<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
            )
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
            )
            else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun accessFromSelectedMediaAwarePermissions(): MediaReadAccess {
        val fullImages = isGranted(Manifest.permission.READ_MEDIA_IMAGES)
        val fullVideos = isGranted(Manifest.permission.READ_MEDIA_VIDEO)
        val selected = isGranted(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)

        return MediaReadAccess(
            canReadImages = fullImages || selected,
            canReadVideos = fullVideos || selected,
            isLimited = selected && !(fullImages && fullVideos),
        )
    }

    private fun accessFromGranularMediaPermissions(): MediaReadAccess {
        val images = isGranted(Manifest.permission.READ_MEDIA_IMAGES)
        val videos = isGranted(Manifest.permission.READ_MEDIA_VIDEO)
        return MediaReadAccess(
            canReadImages = images,
            canReadVideos = videos,
            isLimited = (images || videos) && !(images && videos),
        )
    }

    private fun accessFromLegacyStoragePermission(): MediaReadAccess {
        val granted = isGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
        return MediaReadAccess(
            canReadImages = granted,
            canReadVideos = granted,
            isLimited = false,
        )
    }

    private fun isGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission,
        ) == PackageManager.PERMISSION_GRANTED
    }
}
