package dev.devinsondev.gallery.feature.viewer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import dev.devinsondev.gallery.R
import dev.devinsondev.gallery.core.media.MediaSafetyPolicy
import dev.devinsondev.gallery.core.media.SafeImageDecoder
import dev.devinsondev.gallery.feature.gallery.domain.GalleryMedia
import dev.devinsondev.gallery.feature.gallery.domain.MediaKind

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaViewerScreen(
    media: GalleryMedia,
    imageDecoder: SafeImageDecoder,
    onBack: () -> Unit,
) {
    val verdict = MediaSafetyPolicy.evaluate(media)

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = media.displayName,
                        maxLines = 1,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                ),
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Black),
        ) {
            when {
                !verdict.isAllowed -> BlockedMediaNotice(verdict.issue)
                media.kind == MediaKind.IMAGE -> SafeImageViewer(
                    media = media,
                    imageDecoder = imageDecoder,
                )
                media.kind == MediaKind.VIDEO -> VideoViewer(media)
            }
        }
    }
}
