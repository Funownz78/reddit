package ru.fwnz.humblr.domain

interface Me {
    val id: String
    val accountCreated: Long
    val name: String
    val avatarUrl: String
    val karma: Int
    val link: String
}