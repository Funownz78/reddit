package ru.fwnz.humblr.ui.main.subreddits.link

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.fwnz.humblr.R
import ru.fwnz.humblr.databinding.CommentItemBinding
import ru.fwnz.humblr.databinding.LevelTabItemBinding
import ru.fwnz.humblr.domain.Comments
import ru.fwnz.humblr.domain.Subreddit
import ru.fwnz.humblr.ui.main.subreddits.subreddits.SubredditsViewModel

class CommentsByLinkAdapter(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val likeAction: (String) -> StateFlow<LinkViewModel.SubscribeStates>,
    private val unVoteAction: (String) -> StateFlow<LinkViewModel.SubscribeStates>,
    private val dislikeAction: (String) -> StateFlow<LinkViewModel.SubscribeStates>,
    private val saveAction: (String) -> StateFlow<LinkViewModel.SubscribeStates>,
) : ListAdapter<Comments, CommentsByLinkAdapter.CommentsByLinkViewHolder>(CommentsByLinkDiffUtilCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsByLinkViewHolder {
        val binding = CommentItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CommentsByLinkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentsByLinkViewHolder, position: Int) {
        getItem(position)?.let { holder.bindLink(it) }
    }

    inner class CommentsByLinkViewHolder(private val binding: CommentItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bindLink(item: Comments) {
                binding.apply {
                    with(item) {
                        repeat(level) {
                            val layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                            )
                            val tabView = LevelTabItemBinding.inflate(
                                LayoutInflater.from(root.context), root, false)
                            root.addView(tabView.root, 0, layoutParams)
                        }
                        authorButton.text = author
                        val timeInHours = (((System.currentTimeMillis()/1000) - createdUtc) / 3600).toInt()
                        estimatedHoursText.text =
                            context.resources.getQuantityString(
                                R.plurals.estimated_hours,
                                timeInHours,
                                timeInHours,
                            )
                        selftextText.text = body
                        scoreText.text = score.toString()
                        likeButton.setOnClickListener { setLikeListener(item) }
                        dislikeButton.setOnClickListener { setDislikeListener(item) }
                        saveButton.setOnClickListener { setSaveListener(item) }
                        if (saved) {
                            saveButton.setCompoundDrawablesWithIntrinsicBounds(
                                AppCompatResources.getDrawable(context, R.drawable.baseline_bookmark_added_24),
                                null, null, null
                            )
                            saveButton.text = context.getString(R.string.saved)
                            saveButton.isEnabled = false
                        } else {
                            saveButton.setCompoundDrawablesWithIntrinsicBounds(
                                AppCompatResources.getDrawable(context, R.drawable.baseline_bookmark_add_24),
                                null, null, null
                            )
                            saveButton.text = context.getString(R.string.save)
                            saveButton.isEnabled = true
                        }
                        when (likes) {
                            true -> {
                                likeButton.setImageResource(R.drawable.arrow_like)
                                dislikeButton.setImageResource(R.drawable.arrow_down)
                            }
                            false -> {
                                likeButton.setImageResource(R.drawable.arrow_up)
                                dislikeButton.setImageResource(R.drawable.arrow_dislike)
                            }
                            null -> {
                                likeButton.setImageResource(R.drawable.arrow_up)
                                dislikeButton.setImageResource(R.drawable.arrow_down)
                            }
                        }
                    }
                }
            }

            private fun setLikeListener(comments: Comments) {
                val commentId = "t1_${comments.id}"
                val state =
                    if (comments.likes == true) unVoteAction(commentId)
                    else likeAction(commentId)
                lifecycleOwner.lifecycleScope.launch {
                    state.collect {
                        when (it) {
                            LinkViewModel.SubscribeStates.IN_PROGRESS -> {
                                binding.apply {
                                    likeButton.visibility = View.GONE
                                    likeProgress.visibility = View.VISIBLE
                                    dislikeButton.isEnabled = false
                                }
                            }
                            LinkViewModel.SubscribeStates.SUCCESS -> {
                                binding.apply {
                                    likeButton.visibility = View.VISIBLE
                                    likeProgress.visibility = View.GONE
                                    dislikeButton.isEnabled = true
                                    when (comments.likes) {
                                        true -> {
                                            likeButton.setImageDrawable(
                                                AppCompatResources.getDrawable(
                                                    context,
                                                    R.drawable.arrow_up
                                                )
                                            )
                                            dislikeButton.setImageDrawable(
                                                AppCompatResources.getDrawable(
                                                    context,
                                                    R.drawable.arrow_down
                                                )
                                            )
                                            comments.score = comments.score - 1
                                            comments.likes = null
                                            scoreText.text = comments.score.toString()
                                        }
                                        false -> {
                                            likeButton.setImageDrawable(
                                                AppCompatResources.getDrawable(
                                                    context,
                                                    R.drawable.arrow_like
                                                )
                                            )
                                            dislikeButton.setImageDrawable(
                                                AppCompatResources.getDrawable(
                                                    context,
                                                    R.drawable.arrow_down
                                                )
                                            )
                                            comments.score = comments.score + 2
                                            comments.likes = true
                                            scoreText.text = comments.score.toString()
                                        }
                                        null -> {
                                            likeButton.setImageDrawable(
                                                AppCompatResources.getDrawable(
                                                    context,
                                                    R.drawable.arrow_like
                                                )
                                            )
                                            dislikeButton.setImageDrawable(
                                                AppCompatResources.getDrawable(
                                                    context,
                                                    R.drawable.arrow_down
                                                )
                                            )
                                            comments.score = comments.score + 1
                                            comments.likes = true
                                            scoreText.text = comments.score.toString()
                                        }
                                    }
                                }
                                cancel()
                            }
                            LinkViewModel.SubscribeStates.FAILURE -> {
                                binding.apply {
                                    likeButton.visibility = View.VISIBLE
                                    likeProgress.visibility = View.GONE
                                    dislikeButton.isEnabled = true
                                }
                                Snackbar.make(
                                    binding.root,
                                    "No internet connection",
                                    Snackbar.LENGTH_LONG
                                ).show()
                                cancel()
                            }
                        }
                    }
                }
            }

            private fun setDislikeListener(comments: Comments) {
                val commentId = "t1_${comments.id}"
                val state =
                    if (comments.likes == false) unVoteAction(commentId)
                    else dislikeAction(commentId)
                lifecycleOwner.lifecycleScope.launch {
                    state.collect {
                        when (it) {
                            LinkViewModel.SubscribeStates.IN_PROGRESS -> {
                                binding.apply {
                                    dislikeButton.visibility = View.GONE
                                    dislikeProgress.visibility = View.VISIBLE
                                    likeButton.isEnabled = false
                                }
                            }
                            LinkViewModel.SubscribeStates.SUCCESS -> {
                                binding.apply {
                                    dislikeButton.visibility = View.VISIBLE
                                    dislikeProgress.visibility = View.GONE
                                    likeButton.isEnabled = true
                                    when (comments.likes) {
                                        true -> {
                                            likeButton.setImageDrawable(
                                                AppCompatResources.getDrawable(
                                                    context,
                                                    R.drawable.arrow_up
                                                )
                                            )
                                            dislikeButton.setImageDrawable(
                                                AppCompatResources.getDrawable(
                                                    context,
                                                    R.drawable.arrow_dislike
                                                )
                                            )
                                            comments.score = comments.score - 2
                                            comments.likes = false
                                            scoreText.text = comments.score.toString()
                                        }
                                        false -> {
                                            likeButton.setImageDrawable(
                                                AppCompatResources.getDrawable(
                                                    context,
                                                    R.drawable.arrow_up
                                                )
                                            )
                                            dislikeButton.setImageDrawable(
                                                AppCompatResources.getDrawable(
                                                    context,
                                                    R.drawable.arrow_down
                                                )
                                            )
                                            comments.score = comments.score + 1
                                            comments.likes = null
                                            scoreText.text = comments.score.toString()
                                        }
                                        null -> {
                                            likeButton.setImageDrawable(
                                                AppCompatResources.getDrawable(
                                                    context,
                                                    R.drawable.arrow_up
                                                )
                                            )
                                            dislikeButton.setImageDrawable(
                                                AppCompatResources.getDrawable(
                                                    context,
                                                    R.drawable.arrow_dislike
                                                )
                                            )
                                            comments.score = comments.score - 1
                                            comments.likes = false
                                            scoreText.text = comments.score.toString()
                                        }
                                    }
                                }
                                cancel()
                            }
                            LinkViewModel.SubscribeStates.FAILURE -> {
                                binding.apply {
                                    dislikeButton.visibility = View.VISIBLE
                                    dislikeProgress.visibility = View.GONE
                                    likeButton.isEnabled = true
                                }
                                Snackbar.make(
                                    binding.root,
                                    "No internet connection",
                                    Snackbar.LENGTH_LONG
                                ).show()
                                cancel()
                            }
                        }
                    }
                }
            }

            private fun setSaveListener(comments: Comments) {
                val commentId = "t1_${comments.id}"
                val state = saveAction(commentId)
                lifecycleOwner.lifecycleScope.launch {
                    state.collect {
                        when (it) {
                            LinkViewModel.SubscribeStates.IN_PROGRESS -> {
                                binding.apply {
                                    saveButton.visibility = View.GONE
                                    saveProgress.visibility = View.VISIBLE
                                }
                            }
                            LinkViewModel.SubscribeStates.SUCCESS -> {
                                binding.apply {
                                    saveButton.visibility = View.VISIBLE
                                    saveProgress.visibility = View.GONE
                                    saveButton.isEnabled = false
                                    saveButton.setCompoundDrawablesWithIntrinsicBounds(
                                        AppCompatResources.getDrawable(context, R.drawable.baseline_bookmark_added_24),
                                        null, null, null
                                    )
                                    saveButton.text = context.getString(R.string.saved)
                                }
                                cancel()
                            }
                            LinkViewModel.SubscribeStates.FAILURE -> {
                                binding.apply {
                                    saveButton.visibility = View.VISIBLE
                                    saveProgress.visibility = View.GONE
                                }
                                Snackbar.make(
                                    binding.root,
                                    "No internet connection",
                                    Snackbar.LENGTH_LONG
                                ).show()
                                cancel()
                            }
                        }
                    }
                }
            }

    }

    class CommentsByLinkDiffUtilCallBack : DiffUtil.ItemCallback<Comments>() {
        override fun areItemsTheSame(oldItem: Comments, newItem: Comments): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Comments, newItem: Comments): Boolean {
            return oldItem.id == newItem.id
        }
    }
}