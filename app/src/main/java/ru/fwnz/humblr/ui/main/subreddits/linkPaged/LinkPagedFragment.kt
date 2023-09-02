package ru.fwnz.humblr.ui.main.subreddits.linkPaged

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
import ru.fwnz.humblr.databinding.FragmentLinkPagedBinding
import ru.fwnz.humblr.databinding.FragmentLinksBinding
import ru.fwnz.humblr.ui.main.subreddits.link.CommentsByLinkAdapter
import ru.fwnz.humblr.ui.main.subreddits.link.LinkFragmentArgs
import ru.fwnz.humblr.ui.main.subreddits.link.LinkFragmentDirections
import ru.fwnz.humblr.ui.main.subreddits.link.LinkViewModel
import ru.fwnz.humblr.ui.main.subreddits.links.LinksViewModel

class LinkPagedFragment : Fragment() {
    private var _binding: FragmentLinkPagedBinding? = null
    private val binding get() = _binding!!
    private val linkPagedViewModelFactory = App.daggerComponent.getLinkPagedViewModelFactory()
    private val viewModel: LinkPagedViewModel by viewModels { linkPagedViewModelFactory }
    private val args: LinkFragmentArgs by navArgs()
    private lateinit var adapter: AllCommentsByLinkAdapter
    private val likeAction: (String) -> StateFlow<LinkPagedViewModel.SubscribeStates> = { commentId ->
        viewModel.likeComment(commentId)
    }
    private val unVoteAction: (String) -> StateFlow<LinkPagedViewModel.SubscribeStates> = { commentId ->
        viewModel.unVoteComment(commentId)
    }
    private val dislikeAction: (String) -> StateFlow<LinkPagedViewModel.SubscribeStates> = { commentId ->
        viewModel.dislikeComment(commentId)
    }
    private val saveAction: (String) -> StateFlow<LinkPagedViewModel.SubscribeStates> = { commentId ->
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
        _binding = FragmentLinkPagedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AllCommentsByLinkAdapter(
            context = requireContext(),
            lifecycleOwner = viewLifecycleOwner,
            likeAction = likeAction,
            unVoteAction = unVoteAction,
            dislikeAction = dislikeAction,
            saveAction = saveAction,
        )
        binding.recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.commentsByLink.collect {comments ->
                    comments?.let {
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
                                adapter.submitList(it.drop(1))
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