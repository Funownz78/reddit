package ru.fwnz.humblr.ui.main.subreddits.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.fwnz.humblr.ui.main.subreddits.info.InfoViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class UserViewModelFactory @Inject constructor(
    private val userViewModel: UserViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = userViewModel as T
}