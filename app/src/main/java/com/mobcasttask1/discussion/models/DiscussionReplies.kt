package com.mobcasttask1.discussion.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class DiscussionReplies(
    @Json(name = "ReplyId")
    val replyId: Int?,
    @Json(name = "BroadcastID")
    val broadcastId: Int?,
    @Json(name = "EmployeeID")
    val employeeId: Int?,
    @Json(name = "EmployeeName")
    val employeeName: String?,
    @Json(name = "EmployeeProfilePictureURL")
    val employeeProfilePictureURL: String?,
    @Json(name = "Reply")
    val reply:String?,
    @Json(name = "ParentReplyID")
    val parentReplyId:Int?,
    @Json(name = "Timestamp")
    val timestamp:String?,
    @Json(name = "LikeCount")
    val likedCount:Int?,
    @Json(name = "IsLiked")
    val isLiked:Boolean?,
    @Json(name = "FileInfo")
    val fileData:List<DiscussionItemData>?,
    @Json(name = "ChildReplies")
    val childReplies:List<DiscussionReplies>?
)
