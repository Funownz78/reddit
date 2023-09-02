package ru.fwnz.humblr.ui.main.subreddits.links

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class LinksViewModelFactory @Inject constructor(
    private val linksViewModel: LinksViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = linksViewModel as T
}