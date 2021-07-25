package com.mobcast.topic

import com.mobcast.data.api.APIService
import com.mobcast.data.utils.ResultState
import com.mobcast.data.utils.safeAPICall
import com.mobcast.topic.models.Topics
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class TopicRepository @Inject constructor(private val retrofitAPIService: APIService, private val dispatcher: CoroutineDispatcher) {

    suspend fun getTopics():ResultState<Topics> {
        return safeAPICall(dispatcher) {
            retrofitAPIService.getTopics()
        }
    }
}