package ru.fwnz.humblr.ui.main.profile.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.fwnz.humblr.ui.main.profile.profile.ProfileViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class FriendsViewModelFactory @Inject constructor(
    private val friendsViewModel: FriendsViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = friendsViewModel as T
}