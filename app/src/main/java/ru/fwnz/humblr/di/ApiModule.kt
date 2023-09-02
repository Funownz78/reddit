package ru.fwnz.humblr.di

import android.util.Log
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.addAdapter
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.fwnz.humblr.App
import ru.fwnz.humblr.data.RedditApi
import javax.inject.Singleton
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import ru.fwnz.humblr.data.dto.commentsByLink.*


@Module
class ApiModule {
    @OptIn(ExperimentalStdlibApi::class)
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val logging =
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.HEADERS }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain -> return@addInterceptor addApiAuthorizationToHeader(chain) }
            .build()
        val moshi = Moshi.Builder()
            .add(RepliesAdapter())
            .add(KotlinJsonAdapterFactory())
//            .add(object : Any() {
//                @ToJson
//                fun toJson(writer: JsonWriter, replies: Replies) { }
//            })
//            .addAdapter(RepliesAdapter())
//            .add(
//                PolymorphicJsonAdapterFactory.of(Node::class.java, "replies")
//                .withSubtype(NodeWithoutReplies::class.java, "")
//                    .withFallbackJsonAdapter(RepliesAdapter)
////                .withSubtype(HoldemHand.class, "holdem"))
////            .add(KotlinJsonAdapterFactory())
////            .add(RepliesAdapter::class.java ,RepliesAdapter())
            .build()


        return Retrofit.Builder()
            .baseUrl(RedditApi.BASE_URL)
            .client(client)
//            .addConverterFactory(MoshiConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideRedditApi(retrofit: Retrofit): RedditApi =
        retrofit.create(RedditApi::class.java)

//    private fun addApiAuthorizationToHeader(chain: Interceptor.Chain): Response {
//        val authComponent = App.daggerComponent.getAppAuthComponent()
//        val request = chain.request().newBuilder()
//            .addHeader("Authorization", "Bearer ${authComponent.accessToken}")
//            .build()
//        return chain.proceed(request)
//    }

    private val TAG = "ApiModule"

    private fun addApiAuthorizationToHeader(chain: Interceptor.Chain): Response {
        val authComponent = App.daggerComponent.getAppAuthComponent()
        val originalRequest = chain.request()
        Log.d(TAG, "addApiAuthorizationToHeader authComponent.accessToken: ${authComponent.accessToken}")
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${authComponent.accessToken}")
            .build()
        val response = chain.proceed(request)
        Log.d(TAG, "addApiAuthorizationToHeader: ${response.code}")
        if (response.code == 401) {
            response.close()
            Log.d(TAG, "addApiAuthorizationToHeader: response.close")
            authComponent.performTokenRefresh()
            Log.d(TAG, "addApiAuthorizationToHeader: code 401")
            Log.d(TAG, "addApiAuthorizationToHeader authComponent.accessToken: ${authComponent.accessToken}")
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer ${authComponent.accessToken}")
                .build()
            Log.d(TAG, "addApiAuthorizationToHeader: second request builded")
            return chain.proceed(originalRequest)
        }
        return response
    }

}