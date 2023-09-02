package ru.fwnz.humblr.ui.main.profile.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.fwnz.humblr.domain.Friend
import ru.fwnz.humblr.domain.FriendsRepository
import ru.fwnz.humblr.ui.main.ApiLoadState
import javax.inject.Inject

class FriendsViewModel @Inject constructor(
    friendsRepository: FriendsRepository
) : ViewModel() {
    private val _friends = MutableStateFlow<List<Friend>?>(null)
    val friend = _friends.asStateFlow()

    private val _loadState = MutableStateFlow(ApiLoadState.IN_PROGRESS)
    val loadState = _loadState.asStateFlow()

    init {
        viewModelScope.launch {
            _loadState.value = ApiLoadState.IN_PROGRESS
            _friends.value = friendsRepository.getFriends()
            _loadState.value = ApiLoadState.SUCCESS
        }
    }
}