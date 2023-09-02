package ru.fwnz.humblr.domain

interface Comment {
    val body: String
    val likes: Boolean?
    val name: String
}