package com.mobcast.discussion

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mobcast.R
import com.mobcast.databinding.DiscussionRecyclerViewItemBinding
import com.mobcast.discussion.models.DiscussionItem
import com.mobcast.discussion.models.DiscussionReply
import com.stfalcon.imageviewer.StfalconImageViewer
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class DiscussionItemAdapter @Inject constructor(val move:(replies:List<DiscussionReply>?) -> Unit) :
    PagingDataAdapter<DiscussionItem, DiscussionItemAdapter.DiscussionViewHolder>(
        diffCallback
    ) {



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
        private val originalDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        private val targetDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
    }

    inner class DiscussionViewHolder(private val itemBinding: DiscussionRecyclerViewItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(data: DiscussionItem) {
            data.fileData?.let { it ->
                if (it.isNotEmpty()) {
                    if (it[0].type=="video" || it[0].type=="image") {
                        if (!it[0].thumbnailURL.isNullOrEmpty()) {
                            itemBinding.discussionImage.load(it[0].thumbnailURL)
                            if (it[0].type=="image") {
                                if (!it[0].remoteURL.isNullOrEmpty()) {
                                    itemBinding.discussionImage.setOnClickListener { _ ->
                                        StfalconImageViewer.Builder(
                                            itemView.context,
                                            arrayOf(it[0].thumbnailURL)
                                        ) { view, imageURl ->
                                            view.load(imageURl)
                                        }.show()
                                    }
                                }
                            } else {
                                itemBinding.playVideoDiscussionButton.visibility = View.VISIBLE
                                if (!it[0].remoteURL.isNullOrEmpty()) {
                                    itemBinding.playVideoDiscussionButton.setOnClickListener { _ ->
                                        itemView.context.startActivity(
                                            Intent(
                                                itemView.context,
                                                VideoPlayActivity::class.java
                                            ).apply {
                                                putExtra("videoURL", it[0].remoteURL)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        if (!it[0].remoteURL.isNullOrEmpty()) {
                            itemBinding.discussionImage.setOnClickListener { _ ->
                                try {
                                    val manager =
                                        itemView.context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                                    val request = DownloadManager.Request(Uri.parse(it[0].remoteURL))
                                    request.setTitle(it[0].name ?: "NA")
                                    request.addRequestHeader("cookie",android.webkit.CookieManager.getInstance().getCookie(it[0].remoteURL))
                                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"MobCast")
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                    manager.enqueue(request)
                                } catch (e: Exception) {
                                    Toast.makeText(itemView.context, itemView.context.getString(R.string.cantDownloadFile), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                }
            }
            itemBinding.replyCount.setOnClickListener {
                move(data.replies)
            }
            itemBinding.discussionTitle.text = data.title ?: ""
            data.sentDate?.let { date ->
                data.by?.let { by ->
                    try {
                        itemBinding.discussionDetails.text = itemView.context.getString(R.string.discussionDetails).format(by,targetDateFormat.format(originalDateFormat.parse(date)!!))
                    } catch (e:Exception) {
                        itemBinding.discussionDetails.text = itemView.context.getString(R.string.discussionDetailsWithoutDate).format(by)
                    }

                }
            }
            if (data.sentDate==null && data.by!=null) {
                itemBinding.discussionDetails.text = itemView.context.getString(R.string.discussionDetailsWithoutDate).format(data.by)
            }
            data.likeCount?.let {
                itemBinding.likesCount.text = it.toString()
            }
            data.replyCount?.let {
                itemBinding.replyCount.text = it.toString()
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