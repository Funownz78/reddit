package ru.fwnz.humblr.ui.main.favorite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.fwnz.humblr.R
import ru.fwnz.humblr.databinding.SubredditItemBinding
import ru.fwnz.humblr.domain.Subreddit

class FavoritesSubredditsAdapter() :
    ListAdapter<Subreddit, FavoritesSubredditsAdapter.FavoritesSubredditsViewHolder>(
        FavoritesSubredditsDiffUtilCallback()
    ) {

    private companion object {
        const val TITLE_LENGTH = 51
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoritesSubredditsViewHolder {
        val binding = SubredditItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FavoritesSubredditsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoritesSubredditsViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class FavoritesSubredditsViewHolder(private val binding: SubredditItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(subreddit: Subreddit) {
            binding.apply {
                with(subreddit) {
                    var title = "r/$displayName: ${publicDescription.ifEmpty { description }}"
                    if (title.length > TITLE_LENGTH) {
                        title = title.take(TITLE_LENGTH).dropLastWhile { it != ' ' } + "..."
                    }
                    commentBodyText.text = title

                    if (!userIsSubscriber) {
                        firstBackgroundView.setBackgroundResource(R.drawable.subreddit_not_follow_text_background)
                        secondBackgroundView.setBackgroundResource(R.drawable.subreddit_not_follow_text_background)
                        likeImage.setImageResource(R.drawable.subreddit_not_follow)
                    } else {
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
    }

    class FavoritesSubredditsDiffUtilCallback : DiffUtil.ItemCallback<Subreddit>() {
        override fun areItemsTheSame(oldItem: Subreddit, newItem: Subreddit): Boolean =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: Subreddit, newItem: Subreddit): Boolean =
            oldItem.userIsSubscriber == newItem.userIsSubscriber
    }
}



