package ru.fwnz.humblr.ui.main.subreddits.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ru.fwnz.humblr.App
import ru.fwnz.humblr.R
import ru.fwnz.humblr.databinding.FragmentUserBinding
import ru.fwnz.humblr.domain.Subreddit
import ru.fwnz.humblr.ui.main.ApiLoadState
import ru.fwnz.humblr.ui.main.subreddits.links.LinksAdapter
import ru.fwnz.humblr.ui.main.subreddits.links.LinksViewModel
import ru.fwnz.humblr.ui.main.subreddits.subreddits.SubredditsViewModel

class UserFragment : Fragment() {
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private val userViewModelFactory = App.daggerComponent.getUserViewModelFactory()
    private val viewModel: UserViewModel by viewModels { userViewModelFactory }
    private val args: UserFragmentArgs by navArgs()
    private lateinit var adapter: CommentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        viewModel.initUserCommentsPagingData(args.name)
        return binding.root
    }

    private var stateJob: Job? = null
    private fun setupLoadStatusCollecting() {
        adapter = CommentsAdapter()
        binding.recyclerView.adapter = adapter
        stateJob?.cancel()
        stateJob = viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.combine(viewModel.loadState) { pagingState, normalState ->
                val sourceState = pagingState.source.refresh is LoadState.Loading
                val loadState = normalState == ApiLoadState.IN_PROGRESS
                loadState || sourceState
            }.collectLatest { loadStates ->
                binding.progressCircular.visibility =
                    if (loadStates) View.VISIBLE
                    else View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(R.string.user).uppercase()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLoadStatusCollecting()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                while (viewModel.userCommentsPagingData == null)
                    delay(100)
                viewModel.userCommentsPagingData?.collect{
                    adapter.submitData(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.userInfo.collect{ userInfo ->
                    userInfo?.let {
                        binding.apply {
                            followButton.isEnabled = !it.userIsSubscriber
                            if (!userInfo.userIsSubscriber) {
                                followButton.setOnClickListener { setSubscribeListener(userInfo.subredditDisplayName) }
                            }

                            textName.text = userInfo.name
                            textLink.text = userInfo.link
                            Glide.with(requireContext())
                                .load(userInfo.iconImg)
                                .centerCrop()
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .error(R.drawable.logo)
                                .into(avatarImage)
                        }
                    }
                }
            }
        }

    }
    private fun setSubscribeListener(name: String) {
        val state = viewModel.subscribeSubreddit(name)
        viewLifecycleOwner.lifecycleScope.launch {
            state.collect {
                when (it) {
                    SubredditsViewModel.SubscribeStates.IN_PROGRESS -> {
                        binding.followButton.visibility = View.INVISIBLE
                        binding.followProgress.visibility = View.VISIBLE
                    }
                    SubredditsViewModel.SubscribeStates.SUCCESS -> {
                        binding.followButton.visibility = View.VISIBLE
                        binding.followProgress.visibility = View.INVISIBLE
                        binding.followButton.isEnabled = false
                        cancel()
                    }
                    SubredditsViewModel.SubscribeStates.FAILURE -> {
                        binding.followButton.visibility = View.VISIBLE
                        binding.followProgress.visibility = View.INVISIBLE
                        binding.followButton.isEnabled = true
                        Snackbar.make(
                            binding.root,
                            "Подписаться не удалось",
                            Snackbar.LENGTH_LONG
                        ).show()
                        cancel()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}