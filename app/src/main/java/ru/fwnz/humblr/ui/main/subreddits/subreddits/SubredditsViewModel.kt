package ru.fwnz.humblr.ui.main.subreddits.subreddits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.fwnz.humblr.data.QueryOptions
import ru.fwnz.humblr.domain.Subreddit
import ru.fwnz.humblr.domain.SubredditQueryRepository
import ru.fwnz.humblr.domain.SubscribeRepository
import javax.inject.Inject

class SubredditsViewModel @Inject constructor(
    private val subredditQueryRepository: SubredditQueryRepository,
    private val subscribeRepository: SubscribeRepository,
) : ViewModel() {

    private val _subredditListState = MutableStateFlow(SubredditListMode.NEW)
    val subredditListState = _subredditListState.asStateFlow()

    var subreddits = fetchSubreddits(QueryOptions.NewQuery)

    private fun fetchSubreddits(queryOptions: QueryOptions): Flow<PagingData<Subreddit>> {
        return subredditQueryRepository.getSubredditPager(queryOptions).cachedIn(viewModelScope)
    }

    fun fetchSubredditsNew() {
        _subredditListState.value = SubredditListMode.NEW
        subreddits = fetchSubreddits(QueryOptions.NewQuery)
    }

    fun fetchSubredditsPopular() {
        _subredditListState.value = SubredditListMode.POPULAR
        subreddits = fetchSubreddits(QueryOptions.BestQuery)
    }

    fun fetchSubredditsSearch(q: String) {
        _subredditListState.value = SubredditListMode.SEARCH
        subreddits = fetchSubreddits(QueryOptions.SearchQuery(q))
    }

    fun subscribeSubreddit(name: String): StateFlow<SubscribeStates> {
        val mutableSubscribeStatus = MutableStateFlow(SubscribeStates.IN_PROGRESS)
        val subscribeStatus = mutableSubscribeStatus.asStateFlow()
        viewModelScope.launch {
            performSubscribeReddit(name, mutableSubscribeStatus)
        }
        return subscribeStatus
    }

    private suspend fun performSubscribeReddit(
        name: String,
        _subscribeStatus: MutableStateFlow<SubscribeStates>
    ) {
        _subscribeStatus.value =
            if (subscribeRepository.subscribeSubreddit(name)) SubscribeStates.SUCCESS else SubscribeStates.FAILURE
    }

    fun unsubscribeSubreddit(name: String): StateFlow<SubscribeStates> {
        val mutableSubscribeStatus = MutableStateFlow(SubscribeStates.IN_PROGRESS)
        val subscribeStatus = mutableSubscribeStatus.asStateFlow()
        viewModelScope.launch {
            performUnsubscribeReddit(name, mutableSubscribeStatus)
        }
        return subscribeStatus
    }

    private suspend fun performUnsubscribeReddit(
        name: String,
        _subscribeStatus: MutableStateFlow<SubscribeStates>
    ) {
        _subscribeStatus.value =
            if (subscribeRepository.unsubscribeSubreddit(name)) SubscribeStates.SUCCESS else SubscribeStates.FAILURE
    }


    enum class SubredditListMode { NEW, POPULAR, SEARCH }
    enum class SubscribeStates { IN_PROGRESS, SUCCESS, FAILURE }
}