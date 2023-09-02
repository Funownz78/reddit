package ru.fwnz.humblr.ui.main.subreddits.subreddits

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.fwnz.humblr.R
import ru.fwnz.humblr.databinding.SubredditItemBinding
import ru.fwnz.humblr.domain.Subreddit

class SubredditsAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val subscribeAction: (String) -> StateFlow<SubredditsViewModel.SubscribeStates>,
    private val unsubscribeAction: (String) -> StateFlow<SubredditsViewModel.SubscribeStates>,
    private val navigationAction: (String) -> Unit,
) :
    PagingDataAdapter<Subreddit, SubredditsAdapter.SubredditViewHolder>(DiffUtilCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubredditViewHolder {
        val binding = SubredditItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SubredditViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubredditViewHolder, position: Int) {
        getItem(position)?.let { holder.bindPost(it) }
    }

    private companion object {
        const val TITLE_LENGTH = 51
    }

    inner class SubredditViewHolder(private val binding: SubredditItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindPost(subreddit: Subreddit) {
            binding.apply {
                with(subreddit) {
                    root.setOnClickListener { navigationAction(displayName) }

                    var title = "r/$displayName: ${publicDescription.ifEmpty { description }}"
                    if (title.length > TITLE_LENGTH) {
                        title = title.take(TITLE_LENGTH).dropLastWhile { it != ' ' } + "..."
                    }
                    commentBodyText.text = title

                    if (!userIsSubscriber) {
                        likeImage.setOnClickListener { setSubscribeListener(subreddit) }
                        firstBackgroundView.setBackgroundResource(R.drawable.subreddit_not_follow_text_background)
                        secondBackgroundView.setBackgroundResource(R.drawable.subreddit_not_follow_text_background)
                        likeImage.setImageResource(R.drawable.subreddit_not_follow)
                    } else {
                        likeImage.setOnClickListener { setUnsubscribeListener(subreddit) }
                        firstBackgroundView.setBackgroundResource(R.drawable.subreddit_follow_text_background)
                        secondBackgroundView.setBackgroundResource(R.drawable.subreddit_follow_text_background)
                        likeImage.setImageResource(R.drawable.subreddit_follow)
                    }
                }
                root.post {
                    if (commentBodyText.lineCount < 2)
                        secondBackgroundView.visibility = View.GONE
                    else
                        secondBackgroundView.visibility = View.VISIBLE
                }
            }
        }

        private fun setSubscribeListener(subreddit: Subreddit) {
            val state = subscribeAction(subreddit.displayName)
            lifecycleOwner.lifecycleScope.launch {
                state.collect {
                    when (it) {
                        SubredditsViewModel.SubscribeStates.IN_PROGRESS -> {
                            binding.progressCircular.visibility = View.VISIBLE
                            binding.likeImage.visibility = View.INVISIBLE
                        }
                        SubredditsViewModel.SubscribeStates.SUCCESS -> {
                            binding.progressCircular.visibility = View.INVISIBLE
                            binding.likeImage.visibility = View.VISIBLE
                            subreddit.userIsSubscriber = true
                            binding.likeImage.setOnClickListener {
                                setUnsubscribeListener(
                                    subreddit
                                )
                            }
                            binding.firstBackgroundView.setBackgroundResource(R.drawable.subreddit_follow_text_background)
                            binding.secondBackgroundView.setBackgroundResource(R.drawable.subreddit_follow_text_background)
                            binding.likeImage.setImageResource(R.drawable.subreddit_follow)
                            cancel()
                        }
                        SubredditsViewModel.SubscribeStates.FAILURE -> {
                            binding.progressCircular.visibility = View.INVISIBLE
                            binding.likeImage.visibility = View.VISIBLE
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

        private fun setUnsubscribeListener(subreddit: Subreddit) {
            val state = unsubscribeAction(subreddit.displayName)
            lifecycleOwner.lifecycleScope.launch {
                state.collect {
                    when (it) {
                        SubredditsViewModel.SubscribeStates.IN_PROGRESS -> {
                            binding.progressCircular.visibility = View.VISIBLE
                            binding.likeImage.visibility = View.INVISIBLE
                        }
                        SubredditsViewModel.SubscribeStates.SUCCESS -> {
                            binding.progressCircular.visibility = View.INVISIBLE
                            binding.likeImage.visibility = View.VISIBLE
                            subreddit.userIsSubscriber = false
                            binding.likeImage.setOnClickListener { setSubscribeListener(subreddit) }
                            binding.firstBackgroundView.setBackgroundResource(R.drawable.subreddit_not_follow_text_background)
                            binding.secondBackgroundView.setBackgroundResource(R.drawable.subreddit_not_follow_text_background)
                            binding.likeImage.setImageResource(R.drawable.subreddit_not_follow)
                            cancel()
                        }
                        SubredditsViewModel.SubscribeStates.FAILURE -> {
                            binding.progressCircular.visibility = View.INVISIBLE
                            binding.likeImage.visibility = View.VISIBLE
                            Snackbar.make(
                                binding.root,
                                "Отписаться не удалось",
                                Snackbar.LENGTH_LONG
                            ).show()
                            cancel()
                        }
                    }
                }
            }
        }
    }

    class DiffUtilCallBack : DiffUtil.ItemCallback<Subreddit>() {
        override fun areItemsTheSame(oldItem: Subreddit, newItem: Subreddit): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Subreddit, newItem: Subreddit): Boolean {
            return oldItem.userIsSubscriber == newItem.userIsSubscriber
        }
    }
}