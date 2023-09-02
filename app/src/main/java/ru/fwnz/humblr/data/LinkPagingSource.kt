package ru.fwnz.humblr.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import okio.IOException
import retrofit2.HttpException
import ru.fwnz.humblr.domain.Link
import ru.fwnz.humblr.domain.LinksRepository

class LinkPagingSource(
    private val name: String,
    private val linksRepository: LinksRepository
) : PagingSource<String, Link>() {
    private companion object {
        const val FIRST_RECORD = ""
    }

    override fun getRefreshKey(state: PagingState<String, Link>): String = FIRST_RECORD

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Link> {
        return try {
            val after = params.key ?: FIRST_RECORD
            val response = linksRepository.fetchLinks(name, after)
            val linksList: List<Link> = response.data.children.map { it.data }
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