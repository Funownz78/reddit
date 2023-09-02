package ru.fwnz.humblr.data

import retrofit2.Response
import retrofit2.http.*
import ru.fwnz.humblr.data.dto.commentsByLink.CommentsByLinkDto
import ru.fwnz.humblr.data.dto.commentsByLink.CommentsByLinkDtoItem
import ru.fwnz.humblr.data.dto.commentsByUser.CommentsByUserDto
import ru.fwnz.humblr.data.dto.commentsSaved.CommentsSavedDto
import ru.fwnz.humblr.data.dto.friends.SubscribedDto
import ru.fwnz.humblr.data.dto.linksList.LinksListDto
import ru.fwnz.humblr.data.dto.me.MeDto
import ru.fwnz.humblr.data.dto.postsList.PostsListDto
import ru.fwnz.humblr.data.dto.subredditAbout.SubredditAboutDto
import ru.fwnz.humblr.data.dto.subredditsList.SubredditsListDto
import ru.fwnz.humblr.data.dto.userabout.UserAboutDto

interface RedditApi {
    companion object {
        const val BASE_URL = "https://oauth.reddit.com/"
        const val PAGE_SIZE = 25
    }

    @Headers("Accept-Version: v1")
    @GET("api/v1/me")
    suspend fun me(): MeDto

    @Headers("Accept-Version: v1")
    @GET("subreddits/new")
    suspend fun getNewSubreddit(
        @Query("after") after: String?,
    ): SubredditsListDto

    @Headers("Accept-Version: v1")
    @GET("subreddits/popular")
    suspend fun getBestSubreddit(
        @Query("after") after: String?,
    ): SubredditsListDto

    @Headers("Accept-Version: v1")
    @GET("subreddits/search")
    suspend fun getSearchedSubreddit(
        @Query("q") q: String,
        @Query("after") after: String?,
    ): SubredditsListDto

    @POST("api/subscribe")
    suspend fun subscribeSubreddit(
        @Query("sr_name") subredditName: String,
        @Query("action") action: String = "sub",
        @Query("skip_initial_defaults") skipInitialDefaults: Boolean = true,
    ): Response<Unit>

    @POST("api/subscribe")
    suspend fun unsubscribeSubreddit(
        @Query("sr_name") subredditName: String,
        @Query("action") action: String = "unsub",
    ): Response<Unit>

    @POST("api/vote")
    suspend fun setCommentLiked(
        @Query("id") commentId: String,
        @Query("dir") dir: Int = 1,
    ): Response<Unit>

    @POST("api/vote")
    suspend fun setCommentUnVoted(
        @Query("id") commentId: String,
        @Query("dir") dir: Int = 0,
    ): Response<Unit>

    @POST("api/vote")
    suspend fun setCommentDisliked(
        @Query("id") commentId: String,
        @Query("dir") dir: Int = -1,
    ): Response<Unit>

    @POST("api/save")
    suspend fun saveComment(
        @Query("id") commentId: String,
    ): Response<Unit>

    @POST("api/unsave")
    suspend fun unSaveComment(
        @Query("id") commentId: String,
    ): Response<Unit>

    @GET("r/{name}")
    suspend fun getLinksForSubreddit(
        @Path("name") name: String,
        @Query("after") after: String?
    ): LinksListDto

    @GET("user/{name}/about")
    suspend fun getUserAbout(
        @Path("name") name: String,
    ): UserAboutDto

    @GET("user/{name}/comments")
    suspend fun getUserComments(
        @Path("name") q: String,
        @Query("after") after: String?,
    ): CommentsByUserDto

    @GET("r/{name}/comments/{id}")
    suspend fun getLinkComments(
        @Path("name") name: String,
        @Path("id") id: String,
        @Query("limit") limit: Int = -1,
    ): List<CommentsByLinkDtoItem>

    @Headers("Accept-Version: v1")
    @GET("subreddits/mine/subscriber?limit=10000")
    suspend fun getSubscribed(): SubscribedDto

    @Headers("Accept-Version: v1")
    @GET("r/{name}/about.json")
    suspend fun getSubredditInfo(
        @Path("name") name: String
    ): SubredditAboutDto

    @GET("user/{name}/saved?limit=10000")
    suspend fun getSavedComments(
        @Path("name") name: String
    ): CommentsSavedDto
}
