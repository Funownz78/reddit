package ru.fwnz.humblr.domain

interface SubredditInfo {
    val description: String
    val displayName: String    // subscribeSubreddit(sr_name = display_name
    val displayNamePrefixed: String
    var userIsSubscriber: Boolean
    val url: String     //"/r/Home/"   // "https://www.reddit.com$url"
}