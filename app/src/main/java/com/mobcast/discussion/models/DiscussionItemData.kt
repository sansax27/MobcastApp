package com.mobcast.discussion.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable


@JsonClass(generateAdapter = true)
data class DiscussionItemData(
    @Json(name = "FileID")
    val fileId: Int?,
    @Json(name = "Type")
    val type: String?,
    @Json(name = "Name")
    val name: String?,
    @Json(name = "RemoteURL")
    val remoteURL: String?,
    @Json(name = "ThumbnailURL")
    val thumbnailURL: String?,
    @Json(name = "Size")
    val size: Int?,
    @Json(name = "Language")
    val language: String?,
    @Json(name = "PageCount")
    val pageCount: Int?,
    @Json(name = "Duration")
    val duration: Int?,
    @Json(name = "IsViewed")
    val isReviewed: Boolean?
) : Serializable