package ru.fwnz.humblr.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.fwnz.humblr.data.dto.commentsByUser.CommentsByUserDto
import ru.fwnz.humblr.data.dto.userabout.UserAboutDto

interface UserRepository {
    suspend fun fetchUserComments(name: String, after: String): CommentsByUserDto
    fun getUserCommentsPager(name: String): Flow<PagingData<Comment>>

    suspend fun getUserInfo(name: String): UserInfo
}