package com.mobcast.discussion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mobcast.R
import com.mobcast.databinding.CommentRecyclerViewItemBinding
import com.mobcast.discussion.models.DiscussionReply
import java.text.SimpleDateFormat
import java.util.*



class DiscussionReplyAdapter constructor(private val topLevel:Boolean) :
    ListAdapter<DiscussionReply, DiscussionReplyAdapter.DiscussionReplyViewHolder>(
        diffCallback
    ) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<DiscussionReply>() {
            override fun areItemsTheSame(
                oldItem: DiscussionReply,
                newItem: DiscussionReply
            ): Boolean {
                return oldItem.replyId == newItem.replyId
            }

            override fun areContentsTheSame(
                oldItem: DiscussionReply,
                newItem: DiscussionReply
            ): Boolean {
                return oldItem == newItem
            }
        }

        private val originalDateParser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)

    }

    inner class DiscussionReplyViewHolder(private val itemBinding: CommentRecyclerViewItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: DiscussionReply) {
            data.employeeProfilePictureURL?.let {
                itemBinding.replyPersonImage.load(it)
            }
            itemBinding.replyPersonName.text = data.employeeName ?: "NA"
            itemBinding.replyText.text = data.reply ?: "NA"
            data.likedCount?.let {
                itemBinding.replyLikeCount.text = it.toString()
            }
            if (topLevel) {
                itemBinding.reply.visibility = View.VISIBLE
            }
            val discussionAdapter = DiscussionReplyAdapter(false)
            if (data.childReplies.isNullOrEmpty()) {
                itemBinding.replyEndDivider.visibility = View.VISIBLE
            } else {
                itemBinding.replyTreeUI.visibility = View.VISIBLE
                itemBinding.repliesRV.apply {
                    adapter = discussionAdapter
                    discussionAdapter.submitList(
                        if (data.childReplies.size > 2) {
                            data.childReplies.subList(0, 2)
                        } else {
                            data.childReplies
                        }
                    )
                    visibility = View.VISIBLE
                    layoutManager = LinearLayoutManager(itemView.context).apply {
                        orientation = RecyclerView.VERTICAL
                    }
                }
                if (data.childReplies.size > 2) {
                    itemBinding.seeAllReplies.apply {
                        visibility = View.VISIBLE
                        text = itemView.context.resources.getString(R.string.seeAllReplies)
                            .format(data.childReplies.size)
                        setOnClickListener {
                            discussionAdapter.submitList(data.childReplies)
                            visibility = View.GONE
                        }
                    }
                }
            }
            data.timestamp?.let {
                originalDateParser.parse(it)?.let { date ->
                    ((Date().time - date.time)/1000).apply {
                        val months = this / 2628000
                        val days = this / 86400
                        val hours = this / 3600
                        val minutes = this / 60
                        itemBinding.replyTimeAgo.text = if (months > 0) {
                            if (months == 1L) {
                                itemView.context.getString(R.string.commentMonthsAgo)
                                    .format(months, "")
                            } else {
                                itemView.context.getString(R.string.commentMonthsAgo)
                                    .format(months, "s")
                            }
                        } else if (days > 0) {
                            if (months == 1L) {
                                itemView.context.getString(R.string.commentDaysAgo).format(days, "")
                            } else {
                                itemView.context.getString(R.string.commentDaysAgo)
                                    .format(days, "s")
                            }
                        } else if (hours > 0) {
                            if (hours == 1L) {
                                itemView.context.getString(R.string.commentHoursAgo)
                                    .format(hours, "")
                            } else {
                                itemView.context.getString(R.string.commentHoursAgo)
                                    .format(hours, "s")
                            }
                        } else if (minutes > 0) {
                            if (minutes == 1L) {
                                itemView.context.getString(R.string.commentMinAgo)
                                    .format(minutes, "")
                            } else {
                                itemView.context.getString(R.string.commentMinAgo)
                                    .format(minutes, "s")
                            }
                        } else {
                            itemView.context.getString(R.string.commentFewSecondsAgo)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscussionReplyViewHolder {
        return DiscussionReplyViewHolder(
            (CommentRecyclerViewItemBinding.inflate(
                LayoutInflater.from(
                    parent.context,
                ), parent, false
            ))
        )
    }

    override fun onBindViewHolder(holder: DiscussionReplyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}