package ru.fwnz.humblr.data

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.fwnz.humblr.data.dto.commentsByLink.Children
import ru.fwnz.humblr.data.dto.commentsByUser.CommentsByUserDto
import ru.fwnz.humblr.data.dto.linksList.LinksListDto
import ru.fwnz.humblr.data.dto.subredditsList.SubredditsListDto
import ru.fwnz.humblr.domain.*
import javax.inject.Inject

class RedditRepository @Inject constructor(
    private val redditApi: RedditApi
) :
    MeRepository,
    SubredditQueryRepository,
    SubscribeRepository,
    LinksRepository,
    UserRepository,
    CommentsRepository,
    CommentActionRepository,
    FriendsRepository,
    SubredditInfoRepository,
    FavoriteRepository
{
    override suspend fun unSaveComment(name: String) {
        redditApi.unSaveComment(name)
    }

    override suspend fun getSavedComments(): List<Comment> {
        val me = getMe()
        return redditApi.getSavedComments(me.name).data.children.map { it.data }
    }

    override suspend fun getFollowedSubreddits(): List<Subreddit> =
            redditApi.getSubscribed()
            .data.children
            .map { it.data }
            .filter { it.subredditType != "user" }

    override suspend fun getSubredditInfo(subredditName: String): SubredditInfo =
        redditApi.getSubredditInfo(subredditName).data

    override suspend fun getFriends(): List<Friend> =
        redditApi.getSubscribed()
            .data.children
            .map { it.data }
            .filter { it.subredditType == "user" }

    override suspend fun setLiked(commentId: String): Boolean =
        redditApi.setCommentLiked(commentId).isSuccessful

    override suspend fun setUnVoted(commentId: String): Boolean  =
        redditApi.setCommentUnVoted(commentId).isSuccessful

    override suspend fun setDisliked(commentId: String): Boolean =
        redditApi.setCommentDisliked(commentId).isSuccessful

    override suspend fun saveComment(commentId: String): Boolean =
        redditApi.saveComment(commentId).isSuccessful

    data class LastCommentsResponse(
        val subredditName: String,
        val id: String,
        val comments: List<Comments>
    )

    private var lastCommentsResponse: LastCommentsResponse? = null

    override suspend fun getCommentsWithLink(subredditName: String, id: String): List<Comments> {
        return if (lastCommentsResponse?.id == id && lastCommentsResponse?.subredditName == subredditName) {
            lastCommentsResponse!!.comments
        } else {
            val commentsByLinkDto = redditApi.getLinkComments(subredditName, id)
            val data = commentsByLinkDto[0].data.children[0].data as Map<*, *>
            Log.d("TAG", "getCommentsWithLink: $data")
            val linkItem = mutableListOf(Comments(
                id = data.getOrDefault("id", "") as? String ?: "" ,
                level = -1,
                author = data.getOrDefault("author", "") as? String ?: "",
                title = data.getOrDefault("title", "") as? String ?: "",
                body = data.getOrDefault("selftext", "") as? String ?: "",
                createdUtc = (data.getOrDefault("created_utc", 0.0) as? Double ?: 0.0).toLong(),
                score = (data.getOrDefault("score", 0.0) as? Double ?: 0.0).toInt(),
                likes = data["likes"] as? Boolean,
                saved = data["saved"] as? Boolean ?: false,
            ))
            commentsByLinkDto[1].data.children.forEach {
                collectCommentsHierarchy(linkItem, it)
            }
            lastCommentsResponse = LastCommentsResponse(subredditName, id, linkItem)
            linkItem
        }
    }

    private fun collectCommentsHierarchy(
        commentsList: MutableList<Comments>,
        source: Children,
    ) {
        val level = 0
        val data = source.data as? Map<*,*>
        data?.let {
            val currentComment = Comments(
                id = data.getOrDefault("id", "") as? String ?: "" ,
                level = level,
                author = data.getOrDefault("author", "") as? String ?: "",
                title = data.getOrDefault("title", "") as? String ?: "",
                body = data.getOrDefault("body", "") as? String ?: "",
                createdUtc = (data.getOrDefault("created_utc", 0.0) as? Double ?: 0.0).toLong(),
                score = (data.getOrDefault("score", 0.0) as? Double ?: 0.0).toInt(),
                likes = data["likes"] as? Boolean,
                saved = data["saved"] as? Boolean ?: false,
            )
            commentsList.add(currentComment)
            val replies = data.getOrDefault("replies", "") as? Map<*,*>
            val repliesData = replies?.getOrDefault("data", null) as? Map<*,*>
            val repliesDataChildren = repliesData?.getOrDefault("children", null) as? List<*>
            if (repliesDataChildren?.isNotEmpty() == true) {
                repliesDataChildren.forEach {
                    collectCommentsHierarchyForAny(commentsList, it, level + 1)
                }
            }
        }
    }

    private fun collectCommentsHierarchyForAny(
        commentsList: MutableList<Comments>,
        source: Any?,
        level: Int
    ) {
        val children = source as? Map<*,*>
        val data = children?.getOrDefault("data", null) as? Map<*,*>
        data?.let {
            val currentComment = Comments(
                id = data.getOrDefault("id", "") as? String ?: "" ,
                level = level,
                author = data.getOrDefault("author", "") as? String ?: "",
                title = data.getOrDefault("title", "") as? String ?: "",
                body = data.getOrDefault("body", "") as? String ?: "",
                createdUtc = (data.getOrDefault("created_utc", 0.0) as? Double ?: 0.0).toLong(),
                score = (data.getOrDefault("score", 0.0) as? Double ?: 0.0).toInt(),
                likes = data["likes"] as? Boolean,
                saved = data["saved"] as? Boolean ?: false,
            )
            commentsList.add(currentComment)
            val replies = data.getOrDefault("replies", "") as? Map<*,*>
            val repliesData = replies?.getOrDefault("data", null) as? Map<*,*>
            val repliesDataChildren = repliesData?.getOrDefault("children", null) as? List<*>
            if (repliesDataChildren?.isNotEmpty() == true) {
                repliesDataChildren.forEach {
                    collectCommentsHierarchyForAny(commentsList, it, level + 1)
                }
            }
        }
    }



    override suspend fun getMe(): Me = redditApi.me()

    override suspend fun executeSubredditQuery(
        queryOptions: QueryOptions,
        after: String
    ): SubredditsListDto = when (queryOptions) {
        is QueryOptions.NewQuery -> redditApi.getNewSubreddit(after)
        is QueryOptions.BestQuery -> redditApi.getBestSubreddit(after)
        is QueryOptions.SearchQuery -> redditApi.getSearchedSubreddit(queryOptions.q, after)
    }

    override fun getSubredditPager(queryOptions: QueryOptions): Flow<PagingData<Subreddit>> {
        return Pager(
            PagingConfig(pageSize = RedditApi.PAGE_SIZE, enablePlaceholders = false)
        ) {
            SubredditPagingSource(queryOptions = queryOptions, queryRepository = this)
        }.flow
    }

    override suspend fun fetchLinks(name: String, after: String): LinksListDto {
        return redditApi.getLinksForSubreddit(name, after)
    }

    override fun getSubredditPager(name: String): Flow<PagingData<Link>> {
        return Pager(
            PagingConfig(pageSize = RedditApi.PAGE_SIZE, enablePlaceholders = false)
        ) {
            LinkPagingSource(name, this)
        }.flow
    }

    override suspend fun subscribeSubreddit(name: String): Boolean =
        redditApi.subscribeSubreddit(name).isSuccessful

    override suspend fun unsubscribeSubreddit(name: String): Boolean =
        redditApi.unsubscribeSubreddit(name).isSuccessful

    override suspend fun fetchUserComments(name: String, after: String): CommentsByUserDto {
        return redditApi.getUserComments(name, after)
    }

    override fun getUserCommentsPager(name: String): Flow<PagingData<Comment>> {
        return Pager(
            PagingConfig(RedditApi.PAGE_SIZE, enablePlaceholders = false)
        ) {
            CommentPagingSource(name, this)
        }.flow
    }

    override suspend fun getUserInfo(name: String): UserInfo =
        redditApi.getUserAbout(name).data
}