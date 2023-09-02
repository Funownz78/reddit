package ru.fwnz.humblr.ui.main.subreddits.link

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class LinkViewModelFactory @Inject constructor(
    private val linkViewModel: LinkViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = linkViewModel as T
}