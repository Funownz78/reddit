package ru.fwnz.humblr.domain

interface MeRepository {
    suspend fun getMe(): Me
}