package com.mobcasttask1.data.api

import com.mobcasttask1.discussion.models.DiscussionItems
import com.mobcasttask1.topic.models.Topics
import retrofit2.http.GET

interface APIService {


    @GET("fcfbd6d3-5942-47bc-bc78-9eb0207c53a9")
    suspend fun getDiscussionItems(): Topics

    @GET("0574935c-88d3-49e2-ba81-e6caf5515f14")
    suspend fun getTopics(): DiscussionItems
}