package ru.fwnz.humblr.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.fwnz.humblr.data.QueryOptions
import ru.fwnz.humblr.data.dto.subredditsList.SubredditsListDto

interface SubredditQueryRepository {
    suspend fun executeSubredditQuery(queryOptions: QueryOptions, after: String): SubredditsListDto
    fun getSubredditPager(queryOptions: QueryOptions): Flow<PagingData<Subreddit>>
}