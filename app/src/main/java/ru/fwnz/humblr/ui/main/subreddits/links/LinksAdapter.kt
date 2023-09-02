package ru.fwnz.humblr.ui.main.subreddits.links

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.fwnz.humblr.R
import ru.fwnz.humblr.databinding.LinkItemBinding
import ru.fwnz.humblr.domain.Link

private const val TAG = "LinksAdapter"
class LinksAdapter(
    private val context: Context,
    private val navigationActionForUser: (String) -> Unit,
    private val navigationActionForLink: (String) -> Unit,
) : PagingDataAdapter<Link, LinksAdapter.LinksViewHolder>(LinksDiffUtilCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinksViewHolder {
        val binding = LinkItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return LinksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LinksViewHolder, position: Int) {
        getItem(position)?.let { holder.bindLink(it) }
    }

    inner class LinksViewHolder(private val binding: LinkItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bindLink(link: Link) {
                binding.apply {
                    with(link) {
                        root.setOnClickListener {
                            navigationActionForLink(link.id)
                        }
                        authorButton.text = author
                        authorButton.setOnClickListener { navigationActionForUser(author) }
                        val timeInHours = (((System.currentTimeMillis()/1000) - createdUtcAt) / 3600).toInt()
                        estimatedHoursText.text =
                            context.resources.getQuantityString(
                                R.plurals.estimated_hours,
                                timeInHours,
                                timeInHours,
                            )
                        commentsCountText.text =
                            context.resources.getQuantityString(
                                R.plurals.comments,
                                numComments,
                                numComments,
                            )
                        titleText.text = title
                        selftextText.text = selftext
                        likesCountText.text = ups.toString()
                        shareButton.setOnClickListener {
                            val shareIntent = Intent(Intent.ACTION_SEND)
                            shareIntent.type = "text/plain"
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, title)
                            shareIntent.putExtra(Intent.EXTRA_TEXT, url)
                            context.startActivity(Intent.createChooser(shareIntent, context.getString(
                                                            R.string.share_via)))
                        }
                    }
                }
            }
    }

    class LinksDiffUtilCallBack : DiffUtil.ItemCallback<Link>() {
        override fun areItemsTheSame(oldItem: Link, newItem: Link): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Link, newItem: Link): Boolean {
            return oldItem.name == newItem.name
        }
    }
}
