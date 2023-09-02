package ru.fwnz.humblr.ui.main.subreddits.subreddits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class SubredditsViewModelFactory  @Inject constructor(
    private val subredditsViewModel: SubredditsViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = subredditsViewModel as T
}