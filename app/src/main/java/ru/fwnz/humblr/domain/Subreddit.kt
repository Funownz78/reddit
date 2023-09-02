package ru.fwnz.humblr.domain

interface Subreddit {
    val name: String
    val displayName: String
    val publicDescription: String
    val description: String
    var userIsSubscriber: Boolean
}