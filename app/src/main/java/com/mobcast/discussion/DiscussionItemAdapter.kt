package com.mobcast.discussion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mobcast.R
import com.mobcast.databinding.DiscussionRecyclerViewItemBinding
import com.mobcast.discussion.models.DiscussionItem
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class DiscussionItemAdapter @Inject constructor() :
    PagingDataAdapter<DiscussionItem, DiscussionItemAdapter.DiscussionViewHolder>(
        diffCallback
    ) {

    private val originalDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    private val targetDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<DiscussionItem>() {
            override fun areItemsTheSame(
                oldItem: DiscussionItem,
                newItem: DiscussionItem
            ): Boolean {
                return oldItem.broadcastId == newItem.broadcastId
            }

            override fun areContentsTheSame(
                oldItem: DiscussionItem,
                newItem: DiscussionItem
            ): Boolean {
                return oldItem.unixTimeStamp == newItem.unixTimeStamp
            }

        }
    }

    inner class DiscussionViewHolder(private val itemBinding: DiscussionRecyclerViewItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(data: DiscussionItem) {
            data.fileData?.let { it ->
                if (it.isNotEmpty()) {
                    it[0].type?.let { type ->
                        if (type == "image") {
                            it[0].remoteURL?.let {
                                itemBinding.discussionImage.load(it)
                            }
                        }
                    }
                }
            }
            itemBinding.discussionTitle.text = data.description ?: ""
            data.sentDate?.let { date ->
                data.by?.let { by ->
                    originalDateFormat.parse(date)?.let {
                        itemBinding.discussionDetails.text = itemView.context.getString(R.string.discussionDetails).format(by,targetDateFormat.format(it))
                    }
                }
            }
            data.likeCount?.let {
                itemBinding.discussionLikes.text = it.toString()
            }
            data.replyCount?.let {
                itemBinding.discussionReplyCount.text = it.toString()
            }
            itemBinding.discussionDetails.apply {
                if (text.isEmpty()) {
                    visibility = View.GONE
                }
            }
            itemBinding.discussionTitle.apply {
                if (text.isEmpty()) {
                    visibility = View.GONE
                }
            }
        }
    }

    override fun onBindViewHolder(holder: DiscussionViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscussionViewHolder {
        return DiscussionViewHolder(DiscussionRecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
}