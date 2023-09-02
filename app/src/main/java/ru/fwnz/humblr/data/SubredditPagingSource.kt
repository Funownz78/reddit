package ru.fwnz.humblr.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import okio.IOException
import retrofit2.HttpException
import ru.fwnz.humblr.domain.Subreddit
import ru.fwnz.humblr.domain.SubredditQueryRepository

class SubredditPagingSource(
    private val queryOptions: QueryOptions,
    private val queryRepository: SubredditQueryRepository
) : PagingSource<String, Subreddit>() {
    private companion object {
        const val FIRST_RECORD = ""
    }

    override fun getRefreshKey(state: PagingState<String, Subreddit>): String = FIRST_RECORD

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Subreddit> {
        return try {
            val after = params.key ?: FIRST_RECORD
            val response = queryRepository.executeSubredditQuery(queryOptions, after)
            val subredditList: List<Subreddit> = response.data.children.map { it.data }
            LoadResult.Page(
                subredditList,
                response.data.before,
                response.data.after
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}