package ru.fwnz.humblr.ui.main.favorite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.fwnz.humblr.App
import ru.fwnz.humblr.R
import ru.fwnz.humblr.databinding.FragmentFavoriteBinding
import ru.fwnz.humblr.ui.main.ApiLoadState
import ru.fwnz.humblr.ui.main.subreddits.subreddits.SubredditsAdapter
import ru.fwnz.humblr.ui.main.subreddits.subreddits.SubredditsViewModel
import ru.fwnz.humblr.ui.main.subreddits.user.CommentsAdapter

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private val favoritesViewModelFactory = App.daggerComponent.getFavoritesViewModelFactory()
    private val viewModel: FavoritesViewModel by viewModels { favoritesViewModelFactory }
    private var currentFlowCollector: Job? = null
    private var subredditAdapter: FavoritesSubredditsAdapter? = null
    private var commentsAdapter: FavoritesCommentsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(R.string.favorites).uppercase()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subredditAdapter = FavoritesSubredditsAdapter()
        commentsAdapter = FavoritesCommentsAdapter()

        binding.radioGroup.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.subreddits_button -> viewModel.getSubreddits()
                R.id.comments_button -> viewModel.getComments()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.loadState.collectLatest { loadStates ->
                    val sourceIsRefresh = loadStates == ApiLoadState.IN_PROGRESS
                    binding.progressCircular.visibility =
                        if (sourceIsRefresh) View.VISIBLE
                        else View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.favoritesListState.collect { it ->
                    currentFlowCollector?.cancel()
                    currentFlowCollector = if (it == FavoritesViewModel.FavoritesListMode.SUBREDDITS) {
                        binding.recyclerView.adapter = subredditAdapter
                        launch {
                            viewModel.subreddits.collect{ subredditsList ->
                                subredditsList?.let { subredditAdapter?.submitList(it) }
                            }
                        }
                    } else {
                        binding.recyclerView.adapter = commentsAdapter
                        launch {
                            viewModel.comments.collect{ commentsList ->
                                commentsList?.let { commentsAdapter?.submitList(it) }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}