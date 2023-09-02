package ru.fwnz.humblr.ui.main.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.fwnz.humblr.domain.Comment
import ru.fwnz.humblr.domain.FavoriteRepository
import ru.fwnz.humblr.domain.Subreddit
import ru.fwnz.humblr.ui.main.ApiLoadState
import ru.fwnz.humblr.ui.main.subreddits.subreddits.SubredditsViewModel
import javax.inject.Inject

class FavoritesViewModel @Inject constructor(
    private val followedSubredditsRepository: FavoriteRepository
): ViewModel() {

    private val _favoritesListState = MutableStateFlow(FavoritesListMode.SUBREDDITS)
    val favoritesListState = _favoritesListState.asStateFlow()

    private val _subreddits = MutableStateFlow<List<Subreddit>?>(null)
    val subreddits = _subreddits.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>?>(null)
    val comments = _comments.asStateFlow()

    private val _loadingState = MutableStateFlow(ApiLoadState.SUCCESS)
    val loadState = _loadingState.asStateFlow()

    init {
        _loadingState.value = ApiLoadState.IN_PROGRESS
        getSubreddits()
        _loadingState.value = ApiLoadState.SUCCESS
    }

    fun getSubreddits() {
        viewModelScope.launch {
            _loadingState.value = ApiLoadState.IN_PROGRESS
            try {
                _subreddits.value = followedSubredditsRepository.getFollowedSubreddits()
                _loadingState.value = ApiLoadState.SUCCESS
                _favoritesListState.value = FavoritesListMode.SUBREDDITS
            } catch (e: Exception) {
                _loadingState.value = ApiLoadState.FAILURE
            }
        }
    }

    fun getComments() {
        viewModelScope.launch {
            _loadingState.value = ApiLoadState.IN_PROGRESS
            try {
                _comments.value = followedSubredditsRepository.getSavedComments()
                _loadingState.value = ApiLoadState.SUCCESS
                _favoritesListState.value = FavoritesListMode.COMMENTS
            } catch (e: Exception) {
                _loadingState.value = ApiLoadState.FAILURE
            }
        }
    }

    enum class FavoritesListMode { SUBREDDITS, COMMENTS }
}