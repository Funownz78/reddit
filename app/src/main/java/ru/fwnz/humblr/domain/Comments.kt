package ru.fwnz.humblr.domain

data class Comments (
    val id: String,
    val level: Int = 0,
    val author: String,
    val title: String,
    val body: String,
    val createdUtc: Long,
    var score: Int,
    var likes: Boolean? = null,
    var saved: Boolean
)
