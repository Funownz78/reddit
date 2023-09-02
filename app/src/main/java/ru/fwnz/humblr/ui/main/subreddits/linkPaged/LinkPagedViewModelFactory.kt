package ru.fwnz.humblr.ui.main.subreddits.linkPaged

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class LinkPagedViewModelFactory @Inject constructor(
    private val linkPagedViewModel: LinkPagedViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = linkPagedViewModel as T
}