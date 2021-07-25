package com.mobcast.topic.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Topics(@Json(name = "BroadcastTags") val topics:List<Topic>?)