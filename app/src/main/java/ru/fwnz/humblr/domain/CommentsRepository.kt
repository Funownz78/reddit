package ru.fwnz.humblr.domain

interface CommentsRepository {
    suspend fun getCommentsWithLink(subredditName: String, id: String): List<Comments>
}