package ru.fwnz.humblr.ui.main.subreddits.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.fwnz.humblr.R
import ru.fwnz.humblr.databinding.CommentByUserItemBinding
import ru.fwnz.humblr.domain.Comment

class CommentsAdapter(
) : PagingDataAdapter<Comment, CommentsAdapter.CommentViewHolder>(LinksDiffUtilCallBack()) {
    private companion object {
        const val TITLE_LENGTH = 51
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = CommentByUserItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        getItem(position)?.let { holder.bindLink(it) }
    }

    inner class CommentViewHolder(private val binding: CommentByUserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindLink(comment: Comment) {
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

    class LinksDiffUtilCallBack : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.body == newItem.body
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.body == newItem.body
        }
    }
}