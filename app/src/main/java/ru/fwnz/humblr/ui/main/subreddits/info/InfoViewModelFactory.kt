package ru.fwnz.humblr.ui.main.subreddits.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.fwnz.humblr.ui.main.subreddits.links.LinksViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class InfoViewModelFactory @Inject constructor(
    private val infoViewModel: InfoViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = infoViewModel as T
}