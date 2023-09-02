package ru.fwnz.humblr.domain

interface Friend {
    val displayNamePrefixed: String
    val subredditType: String
    val iconImg: String
    val createdUtcAt: Long
}