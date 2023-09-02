package ru.fwnz.humblr.domain

interface FavoriteRepository {
    suspend fun getFollowedSubreddits(): List<Subreddit>
    suspend fun getSavedComments(): List<Comment>
    suspend fun unSaveComment(name: String)
}