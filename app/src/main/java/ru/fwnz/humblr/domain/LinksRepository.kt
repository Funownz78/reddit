package ru.fwnz.humblr.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.fwnz.humblr.data.dto.linksList.LinksListDto

interface LinksRepository {
    suspend fun fetchLinks(name: String, after: String): LinksListDto
    fun getSubredditPager(name: String): Flow<PagingData<Link>>

}