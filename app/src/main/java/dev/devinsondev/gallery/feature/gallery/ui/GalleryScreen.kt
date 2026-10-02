package dev.devinsondev.gallery.feature.gallery.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.devinsondev.gallery.R
import dev.devinsondev.gallery.core.media.ThumbnailLoader
import dev.devinsondev.gallery.feature.gallery.domain.GalleryMedia

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    state: GalleryUiState,
    gridState: LazyGridState,
    thumbnailLoader: ThumbnailLoader,
    onRequestMediaAccess: () -> Unit,
    onRefresh: () -> Unit,
    onOpen: (GalleryMedia) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.gallery_title)) },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = stringResource(R.string.refresh),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        if (!state.access.hasAnyAccess) {
            AccessRequired(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                onRequestMediaAccess = onRequestMediaAccess,
            )
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (state.access.isLimited) {
                LimitedAccessBanner(
                    modifier = Modifier.fillMaxWidth(),
                    onRequestMediaAccess = onRequestMediaAccess,
                )
            }

            when (val content = state.content) {
                GalleryContent.Loading -> LoadingContent()
                GalleryContent.Error -> ErrorContent(onRefresh)
                is GalleryContent.Ready -> {
                    if (content.items.isEmpty()) {
                        EmptyContent()
                    } else {
                        MediaGrid(
                            items = content.items,
                            gridState = gridState,
                            thumbnailLoader = thumbnailLoader,
                            onOpen = onOpen,
                            contentPadding = PaddingValues(bottom = 16.dp),
                        )
                    }
                }
            }
        }
    }
}
