package ru.fwnz.humblr.domain

interface CommentActionRepository {
    suspend fun setLiked(commentId: String): Boolean
    suspend fun setUnVoted(commentId: String): Boolean
    suspend fun setDisliked(commentId: String): Boolean
    suspend fun saveComment(commentId: String): Boolean
}