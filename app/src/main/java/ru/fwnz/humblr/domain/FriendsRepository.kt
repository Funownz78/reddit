package ru.fwnz.humblr.domain

interface FriendsRepository {
    suspend fun getFriends(): List<Friend>
}