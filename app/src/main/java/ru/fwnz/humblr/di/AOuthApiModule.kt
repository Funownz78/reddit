package ru.fwnz.humblr.di

import android.util.Base64
import android.util.Log
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.fwnz.humblr.App
import ru.fwnz.humblr.BuildConfig
import ru.fwnz.humblr.data.RedditApi
import ru.fwnz.humblr.data.RedditOAuthApi
import javax.inject.Singleton

@Module
class AOuthApiModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val logging =
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.HEADERS }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain -> return@addInterceptor addApiAuthorizationToHeader(chain) }
            .build()

        return Retrofit.Builder()
            .baseUrl(RedditOAuthApi.BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideRedditApi(retrofit: Retrofit): RedditOAuthApi =
        retrofit.create(RedditOAuthApi::class.java)

//    @Headers("Authorization: Basic dmJZYkgtdXBub2FKdE1LbnpmS0tOQTo=")

    private fun addApiAuthorizationToHeader(chain: Interceptor.Chain): Response {
        val authComponent = App.daggerComponent.getAppAuthComponent()
        val clientIdEncrypted = Base64.encodeToString(
            "${BuildConfig.API_CLIENT_ID}:".toByteArray(),
            Base64.NO_WRAP
        )
        val requestUrl = chain.request().url.newBuilder()
            .addQueryParameter("grant_type", "refresh_token")
            .addQueryParameter("refresh_token", authComponent.refreshToken)
            .build()
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Basic $clientIdEncrypted")
            .url(requestUrl)
            .build()

        return chain.proceed(request)
    }

}