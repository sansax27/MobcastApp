package com.mobcast.topic.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Topic(
    @Json(name = "TagID") val tagId: Int?,
    @Json(name = "ModuleID") val moduleId: Int?,
    @Json(name = "TagName") val tagName: String?,
    @Json(name = "IconID") val icon: Icon?,
    @Json(name = "BackgroundColor") val backgroundColor: String?,
    @Json(name = "Priority") val priority: Int?,
    @Json(name = "TagDescription") val tagDescription: String?
)
