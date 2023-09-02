package ru.fwnz.humblr.ui.main.subreddits.link

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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.fwnz.humblr.App
import ru.fwnz.humblr.R
import ru.fwnz.humblr.databinding.FragmentLinkBinding

class LinkFragment : Fragment() {
    private var _binding: FragmentLinkBinding? = null
    private val binding get() = _binding!!
    private val linkViewModelFactory = App.daggerComponent.getLinkViewModelFactory()
    private val viewModel: LinkViewModel by viewModels { linkViewModelFactory }
    private val args: LinkFragmentArgs by navArgs()
    private lateinit var adapter: CommentsByLinkAdapter
    private val likeAction: (String) -> StateFlow<LinkViewModel.SubscribeStates> = { commentId ->
        viewModel.likeComment(commentId)
    }
    private val unVoteAction: (String) -> StateFlow<LinkViewModel.SubscribeStates> = { commentId ->
        viewModel.unVoteComment(commentId)
    }
    private val dislikeAction: (String) -> StateFlow<LinkViewModel.SubscribeStates> = { commentId ->
        viewModel.dislikeComment(commentId)
    }
    private val saveAction: (String) -> StateFlow<LinkViewModel.SubscribeStates> = { commentId ->
        viewModel.saveComment(commentId)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.initCommentsList(args.subredditName, args.linkId)
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(R.string.link).uppercase()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLinkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CommentsByLinkAdapter(
            context = requireContext(),
            lifecycleOwner = viewLifecycleOwner,
            likeAction = likeAction,
            unVoteAction = unVoteAction,
            dislikeAction = dislikeAction,
            saveAction = saveAction,
        )
        binding.recyclerView.adapter = adapter

        binding.showAllButton.setOnClickListener {
            val action = LinkFragmentDirections.actionLinkFragmentToLinkPagedFragment(
                args.subredditName,
                args.linkId
            )
            findNavController().navigate(action)
        }
        binding.moreResponsesText.visibility = View.INVISIBLE

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.commentsByLink.collect {comments ->
                    comments?.let {
                        binding.progressCircular.visibility = View.GONE
                        if (it.isNotEmpty()) {
                            binding.apply {
                                authorButton.text = it[0].author
                                val timeInHours = (((System.currentTimeMillis()/1000) - it[0].createdUtc) / 3600).toInt()
                                estimatedHoursText.text =
                                    resources.getQuantityString(
                                        R.plurals.estimated_hours,
                                        timeInHours,
                                        timeInHours,
                                    )
                                titleText.text = it[0].title
                                selftextText.text = it[0].body
                                commentsCountText.text = resources.getQuantityString(
                                    R.plurals.comments,
                                    it.size-1,
                                    it.size-1,
                                )
                                shareButton.visibility = View.INVISIBLE
                                likesCountText.text = it[0].score.toString()

                                val drawable = ContextCompat.getDrawable(
                                    requireContext(),
                                    when (it[0].likes) {
                                        true -> R.drawable.heart_filled
                                        false -> R.drawable.dislike
                                        null -> R.drawable.favorite_gray
                                    })
                                likesCountText.setCompoundDrawablesWithIntrinsicBounds(
                                     null, null, drawable,null
                                )
                                val commentsCount = 2
                                adapter.submitList(it.slice(1..commentsCount))
                                moreResponsesText.visibility = View.VISIBLE
                                moreResponsesText.text = getString(R.string.more_response, it.size - commentsCount - 1)
                            }
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