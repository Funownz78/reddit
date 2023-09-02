package ru.fwnz.humblr.ui.main.subreddits.info

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import ru.fwnz.humblr.App
import ru.fwnz.humblr.R
import ru.fwnz.humblr.databinding.FragmentInfoBinding
import ru.fwnz.humblr.domain.Subreddit
import ru.fwnz.humblr.domain.SubredditInfo
import ru.fwnz.humblr.ui.main.ApiLoadState
import ru.fwnz.humblr.ui.main.subreddits.subreddits.SubredditsViewModel

class InfoFragment : Fragment() {
    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!
    private val infoViewModelFactory = App.daggerComponent.getInfoViewModelFactory()
    private val viewModel: InfoViewModel by viewModels { infoViewModelFactory }
    private val args: InfoFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.initSubredditInfo(args.subredditDisplayName)
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(R.string.info).uppercase()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.loadingState.collect {
                    binding.progressCircular.visibility =
                        if (it == ApiLoadState.IN_PROGRESS) View.VISIBLE
                        else View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.subredditInfo.collect{ userInfo ->
                    userInfo?.let { userInfo ->
                        binding.apply {
                            shareSubredditButton.setOnClickListener {
                                val url = Uri.parse("https://www.reddit.com${userInfo.url}")
                                val browserIntent = Intent(Intent.ACTION_VIEW, url)
                                startActivity(browserIntent)
                            }
                            if (userInfo.userIsSubscriber) {
                                followSubbreditFromInfoButton.apply {
                                    backgroundTintList = ContextCompat
                                        .getColorStateList(requireContext(), R.color.humblr_exit_button)
                                    setCompoundDrawablesWithIntrinsicBounds(
                                        null, null,
                                        ContextCompat.getDrawable(requireContext(), R.drawable.unfollow_user),
                                        null
                                    )
                                    text = getString(R.string.unfollow)
                                    setOnClickListener { setUnsubscribeListener(userInfo) }
                                }
                            } else {
                                followSubbreditFromInfoButton.apply {
                                    backgroundTintList = ContextCompat
                                        .getColorStateList(requireContext(), R.color.humblr_primary)
                                    setCompoundDrawablesWithIntrinsicBounds(
                                        null, null,
                                        ContextCompat.getDrawable(requireContext(), R.drawable.follow_user),
                                        null
                                    )
                                    text = getString(R.string.follow)
                                    setOnClickListener { setSubscribeListener(userInfo) }
                                }
                            }
                            textName.text = userInfo.displayNamePrefixed
                            textLink.text = userInfo.description
                        }
                    }
                }
            }
        }
    }

    private fun setSubscribeListener(subreddit: SubredditInfo) {
        val state = viewModel.subscribeSubreddit(subreddit.displayName)
        viewLifecycleOwner.lifecycleScope.launch {
            state.collect {
                binding.apply {

                    when (it) {
                        ApiLoadState.IN_PROGRESS -> {
                            binding.progressCircular.visibility = View.VISIBLE
                            binding.followSubbreditFromInfoButton.visibility = View.INVISIBLE
                        }
                        ApiLoadState.SUCCESS -> {
                            binding.progressCircular.visibility = View.INVISIBLE
                            binding.followSubbreditFromInfoButton.visibility = View.VISIBLE
                            subreddit.userIsSubscriber = true
                            binding.followSubbreditFromInfoButton.setOnClickListener {
                                setUnsubscribeListener(subreddit) }
                            followSubbreditFromInfoButton.apply {
                                backgroundTintList = ContextCompat
                                    .getColorStateList(requireContext(), R.color.humblr_exit_button)
                                setCompoundDrawablesWithIntrinsicBounds(
                                    null, null,
                                    ContextCompat.getDrawable(requireContext(), R.drawable.unfollow_user),
                                    null
                                )
                                text = getString(R.string.unfollow)
                            }
                            cancel()
                        }
                        ApiLoadState.FAILURE -> {
                            binding.progressCircular.visibility = View.INVISIBLE
                            binding.followSubbreditFromInfoButton.visibility = View.VISIBLE
                            Snackbar.make(
                                binding.root,
                                getString(R.string.no_internet),
                                Snackbar.LENGTH_LONG
                            ).show()
                            cancel()
                        }
                    }
                }
            }
        }
    }

    private fun setUnsubscribeListener(subreddit: SubredditInfo) {
        val state = viewModel.unSubscribeSubreddit(subreddit.displayName)
        viewLifecycleOwner.lifecycleScope.launch {
            state.collect {
                binding.apply {
                    when (it) {
                        ApiLoadState.IN_PROGRESS -> {
                            progressCircular.visibility = View.VISIBLE
                            followSubbreditFromInfoButton.visibility = View.INVISIBLE
                        }
                        ApiLoadState.SUCCESS -> {
                            progressCircular.visibility = View.INVISIBLE
                            followSubbreditFromInfoButton.visibility = View.VISIBLE
                            subreddit.userIsSubscriber = false
                            followSubbreditFromInfoButton.setOnClickListener { setSubscribeListener(subreddit) }
                            followSubbreditFromInfoButton.apply {
                                backgroundTintList = ContextCompat
                                    .getColorStateList(requireContext(), R.color.humblr_primary)
                                setCompoundDrawablesWithIntrinsicBounds(
                                    null, null,
                                    ContextCompat.getDrawable(requireContext(), R.drawable.follow_user),
                                    null
                                )
                                text = getString(R.string.follow)
                            }
                            cancel()
                        }
                        ApiLoadState.FAILURE -> {
                            binding.progressCircular.visibility = View.INVISIBLE
                            binding.followSubbreditFromInfoButton.visibility = View.VISIBLE
                            Snackbar.make(
                                binding.root,
                                getString(R.string.no_internet),
                                Snackbar.LENGTH_LONG
                            ).show()
                            cancel()
                        }
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