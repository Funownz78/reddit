package ru.fwnz.humblr.ui.main.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class FavoritesViewModelFactory  @Inject constructor(
    private val favoritesViewModel: FavoritesViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = favoritesViewModel as T
}