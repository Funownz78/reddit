package ru.fwnz.humblr.ui.main.favorite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.fwnz.humblr.R
import ru.fwnz.humblr.databinding.CommentByUserItemBinding
import ru.fwnz.humblr.databinding.SubredditItemBinding
import ru.fwnz.humblr.domain.Comment
import ru.fwnz.humblr.domain.Subreddit
import ru.fwnz.humblr.ui.main.subreddits.user.CommentsAdapter

class FavoritesCommentsAdapter() :
    ListAdapter<Comment, FavoritesCommentsAdapter.FavoritesCommentsViewHolder>(
        FavoritesCommentsDiffUtilCallback()
    ) {

    private companion object {
        const val TITLE_LENGTH = 51
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoritesCommentsViewHolder {
        val binding = CommentByUserItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FavoritesCommentsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoritesCommentsViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class FavoritesCommentsViewHolder(private val binding: CommentByUserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            binding.apply {
                with(comment) {
                    var title = body
                    if (title.length > TITLE_LENGTH) {
                        title = title.take(TITLE_LENGTH).dropLastWhile { it != ' ' } + "..."
                    }
                    commentBodyText.text = title
                    likeImage.setImageResource(
                        when (likes) {
                            null -> {
                                firstBackgroundView.setBackgroundResource(R.drawable.subreddit_not_follow_text_background)
                                secondBackgroundView.setBackgroundResource(R.drawable.subreddit_not_follow_text_background)
                                R.drawable.favorite_gray
                            }
                            true -> {
                                firstBackgroundView.setBackgroundResource(R.drawable.subreddit_follow_text_background)
                                secondBackgroundView.setBackgroundResource(R.drawable.subreddit_follow_text_background)
                                R.drawable.heart_filled
                            }
                            false -> {
                                firstBackgroundView.setBackgroundResource(R.drawable.subreddit_not_follow_text_background)
                                secondBackgroundView.setBackgroundResource(R.drawable.subreddit_not_follow_text_background)
                                R.drawable.dislike
                            }
                        }
                    )

                }
            }
        }
    }

    class FavoritesCommentsDiffUtilCallback : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean =
            oldItem.body == newItem.body

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean =
            oldItem.body == newItem.body
    }
}



