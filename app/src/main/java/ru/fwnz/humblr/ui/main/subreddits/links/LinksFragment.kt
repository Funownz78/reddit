package ru.fwnz.humblr.ui.main.subreddits.links

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.fwnz.humblr.App
import ru.fwnz.humblr.R
import ru.fwnz.humblr.databinding.FragmentLinksBinding

class LinksFragment : Fragment() {
    private var _binding: FragmentLinksBinding? = null
    private val binding get() = _binding!!
    private val linksViewModelFactory = App.daggerComponent.getLinksViewModelFactory()
    private val viewModel: LinksViewModel by viewModels { linksViewModelFactory }
    private val args: LinksFragmentArgs by navArgs()
    private lateinit var adapter: LinksAdapter
    private var linksPagingDataJob: Job? = null
    private val navigationActionForUser: (String) -> Unit = { name ->
        val action = LinksFragmentDirections.actionLinksFragmentToUserFragment(name)
        findNavController().navigate(action)
    }
    private val navigationActionForLink: (String) -> Unit = { linkId ->
        val action = LinksFragmentDirections.actionLinksFragmentToLinkFragment(args.name, linkId)
        findNavController().navigate(action)
    }

    private var stateJob: Job? = null
    private fun setupLoadStatusCollecting() {
        adapter = LinksAdapter(
            requireContext(),
            navigationActionForUser,
            navigationActionForLink
        )
        binding.recyclerView.adapter = adapter
        stateJob?.cancel()
        stateJob = viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                val sourceIsRefresh = loadStates.source.refresh is LoadState.Loading
                binding.progressCircular.visibility =
                    if (sourceIsRefresh) View.VISIBLE
                    else View.GONE
            }
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLinksBinding.inflate(inflater, container, false)
        viewModel.initLinksPagingData(args.name)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(R.string.links).uppercase()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLoadStatusCollecting()
        Log.d("TAG", "onResume: ")
        linksPagingDataJob = viewLifecycleOwner.lifecycleScope.launch {
            while (viewModel.linksPagingData == null)
                delay(100)
            viewModel.linksPagingData!!.collect {
                adapter.submitData(it)
            }
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.actionbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_info -> {
                        val action = LinksFragmentDirections
                            .actionLinksFragmentToInfoFragment(args.name)
                        findNavController().navigate(action)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}