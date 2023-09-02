package ru.fwnz.humblr.domain

interface SubscribeRepository {
    suspend fun subscribeSubreddit(name: String): Boolean
    suspend fun unsubscribeSubreddit(name: String): Boolean
}