package ru.fwnz.humblr.data

import okhttp3.Interceptor
import okhttp3.Response
import ru.fwnz.humblr.App

//class AuthInterceptor : Interceptor {
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val originalRequest = chain.request()
//        val request = chain.request().newBuilder()
//
//
//        val response = chain.proceed(originalRequest)
//        val authComponent = App.daggerComponent.getAppAuthComponent()
//    }
//}