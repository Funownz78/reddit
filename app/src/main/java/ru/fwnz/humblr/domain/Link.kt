package ru.fwnz.humblr.domain

interface Link {
    val name: String
    val id: String
    val title: String
    val selftext: String
    val author: String
    val authorFullname: String
    val ups: Int
    val url: String
    val likes: Boolean?
    val numComments: Int
    val createdUtcAt: Long
}