package ru.fwnz.humblr.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import okio.IOException
import retrofit2.HttpException
import ru.fwnz.humblr.domain.*

class CommentPagingSource(
    private val name: String,
    private val userRepository: UserRepository
) : PagingSource<String, Comment>() {
    private companion object {
        const val FIRST_RECORD = ""
    }

    override fun getRefreshKey(state: PagingState<String, Comment>): String = FIRST_RECORD

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Comment> {
        return try {
            val after = params.key ?: FIRST_RECORD
            val response = userRepository.fetchUserComments(name, after)
            val linksList: List<Comment> = response.data.children.map { it.data }
            LoadResult.Page(
                linksList,
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