package dev.devinsondev.gallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import dev.devinsondev.gallery.core.media.SafeImageDecoder
import dev.devinsondev.gallery.core.media.ThumbnailLoader
import dev.devinsondev.gallery.core.permission.MediaPermissionGateway
import dev.devinsondev.gallery.feature.gallery.data.MediaStoreGalleryRepository
import dev.devinsondev.gallery.feature.gallery.ui.GalleryRoute
import dev.devinsondev.gallery.feature.gallery.ui.GalleryViewModel
import dev.devinsondev.gallery.ui.theme.GalleryTheme

class MainActivity : ComponentActivity() {
    private val permissionGateway by lazy { MediaPermissionGateway(this) }
    private val repository by lazy { MediaStoreGalleryRepository(applicationContext) }
    private val thumbnailLoader by lazy { ThumbnailLoader(contentResolver) }
    private val imageDecoder by lazy { SafeImageDecoder(contentResolver) }

    private val viewModel by viewModels<GalleryViewModel> {
        GalleryViewModel.Factory(repository, permissionGateway)
    }

    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) {
        viewModel.refresh()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GalleryTheme {
                GalleryRoute(
                    viewModel = viewModel,
                    thumbnailLoader = thumbnailLoader,
                    imageDecoder = imageDecoder,
                    onRequestMediaAccess = ::requestMediaAccess,
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun requestMediaAccess() {
        requestPermissions.launch(permissionGateway.permissionsToRequest())
    }
}
