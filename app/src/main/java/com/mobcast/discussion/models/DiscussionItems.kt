package com.mobcast.discussion.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class DiscussionItems(
    @Json(name = "data") val discussionItems: List<DiscussionItem>?,
    @Json(name = "message") val message: String?,
    @Json(name = "status_code") val statusCode: Int?
)
