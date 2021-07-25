package com.mobcast.topic.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Icon(
    @Json(name = "IconID") val iconId: Int?,
    @Json(name = "IconName") val iconName: String?,
    @Json(name = "IconRemoteURL") val iconURL: String?
)
