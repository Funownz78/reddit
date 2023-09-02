package ru.fwnz.humblr.ui.main.profile.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.fwnz.humblr.domain.FavoriteRepository
import ru.fwnz.humblr.domain.Me
import ru.fwnz.humblr.domain.MeRepository
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val meRepository: MeRepository,
    private var favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _me = MutableStateFlow<Me?>(null)
    val me = _me.asStateFlow()

    private val _loadingState = MutableStateFlow(LoadingState.NotLoading)
    val loadingState = _loadingState.asStateFlow()

    init {
        viewModelScope.launch {
            _loadingState.value = LoadingState.Loading
            _me.value = meRepository.getMe()
            _loadingState.value = LoadingState.NotLoading
        }
    }

    fun unSaveComments() {
        viewModelScope.launch {
            _loadingState.value = LoadingState.Loading
            favoriteRepository.getSavedComments().forEach {
                favoriteRepository.unSaveComment(it.name)
            }
            _loadingState.value = LoadingState.NotLoading
        }

    }

    enum class LoadingState {
        Loading, NotLoading
    }
}