package ru.fwnz.humblr.ui.main.profile.friends

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import ru.fwnz.humblr.R
import ru.fwnz.humblr.databinding.FriendItemBinding
import ru.fwnz.humblr.domain.Friend
import java.text.SimpleDateFormat
import java.util.*

class FriendsAdapter(
    private val context: Context
) : ListAdapter<Friend, FriendsAdapter.FriendViewHolder>(FriendsDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = FriendItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class FriendViewHolder(private val binding: FriendItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: Friend) {
            binding.apply {
                with(friend) {
                    Glide.with(avatarImage)
                        .load(iconImg)
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(avatarImage)
                    textName.text = displayNamePrefixed
                    val unixTime = createdUtcAt // example Unix timestamp
                    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                    val date = Date(unixTime * 1000)
                    val dateString = dateFormat.format(date)
                    textLink.text = context.getString(
                        R.string.created,
                        dateString
                    )


                }
            }
        }
    }

    class FriendsDiffUtilCallback : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean =
            oldItem.displayNamePrefixed == newItem.displayNamePrefixed

        override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean =
            oldItem.displayNamePrefixed == newItem.displayNamePrefixed
    }
}