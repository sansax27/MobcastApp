package com.mobcasttask1.discussion.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class DiscussionItem(
    @Json(name = "BroadcastID")
    val broadcastId: Int?,
    @Json(name = "ModuleID")
    val moduleId: Int?,
    @Json(name = "TagID")
    val tagId: Int?,
    @Json(name = "Type")
    val type: String?,
    @Json(name = "Title")
    val title: String?,
    @Json(name = "Description")
    val description: String?,
    @Json(name = "By")
    val by: String?,
    @Json(name = "CreatedByProfilePicture")
    val createdByProfilePicture: String?,
    @Json(name = "LikeCount")
    val likeCount: Int?,
    @Json(name = "IsLiked")
    val isLiked: Boolean?,
    @Json(name = "SentOn")
    val sentOn: String?,
    @Json(name = "SentDate")
    val sentDate: String?,
    @Json(name = "SentTime")
    val sentTime: String?,
    @Json(name = "IsRead")
    val isRead: Boolean?,
    @Json(name = "UnixTimestamp")
    val unixTimeStamp: String?,
    @Json(name = "FileInfo")
    val fileData:List<DiscussionItemData>?,
    @Json(name = "Replies")
    val replies:List<DiscussionReplies>?,
    @Json(name = "ReplyCount")
    val replyCount:Int?
)
