package com.mobcast.discussion.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable


@JsonClass(generateAdapter = true)
data class Employees(@Json(name = "data") val employees:List<Employee>?) : Serializable
