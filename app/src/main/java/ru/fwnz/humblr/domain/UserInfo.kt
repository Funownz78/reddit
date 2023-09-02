package ru.fwnz.humblr.domain

interface UserInfo {
    val name: String
    val link: String
    val iconImg: String
    var userIsSubscriber: Boolean
    val subredditDisplayName: String
}