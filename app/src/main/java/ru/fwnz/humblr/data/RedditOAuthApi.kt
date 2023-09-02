package ru.fwnz.humblr.data

import retrofit2.http.POST
import ru.fwnz.humblr.data.dto.aouth.AOuthDto

interface RedditOAuthApi {
    companion object {
        const val BASE_URL = "https://www.reddit.com/"
    }

    @POST("api/v1/access_token")
    suspend fun getRefreshToken(): AOuthDto

}