package dev.devinsondev.gallery.feature.gallery.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.devinsondev.gallery.core.permission.MediaPermissionGateway
import dev.devinsondev.gallery.feature.gallery.domain.GalleryMedia
import dev.devinsondev.gallery.feature.gallery.domain.GalleryRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GalleryViewModel(
    private val repository: GalleryRepository,
    private val permissionGateway: MediaPermissionGateway,
) : ViewModel() {
    private val _state = MutableStateFlow(GalleryUiState())
    val state: StateFlow<GalleryUiState> = _state.asStateFlow()

    private var loadJob: Job? = null

    fun refresh() {
        val access = permissionGateway.currentAccess()
        loadJob?.cancel()

        if (!access.hasAnyAccess) {
            _state.update {
                it.copy(
                    access = access,
                    content = GalleryContent.Ready(emptyList()),
                    selectedMedia = null,
                )
            }
            return
        }

        _state.update {
            it.copy(
                access = access,
                content = GalleryContent.Loading,
            )
        }

        loadJob = viewModelScope.launch {
            runCatching { repository.loadMedia(access) }
                .onSuccess { media ->
                    _state.update {
                        it.copy(
                            access = access,
                            content = GalleryContent.Ready(media),
                        )
                    }
                }
                .onFailure {
                    _state.update {
                        it.copy(
                            access = access,
                            content = GalleryContent.Error,
                        )
                    }
                }
        }
    }

    fun open(media: GalleryMedia) {
        _state.update { it.copy(selectedMedia = media) }
    }

    fun closeViewer() {
        _state.update { it.copy(selectedMedia = null) }
    }

    class Factory(
        private val repository: GalleryRepository,
        private val permissionGateway: MediaPermissionGateway,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(GalleryViewModel::class.java))
            return GalleryViewModel(repository, permissionGateway) as T
        }
    }
}
