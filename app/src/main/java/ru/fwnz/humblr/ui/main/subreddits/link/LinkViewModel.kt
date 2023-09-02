package ru.fwnz.humblr.ui.main.subreddits.link

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.fwnz.humblr.domain.CommentActionRepository
import ru.fwnz.humblr.domain.Comments
import ru.fwnz.humblr.domain.CommentsRepository
import javax.inject.Inject

private const val TAG = "LinkLog"
class LinkViewModel @Inject constructor(
    private val commentsRepository: CommentsRepository,
    private val commentActionRepository: CommentActionRepository
) : ViewModel() {
    private var _commentsByLink = MutableStateFlow<List<Comments>?>(null)
    val commentsByLink = _commentsByLink.asStateFlow()

    fun initCommentsList(subredditName: String, id: String) {
        viewModelScope.launch {
            _commentsByLink.value = commentsRepository.getCommentsWithLink(subredditName, id).also {
                Log.d(TAG, "initCommentsList: ${it.joinToString()} ")
            }
        }
    }

    fun likeComment(commentId: String): StateFlow<SubscribeStates> {
        val mutableSubscribeStatus = MutableStateFlow(SubscribeStates.IN_PROGRESS)
        val subscribeStatus = mutableSubscribeStatus.asStateFlow()
        viewModelScope.launch {
            performLikeComment(commentId, mutableSubscribeStatus)
        }
        return subscribeStatus
    }

    private suspend fun performLikeComment(
        commentId: String,
        _subscribeStatus: MutableStateFlow<SubscribeStates>
    ) {
        _subscribeStatus.value =
            if (commentActionRepository.setLiked(commentId)) SubscribeStates.SUCCESS else SubscribeStates.FAILURE
    }

    fun unVoteComment(commentId: String): StateFlow<SubscribeStates> {
        val mutableSubscribeStatus = MutableStateFlow(SubscribeStates.IN_PROGRESS)
        val subscribeStatus = mutableSubscribeStatus.asStateFlow()
        viewModelScope.launch {
            performUnVoteComment(commentId, mutableSubscribeStatus)
        }
        return subscribeStatus
    }

    private suspend fun performUnVoteComment(
        commentId: String,
        _subscribeStatus: MutableStateFlow<SubscribeStates>
    ) {
        _subscribeStatus.value =
            if (commentActionRepository.setUnVoted(commentId)) SubscribeStates.SUCCESS else SubscribeStates.FAILURE
    }

    fun dislikeComment(commentId: String): StateFlow<SubscribeStates> {
        val mutableSubscribeStatus = MutableStateFlow(SubscribeStates.IN_PROGRESS)
        val subscribeStatus = mutableSubscribeStatus.asStateFlow()
        viewModelScope.launch {
            performDislikeComment(commentId, mutableSubscribeStatus)
        }
        return subscribeStatus
    }

    private suspend fun performDislikeComment(
        commentId: String,
        _subscribeStatus: MutableStateFlow<SubscribeStates>
    ) {
        _subscribeStatus.value =
            if (commentActionRepository.setDisliked(commentId)) SubscribeStates.SUCCESS else SubscribeStates.FAILURE
    }

    fun saveComment(commentId: String): StateFlow<SubscribeStates> {
        val mutableSubscribeStatus = MutableStateFlow(SubscribeStates.IN_PROGRESS)
        val subscribeStatus = mutableSubscribeStatus.asStateFlow()
        viewModelScope.launch {
            performSaveComment(commentId, mutableSubscribeStatus)
        }
        return subscribeStatus
    }

    private suspend fun performSaveComment(
        commentId: String,
        _subscribeStatus: MutableStateFlow<SubscribeStates>
    ) {
        _subscribeStatus.value =
            if (commentActionRepository.saveComment(commentId)) SubscribeStates.SUCCESS else SubscribeStates.FAILURE
    }

    enum class SubscribeStates { IN_PROGRESS, SUCCESS, FAILURE }
}