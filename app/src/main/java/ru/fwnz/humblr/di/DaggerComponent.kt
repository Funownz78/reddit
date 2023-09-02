package ru.fwnz.humblr.di

import dagger.Component
import ru.fwnz.humblr.data.AppAuthComponent
import ru.fwnz.humblr.ui.main.favorite.FavoritesViewModelFactory
import ru.fwnz.humblr.ui.main.profile.friends.FriendsViewModelFactory
import ru.fwnz.humblr.ui.main.profile.profile.ProfileViewModelFactory
import ru.fwnz.humblr.ui.main.subreddits.info.InfoViewModelFactory
import ru.fwnz.humblr.ui.main.subreddits.link.LinkViewModelFactory
import ru.fwnz.humblr.ui.main.subreddits.linkPaged.LinkPagedViewModelFactory
import ru.fwnz.humblr.ui.main.subreddits.links.LinksViewModelFactory
import ru.fwnz.humblr.ui.main.subreddits.subreddits.SubredditsViewModelFactory
import ru.fwnz.humblr.ui.main.subreddits.user.UserViewModelFactory
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppModule::class,
    AuthModule::class,
    ApiModule::class,
    ApiRepositoriesModule::class
])
interface DaggerComponent {
    fun getAppAuthComponent(): AppAuthComponent
    fun getProfileViewModelFactory(): ProfileViewModelFactory
    fun getFriendsViewModelFactory(): FriendsViewModelFactory
    fun getSubredditsViewModelFactory(): SubredditsViewModelFactory
    fun getLinksViewModelFactory(): LinksViewModelFactory
    fun getLinkViewModelFactory(): LinkViewModelFactory
    fun getInfoViewModelFactory(): InfoViewModelFactory
    fun getLinkPagedViewModelFactory(): LinkPagedViewModelFactory
    fun getUserViewModelFactory(): UserViewModelFactory
    fun getFavoritesViewModelFactory(): FavoritesViewModelFactory
}

