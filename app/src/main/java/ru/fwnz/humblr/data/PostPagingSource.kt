package ru.fwnz.humblr.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.fwnz.humblr.domain.Post
import ru.fwnz.humblr.domain.SubredditQueryRepository

class PostPagingSource(
    private val queryOptions: QueryOptions,
    private val queryRepository: SubredditQueryRepository
) : PagingSource<String, Post> (){
    override fun getRefreshKey(state: PagingState<String, Post>): String? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Post> {
        TODO("Not yet implemented")
    }
}