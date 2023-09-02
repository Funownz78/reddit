package ru.fwnz.humblr.data

sealed class QueryOptions {
    data class SearchQuery(val q: String): QueryOptions()
    object NewQuery: QueryOptions()
    object BestQuery : QueryOptions()
}
