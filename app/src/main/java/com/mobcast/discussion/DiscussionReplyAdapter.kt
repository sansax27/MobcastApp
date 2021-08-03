package com.mobcast.discussion

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mobcast.R
import com.mobcast.databinding.CommentRecyclerViewItemBinding
import com.mobcast.discussion.models.DiscussionReply
import com.mobcast.discussion.ui.VideoPlayActivity
import com.stfalcon.imageviewer.StfalconImageViewer
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class DiscussionReplyAdapter constructor(
    private val topLevel: Boolean
) :
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
            if (!data.reply.isNullOrEmpty()) {
                val spannableString = SpannableString(data.reply)
                var i = 0
                while (i < data.reply.length) {
                    if (data.reply[i] == '@') {
                        val start = i
                        i++
                        while (i < data.reply.length && (data.reply[i] != ' ' && data.reply[i] != '@')) {
                            i++
                        }
                        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#DCABAB")),
                        start,
                        i,
                        Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                    } else {
                        i++
                    }
                }
                itemBinding.replyText.text = spannableString
            } else {
                itemBinding.replyText.visibility = View.GONE
            }
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
            if (!data.fileData.isNullOrEmpty()) {
                val file = data.fileData[0]
                if (file.type == "image" || file.type == "video") {
                    file.thumbnailURL?.let {
                        if (it.isNotEmpty()) {
                            itemBinding.replyDataImage.load(it)
                            itemBinding.replyDataImageCardView.visibility = View.VISIBLE
                            if (file.type == "image") {
                                if (!file.remoteURL.isNullOrEmpty()) {
                                    itemBinding.replyDataImage.setOnClickListener {
                                        StfalconImageViewer.Builder(
                                            itemView.context,
                                            arrayOf(file.remoteURL)
                                        ) { view, imageURl ->
                                            view.load(imageURl)
                                        }.show()
                                    }
                                }
                            } else {
                                itemBinding.playVideo.visibility = View.VISIBLE
                                if (!file.remoteURL.isNullOrEmpty()) {
                                    itemBinding.playVideo.setOnClickListener {
                                        itemView.context.startActivity(
                                            Intent(
                                                itemView.context,
                                                VideoPlayActivity::class.java
                                            ).apply {
                                                putExtra("videoURL", file.remoteURL)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    itemBinding.docDataName.text = itemView.context.getString(R.string.docName)
                        .format(file.name ?: "NA", file.type ?: "NA")
                    itemBinding.docDataImageCardView.visibility = View.VISIBLE
                    itemBinding.docDataName.visibility = View.VISIBLE
                    if (!file.remoteURL.isNullOrEmpty()) {
                        itemBinding.docDataName.setOnClickListener {
                            try {
                                val manager =
                                    itemView.context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                                val request = DownloadManager.Request(Uri.parse(file.remoteURL))
                                request.setTitle(file.name ?: "NA")
                                request.addRequestHeader(
                                    "cookie",
                                    android.webkit.CookieManager.getInstance()
                                        .getCookie(file.remoteURL)
                                )
                                request.setDestinationInExternalFilesDir(itemView.context,Environment.DIRECTORY_DOCUMENTS, file.name ?: "NA")
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                manager.enqueue(request)
                            } catch (e: Exception) {
                                Timber.e(e)
                                Toast.makeText(
                                    itemView.context,
                                    itemView.context.getString(R.string.cantDownloadFile),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
            data.timestamp?.let {
                originalDateParser.parse(it)?.let { date ->
                    ((Date().time - date.time) / 1000).apply {
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