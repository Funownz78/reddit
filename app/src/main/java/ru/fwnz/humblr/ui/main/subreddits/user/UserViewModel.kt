package ru.fwnz.humblr.ui.main.subreddits.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.fwnz.humblr.domain.Comment
import ru.fwnz.humblr.domain.SubscribeRepository
import ru.fwnz.humblr.domain.UserInfo
import ru.fwnz.humblr.domain.UserRepository
import ru.fwnz.humblr.ui.main.ApiLoadState
import ru.fwnz.humblr.ui.main.subreddits.subreddits.SubredditsViewModel
import javax.inject.Inject

private const val TAG = "UserViewModel"
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val subscribeRepository: SubscribeRepository
) : ViewModel() {
    var userCommentsPagingData: Flow<PagingData<Comment>>? = null
    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo = _userInfo.asStateFlow()

    private val _loadState = MutableStateFlow(ApiLoadState.IN_PROGRESS)
    val loadState = _loadState.asStateFlow()

    fun initUserCommentsPagingData(name: String) {
        viewModelScope.launch {
            _loadState.value = ApiLoadState.IN_PROGRESS
            _userInfo.value = userRepository.getUserInfo(name)
            _loadState.value = ApiLoadState.SUCCESS
        }
        userCommentsPagingData = userRepository.getUserCommentsPager(name)
    }

    fun subscribeSubreddit(name: String): StateFlow<SubredditsViewModel.SubscribeStates> {
        val mutableSubscribeStatus = MutableStateFlow(SubredditsViewModel.SubscribeStates.IN_PROGRESS)
        val subscribeStatus = mutableSubscribeStatus.asStateFlow()
        viewModelScope.launch {
            performSubscribeReddit(name, mutableSubscribeStatus)
        }
        return subscribeStatus
    }

    private suspend fun performSubscribeReddit(
        name: String,
        _subscribeStatus: MutableStateFlow<SubredditsViewModel.SubscribeStates>
    ) {
        val t = subscribeRepository.subscribeSubreddit(name)
        Log.d(TAG, "performSubscribeReddit subscribeRepository.subscribeSubreddit(name): $t")

        _subscribeStatus.value =
            if (t) SubredditsViewModel.SubscribeStates.SUCCESS else SubredditsViewModel.SubscribeStates.FAILURE
//        _subscribeStatus.value =
//            if (subscribeRepository.subscribeSubreddit(name)) SubredditsViewModel.SubscribeStates.SUCCESS else SubredditsViewModel.SubscribeStates.FAILURE
    }
}