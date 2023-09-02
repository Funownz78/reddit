package ru.fwnz.humblr.di

import dagger.Binds
import dagger.Module
import ru.fwnz.humblr.data.RedditRepository
import ru.fwnz.humblr.domain.*
import javax.inject.Singleton

@Module
abstract class ApiRepositoriesModule {
    @Binds
    @Singleton
    abstract fun bindsMeRepository(repo: RedditRepository): MeRepository

    @Binds
    @Singleton
    abstract fun bindsSubredditQueryRepository(repo: RedditRepository): SubredditQueryRepository

    @Binds
    @Singleton
    abstract fun bindsSubscribeRepository(repo: RedditRepository): SubscribeRepository

    @Binds
    @Singleton
    abstract fun bindsLinksRepository(repo: RedditRepository): LinksRepository

    @Binds
    @Singleton
    abstract fun bindsUserRepository(repo: RedditRepository): UserRepository

    @Binds
    @Singleton
    abstract fun bindsCommentsRepository(repo: RedditRepository): CommentsRepository

    @Binds
    @Singleton
    abstract fun bindsCommentActionRepository(repo: RedditRepository): CommentActionRepository

    @Binds
    @Singleton
    abstract fun bindsFriendsRepository(repo: RedditRepository): FriendsRepository

    @Binds
    @Singleton
    abstract fun bindsSubredditInfoRepository(repo: RedditRepository): SubredditInfoRepository

    @Binds
    @Singleton
    abstract fun bindsFollowedSubredditsRepository(repo: RedditRepository): FavoriteRepository
}