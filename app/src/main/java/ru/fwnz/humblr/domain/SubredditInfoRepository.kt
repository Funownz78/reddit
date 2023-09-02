package ru.fwnz.humblr.domain

interface SubredditInfoRepository {
    suspend fun getSubredditInfo(subredditName: String): SubredditInfo
}