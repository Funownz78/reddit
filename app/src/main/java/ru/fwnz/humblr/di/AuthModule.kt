package ru.fwnz.humblr.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.fwnz.humblr.data.AppAuthComponent
import javax.inject.Singleton

@Module
class AuthModule {
    @Provides
    @Singleton
    fun provideAppAuthComponent(context: Context) = AppAuthComponent(context)
}