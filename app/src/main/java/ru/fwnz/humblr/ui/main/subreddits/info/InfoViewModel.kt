package ru.fwnz.humblr.ui.main.subreddits.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.fwnz.humblr.domain.SubredditInfo
import ru.fwnz.humblr.domain.SubredditInfoRepository
import ru.fwnz.humblr.domain.SubscribeRepository
import ru.fwnz.humblr.ui.main.ApiLoadState
import javax.inject.Inject

class InfoViewModel @Inject constructor(
    private val subredditInfoRepository: SubredditInfoRepository,
    private val subscribeRepository: SubscribeRepository
) : ViewModel() {
    private val _subredditInfo = MutableStateFlow<SubredditInfo?>(null)
    val subredditInfo = _subredditInfo.asStateFlow()

    private val _loadingState = MutableStateFlow(ApiLoadState.IN_PROGRESS)
    val loadingState = _loadingState.asStateFlow()

    fun initSubredditInfo(name: String) {
        viewModelScope.launch {
            _loadingState.value = ApiLoadState.IN_PROGRESS
            _subredditInfo.value = subredditInfoRepository.getSubredditInfo(name)
            _loadingState.value = ApiLoadState.SUCCESS
        }
    }

    fun subscribeSubreddit(name: String): StateFlow<ApiLoadState> {
        val mutableSubscribeStatus = MutableStateFlow(ApiLoadState.IN_PROGRESS)
        val subscribeStatus = mutableSubscribeStatus.asStateFlow()
        viewModelScope.launch {
            performSubscribeReddit(name, mutableSubscribeStatus)
        }
        return subscribeStatus
    }

    private suspend fun performSubscribeReddit(
        name: String,
        _subscribeStatus: MutableStateFlow<ApiLoadState>
    ) {
        _subscribeStatus.value =
            if (subscribeRepository.subscribeSubreddit(name)) ApiLoadState.SUCCESS
            else ApiLoadState.FAILURE
    }

    fun unSubscribeSubreddit(name: String): StateFlow<ApiLoadState> {
        val mutableSubscribeStatus = MutableStateFlow(ApiLoadState.IN_PROGRESS)
        val subscribeStatus = mutableSubscribeStatus.asStateFlow()
        viewModelScope.launch {
            performUnSubscribeReddit(name, mutableSubscribeStatus)
        }
        return subscribeStatus
    }

    private suspend fun performUnSubscribeReddit(
        name: String,
        _subscribeStatus: MutableStateFlow<ApiLoadState>
    ) {
        _subscribeStatus.value =
            if (subscribeRepository.unsubscribeSubreddit(name)) ApiLoadState.SUCCESS
            else ApiLoadState.FAILURE
    }

}