package ru.fwnz.humblr.ui.main.subreddits.links

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.fwnz.humblr.domain.Link
import ru.fwnz.humblr.domain.LinksRepository
import javax.inject.Inject

class LinksViewModel @Inject constructor(
    private val linksRepository: LinksRepository
) : ViewModel() {
    var linksPagingData: Flow<PagingData<Link>>? = null

    fun initLinksPagingData(name: String) {
        linksPagingData = linksRepository.getSubredditPager(name)
    }
}