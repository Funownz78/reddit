package ru.fwnz.humblr.ui.main.subreddits.subreddits

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.fwnz.humblr.App
import ru.fwnz.humblr.R
import ru.fwnz.humblr.databinding.FragmentSubredditsBinding


class SubredditsFragment : Fragment() {

    private var stateJob: Job? = null
    private var _binding: FragmentSubredditsBinding? = null
    private val binding get() = _binding!!
    private val subredditsViewModelFactory = App.daggerComponent.getSubredditsViewModelFactory()
    private val viewModel: SubredditsViewModel by viewModels { subredditsViewModelFactory }
    private var adapter: SubredditsAdapter? = null
    private var currentSubredditListState = SubredditsViewModel.SubredditListMode.NEW
    private var currentPagingDataCollectJob: Job? = null
    private val subscribeAction: (String) -> StateFlow<SubredditsViewModel.SubscribeStates> =
        { name ->
            viewModel.subscribeSubreddit(name = name)
        }
    private val unsubscribeAction: (String) -> StateFlow<SubredditsViewModel.SubscribeStates> =
        { name ->
            viewModel.unsubscribeSubreddit(name = name)
        }
    private val navigationAction: (String) -> Unit = {
        val action = SubredditsFragmentDirections
            .actionNavigationHomeToLinksFragment(it)
        findNavController().navigate(action)
    }


    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(R.string.home).uppercase()
        viewModel.fetchSubredditsNew()
        adapter = SubredditsAdapter(
            viewLifecycleOwner,
            subscribeAction,
            unsubscribeAction,
            navigationAction
        )
        binding.recyclerView.adapter = adapter
        setupPagingDataCollect()
        setupLoadStatusCollecting()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubredditsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.apply {
            newButton.requestFocus()
            searchEditText.setOnEditorActionListener { textView, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    adapter = SubredditsAdapter(
                        viewLifecycleOwner,
                        subscribeAction,
                        unsubscribeAction,
                        navigationAction,
                    )
                    binding.recyclerView.adapter = adapter
                    viewModel.fetchSubredditsSearch(textView.text.toString())
                    setupPagingDataCollect()
                    setupLoadStatusCollecting()
                    true
                } else {
                    false
                }
            }
            newButton.setOnClickListener {
                if (currentSubredditListState != SubredditsViewModel.SubredditListMode.NEW) {
                    adapter = SubredditsAdapter(
                        viewLifecycleOwner,
                        subscribeAction,
                        unsubscribeAction,
                        navigationAction,
                    )
                    binding.recyclerView.adapter = adapter
                    viewModel.fetchSubredditsNew()
                    setupPagingDataCollect()
                    setupLoadStatusCollecting()
                }
            }
            popularButton.setOnClickListener {
                if (currentSubredditListState != SubredditsViewModel.SubredditListMode.POPULAR) {
                    adapter = SubredditsAdapter(
                        viewLifecycleOwner,
                        subscribeAction,
                        unsubscribeAction,
                        navigationAction,
                    )
                    binding.recyclerView.adapter = adapter
                    viewModel.fetchSubredditsPopular()
                    setupPagingDataCollect()
                    setupLoadStatusCollecting()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.subredditListState.collect {
                    if (currentSubredditListState != it) {
                        currentSubredditListState = it
                        when (it) {
                            SubredditsViewModel.SubredditListMode.NEW -> {
                                binding.newButton.setTextColor(
                                    getColor(
                                        requireContext(),
                                        R.color.humblr_primary
                                    )
                                )
                                binding.popularButton.setTextColor(
                                    getColor(
                                        requireContext(),
                                        androidx.appcompat.R.color.material_grey_800
                                    )
                                )
                                binding.searchEditText.text =
                                    Editable.Factory.getInstance().newEditable("")
                            }
                            SubredditsViewModel.SubredditListMode.POPULAR -> {
                                binding.newButton.setTextColor(
                                    getColor(
                                        requireContext(),
                                        androidx.appcompat.R.color.material_grey_800
                                    )
                                )
                                binding.popularButton.setTextColor(
                                    getColor(
                                        requireContext(),
                                        R.color.humblr_primary
                                    )
                                )
                                binding.searchEditText.text =
                                    Editable.Factory.getInstance().newEditable("")
                            }
                            SubredditsViewModel.SubredditListMode.SEARCH -> {
                                binding.newButton.setTextColor(
                                    getColor(
                                        requireContext(),
                                        androidx.appcompat.R.color.material_grey_800
                                    )
                                )
                                binding.popularButton.setTextColor(
                                    getColor(
                                        requireContext(),
                                        androidx.appcompat.R.color.material_grey_800
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
        return root
    }

    private fun setupLoadStatusCollecting() {
        stateJob?.cancel()
        stateJob = viewLifecycleOwner.lifecycleScope.launch {
            while (adapter == null)
                delay(100)
            adapter?.loadStateFlow?.collectLatest { loadStates ->
                val sourceIsRefresh = loadStates.source.refresh is LoadState.Loading
                binding.progressCircular.visibility =
                    if (sourceIsRefresh) View.VISIBLE
                    else View.GONE
            }
        }
    }

    private fun setupPagingDataCollect() {
        currentPagingDataCollectJob?.cancel()
        currentPagingDataCollectJob = viewLifecycleOwner.lifecycleScope.launch {
                viewModel.subreddits.collect {
                    adapter?.submitData(it)
                }
        }
    }

//    private fun setupPagingDataCollect() {
//        currentPagingDataCollectJob?.cancel()
//        currentPagingDataCollectJob = viewLifecycleOwner.lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.RESUMED) {
//                viewModel.subreddits.collect {
//                    adapter?.submitData(it)
//                }
//            }
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}