package com.mobcast.discussion.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable


@JsonClass(generateAdapter = true)
data class Employee(
    @Json(name = "EmployeeID") val employeeId: String?,
    @Json(name = "Name") var employeeName: String?,
    @Json(name = "ProfilePicture") val profilePicture: String?
) : Serializable
