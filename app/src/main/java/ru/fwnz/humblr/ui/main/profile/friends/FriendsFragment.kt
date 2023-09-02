package ru.fwnz.humblr.ui.main.profile.friends

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.fwnz.humblr.App
import ru.fwnz.humblr.R
import ru.fwnz.humblr.databinding.FragmentFriendsBinding
import ru.fwnz.humblr.databinding.FragmentProfileBinding
import ru.fwnz.humblr.ui.main.ApiLoadState
import ru.fwnz.humblr.ui.main.profile.profile.ProfileViewModel

class FriendsFragment : Fragment() {
    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!
    private val friendsViewModelFactory = App.daggerComponent.getFriendsViewModelFactory()
    private val viewModel: FriendsViewModel by viewModels { friendsViewModelFactory }
    private lateinit var adapter: FriendsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = FriendsAdapter(requireContext())
        binding.recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.friend.collect {
                    adapter.submitList(it)
                }
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

    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(R.string.friends_list).uppercase()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}